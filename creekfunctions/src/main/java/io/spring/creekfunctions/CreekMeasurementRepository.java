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

import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.Message;

/**
 * Stores CreekMeasurements to a relational repository for table creek_measurement
 */
public class CreekMeasurementRepository implements Consumer<Message<String>> {

	private static final Log logger = LogFactory.getLog(CreekMeasurementRepository.class);

	private ObjectMapper objectMapper;

	private DataSource dataSource;

	public CreekMeasurementRepository(DataSource dataSource) {
		this.dataSource = dataSource;
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());
	}

	@Override
	public void accept(Message<String> stringMessage) {
		List<CreekMeasurement> creekMeasurements = null;
		try {
			creekMeasurements = objectMapper.readValue(stringMessage.getPayload(), new TypeReference<List<CreekMeasurement>>() {});
			for(CreekMeasurement creekMeasurement : creekMeasurements) {
				storeCreekMeasurement(creekMeasurement);
			}
		}
		catch (JsonProcessingException jpe) {
			throw new IllegalStateException(jpe);
		}
	}

	private void storeCreekMeasurement(CreekMeasurement creekMeasurement) {
		JdbcTemplate template = new JdbcTemplate(this.dataSource);

		try {
			template.update("INSERT INTO creek_measurement (creek_measurement_key, date_captured, sensor_id, " +
							"status, stream_height) VALUES(?, ?, ?, ?, ?)",
					creekMeasurement.getSensorId()+creekMeasurement.getDateCaptured(),
					creekMeasurement.getDateCaptured(), creekMeasurement.getSensorId(),
					creekMeasurement.getStatus(), creekMeasurement.getStreamHeight());
		}
		catch (org.springframework.dao.DuplicateKeyException dke) {
			logger.info("Data already present in repo skipping this entry. ");
		}
	}
}
