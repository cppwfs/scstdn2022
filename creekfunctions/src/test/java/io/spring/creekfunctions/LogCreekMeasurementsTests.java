/*
 * Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the \\"License\\");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an \\"AS IS\\" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.spring.creekfunctions;

import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(OutputCaptureExtension.class)
public class LogCreekMeasurementsTests {

	private Consumer<Message<String>> logCreekMeasurements;

	@BeforeEach
	public void prepLogMeasurements() {
			this.logCreekMeasurements = new LogCreekMeasurements();
	}


	@Test
	public void testLogCreekMeasurements(CapturedOutput output) {
		String message = "[{\"sensorId\":\"02312700\",\"dateCaptured\":1647945000.000000000,\"streamHeight\":39.93,\"status\":\"P\"}," +
				"{\"sensorId\":\"02312700\",\"dateCaptured\":1647945900.000000000,\"streamHeight\":39.93,\"status\":\"P\"}," +
				"{\"sensorId\":\"02312700\",\"dateCaptured\":1647953100.000000000,\"streamHeight\":39.93,\"status\":\"P\"}," +
				"{\"sensorId\":\"02335757\",\"dateCaptured\":1647945000.000000000,\"streamHeight\":3.35,\"status\":\"P\"}," +
				"{\"sensorId\":\"02335757\",\"dateCaptured\":1647951300.000000000,\"streamHeight\":3.33,\"status\":\"P\"}," +
				"{\"sensorId\":\"02336300\",\"dateCaptured\":1647945000.000000000,\"streamHeight\":2.77,\"status\":\"P\"}," +
				"{\"sensorId\":\"02336300\",\"dateCaptured\":1647950400.000000000,\"streamHeight\":5.77,\"status\":\"P\"}]";
		this.logCreekMeasurements.accept(new GenericMessage<>(message));
		String result = output.getAll();
		assertThat(result).contains("02312700 ✅");
		assertThat(result).contains("02335757 ✅");
		assertThat(result).contains("02336300 ❌");
	}

	@Test
	public void testLogCreekMeasurementSingle(CapturedOutput output) {
		String message = "[{\"sensorId\":\"02312700\",\"dateCaptured\":1647945000.000000000,\"streamHeight\":39.93,\"status\":\"P\"}," +
				"{\"sensorId\":\"02312700\",\"dateCaptured\":1647945900.000000000,\"streamHeight\":39.93,\"status\":\"P\"}," +
				"{\"sensorId\":\"02312700\",\"dateCaptured\":1647953100.000000000,\"streamHeight\":39.93,\"status\":\"P\"}," +
				"{\"sensorId\":\"02335757\",\"dateCaptured\":1647945000.000000000,\"streamHeight\":3.35,\"status\":\"P\"}," +
				"{\"sensorId\":\"02335757\",\"dateCaptured\":1647951300.000000000,\"streamHeight\":3.33,\"status\":\"P\"}," +
				"{\"sensorId\":\"02336300\",\"dateCaptured\":1647950400.000000000,\"streamHeight\":5.77,\"status\":\"P\"}]";
		this.logCreekMeasurements.accept(new GenericMessage<>(message));
		String result = output.getAll();
		assertThat(result).contains("02312700 ✅");
		assertThat(result).contains("02335757 ✅");
		assertThat(result).contains("02336300 ✅");
	}

	@Test
	public void testLogCreekMeasurementNoData(CapturedOutput output) {
		assertThatThrownBy(() -> {
			this.logCreekMeasurements.accept(new GenericMessage<>(""));
		}).isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("Unable to parse CreekMeasurements");

	}
}
