# Project Base for Vaadin, Spring Boot and MQTT
This project can be used as a starting point to create your own Vaadin application with Spring Boot and MQTT. It contains everything to get you started.

## Preview
<img width="960" alt="preview" src="https://github.com/user-attachments/assets/34f84d7a-d9fa-4e77-bafd-38b257658341">

## Features
- [X] Connect and listen to MQTT broker
- [X] Configurable auto mqtt connection start and reconnect
- [X] Store received MQTT messages within specified database
- [X] Display MQTT connection status
- [X] Display MQTT messages within a grid
- [X] Update mqtt connection status and messages using Vaadin built-in websockets in real time
- [X] Add button to re-connect to MQTT broker
- [X] Add button to refresh MQTT messages grid
- [X] Send messages to MQTT broker
- [X] Periodically check whether the MQTT connection is alive
- [X] Enable/disable components based on the MQTT connection status

## Running the Application
Before you continue, make sure to configure your application.properties.
There are two ways to run the application :  using `mvn spring-boot:run` or by running the `Application` class directly from your IDE.
