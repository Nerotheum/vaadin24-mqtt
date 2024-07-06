package com.nerotheum.vaadinmqtt.mqtt;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mqtt_messages")
public class MqttMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String topic;
    private String message;
    private LocalDateTime dateTime;

    public MqttMessage(String topic, String message) {
        this.topic = topic;
        this.message = message;
        this.dateTime = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }
    
    public String getTopic() {
        return topic;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
