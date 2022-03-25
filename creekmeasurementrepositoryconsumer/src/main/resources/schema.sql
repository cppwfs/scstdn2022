CREATE TABLE creek_measurement
(
    creek_measurement_key varchar(255) PRIMARY KEY NOT NULL,
    date_captured timestamp,
    sensor_id varchar(255),
    status varchar(255),
    stream_height real
)
;
