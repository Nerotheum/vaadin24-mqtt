package com.nerotheum.vaadinmqtt.mqtt;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.logging.Logger;

@Service
public class MqttConnectionService {
    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Value("${mqtt.client.id}")
    private String clientId;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private IMqttClient mqttClient;
    private MqttMessageService mqttMessageService;

    @Autowired
    public MqttConnectionService(MqttMessageService mqttMessageService) {
        this.mqttMessageService = mqttMessageService;
    }

    @PostConstruct
    public void connect() throws MqttException {
        mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setCleanSession(true);

        mqttClient.connect(options);
        logger.info("Connected to MQTT broker: " + brokerUrl);
        mqttClient.subscribe("#", (topic, message) -> {
            MqttMessage mqttMessage = new MqttMessage(topic, new String(message.getPayload()));
            logger.info("Received message: " + mqttMessage.toString());
            mqttMessageService.add(mqttMessage);
        });
    }

    @PreDestroy
    public void disconnect() throws MqttException {
        mqttClient.disconnect();
        logger.warning("Connection lost to MQTT broker. Attempting to reconnect!");
        this.connect();
    }

    public IMqttClient getMqttClient() {
        return mqttClient;
    }
}
