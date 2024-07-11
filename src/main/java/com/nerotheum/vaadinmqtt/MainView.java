package com.nerotheum.vaadinmqtt;

import java.util.List;

import com.nerotheum.vaadinmqtt.mqtt.MqttConnectionService;
import com.nerotheum.vaadinmqtt.mqtt.MqttMessage;
import com.nerotheum.vaadinmqtt.mqtt.MqttMessageService;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import jakarta.annotation.PostConstruct;

@Route
public class MainView extends VerticalLayout {

    private final MqttMessageService mqttMessageService;
    private final Grid<MqttMessage> mqttMessageGrid;
    private final MqttConnectionService mqttConnectionService;

    public MainView(@Autowired MqttMessageService mqttMessageService, @Autowired MqttConnectionService mqttConnectionService) {
        this.mqttMessageService = mqttMessageService;
        this.mqttMessageGrid = new Grid<>(MqttMessage.class);
        this.mqttConnectionService = mqttConnectionService;
    }

    @PostConstruct
    private void init() {
        String mqttConnection = mqttConnectionService.getMqttClient().isConnected() ? "success" : "error";
        Span mqttStatus = new Span("MQTT connection status: " + mqttConnection);
        mqttStatus.getElement().getThemeList().add("badge " + mqttConnection);
        add(mqttStatus);

        HorizontalLayout publishLayout = new HorizontalLayout();
        TextField topicField = new TextField("Topic");
        TextField messageField = new TextField("Message");
        Button publishButton = new Button("Publish");
        publishLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END, publishButton);
        publishLayout.add(topicField, messageField, publishButton);
        add(publishLayout);

        List<MqttMessage> messages = mqttMessageService.findAll();
        mqttMessageGrid.setItems(messages);
        mqttMessageGrid.setColumns("id", "topic", "message", "dateTime");
        add(mqttMessageGrid);
    }
}
