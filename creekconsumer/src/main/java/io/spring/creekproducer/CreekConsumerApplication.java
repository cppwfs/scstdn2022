package io.spring.creekproducer;

import java.util.function.Consumer;

import io.spring.creekfunctions.LogCreekMeasurements;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;

@SpringBootApplication
public class CreekConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreekConsumerApplication.class, args);
	}

	@Bean
	public Consumer<Message<String>> logCreekMeasurements() {
		return new LogCreekMeasurements();
	}
}
