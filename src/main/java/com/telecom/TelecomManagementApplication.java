package com.telecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Điểm khởi động; Spring tìm các component trong com.telecom và package con.
@SpringBootApplication
public class TelecomManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelecomManagementApplication.class, args);
    }
}
