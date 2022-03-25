package io.spring.creekconsumer;

import java.util.function.Consumer;

import io.spring.creekfunctions.LogCreekMeasurements;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;

@SpringBootApplication
public class CreekLogConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreekLogConsumerApplication.class, args);
	}

	@Bean
	public Consumer<Message<String>> logCreekMeasurements() {
		return new LogCreekMeasurements();
	}
}
