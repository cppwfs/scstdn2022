package io.spring.creekconsumer;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import io.spring.creekfunctions.CreekMeasurement;
import io.spring.creekfunctions.ReportCreekMeasurements;

@SpringBootApplication
@EnableConfigurationProperties(ConsumerProperties.class)
public class ConsumerApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(ConsumerApplication.class).web(WebApplicationType.NONE).run(args);
	}
	
	@Bean	
	public Function<List<CreekMeasurement>, String> generateReport() {
		return new ReportCreekMeasurements();
	}

	@Bean
	@ConditionalOnProperty(value="spring.cloud.function.definition", havingValue = "generateReport|log")
	public Consumer<String> log() {
		System.out.println("==> Creating LOGGER");
		return v -> {
			System.out.println("Request log string: " +v);
		};
	}
	
	@Bean
	@ConditionalOnProperty(value="spring.cloud.function.definition", havingValue = "generateReport|sms")
	public Consumer<String> sms(ConsumerProperties consumerProperties) {
		System.out.println("==> Creating SMS SENDER");
		return v -> {
			System.out.println("Request sms string: " +v);
			try {	
				StringBuilder builder = new StringBuilder("https://textbelt.com/text");
				builder.append("?phone=");
				builder.append(URLEncoder.encode(consumerProperties.getPhoneNumber(), StandardCharsets.UTF_8.toString()));
				builder.append("&message=");
				builder.append(URLEncoder.encode("Here is your kayaking report: \n" + v, StandardCharsets.UTF_8.toString()));
				builder.append("&key="); 
				builder.append(URLEncoder.encode(consumerProperties.getSmsKey(), StandardCharsets.UTF_8.toString()));
				System.out.println("Request string: " + builder);
				RestTemplate rest = new RestTemplate();
				rest.getForObject(builder.toString(),String.class);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		};
	}
}
