package com.nerotheum.vaadinmqtt.mqtt.events;

import org.springframework.context.ApplicationEvent;

public class MqttConnectionStatusChangeEvent extends ApplicationEvent {
    private final boolean connected;

    public MqttConnectionStatusChangeEvent(Object source, boolean connected) {
        super(source);
        this.connected = connected;
    }

    public boolean isConnected() {
        return connected;
    }
}

