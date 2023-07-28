package com.webapp.bankingportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // Add this annotation to enable caching support
@EnableAsync
public class BankingportalApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingportalApplication.class, args);
	}

}
