package com.nerotheum.vaadinmqtt.mqtt.events;

import org.springframework.context.ApplicationEvent;

import com.nerotheum.vaadinmqtt.mqtt.MqttValue;

public class MqttConnectionMessageReceiveEvent extends ApplicationEvent {
    private final MqttValue mqttValue;

    public MqttConnectionMessageReceiveEvent(Object source, MqttValue mqttValue) {
        super(source);
        this.mqttValue = mqttValue;
    }

    public MqttValue getMqttValue() {
        return mqttValue;
    }
}
