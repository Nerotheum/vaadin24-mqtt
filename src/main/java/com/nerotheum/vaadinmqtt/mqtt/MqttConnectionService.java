package com.nerotheum.vaadinmqtt.mqtt;

import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.nerotheum.vaadinmqtt.mqtt.events.MqttConnectionMessageReceiveEvent;
import com.nerotheum.vaadinmqtt.mqtt.events.MqttConnectionStatusChangeEvent;
import com.vaadin.flow.component.UI;

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
    private final ApplicationEventPublisher eventPublisher;
    private IMqttClient mqttClient;
    private MqttValueService mqttValueService;
    private UI ui;

    @Autowired
    public MqttConnectionService(ApplicationEventPublisher eventPublisher, MqttValueService mqttValueService) {
        this.eventPublisher = eventPublisher;
        this.mqttValueService = mqttValueService;
    }

    public void connect() {
        try {
            this.ui = UI.getCurrent();
            mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setCleanSession(true);

            mqttClient.connect(options);
            mqttClient.subscribe("#", (topic, message) -> {
                MqttValue mqttValue = new MqttValue(topic, new String(message.getPayload()));
                logger.info("Received message: " + mqttValue.toString());
                mqttValueService.add(mqttValue);
                publishEvent(new MqttConnectionMessageReceiveEvent(this, mqttValue));
            });
            
            logger.info("Connected to MQTT broker: " + brokerUrl);
        } catch(Exception ex) {
            logger.warning(ex.getMessage());
        }
        publishEvent(new MqttConnectionStatusChangeEvent(this, mqttClient.isConnected()));
    }

    @PreDestroy
    public void disconnect() {
        try {
            mqttClient.disconnect();
            logger.info("Manually closed the connection to MQTT broker");
            publishEvent(new MqttConnectionStatusChangeEvent(this, false));
        } catch (Exception ex) {
            logger.warning(ex.getMessage());
        }
    }

    public void publish(MqttValue mqttValue) {
        try {
            MqttMessage mqttMessage = new MqttMessage(mqttValue.getMessage().getBytes());
            mqttMessage.setQos(2);
            mqttClient.publish(mqttValue.getTopic(), mqttMessage);
        } catch (MqttException ex) {
            logger.warning("Error while publishing a MQTT message: " + ex.getMessage());
        }
    }

    public IMqttClient getMqttClient() {
        return mqttClient;
    }

    public void publishEvent(ApplicationEvent applicationEvent) {
        ui.access(() -> {
            eventPublisher.publishEvent(applicationEvent);
        });
    }
}
