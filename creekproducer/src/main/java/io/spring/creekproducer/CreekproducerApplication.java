package io.spring.creekproducer;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

import io.spring.creekfunctions.TransformCreekMeasurement;
import reactor.core.publisher.Mono;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class CreekproducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreekproducerApplication.class, args);
	}

	@Bean
	public Supplier<Message<String>> creekDataSupplier() {
		return () -> getCreekData();
	}

	protected Message<String> getCreekData() {
		LocalDateTime endTime = LocalDateTime.now();
		LocalDateTime startTime = endTime.minusHours(4);

		Mono<String> creekMono = WebClient.create()
				.get()
				.uri("https://waterservices.usgs.gov/nwis/iv/?sites=" + "02336300,02335757,02312700" +
						"&parameterCd=00065&startDT=" + startTime + "-05:00&endDT=" +
						endTime + "-05:00&siteStatus=all&format=rdb")
				.retrieve().bodyToMono(String.class);
		return new GenericMessage<>(creekMono.block());
	}

	@Bean
	public Function<Message<String>, Message<String>> transformCreekMeasurement() {
		return new TransformCreekMeasurement();
	}
}
