package com.nerotheum.vaadinmqtt.mqtt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqttMessageService {
    private final MqttMessageRepository repository;

    public MqttMessageService(@Autowired MqttMessageRepository repository) {
        this.repository = repository;
    }

    public List<MqttMessage> findAll() {
        return repository.findAll();
    }

    public void add(MqttMessage entity) {
        repository.save(entity);
    }
}
