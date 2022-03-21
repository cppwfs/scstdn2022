package io.spring.streampoc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableConfigurationProperties(CreekProperties.class)
public class StreampocApplication {

	private static final Log logger = LogFactory
			.getLog(StreampocApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(StreampocApplication.class, args);
	}

	@Bean
	public ApplicationRunner applicationRunner(CreekProperties creekProperties, Function<String, List<CreekMeasurement>> transformCreekMeasurement, Consumer<List<CreekMeasurement>> produceReport) {
		return new ApplicationRunner() {

			@Override
			public void run(ApplicationArguments args) throws Exception {
				LocalDateTime endTime = LocalDateTime.now();
				LocalDateTime startTime = endTime.minusHours(4);

				Mono<String> creekMono = WebClient.create()
						.get()
						.uri("https://waterservices.usgs.gov/nwis/iv/?sites=" + creekProperties.getSites() +
								"&parameterCd=00065&startDT=" + startTime + "-05:00&endDT=" +
								endTime + "-05:00&siteStatus=all&format=rdb")
						.retrieve().bodyToMono(String.class);

				List<CreekMeasurement> creekMeasurements = transformCreekMeasurement.apply(creekMono.block());
				produceReport.accept(creekMeasurements);

			}
		};
	}

	@Bean
	protected Function<String, List<CreekMeasurement>> transformCreekMeasurement() {
		return new Function<String,List<CreekMeasurement>>() {

			@Override
			public List<CreekMeasurement> apply(String rawData) {
				String[] results = rawData.split(System.lineSeparator());
				List<String> arrayData = Arrays.stream(results)
						.filter(result -> result.startsWith("USGS"))
						.collect(Collectors.toList());
				List<CreekMeasurement> creekMeasurements = new ArrayList<>();
				arrayData.forEach(streamData -> {
					CreekMeasurement creekMeasurement = new CreekMeasurement(streamData);
					creekMeasurements.add(creekMeasurement);
				});
				return creekMeasurements;
			}
			
		};
	}

	@Bean
	protected Consumer<List<CreekMeasurement>> produceReport(CreekProperties properties) {
		return new Consumer<List<CreekMeasurement>>() {

			@Override
			public void accept(List<CreekMeasurement> creekMeasurements) {
				CreekMeasurement controlMeasurement = null;
				CreekMeasurement previousMeasurement = null;
				for (CreekMeasurement measurement : creekMeasurements) {
					if (controlMeasurement == null) {
						controlMeasurement = measurement;
						continue;
					}
					if (!measurement.getSensorId().equals(controlMeasurement.getSensorId())) {
						logger.info(previousMeasurement.getSensorId() + " "
								+ getSymbol(controlMeasurement, previousMeasurement, properties));
		
						controlMeasurement = measurement;
					}
					previousMeasurement = measurement;
				}
				logger.info(previousMeasurement.getSensorId() + " "
						+ getSymbol(controlMeasurement, previousMeasurement, properties));				
			}
			
		};
	}

	private String getSymbol(CreekMeasurement controlMeasurement, CreekMeasurement previousMeasurement,
			CreekProperties properties) {
		double warnPercentage = ((controlMeasurement.getStreamHeight() - previousMeasurement.getStreamHeight())
				/ previousMeasurement.getStreamHeight());
		String symbol = Character.toString('\u2705');
		if (warnPercentage > properties.getWarningPercent()) {
			symbol = Character.toString('\u274c');
		}
		return symbol;
	}

}
