package com.telecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

// Điểm khởi động; Spring tìm các component trong com.telecom và package con.
@SpringBootApplication
@ConfigurationPropertiesScan
public class TelecomManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelecomManagementApplication.class, args);
    }
}
