package com.hospital.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HospitalApplication {

	public static void main(String[] args) {
		System.out.println("Starting Hospital Application...");
		SpringApplication.run(HospitalApplication.class, args);
	}

}
