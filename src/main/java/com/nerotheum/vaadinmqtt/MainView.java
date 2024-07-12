package com.nerotheum.vaadinmqtt;

import java.util.List;

import com.nerotheum.vaadinmqtt.mqtt.MqttConnectionService;
import com.nerotheum.vaadinmqtt.mqtt.MqttValue;
import com.nerotheum.vaadinmqtt.mqtt.MqttValueService;
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

    private final MqttValueService mqttValueService;
    private final Grid<MqttValue> mqttValueGrid;
    private final MqttConnectionService mqttConnectionService;

    public MainView(@Autowired MqttValueService mqttValueService, @Autowired MqttConnectionService mqttConnectionService) {
        this.mqttValueService = mqttValueService;
        this.mqttValueGrid = new Grid<>(MqttValue.class);
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
        publishButton.addClickListener(click -> {
            MqttValue mqttValue = new MqttValue(topicField.getValue(), messageField.getValue());
            mqttConnectionService.publish(mqttValue);
            topicField.setValue("");
            messageField.setValue("");
        });
        publishLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END, publishButton);
        publishLayout.add(topicField, messageField, publishButton);

        Button refreshButton = new Button("Refresh");        
        refreshButton.addClickListener(click -> {
            populateGrid();
        });
        
        HorizontalLayout wrapperLayout = new HorizontalLayout(publishLayout, refreshButton);
        wrapperLayout.setWidthFull();
        wrapperLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        wrapperLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END, refreshButton);
        add(wrapperLayout);

        mqttValueGrid.setColumns("id", "topic", "message", "dateTime");
        populateGrid();
        add(mqttValueGrid);
    }

    public void populateGrid() {
        List<MqttValue> messages = mqttValueService.findAll();
        mqttValueGrid.setItems(messages);
    }
}
