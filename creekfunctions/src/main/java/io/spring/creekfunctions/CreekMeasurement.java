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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.springframework.util.Assert;


public class CreekMeasurement {

	private String creekMeasurementKey;

	private String sensorId;
	private ZonedDateTime dateCaptured;
	private Float streamHeight;
	private String status;
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");

	public CreekMeasurement() {

	}

	public CreekMeasurement(String creekData) {
		Assert.hasLength(creekData, "creekData must not be null or empty");
		String[] rawData = creekData.split("\t");
		this.sensorId = rawData[1];
		this.dateCaptured = ZonedDateTime.parse(rawData[2] + " " +rawData[3], formatter);
		this.streamHeight = Float.valueOf(rawData[4]);
		this.status = rawData[5];
		creekMeasurementKey = this.sensorId + this.dateCaptured;

	}
	public ZonedDateTime getDateCaptured() {
		return dateCaptured;
	}
	public void setDateCaptured(ZonedDateTime dateCaptured) {
		this.dateCaptured = dateCaptured;
	}
	public String getStatus() {
		return status;
	}
	public String getSensorId() {
		return sensorId;
	}
	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public Float getStreamHeight() {
		return streamHeight;
	}
	public void setStreamHeight(Float streamHeight) {
		this.streamHeight = streamHeight;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String toString() {
		return sensorId + " " + this.dateCaptured + " " + this.streamHeight + " " + this.status;
	}
	public String getCreekMeasurementKey() {
		return creekMeasurementKey;
	}

	public void setCreekMeasurementKey(String creekMeasurementKey) {
		this.creekMeasurementKey = creekMeasurementKey;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CreekMeasurement)) return false;
		CreekMeasurement that = (CreekMeasurement) o;
		return Objects.equals(creekMeasurementKey, that.creekMeasurementKey) &&
				Objects.equals(sensorId, that.sensorId) &&
				Objects.equals(streamHeight, that.streamHeight) &&
				Objects.equals(status, that.status);
	}
}
