package org.mizoguchi.misaki;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@EnableTransactionManagement
@MapperScan("org.mizoguchi.misaki.mapper")
@SpringBootApplication
public class MisakiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MisakiApplication.class, args);
    }

}
