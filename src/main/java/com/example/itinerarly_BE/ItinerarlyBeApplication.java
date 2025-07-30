package com.example.itinerarly_BE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ItinerarlyBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItinerarlyBeApplication.class, args);
	}

}
