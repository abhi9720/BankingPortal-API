package com.webapp.bankingportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Entry point for the Banking Portal Spring Boot application.
 *
 * <p>Bootstraps the application with caching ({@link org.springframework.cache.annotation.EnableCaching})
 * and asynchronous method execution ({@link org.springframework.scheduling.annotation.EnableAsync})
 * support enabled.</p>
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
public class BankingportalApplication {

    /**
     * Application entry point.
     *
     * @param args command-line arguments passed to the Spring application context
     */
    public static void main(String[] args) {
        SpringApplication.run(BankingportalApplication.class, args);
    }

}
