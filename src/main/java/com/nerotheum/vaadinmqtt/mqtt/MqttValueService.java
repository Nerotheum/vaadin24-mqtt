package com.nerotheum.vaadinmqtt.mqtt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqttValueService {
    private final MqttValueRepository repository;

    public MqttValueService(@Autowired MqttValueRepository repository) {
        this.repository = repository;
    }

    public List<MqttValue> findAll() {
        return repository.findAll();
    }

    public void add(MqttValue entity) {
        repository.save(entity);
    }
}
