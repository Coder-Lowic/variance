package com.lowic.data.analysis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Lowic
 */
@SpringBootApplication
@MapperScan("com.lowic.data.analysis.mapper")
public class DataAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataAnalysisApplication.class, args);
    }

}
