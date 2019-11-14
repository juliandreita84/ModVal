package com.fiduprod;

import org.apache.camel.main.Main;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fiduprod.route.FileTransferRouteBuilder;

@SpringBootApplication// same as 
@Configuration 
@EnableAutoConfiguration 
@ComponentScan 
public class FileTransferService {

	public static void main(String[] args) throws Exception {
		Main main = new Main();
		main.addRouteBuilder(new FileTransferRouteBuilder());
		main.run();
//		SpringApplication.run(FileTransferService.class, args);
	}
}
