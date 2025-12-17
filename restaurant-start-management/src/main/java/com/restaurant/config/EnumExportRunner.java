package com.restaurant.config;

import com.restaurant.util.EnumToJsExporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Runner để export enum khi ứng dụng khởi động
 */
@Component
@Slf4j
@Order(1) // Chạy sớm khi ứng dụng start
public class EnumExportRunner implements CommandLineRunner {

    @Autowired
    private EnumToJsExporter enumExporter;

    @Override
    public void run(String... args) throws Exception {
        log.info("Đang đồng bộ enum từ backend xuống frontend...");
        enumExporter.exportEnumsOnStartup();
    }
}

