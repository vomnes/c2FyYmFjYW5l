package com.extractor.csv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CsvApplication {

	public static void main(String[] args) {
		SpringApplication.run(CsvApplication.class, args);
		System.out.printf("Running");
	}

}