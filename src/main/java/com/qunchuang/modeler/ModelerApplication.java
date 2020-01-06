package com.qunchuang.modeler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Create Time : 2020/01/06
 *
 * @author zzk
 */
@EnableTransactionManagement
@SpringBootApplication
public class ModelerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ModelerApplication.class);
    }
}
