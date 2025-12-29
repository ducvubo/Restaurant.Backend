package com.restaurant.util;

import com.restaurant.ddd.domain.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Export tất cả enum từ backend sang JavaScript và TypeScript cho frontend
 */
@Component
@Slf4j
public class EnumToJsExporter {

    // Đường dẫn tương đối từ thư mục chạy ứng dụng
    private static final String TS_OUTPUT_FILE = "enums.ts";

    /**
     * Export tất cả enum khi ứng dụng khởi động
     */
    public void exportEnumsOnStartup() {
        try {
            log.info("Bắt đầu export enum từ backend sang frontend...");
            
            List<EnumExportModel> enumModels = getEnumModels();
            
            if (!enumModels.isEmpty()) {
                // Tính toán đường dẫn từ project root
                Path projectRoot = getProjectRoot();
                Path frontendEnumsPath = projectRoot.resolve("Restaurant.Management/src/enums");
                Files.createDirectories(frontendEnumsPath);
                
                String tsPath = frontendEnumsPath.resolve(TS_OUTPUT_FILE).toString();
                
                exportToTsFile(enumModels, tsPath);
                
                log.info("Export enum thành công!");
                log.info("  → TS: {}", tsPath);
            } else {
                log.warn("Không tìm thấy enum nào để export.");
            }
        } catch (Exception e) {
            log.error("Lỗi khi export enum: {}", e.getMessage(), e);
        }
    }

    /**
     * Lấy đường dẫn project root (thư mục chứa Restaurant.Backend và Restaurant.Management)
     */
    private Path getProjectRoot() {
        // Lấy đường dẫn hiện tại (thường là từ target/classes khi chạy)
        String currentPath = System.getProperty("user.dir");
        Path current = Paths.get(currentPath);
        
        // Tìm thư mục root (chứa cả Restaurant.Backend và Restaurant.Management)
        Path root = current;
        while (root != null && !Files.exists(root.resolve("Restaurant.Management"))) {
            root = root.getParent();
            if (root == null || root.equals(root.getRoot())) {
                // Fallback: thử đường dẫn tương đối từ current
                root = current.resolve("../../..").normalize();
                break;
            }
        }
        
        // Nếu không tìm thấy, sử dụng đường dẫn tương đối
        if (root == null || !Files.exists(root.resolve("Restaurant.Management"))) {
            root = Paths.get("../../..").toAbsolutePath().normalize();
        }
        
        return root;
    }

    /**
     * Lấy danh sách tất cả enum models từ các enum class
     */
    private List<EnumExportModel> getEnumModels() {
        List<EnumExportModel> enumModels = new ArrayList<>();
        
        // Thêm các enum cần export ở đây
        enumModels.add(parseEnum(DataStatus.class));
        enumModels.add(parseEnum(ResultCode.class));
        enumModels.add(parseEnum(CustomerType.class));
        enumModels.add(parseEnum(InventoryMethod.class));
        enumModels.add(parseEnum(TransactionType.class));
        enumModels.add(parseEnum(WarehouseType.class));
        enumModels.add(parseEnum(StockOutType.class));
        enumModels.add((parseEnum(StockInType.class)));
        enumModels.add((parseEnum(AdjustmentType.class)));
        enumModels.add((parseEnum(WorkflowType.class)));
        enumModels.add((parseEnum(WorkflowActionType.class)));
        enumModels.add((parseEnum(WorkflowStepType.class)));
        
        // Purchasing Module Enums
        enumModels.add((parseEnum(PurchaseRequisitionStatus.class)));
        enumModels.add((parseEnum(RfqStatus.class)));
        enumModels.add((parseEnum(PurchaseOrderStatus.class)));
        enumModels.add((parseEnum(PurchasePriority.class)));

        return enumModels;
    }

    /**
     * Parse một enum class thành EnumExportModel
     */
    private EnumExportModel parseEnum(Class<? extends Enum<?>> enumClass) {
        EnumExportModel enumModel = new EnumExportModel();
        String className = enumClass.getSimpleName();
        enumModel.setName(toCamelCase(className));
        enumModel.setCapitalizedName(className);

        Enum<?>[] enumConstants = enumClass.getEnumConstants();
        int currentValue = 0;

        for (Enum<?> enumConstant : enumConstants) {
            EnumMemberExportModel memberModel = new EnumMemberExportModel();
            String memberName = enumConstant.name();
            memberModel.setName(memberName); // Giữ nguyên tên từ backend
            memberModel.setText(memberName); // Default text

            // Mặc định sử dụng ordinal
            memberModel.setValue(enumConstant.ordinal());
            
            // Thử lấy giá trị từ method code() nếu có (như ResultCode)
            try {
                Method codeMethod = enumClass.getMethod("code");
                Object codeValue = codeMethod.invoke(enumConstant);
                if (codeValue instanceof Integer) {
                    memberModel.setValue((Integer) codeValue);
                } else if (codeValue instanceof Number) {
                    memberModel.setValue(((Number) codeValue).intValue());
                }
            } catch (NoSuchMethodException e) {
                // Không có method code(), sử dụng ordinal
            } catch (Exception e) {
                log.warn("Lỗi khi lấy code() từ enum {}: {}", memberName, e.getMessage());
            }

            // Lấy text từ method message() nếu có (như ResultCode)
            try {
                Method messageMethod = enumClass.getMethod("message");
                Object messageValue = messageMethod.invoke(enumConstant);
                if (messageValue instanceof String) {
                    memberModel.setText((String) messageValue);
                }
            } catch (NoSuchMethodException e) {
                // Không có method message(), giữ nguyên default text
            } catch (Exception e) {
                log.warn("Lỗi khi lấy message() từ enum {}: {}", memberName, e.getMessage());
            }

            enumModel.getMembers().add(memberModel);
            currentValue++;
        }

        return enumModel;
    }


