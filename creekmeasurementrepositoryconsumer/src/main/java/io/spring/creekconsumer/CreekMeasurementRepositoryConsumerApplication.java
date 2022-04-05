package io.spring.creekconsumer;

import java.util.function.Consumer;

import io.spring.creekfunctions.CreekMeasurementRepository;
import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CreekMeasurementRepositoryConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreekMeasurementRepositoryConsumerApplication.class, args);
	}

	@Bean
	public Consumer<String> creekMeasurementRepository(DataSource dataSource) {
		return new CreekMeasurementRepository(dataSource);
	}
}
