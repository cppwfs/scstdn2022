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

import org.junit.jupiter.api.Test;

public class CreekMeasurementTests {
	@Test
	void basicTest() {
		CreekMeasurement creekMeasurement = new CreekMeasurement(
				"USGS\t02335757\t2022-03-09 10:45\tEST\t5.60\tP\n");
	}
}
