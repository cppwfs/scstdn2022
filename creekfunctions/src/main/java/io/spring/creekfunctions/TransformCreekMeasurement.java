/*
 * Copyright 2017 the original author or authors.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;


/**
 * Accepts a tab delimited list of data from the USGS  Water Data Restful API and converts it ot a list of {@link CreekMeasurement}s.
 */
public class TransformCreekMeasurement implements Function<Message<String>, Message<String>> {
	public Message<String> apply(Message<String> rawData) {
		String[] results = rawData.getPayload().split(System.lineSeparator());
		List<String> arrayData = Arrays.stream(results)
				.filter(result -> result.startsWith("USGS"))
				.collect(Collectors.toList());
		List<CreekMeasurement> creekMeasurements = new ArrayList<>();
		arrayData.forEach(streamData -> {
			CreekMeasurement creekMeasurement = new CreekMeasurement(streamData);
			creekMeasurements.add(creekMeasurement);
		});
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		try {
			return new GenericMessage<>(objectMapper.writeValueAsString(creekMeasurements));
		}
		catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}
}
