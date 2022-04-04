package io.spring.streampoc;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@EnableConfigurationProperties(CreekProperties.class)
public class StreampocApplication {

	private static final Log logger = LogFactory
			.getLog(StreampocApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(StreampocApplication.class, args);
	}

	@Autowired
	private CreekRepository repository;

	@Autowired
	private ConfigurableApplicationContext context;

	@Bean
	public ApplicationRunner applicationRunner(CreekProperties creekProperties, Function<String, List<CreekMeasurement>> transformCreekMeasurement, Consumer<List<CreekMeasurement>> produceReport) {
		return new ApplicationRunner() {

			@Override
			public void run(ApplicationArguments args) throws Exception {
				ZoneId zoneId = ZoneId.of("America/New_York");
				LocalDateTime endTime = LocalDateTime.now(zoneId);
				LocalDateTime startTime = endTime.minusHours(5);
				ZonedDateTime zonedDateTime = ZonedDateTime.of(startTime, zoneId);
				ZonedDateTime endDateTime = ZonedDateTime.of(endTime, zoneId);
				Mono<String> creekMono = WebClient.create()
						.get()
						.uri("https://waterservices.usgs.gov/nwis/iv/?sites=" + creekProperties.getSites() +
								"&parameterCd=00065&startDT=" + zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) + "&endDT=" +
								endDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) + "&siteStatus=all&format=rdb")
						.retrieve().bodyToMono(String.class);

				List<CreekMeasurement> creekMeasurements = transformCreekMeasurement.apply(creekMono.block());
				produceReport.accept(creekMeasurements);
				context.close();
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
					repository.save(creekMeasurement);
				});
				repository.findAll().forEach(cm -> System.out.println(cm));
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
