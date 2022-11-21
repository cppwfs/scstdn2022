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

import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Stores CreekMeasurements to a relational repository for table creek_measurement
 */
public class CreekMeasurementRepository implements Consumer<List<CreekMeasurement>> {

	private static final Log logger = LogFactory.getLog(CreekMeasurementRepository.class);

	private DataSource dataSource;

	public CreekMeasurementRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void accept(List<CreekMeasurement> creekMeasurements) {

			for(CreekMeasurement creekMeasurement : creekMeasurements) {
				storeCreekMeasurement(creekMeasurement);
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
