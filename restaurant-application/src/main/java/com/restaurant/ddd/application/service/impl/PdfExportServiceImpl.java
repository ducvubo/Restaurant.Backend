package com.restaurant.ddd.application.service.impl;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.restaurant.ddd.application.model.adjustment.AdjustmentTransactionDTO;
import com.restaurant.ddd.application.model.stock.StockTransactionDTO;
import com.restaurant.ddd.application.model.inventorycount.InventoryCountDTO;
import com.restaurant.ddd.application.service.AdjustmentTransactionAppService;
import com.restaurant.ddd.application.service.InventoryCountAppService;
import com.restaurant.ddd.application.service.PdfExportService;
import com.restaurant.ddd.application.service.StockTransactionAppService;
import com.restaurant.ddd.domain.enums.AdjustmentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfExportServiceImpl implements PdfExportService {

    private final AdjustmentTransactionAppService adjustmentService;
    private final StockTransactionAppService stockTransactionService;
    private final InventoryCountAppService inventoryCountService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public byte[] exportAdjustmentToPdf(UUID adjustmentId) {
        try {
            // Fetch adjustment data
            var result = adjustmentService.getAdjustment(adjustmentId);
            if (result.getData() == null) {
                throw new RuntimeException("Không tìm thấy phiếu điều chỉnh");
            }
            
            AdjustmentTransactionDTO adjustment = result.getData();
            
            // Create PDF in memory
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(30, 30, 30, 30);
            
            // Use Times New Roman TrueType font from Windows for full Vietnamese support
            PdfFont font = loadTimesNewRomanFont(false);
            PdfFont boldFont = loadTimesNewRomanFont(true);
            
            // Determine document type
            boolean isReceipt = adjustment.getAdjustmentType() == AdjustmentType.INCREASE.code();
            String title = isReceipt ? "PHIẾU NHẬP KHO" : "PHIẾU XUẤT KHO";
            
            // Add title
            Paragraph titlePara = new Paragraph(title)
                .setFont(boldFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setMarginBottom(5);
            document.add(titlePara);
            
            // Add transaction code
            Paragraph codePara = new Paragraph("Mã phiếu: " + adjustment.getTransactionCode())
                .setFont(font)
                .setFontSize(11)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(15);
            document.add(codePara);
            
            // Add transaction info
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(15);
            
            // Add adjuster info FIRST
            if (adjustment.getCreatedByName() != null && !adjustment.getCreatedByName().isEmpty()) {
                addInfoRow(infoTable, "Người điều chỉnh:", adjustment.getCreatedByName(), font, boldFont);
            }
            
            addInfoRow(infoTable, "Kho:", adjustment.getWarehouseName(), font, boldFont);
            
            // Add adjustment type
            String adjustmentTypeName = isReceipt ? "Điều chỉnh tăng" : "Điều chỉnh giảm";
            addInfoRow(infoTable, "Loại phiếu:", adjustmentTypeName, font, boldFont);
            
            addInfoRow(infoTable, "Ngày điều chỉnh:", 
                adjustment.getTransactionDate().format(DATE_FORMATTER), font, boldFont);
            
            if (adjustment.getReferenceNumber() != null && !adjustment.getReferenceNumber().isEmpty()) {
                addInfoRow(infoTable, "Số tham chiếu:", adjustment.getReferenceNumber(), font, boldFont);
            }
            
            addInfoRow(infoTable, "Lý do:", adjustment.getReason(), font, boldFont);
            
            if (adjustment.getNotes() != null && !adjustment.getNotes().isEmpty()) {
                addInfoRow(infoTable, "Ghi chú:", adjustment.getNotes(), font, boldFont);
            }
            
            document.add(infoTable);
            
            // Add items table
            Paragraph itemsTitle = new Paragraph("Danh sách nguyên liệu:")
                .setFont(boldFont)
                .setFontSize(12)
                .setMarginBottom(10);
            document.add(itemsTitle);
            
            Table itemsTable = new Table(UnitValue.createPercentArray(new float[]{1, 3, 2, 2, 3}))
                .useAllAvailableWidth()
                .setMarginBottom(20);
            
            // Table header
            addTableHeader(itemsTable, "STT", boldFont);
            addTableHeader(itemsTable, "Tên nguyên liệu", boldFont);
            addTableHeader(itemsTable, "Đơn vị", boldFont);
            addTableHeader(itemsTable, "Số lượng", boldFont);
            addTableHeader(itemsTable, "Ghi chú", boldFont);
            
            // Table rows
            int index = 1;
            for (var item : adjustment.getItems()) {
                addTableCell(itemsTable, String.valueOf(index++), font, TextAlignment.CENTER);
                addTableCell(itemsTable, item.getMaterialName(), font, TextAlignment.LEFT);
                addTableCell(itemsTable, item.getUnitName(), font, TextAlignment.CENTER);
                addTableCell(itemsTable, formatNumber(item.getQuantity()), font, TextAlignment.RIGHT);
                addTableCell(itemsTable, item.getNotes() != null ? item.getNotes() : "", font, TextAlignment.LEFT);
            }
            
            document.add(itemsTable);
            
            // Add signature section - no names, just titles for manual signing
            Table signatureTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth()
                .setMarginTop(30);
            
            addSignatureCell(signatureTable, "Người lập phiếu", null, font, boldFont);
            addSignatureCell(signatureTable, "Thủ kho", null, font, boldFont);
            
            document.add(signatureTable);
            
            
            document.close();
            
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo file PDF: " + e.getMessage(), e);
        }
    }
    
    private void addInfoRow(Table table, String label, String value, PdfFont font, PdfFont boldFont) {
        // Create a single paragraph with bold label and regular value
        Paragraph para = new Paragraph()
            .add(new com.itextpdf.layout.element.Text(label + " ").setFont(boldFont))
            .add(new com.itextpdf.layout.element.Text(value).setFont(font))
            .setFont(font)
            .setFontSize(10);
        
        Cell cell = new Cell(1, 2)  // Span 2 columns
            .add(para)
            .setBorder(Border.NO_BORDER)
            .setPadding(3);
        
        table.addCell(cell);
    }
    
    private void addTableHeader(Table table, String text, PdfFont font) {
        Cell cell = new Cell()
            .add(new Paragraph(text).setFont(font).setFontSize(10))
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            .setTextAlignment(TextAlignment.CENTER)
            .setPadding(5);
        table.addHeaderCell(cell);
    }
    
    private void addTableCell(Table table, String text, PdfFont font, TextAlignment alignment) {
        Cell cell = new Cell()
            .add(new Paragraph(text).setFont(font).setFontSize(10))
            .setTextAlignment(alignment)
            .setPadding(5);
        table.addCell(cell);
    }
    
    private void addSignatureCell(Table table, String title, String name, PdfFont font, PdfFont boldFont) {
        Cell cell = new Cell()
            .add(new Paragraph(title).setFont(boldFont).setFontSize(11).setTextAlignment(TextAlignment.CENTER));
        
        if (name != null && !name.isEmpty()) {
            cell.add(new Paragraph("\n").setFont(font).setFontSize(10))
                .add(new Paragraph(name).setFont(font).setFontSize(10).setTextAlignment(TextAlignment.CENTER));
        } else {
            cell.add(new Paragraph("\n\n\n").setFont(font).setFontSize(10));
        }
        
        cell.add(new Paragraph("(Ký, ghi rõ họ tên)").setFont(font).setFontSize(9).setTextAlignment(TextAlignment.CENTER).setItalic())
            .setBorder(Border.NO_BORDER)
            .setPadding(5)
            .setTextAlignment(TextAlignment.CENTER);
        table.addCell(cell);
    }
    
    private String formatNumber(java.math.BigDecimal number) {
        if (number == null) return "0";
        // Remove decimal places if the number is a whole number
        if (number.stripTrailingZeros().scale() <= 0) {
            return String.format("%,d", number.longValue());
        }
        return String.format("%,.2f", number);
    }
    
    /**
     * Load Times New Roman font from Windows system fonts
     * Falls back to standard font if not found
     */
    private PdfFont loadTimesNewRomanFont(boolean bold) {
        try {
            // Times New Roman font files in Windows
            String fontFileName = bold ? "timesbd.ttf" : "times.ttf";
            String fontPath = "C:/Windows/Fonts/" + fontFileName;
            
            // Check if font file exists
            File fontFile = new File(fontPath);
            if (fontFile.exists()) {
                // Load with IDENTITY_H encoding for full Unicode/Vietnamese support
                return PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            } else {
                // Fallback to built-in font if Times New Roman not found
                System.out.println("Times New Roman not found at: " + fontPath + ", using fallback font");
                return PdfFontFactory.createFont(bold ? "Times-Bold" : "Times-Roman");
            }
        } catch (Exception e) {
            // If any error, use built-in font
            try {
                return PdfFontFactory.createFont(bold ? "Times-Bold" : "Times-Roman");
            } catch (Exception ex) {
                throw new RuntimeException("Failed to load font", ex);
            }
        }
    }
    
    @Override
    public byte[] exportStockTransactionToPdf(UUID stockTransactionId) {
        try {
            log.info("[PDF Service] Starting PDF generation for stock transaction: {}", stockTransactionId);
            
            // Fetch stock transaction data
            var result = stockTransactionService.getTransaction(stockTransactionId);
            if (result.getData() == null) {
                log.error("[PDF Service] Transaction data is null for ID: {}", stockTransactionId);
                throw new RuntimeException("Đã xảy ra lỗi khi tải thông tin phiếu");
            }
            
            var transaction = result.getData();
            log.info("[PDF Service] Transaction loaded: {}, type: {}", transaction.getTransactionCode(), transaction.getTransactionType());
            
            // Create PDF in memory
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(30, 30, 30, 30);
            
            // Use Times New Roman TrueType font from Windows for full Vietnamese support
            PdfFont font = loadTimesNewRomanFont(false);
            PdfFont boldFont = loadTimesNewRomanFont(true);
            
            // Determine document type based on transaction type
            boolean isStockIn = transaction.getTransactionType() == 1; // 1=IN, 2=OUT
            String title = isStockIn ? "PHIẾU NHẬP KHO" : "PHIẾU XUẤT KHO";
            
            // Add title
            Paragraph titlePara = new Paragraph(title)
                .setFont(boldFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
            document.add(titlePara);
            
            // Add transaction code
            Paragraph codePara = new Paragraph("Mã phiếu: " + transaction.getTransactionCode())
                .setFont(font)
                .setFontSize(11)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(15);
            document.add(codePara);
            
            // Add transaction info
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(15);
            
            // Add performer info FIRST
            if (isStockIn) {
                if (transaction.getReceivedByName() != null && !transaction.getReceivedByName().isEmpty()) {
                    addInfoRow(infoTable, "Người nhập kho:", transaction.getReceivedByName(), font, boldFont);
                }
            } else {
                if (transaction.getIssuedByName() != null && !transaction.getIssuedByName().isEmpty()) {
                    addInfoRow(infoTable, "Người xuất kho:", transaction.getIssuedByName(), font, boldFont);
                }
                Integer stockOutType = transaction.getStockOutType();
                if (stockOutType != null && stockOutType == 1 && transaction.getReceivedByName() != null && !transaction.getReceivedByName().isEmpty()) {
                    addInfoRow(infoTable, "Người tiếp nhận:", transaction.getReceivedByName(), font, boldFont);
                }
            }
            
            addInfoRow(infoTable, "Kho:", transaction.getWarehouseName(), font, boldFont);
            
            // Add type-specific info
            if (isStockIn) {
                String stockInTypeName = transaction.getStockInType() == 2 ? "Chuyển kho nội bộ" : "Nhập từ NCC";
                addInfoRow(infoTable, "Loại phiếu:", stockInTypeName, font, boldFont);
                
                if (transaction.getSupplierName() != null && !transaction.getSupplierName().isEmpty()) {
                    addInfoRow(infoTable, "Nhà cung cấp:", transaction.getSupplierName(), font, boldFont);
                }
                
                if (transaction.getStockInType() == 2 && transaction.getRelatedTransactionCode() != null) {
                    addInfoRow(infoTable, "Từ phiếu xuất:", transaction.getRelatedTransactionCode(), font, boldFont);
                }
            } else {
                String stockOutTypeName = "Xuất kho";
                if (transaction.getStockOutType() != null) {
                    switch (transaction.getStockOutType()) {
                        case 1: stockOutTypeName = "Chuyển kho nội bộ"; break;
                        case 2: stockOutTypeName = "Bán lẻ"; break;
                        case 3: stockOutTypeName = "Hủy/Thanh lý"; break;
                    }
                }
                addInfoRow(infoTable, "Loại phiếu:", stockOutTypeName, font, boldFont);
                
                if (transaction.getDestinationWarehouseName() != null && !transaction.getDestinationWarehouseName().isEmpty()) {
                    addInfoRow(infoTable, "Kho đích:", transaction.getDestinationWarehouseName(), font, boldFont);
                }
                
                if (transaction.getCustomerName() != null && !transaction.getCustomerName().isEmpty()) {
                    addInfoRow(infoTable, "Khách hàng:", transaction.getCustomerName(), font, boldFont);
                }
                
                if (transaction.getDisposalReason() != null && !transaction.getDisposalReason().isEmpty()) {
                    addInfoRow(infoTable, "Lý do hủy:", transaction.getDisposalReason(), font, boldFont);
                }
            }
            
            addInfoRow(infoTable, "Ngày giao dịch:", 
                transaction.getTransactionDate().format(DATE_FORMATTER), font, boldFont);
            
            if (transaction.getReferenceNumber() != null && !transaction.getReferenceNumber().isEmpty()) {
                addInfoRow(infoTable, "Số chứng từ:", transaction.getReferenceNumber(), font, boldFont);
            }
            
            if (transaction.getNotes() != null && !transaction.getNotes().isEmpty()) {
                addInfoRow(infoTable, "Ghi chú:", transaction.getNotes(), font, boldFont);
            }
            
            document.add(infoTable);
            
            // Add items table
            Paragraph itemsTitle = new Paragraph("Danh sách nguyên liệu:")
                .setFont(boldFont)
                .setFontSize(12)
                .setMarginBottom(10);
            document.add(itemsTitle);
            
            Table itemsTable = new Table(UnitValue.createPercentArray(new float[]{1, 3, 2, 2, 2, 2, 3}))
                .useAllAvailableWidth()
                .setMarginBottom(20);
            
            // Table header
            addTableHeader(itemsTable, "STT", boldFont);
            addTableHeader(itemsTable, "Tên nguyên liệu", boldFont);
            addTableHeader(itemsTable, "Đơn vị", boldFont);
            addTableHeader(itemsTable, "Số lượng", boldFont);
            addTableHeader(itemsTable, "Đơn giá", boldFont);
            addTableHeader(itemsTable, "Thành tiền", boldFont);
            addTableHeader(itemsTable, "Ghi chú", boldFont);
            
            // Table rows
            int index = 1;
            java.math.BigDecimal grandTotal = java.math.BigDecimal.ZERO;
            
            if (isStockIn && transaction.getStockInItems() != null) {
                for (var item : transaction.getStockInItems()) {
                    addTableCell(itemsTable, String.valueOf(index++), font, TextAlignment.CENTER);
                    addTableCell(itemsTable, item.getMaterialName(), font, TextAlignment.LEFT);
                    addTableCell(itemsTable, item.getUnitName(), font, TextAlignment.CENTER);
                    addTableCell(itemsTable, formatNumber(item.getQuantity()), font, TextAlignment.RIGHT);
                    addTableCell(itemsTable, formatNumber(item.getUnitPrice()), font, TextAlignment.RIGHT);
                    addTableCell(itemsTable, formatNumber(item.getTotalAmount()), font, TextAlignment.RIGHT);
                    addTableCell(itemsTable, item.getNotes() != null ? item.getNotes() : "", font, TextAlignment.LEFT);
                    
                    if (item.getTotalAmount() != null) {
                        grandTotal = grandTotal.add(item.getTotalAmount());
                    }
                }
            } else if (!isStockIn && transaction.getStockOutItems() != null) {
                for (var item : transaction.getStockOutItems()) {
                    addTableCell(itemsTable, String.valueOf(index++), font, TextAlignment.CENTER);
                    addTableCell(itemsTable, item.getMaterialName(), font, TextAlignment.LEFT);
                    addTableCell(itemsTable, item.getUnitName(), font, TextAlignment.CENTER);
                    addTableCell(itemsTable, formatNumber(item.getQuantity()), font, TextAlignment.RIGHT);
                    addTableCell(itemsTable, formatNumber(item.getUnitPrice()), font, TextAlignment.RIGHT);
                    addTableCell(itemsTable, formatNumber(item.getTotalAmount()), font, TextAlignment.RIGHT);
                    addTableCell(itemsTable, item.getNotes() != null ? item.getNotes() : "", font, TextAlignment.LEFT);
                    
                    if (item.getTotalAmount() != null) {
                        grandTotal = grandTotal.add(item.getTotalAmount());
                    }
                }
            }
            
            document.add(itemsTable);
            
            // Add total amount
            Paragraph totalPara = new Paragraph("Tổng tiền: " + formatNumber(grandTotal) + " đ")
                .setFont(boldFont)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(20);
            document.add(totalPara);
            
            // Add signature section - no names, just titles for manual signing
            Table signatureTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth()
                .setMarginTop(30);
            
            if (isStockIn) {
                // Phiếu nhập kho: Người lập phiếu + Thủ kho
                addSignatureCell(signatureTable, "Người lập phiếu", null, font, boldFont);
                addSignatureCell(signatureTable, "Thủ kho", null, font, boldFont);
            } else {
                // Phiếu xuất kho: Người lập phiếu + Thủ kho
                addSignatureCell(signatureTable, "Người lập phiếu", null, font, boldFont);
                addSignatureCell(signatureTable, "Thủ kho", null, font, boldFont);
            }
            
            document.add(signatureTable);
            
            
            document.close();
            
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo file PDF: " + e.getMessage(), e);
        }
    }
    
    @Override
    public byte[] exportInventoryCountToPdf(UUID inventoryCountId) {
        try {
            // Fetch inventory count data
            var transaction = inventoryCountService.get(inventoryCountId);
            if (transaction == null) {
                throw new RuntimeException("Đã xảy ra lỗi khi tải thông tin phiếu");
            }
            
            // Create PDF in memory
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(30, 30, 30, 30);
            
            // Use Times New Roman TrueType font from Windows for full Vietnamese support
            PdfFont font = loadTimesNewRomanFont(false);
            PdfFont boldFont = loadTimesNewRomanFont(true);
            
            // Add title
            Paragraph titlePara = new Paragraph("PHIẾU KIỂM KÊ KHO")
                .setFont(boldFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
            document.add(titlePara);
            
            // Add count code
            Paragraph codePara = new Paragraph("Mã phiếu: " + transaction.getCountCode())
                .setFont(font)
                .setFontSize(11)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(15);
            document.add(codePara);
            
            // Add transaction info
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(15);
            
            // Add performer info FIRST
            if (transaction.getPerformedByName() != null && !transaction.getPerformedByName().isEmpty()) {
                addInfoRow(infoTable, "Người kiểm kê:", transaction.getPerformedByName(), font, boldFont);
            }
            
            addInfoRow(infoTable, "Kho:", transaction.getWarehouseName(), font, boldFont);
            addInfoRow(infoTable, "Ngày kiểm kê:", 
                transaction.getCountDate().format(DATE_FORMATTER), font, boldFont);
            addInfoRow(infoTable, "Trạng thái:", transaction.getCountStatusName(), font, boldFont);
            
            if (transaction.getAdjustmentTransactionCode() != null) {
                addInfoRow(infoTable, "Phiếu điều chỉnh:", transaction.getAdjustmentTransactionCode(), font, boldFont);
            }
            
            if (transaction.getNotes() != null && !transaction.getNotes().isEmpty()) {
                addInfoRow(infoTable, "Ghi chú:", transaction.getNotes(), font, boldFont);
            }
            
            document.add(infoTable);
            
            // Add items table
            Paragraph itemsTitle = new Paragraph("Chi tiết chênh lệch:")
                .setFont(boldFont)
                .setFontSize(12)
                .setMarginBottom(10);
            document.add(itemsTitle);
            
            // Items table with columns: STT, Tên nguyên liệu, Số lô, Hạn sử dụng, Đơn vị, Tồn sổ sách, Thực tế, Chênh lệch, Ghi chú
            Table itemsTable = new Table(UnitValue.createPercentArray(new float[]{1, 3, 2, 2, 2, 2, 2, 2, 3}))
                .useAllAvailableWidth()
                .setMarginBottom(20);
            
            // Table header
            addTableHeader(itemsTable, "STT", boldFont);
            addTableHeader(itemsTable, "Tên nguyên liệu", boldFont);
            addTableHeader(itemsTable, "Số lô", boldFont);
            addTableHeader(itemsTable, "Hạn SD", boldFont);
            addTableHeader(itemsTable, "Đơn vị", boldFont);
            addTableHeader(itemsTable, "Sổ sách", boldFont);
            addTableHeader(itemsTable, "Thực tế", boldFont);
            addTableHeader(itemsTable, "Chênh lệch", boldFont);
            addTableHeader(itemsTable, "Ghi chú", boldFont);
            
            // Table rows
            int index = 1;
            java.math.BigDecimal totalDiff = java.math.BigDecimal.ZERO;
            
            if (transaction.getItems() != null) {
                for (var item : transaction.getItems()) {
                    addTableCell(itemsTable, String.valueOf(index++), font, TextAlignment.CENTER);
                    addTableCell(itemsTable, item.getMaterialName(), font, TextAlignment.LEFT);
                    addTableCell(itemsTable, item.getBatchNumber() != null ? item.getBatchNumber() : "-", font, TextAlignment.CENTER);
                    addTableCell(itemsTable, item.getTransactionDate() != null ? item.getTransactionDate().format(DATE_ONLY_FORMATTER) : "-", font, TextAlignment.CENTER);
                    addTableCell(itemsTable, item.getUnitName(), font, TextAlignment.CENTER);
                    addTableCell(itemsTable, formatNumber(item.getSystemQuantity()), font, TextAlignment.RIGHT);
                    addTableCell(itemsTable, formatNumber(item.getActualQuantity()), font, TextAlignment.RIGHT);
                    addTableCell(itemsTable, formatNumber(item.getDifferenceQuantity()), font, TextAlignment.RIGHT);
                    addTableCell(itemsTable, item.getNotes() != null ? item.getNotes() : "", font, TextAlignment.LEFT);
                    
                    if (item.getDifferenceQuantity() != null) {
                        totalDiff = totalDiff.add(item.getDifferenceQuantity());
                    }
                }
            }
            
            document.add(itemsTable);
            
            // Summary total difference
            Paragraph totalPara = new Paragraph("Tổng chênh lệch: " + formatNumber(totalDiff))
                .setFont(boldFont)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(20);
            document.add(totalPara);
            
            // Add signature section - no names, just titles for manual signing
            Table signatureTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth()
                .setMarginTop(30);
            
            addSignatureCell(signatureTable, "Người lập phiếu", null, font, boldFont);
            addSignatureCell(signatureTable, "Thủ kho", null, font, boldFont);
            
            document.add(signatureTable);
            
            document.close();
            
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo file PDF: " + e.getMessage(), e);
        }
    }
}
