package com.it.reggie_takeaway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan//加此注解可扫描servlet,filter
@EnableTransactionManagement
public class ReggieTakeawayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReggieTakeawayApplication.class, args);
		log.info("项目启动success");
	}

}
