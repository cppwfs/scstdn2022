package io.spring.creekconsumer;

import java.util.List;
import java.util.function.Function;

import io.spring.creekfunctions.CreekMeasurement;
import io.spring.creekfunctions.TransformCreekMeasurement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CreekTransformerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreekTransformerApplication.class, args);
	}

	@Bean
	public Function<String, List<CreekMeasurement>> transformCreekMeasurement() {
		return new TransformCreekMeasurement();
	}
}