    /**
     * Export enum models ra file TypeScript (kết hợp data và types)
     */
    private void exportToTsFile(List<EnumExportModel> enumModels, String outputPath) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        // Auto-generated comment
        sb.append("// Auto-generated enum file - DO NOT EDIT MANUALLY\n");
        sb.append("// This file is automatically generated from backend enums\n");
        sb.append("// Run backend application to regenerate this file\n\n");
        
        // 1. Định nghĩa các interface cơ bản
        sb.append("export interface EnumItem {\n");
        sb.append("  readonly value: number;\n");
        sb.append("  readonly member: string;\n");
        sb.append("  readonly name: string;\n");
        sb.append("  readonly text: string;\n");
        sb.append("  readonly class: string;\n");
        sb.append("  readonly guidId: string;\n");
        sb.append("}\n\n");

        sb.append("export interface EnumCategory {\n");
        sb.append("  get(value: number): EnumItem | null;\n");
        sb.append("  list: EnumItem[];\n");
        sb.append("}\n\n");

        // 2. Định nghĩa interface Enums chính
        sb.append("export interface Enums {\n");
        for (EnumExportModel enumModel : enumModels) {
            sb.append("  readonly ").append(enumModel.getName()).append(": ")
              .append(enumModel.getCapitalizedName()).append("EnumCategory;\n");
        }
        sb.append("}\n\n");

        // 3. Định nghĩa chi tiết cho từng category
        for (EnumExportModel enumModel : enumModels) {
            sb.append("export interface ").append(enumModel.getCapitalizedName())
              .append("EnumCategory extends EnumCategory {\n");
            for (EnumMemberExportModel member : enumModel.getMembers()) {
                sb.append("  readonly ").append(member.getName()).append(": EnumItem;\n");
            }
            sb.append("}\n\n");

            // Union type cho value
            List<String> values = new ArrayList<>();
            for (EnumMemberExportModel member : enumModel.getMembers()) {
                String valueStr = String.valueOf(member.getValue());
                if (!values.contains(valueStr)) {
                    values.add(valueStr);
                }
            }
            if (!values.isEmpty()) {
                sb.append("export type ").append(enumModel.getCapitalizedName())
                  .append("Value = ").append(String.join(" | ", values)).append(";\n\n");
            }
        }

        // 4. Enum data
        sb.append("const enumsData = {\n");
        for (EnumExportModel enumModel : enumModels) {
            sb.append("  ").append(enumModel.getName()).append(": {\n");
            for (EnumMemberExportModel member : enumModel.getMembers()) {
                sb.append("    ").append(member.getName()).append(": {\n");
                sb.append("      value: ").append(member.getValue()).append(",\n");
                sb.append("      member: \"").append(member.getName()).append("\",\n");
                sb.append("      name: \"").append(member.getDataName() != null ? member.getDataName() : "").append("\",\n");
                sb.append("      text: \"").append(escapeJsString(member.getText())).append("\",\n");
                sb.append("      class: \"").append(member.getClassName() != null ? member.getClassName() : "").append("\",\n");
                sb.append("      guidId: \"").append(member.getGuidId() != null ? member.getGuidId() : "").append("\"\n");
                sb.append("    },\n");
            }
            sb.append("  },\n");
        }
        sb.append("} as const;\n\n");

        // 5. Proxy code
        sb.append(getTsProxyCode());

        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(sb.toString());
        }
    }

    /**
     * Chuyển đổi string sang camelCase
     */
    private String toCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        if (Character.isLowerCase(str.charAt(0))) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * Escape string cho JavaScript
     */
    private String escapeJsString(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    /**
     * TypeScript Proxy code
     */
    private String getTsProxyCode() {
        return """
            interface IEnumMember {
              value: number;
              member: string;
              name: string;
              text: string;
              class: string;
              guidId: string;
            }

            type IEnumCategory = Record<string, IEnumMember>;

            type IProxiedEnumCategory<T extends IEnumCategory> = T & {
              get: (value: number) => IEnumMember | null;
              list: IEnumMember[];
            };

            type IEnums = {
              [P in keyof typeof enumsData]: IProxiedEnumCategory<typeof enumsData[P]>;
            };

            const createCategoryProxy = <T extends IEnumCategory>(category: T): IProxiedEnumCategory<T> => {
              return new Proxy(category, {
                get(target, prop) {
                  if (prop === 'get') {
                    return (value: number): IEnumMember | null => {
                      for (const key in target) {
                        if (target[key].value === value) {
                          return target[key];
                        }
                      }
                      return null;
                    };
                  }
                  if (prop === 'list') {
                    return Object.values(target);
                  }
                  return target[prop as keyof T];
                }
              }) as IProxiedEnumCategory<T>;
            };

            const enumData: IEnums = new Proxy(enumsData, {
              get(target, prop: string) {
                if (prop in target) {
                  const categoryKey = prop as keyof typeof enumsData;
                  return createCategoryProxy(target[categoryKey]);
                }
                return undefined;
              }
            }) as IEnums;

            export const getTextFromEnum = (list: { value: any; text: string }[], value: any) => {
              if (value === null || value === undefined) return "Chưa cập nhật";
              const item = list.find((item) => item.value === value);
              return item ? item.text : "Không xác định";
            };

            export const getEnumText = (enumObject: any, value: any) => {
              if (value === null || value === undefined) return 'Chưa cập nhật';
              const item = enumObject.get(value);
              return item?.text || 'Không xác định';
            };

            export default enumData;
            """;
    }
}

