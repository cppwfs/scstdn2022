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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class CreekMeasurementTests {
	@Test
	void basicTest() {
		CreekMeasurement creekMeasurement = new CreekMeasurement(
				"USGS\t02335757\t2022-03-09 10:45\tEST\t5.60\tP");
		assertThat(creekMeasurement.getCreekMeasurementKey()).isEqualTo("023357572022-03-09T10:45-05:00[America/New_York]");
		assertThat(creekMeasurement.getSensorId()).isEqualTo("02335757");
		assertThat(creekMeasurement.getDateCaptured()).isEqualTo("2022-03-09T10:45-05:00[America/New_York]");
		assertThat(creekMeasurement.getStatus()).isEqualTo("P");
		assertThat(creekMeasurement.getStreamHeight()).isEqualTo(5.6f);
	}

	@Test
	void emptyTest() {
		assertThatThrownBy(() -> {
			new CreekMeasurement("");
		}).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("creekData must not be null or empty");

	}
}
