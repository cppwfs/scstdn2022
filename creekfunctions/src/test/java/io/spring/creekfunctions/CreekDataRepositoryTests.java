/*
 * Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.spring.creekfunctions;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import org.h2.tools.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.messaging.support.GenericMessage;

import static org.assertj.core.api.Assertions.assertThat;

public class CreekDataRepositoryTests {

	private final static String DATASOURCE_URL;

	private final static String DATASOURCE_USER_NAME = "SA";

	private final static String DATASOURCE_USER_PASSWORD = "";

	private final static String DATASOURCE_DRIVER_CLASS_NAME = "org.h2.Driver";

	private static int randomPort;

	static {
		randomPort = 9988;
		DATASOURCE_URL = "jdbc:h2:tcp://localhost:" + randomPort + "/mem:dataflow;DB_CLOSE_DELAY=-1;"
				+ "DB_CLOSE_ON_EXIT=FALSE";
	}

	private CreekDataRepository creekDataRepository;

	private DataSource dataSource;

	private Server server;

	JdbcTemplate template;

	@BeforeEach
	public void setup() {

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(DATASOURCE_DRIVER_CLASS_NAME);
		dataSource.setUrl(DATASOURCE_URL);
		dataSource.setUsername(DATASOURCE_USER_NAME);
		dataSource.setPassword(DATASOURCE_USER_PASSWORD);
		this.dataSource = dataSource;
		try {
			this.server = Server
					.createTcpServer("-tcp", "-ifNotExists", "-tcpAllowOthers", "-tcpPort", String
							.valueOf(randomPort))
					.start();
			template = new JdbcTemplate(this.dataSource);
			template.execute("""
								DROP TABLE IF EXISTS creek_measurement;
								 CREATE TABLE creek_measurement
										(
												creek_measurement_key varchar(255) PRIMARY KEY NOT NULL,
												date_captured timestamp,
												sensor_id varchar(255),
												status varchar(255),
												stream_height real
										);
				"""
			);
			this.creekDataRepository = new CreekDataRepository(this.dataSource);

		}
		catch (SQLException e) {
			throw new IllegalStateException(e);
		}

	}

	@AfterEach
	public void tearDown() {
		this.server.stop();
	}

	@Test
	public void testBasic() {
		String message = "[{\"sensorId\":\"02312700\",\"dateCaptured\":1647945000.000000000,\"streamHeight\":39.93,\"status\":\"P\"}," +
				"{\"sensorId\":\"02312700\",\"dateCaptured\":1647945900.000000000,\"streamHeight\":39.93,\"status\":\"P\"}," +
				"{\"sensorId\":\"02312700\",\"dateCaptured\":1647953100.000000000,\"streamHeight\":39.93,\"status\":\"P\"}," +
				"{\"sensorId\":\"02335757\",\"dateCaptured\":1647945000.000000000,\"streamHeight\":3.35,\"status\":\"P\"}," +
				"{\"sensorId\":\"02335757\",\"dateCaptured\":1647951300.000000000,\"streamHeight\":3.33,\"status\":\"P\"}," +
				"{\"sensorId\":\"02336300\",\"dateCaptured\":1647950400.000000000,\"streamHeight\":5.77,\"status\":\"P\"}]";
		this.creekDataRepository.accept(new GenericMessage<>(message));
		List<Map<String, Object>> result = this.template.queryForList("select * from creek_measurement");
		assertThat(result.size()).isEqualTo(6);
	}
}
