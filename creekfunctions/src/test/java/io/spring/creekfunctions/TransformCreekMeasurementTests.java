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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TransformCreekMeasurementTests {

	TransformCreekMeasurement transformCreekMeasurement;
	ObjectMapper objectMapper;

	@BeforeEach
	public void setup() {
		this.transformCreekMeasurement = new TransformCreekMeasurement();
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());
	}

	@Test
	public void testBasic() throws Exception{
		List<CreekMeasurement> creekMeasurementList = getCreekMeasurementList("USGS\t02335757\t2022-03-09 10:45\tEST\t5.60\tP\n");
		assertThat(creekMeasurementList.size()).isEqualTo(1);
		CreekMeasurement creekMeasurement = new CreekMeasurement();
		creekMeasurement.setStatus("P");
		creekMeasurement.setCreekMeasurementKey("023357572022-03-09T10:45-05:00[America/New_York]");
		creekMeasurement.setSensorId("02335757");
		creekMeasurement.setStreamHeight(5.6f);
		assertThat(creekMeasurementList.get(0)).isEqualTo(creekMeasurement);
	}

	@Test
	void testList() throws Exception{
		String testData = """
				USGS	02335757	2022-03-23 04:45	EDT	3.33	P
				USGS	02335757	2022-03-23 05:00	EDT	3.36	P
				USGS	02335757	2022-03-23 05:15	EDT	3.38	P
				USGS	02335757	2022-03-23 05:30	EDT	3.40	P
				USGS	02335757	2022-03-23 05:45	EDT	3.42	P
				USGS	02335757	2022-03-23 06:00	EDT	3.43	P
				USGS	02335757	2022-03-23 06:15	EDT	3.45	P
				USGS	02335757	2022-03-23 06:30	EDT	3.46	P
				USGS	02335757	2022-03-23 06:45	EDT	3.52	P
				USGS	02335757	2022-03-23 07:00	EDT	3.56	P
				USGS	02335757	2022-03-23 07:15	EDT	3.63	P
				USGS	02335757	2022-03-23 07:30	EDT	3.72	P
				USGS	02335757	2022-03-23 07:45	EDT	3.94	P
				USGS	02335757	2022-03-23 08:00	EDT	4.03	P
				USGS	02335757	2022-03-23 08:15	EDT	4.00	P
				USGS	02335757	2022-03-23 08:30	EDT	4.04	P
				USGS	02335757	2022-03-23 08:45	EDT	4.12	P
				USGS	02335757	2022-03-23 09:00	EDT	4.20	P
				USGS	02335757	2022-03-23 09:15	EDT	4.27	P
				USGS	02335757	2022-03-23 09:30	EDT	4.28	P
				USGS	02335757	2022-03-23 09:45	EDT	4.33	P
				USGS	02335757	2022-03-23 10:00	EDT	4.36	P
				USGS	02335757	2022-03-23 10:15	EDT	4.41	P""";
		List<CreekMeasurement> creekMeasurementList = getCreekMeasurementList(testData);
		assertThat(creekMeasurementList.size()).isEqualTo(23);
	}

	@Test
	public void testEmpty() throws Exception{
		List<CreekMeasurement> creekMeasurementList = getCreekMeasurementList("");
		assertThat(creekMeasurementList.size()).isEqualTo(0);
	}

	private List<CreekMeasurement> getCreekMeasurementList(String message) throws Exception{
		return objectMapper.readValue(
				transformCreekMeasurement.apply(message),
				new TypeReference<List<CreekMeasurement>>() {});
	}
}
