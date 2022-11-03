CREATE TABLE IF NOT EXISTS creek_measurement
(
    creek_measurement_key varchar(255) NOT NULL,
    date_captured timestamp,
    sensor_id varchar(255),
    status varchar(255),
    stream_height real
)
;
