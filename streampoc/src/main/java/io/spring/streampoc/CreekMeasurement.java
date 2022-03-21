package io.spring.streampoc;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.util.Assert;

public class CreekMeasurement {

    private String sensorId;
    private ZonedDateTime dateCaptured;
    private Float streamHeight;
    private String status;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
    
    public CreekMeasurement(String stringData) {
        String[] rawData = stringData.split("\t");
        Assert.isTrue(rawData.length == 6, "There must be 6 fields in data.  There were " + rawData.length);
        this.sensorId = rawData[1];
        this.dateCaptured = ZonedDateTime.parse(rawData[2] + " " +rawData[3], formatter);
        this.streamHeight = Float.valueOf(rawData[4]);
        this.status = rawData[5];
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

}
