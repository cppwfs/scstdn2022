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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Looks at each creek sites data for each payload and determines if the creek
 * is safe-ish for kayaking. It then prints this result to console.
 */
public class LogCreekMeasurements implements Consumer<String> {

	private ObjectMapper objectMapper;

	private Map<String, String> nameCodeMap;

	public LogCreekMeasurements() {
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());
		nameCodeMap = new HashMap<>();
		nameCodeMap.put("02335757", "Big Creek Roswell");
		nameCodeMap.put("02336300", "Peachtree Creek Atlanta");
		nameCodeMap.put("02312700", "Outlet River Lake Panasoffkee");
	}

	@Override
	public void accept(String stringMessage) {
		List<CreekMeasurement> creekMeasurements = null;
		try {
			creekMeasurements = objectMapper.readValue(stringMessage,
					new TypeReference<List<CreekMeasurement>>() {
					});
		}
		catch (JsonProcessingException jpe) {
			throw new IllegalStateException("Unable to parse CreekMeasurements", jpe);
		}
		CreekMeasurement controlMeasurement = null;
		CreekMeasurement previousMeasurement = null;
		for (CreekMeasurement measurement : creekMeasurements) {
			if (controlMeasurement == null) {
				controlMeasurement = measurement;
				continue;
			}
			if (!measurement.getSensorId().equals(controlMeasurement.getSensorId())) {
				System.out.println(getSymbol(controlMeasurement, previousMeasurement) + " " +
						nameCodeMap.get(previousMeasurement.getSensorId()) );
				controlMeasurement = measurement;
			}
			previousMeasurement = measurement;
		}
		System.out.println(getSymbol(controlMeasurement, previousMeasurement) + " " +
				nameCodeMap.get(previousMeasurement.getSensorId()));

	}

	private String getSymbol(CreekMeasurement controlMeasurement, CreekMeasurement previousMeasurement) {
		double warnPercentage = ((previousMeasurement.getStreamHeight() - controlMeasurement.getStreamHeight() )
				/ previousMeasurement.getStreamHeight());
		String symbol = Character.toString('\u2705');
		if (Math.abs(warnPercentage) > .2) {
			symbol = Character.toString('\u274c');
		}
		return symbol;
	}
}
