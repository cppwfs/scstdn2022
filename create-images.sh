#!/bin/zsh

cd creekconsumer
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=creek-consumer -DskipTests
cd ../creekproducer
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=creek-producer -DskipTests
cd ../creektransformer
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=creek-transformer -DskipTests
cd ../creekmeasurementrepositoryconsumer
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=creek-storage -DskipTests
# shellcheck disable=SC2103
cd ..
