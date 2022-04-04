package io.spring.creekproducer;

import java.time.LocalDateTime;
import java.time.ZoneId;
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

	private static final String SAMPLE_DATA = """
				USGS	02335757	2022-03-23 04:45	EDT	3.33	P
				USGS	02335757	2022-03-23 05:00	EDT	3.36	P
				USGS	02335757	2022-03-23 05:15	EDT	3.38	P
				USGS	02335757	2022-03-23 05:30	EDT	3.40	P
				USGS	02335757	2022-03-23 05:45	EDT	3.42	P
				USGS	02335757	2022-03-23 06:00	EDT	3.43	P
				USGS	02335757	2022-03-23 06:15	EDT	3.45	P
				USGS	02335757	2022-03-23 06:30	EDT	3.46	P
				USGS	02335757	2022-03-23 06:45	EDT	3.52	P
				USGS	02335757	2022-03-23 07:00	EDT	3.56	P
				USGS	02335757	2022-03-23 07:15	EDT	3.63	P
				USGS	02335757	2022-03-23 07:30	EDT	3.72	P
				USGS	02335757	2022-03-23 07:45	EDT	3.94	P
				USGS	02335757	2022-03-23 08:00	EDT	4.03	P
				USGS	02335757	2022-03-23 08:15	EDT	4.00	P
				USGS	02335757	2022-03-23 08:30	EDT	4.04	P
				USGS	02335757	2022-03-23 08:45	EDT	4.12	P
				USGS	02335757	2022-03-23 09:00	EDT	4.20	P
				USGS	02335757	2022-03-23 09:15	EDT	4.27	P
				USGS	02335757	2022-03-23 09:30	EDT	4.28	P
				USGS	02335757	2022-03-23 09:45	EDT	4.33	P
				USGS	02335757	2022-03-23 10:00	EDT	4.36	P
				USGS	02335757	2022-03-23 10:15	EDT	4.41	P""";

	public static void main(String[] args) {
		SpringApplication.run(CreekproducerApplication.class, args);
	}

	@Bean
	public Supplier<Message<String>> creekDataSupplier() {
		return () -> getCreekData();
	}

	protected Message<String> getCreekData() {
		ZoneId zoneId = ZoneId.of("America/New_York");
		LocalDateTime endTime = LocalDateTime.now(zoneId);
		LocalDateTime startTime = endTime.minusHours(4);

		Mono<String> creekMono = WebClient.create()
				.get()
				.uri("https://waterservices.usgs.gov/nwis/iv/?sites=" + "02336300,02335757,02312700" +
						"&parameterCd=00065&startDT=" + startTime + "-05:00&endDT=" +
						endTime + "-05:00&siteStatus=all&format=rdb")
				.retrieve().bodyToMono(String.class);

		String result = SAMPLE_DATA;
		try {
			creekMono.block();
		} catch(Exception exception) {
			System.out.println("Failed to retrieve data from USGS using sample date");;
		}
		return new GenericMessage<>(result);
	}

	@Bean
	public Function<Message<String>, Message<String>> transformCreekMeasurement() {
		return new TransformCreekMeasurement();
	}

}
