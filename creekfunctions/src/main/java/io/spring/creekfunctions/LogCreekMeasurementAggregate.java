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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.messaging.Message;

public class LogCreekMeasurementAggregate implements Consumer<Message<String>> {

	ObjectMapper objectMapper = new ObjectMapper();
	private static final Log logger = LogFactory.getLog(LogCreekMeasurementAggregate.class);

	public LogCreekMeasurementAggregate() {
		System.out.println("***** Constructor *****");

	}

	@Override
	public void accept(Message<String> stringMessage) {
		List<CreekMeasurement> creekMeasurements = null;
		System.out.println("***** HERE *****");
		try {
			creekMeasurements = objectMapper.readValue(stringMessage.getPayload(), List.class);
		}
		catch (JsonProcessingException jpe) {
			throw new IllegalStateException(jpe);
		}
			CreekMeasurement controlMeasurement = null;
			CreekMeasurement previousMeasurement = null;
			for (CreekMeasurement measurement : creekMeasurements) {
				if (controlMeasurement == null) {
					controlMeasurement = measurement;
					continue;
				}
				if (!measurement.getSensorId().equals(controlMeasurement.getSensorId())) {
					logger.info(previousMeasurement.getSensorId() + " "
							+ getSymbol(controlMeasurement, previousMeasurement));

					controlMeasurement = measurement;
				}
				previousMeasurement = measurement;
			}
			logger.info(previousMeasurement.getSensorId() + " "
					+ getSymbol(controlMeasurement, previousMeasurement));
		}

	private String getSymbol(CreekMeasurement controlMeasurement, CreekMeasurement previousMeasurement) {
		double warnPercentage = ((controlMeasurement.getStreamHeight() - previousMeasurement.getStreamHeight())
				/ previousMeasurement.getStreamHeight());
		String symbol = Character.toString('\u2705');
		if (warnPercentage >.05) {
			symbol = Character.toString('\u274c');
		}
		return symbol;
	}
	}
