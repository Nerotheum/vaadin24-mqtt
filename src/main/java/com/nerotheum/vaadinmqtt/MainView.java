package com.nerotheum.vaadinmqtt;

import java.util.List;

import com.nerotheum.vaadinmqtt.mqtt.MqttMessage;
import com.nerotheum.vaadinmqtt.mqtt.MqttMessageService;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import jakarta.annotation.PostConstruct;

@Route
public class MainView extends VerticalLayout {

    private final MqttMessageService mqttMessageService;
    private final Grid<MqttMessage> mqttMessageGrid;

    public MainView(@Autowired MqttMessageService service) {
        this.mqttMessageService = service;
        this.mqttMessageGrid = new Grid<>(MqttMessage.class);
        add(mqttMessageGrid);
    }

    @PostConstruct
    private void init() {
        List<MqttMessage> messages = mqttMessageService.findAll();
        mqttMessageGrid.setItems(messages);
        mqttMessageGrid.setColumns("id", "topic", "message", "dateTime");
    }
}
