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
import java.util.function.Function;

/**
 * Looks at each creek sites data for each payload and determines if the creek
 * is safe-ish for kayaking. It then prints this result to console.
 */
public class ReportCreekMeasurements implements Function<List<CreekMeasurement>, String> {
	@Override
	public String apply(List<CreekMeasurement> creekMeasurements) {
		
		StringBuffer buffer = new StringBuffer();
		
		CreekMeasurement controlMeasurement = null;
		CreekMeasurement previousMeasurement = null;
		for (CreekMeasurement measurement : creekMeasurements) {
			if (controlMeasurement == null) {
				controlMeasurement = measurement;
				continue;
			}
			if (!measurement.getSensorId().equals(controlMeasurement.getSensorId())) {
				buffer.append(getSymbol(controlMeasurement, previousMeasurement) + " " +
						previousMeasurement.getName().trim());
				controlMeasurement = measurement;
				buffer.append("\n");
			}
			previousMeasurement = measurement;
		}
		buffer.append(getSymbol(controlMeasurement, previousMeasurement) + " " +
				previousMeasurement.getName());
		return buffer.toString();

	}


	private String getSymbol(CreekMeasurement controlMeasurement, CreekMeasurement previousMeasurement) {
		double warnPercentage = ((previousMeasurement.getStreamHeight() - controlMeasurement.getStreamHeight() )
				/ previousMeasurement.getStreamHeight());
		String symbol = Character.toString('\u2705');
		if (Math.abs(warnPercentage) > .005) {
			symbol = Character.toString('\u274c');
		}
		return symbol;
	}
	
}
