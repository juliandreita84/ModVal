package com.techprimers.messaging.standaloneactivemqexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication// same as 
@Configuration 
@EnableAutoConfiguration 
@ComponentScan 
public class SecurityService {

	public static void main(String[] args) {
		SpringApplication.run(SecurityService.class, args);
	}
}
