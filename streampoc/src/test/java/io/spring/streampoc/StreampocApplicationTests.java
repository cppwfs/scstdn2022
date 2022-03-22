package io.spring.streampoc;

import org.junit.jupiter.api.Test;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

class StreampocApplicationTests {

	@Test
	void contextLoads() {
		ConfigurableApplicationContext ctx = new
				SpringApplicationBuilder(StreampocApplication.class).web(WebApplicationType.NONE).run();
	}

}
