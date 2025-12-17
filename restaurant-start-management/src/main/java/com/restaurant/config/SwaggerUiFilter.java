package com.restaurant.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Filter để inject custom JavaScript và CSS vào Swagger UI HTML
 */
@Slf4j
@Component
@Order(1)
public class SwaggerUiFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        log.info("SwaggerUiFilter - Request URI: {}", requestURI);
        
        // Chỉ xử lý Swagger UI HTML (kiểm tra nhiều pattern)
        boolean isSwaggerUi = requestURI != null && (
                requestURI.contains("/swagger-ui") || 
                requestURI.contains("/swagger-ui.html") ||
                requestURI.equals("/swagger-ui/index.html") ||
                requestURI.endsWith("/swagger-ui/index.html") ||
                requestURI.equals("/swagger-ui/") ||
                requestURI.matches(".*/swagger-ui/.*\\.html?")
        );
        
        if (isSwaggerUi) {
            log.info("SwaggerUiFilter - Processing Swagger UI request: {}", requestURI);
            
            CharResponseWrapper responseWrapper = new CharResponseWrapper(response);
            
            filterChain.doFilter(request, responseWrapper);
            
            String content = responseWrapper.toString();
            log.info("SwaggerUiFilter - Response content length: {}", content != null ? content.length() : 0);
            log.debug("SwaggerUiFilter - Response content preview: {}", 
                    content != null && content.length() > 200 ? content.substring(0, 200) : content);
            
            if (content != null && content.toLowerCase().contains("<!doctype html>")) {
                log.info("SwaggerUiFilter - Detected HTML response, injecting custom CSS and JS");
                
                // Inject custom CSS vào <head>
                if (content.contains("</head>")) {
                    String customCss = "<link rel=\"stylesheet\" type=\"text/css\" href=\"/api/custom.css\" />";
                    content = content.replace("</head>", "    " + customCss + "\n  </head>");
                    log.info("SwaggerUiFilter - Injected CSS into </head>");
                }
                
                // Inject custom JS vào cuối </body> (trước các script khác)
                if (content.contains("</body>")) {
                    String customJs = "<script src=\"/api/custom.js\"></script>";
                    content = content.replace("</body>", "    " + customJs + "\n  </body>");
                    log.info("SwaggerUiFilter - Injected JS into </body>");
                }
                
                // Set content length và type
                byte[] contentBytes = content.getBytes("UTF-8");
                response.setContentLength(contentBytes.length);
                response.setContentType("text/html;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                
                // Ghi lại response
                response.getOutputStream().write(contentBytes);
                response.getOutputStream().flush();
                
                log.info("SwaggerUiFilter - Successfully injected custom CSS and JS");
            } else {
                log.warn("SwaggerUiFilter - Response is not HTML or does not contain expected tags");
                // Nếu không phải HTML, trả về response gốc
                byte[] originalBytes = responseWrapper.getOriginalAsBytes();
                if (originalBytes != null && originalBytes.length > 0) {
                    response.getOutputStream().write(originalBytes);
                }
            }
        } else {
            log.debug("SwaggerUiFilter - Skipping non-Swagger UI request: {}", requestURI);
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Wrapper để capture response content
     */
    private static class CharResponseWrapper extends HttpServletResponseWrapper {
        private CharArrayWriter charWriter;
        private java.io.ByteArrayOutputStream byteStream;
        private PrintWriter writer;
        private ServletOutputStream outputStream;
        private boolean usingWriter = false;
        private boolean usingStream = false;

        public CharResponseWrapper(HttpServletResponse response) {
            super(response);
            this.charWriter = new CharArrayWriter();
            this.byteStream = new java.io.ByteArrayOutputStream();
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            if (usingStream) {
                throw new IllegalStateException("getOutputStream() has already been called");
            }
            usingWriter = true;
            if (writer == null) {
                writer = new PrintWriter(charWriter);
            }
            return writer;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (usingWriter) {
                throw new IllegalStateException("getWriter() has already been called");
            }
            usingStream = true;
            if (outputStream == null) {
                outputStream = new ServletOutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        byteStream.write(b);
                    }
                    
                    @Override
                    public void write(byte[] b, int off, int len) throws IOException {
                        byteStream.write(b, off, len);
                    }
                    
                    @Override
                    public boolean isReady() {
                        return true;
                    }
                    
                    @Override
                    public void setWriteListener(WriteListener listener) {
                        // Not needed for synchronous writes
                    }
                };
            }
            return outputStream;
        }

        @Override
        public String toString() {
            if (usingWriter) {
                if (writer != null) {
                    writer.flush();
                }
                return charWriter.toString();
            } else if (usingStream) {
                try {
                    if (outputStream != null) {
                        outputStream.flush();
                    }
                    return new String(byteStream.toByteArray(), java.nio.charset.StandardCharsets.UTF_8);
                } catch (Exception e) {
                    return "";
                }
            }
            return "";
        }

        public byte[] getOriginalAsBytes() {
            if (usingWriter) {
                if (writer != null) {
                    writer.flush();
                }
                return charWriter.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
            } else if (usingStream) {
                try {
                    if (outputStream != null) {
                        outputStream.flush();
                    }
                    return byteStream.toByteArray();
                } catch (Exception e) {
                    return new byte[0];
                }
            }
            return new byte[0];
        }
    }
}

