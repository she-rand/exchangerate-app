package com.lili.springboot.webapp.exchange_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main SpringBoot Application Class
 * 
 * @SpringBootApplication is a convenience annotation that combines:
 * - @Configuration: Marks this as a configuration class
 * - @EnableAutoConfiguration: Tells Spring to auto-configure based on dependencies
 * - @ComponentScan: Tells Spring to scan for components in this package and subpackages
 */
@SpringBootApplication
@EnableCaching
public class ExchangeAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExchangeAppApplication.class, args);
	}

}
