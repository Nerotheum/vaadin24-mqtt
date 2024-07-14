package com.nerotheum.vaadinmqtt;

import java.util.List;

import com.nerotheum.vaadinmqtt.mqtt.MqttConnectionService;
import com.nerotheum.vaadinmqtt.mqtt.MqttValue;
import com.nerotheum.vaadinmqtt.mqtt.MqttValueService;
import com.nerotheum.vaadinmqtt.utils.NotificationUtil;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
    private Span mqttStatusInfo;

    @Autowired 
    public MainView(MqttValueService mqttValueService, MqttConnectionService mqttConnectionService) {
        this.mqttValueService = mqttValueService;
        this.mqttValueGrid = new Grid<>(MqttValue.class);
        this.mqttConnectionService = mqttConnectionService;
    }

    @PostConstruct
    private void init() {
        createStatusInfo();
        createToolbar();
        createGrid();
    }

    public void createStatusInfo() {
        String mqttConnection = mqttConnectionService.getMqttClient().isConnected() ? "success" : "error";
        if(mqttStatusInfo == null) {
            mqttStatusInfo = new Span("MQTT connection status: " + mqttConnection);
            mqttStatusInfo.setHeight("40px");
            mqttStatusInfo.getElement().getThemeList().add("badge " + mqttConnection);

            Button retryBtn = new Button("Reconnect");
            retryBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            retryBtn.addClickListener(click -> {
                mqttConnectionService.connect();
                createStatusInfo();
            });

            HorizontalLayout wrapperLayout = new HorizontalLayout(mqttStatusInfo, retryBtn);
            wrapperLayout.setWidthFull();
            wrapperLayout.expand(mqttStatusInfo);
            mqttStatusInfo.getStyle().set("flex-grow", "1");
            add(wrapperLayout);
        } else {
            mqttStatusInfo.setText("MQTT connection status: " + mqttConnection);
            mqttStatusInfo.getElement().getThemeList().clear();
            mqttStatusInfo.getElement().getThemeList().add("badge " + mqttConnection);
        }
    }

    public void createToolbar() {
        HorizontalLayout publishLayout = new HorizontalLayout();
        TextField topicField = new TextField("Topic");
        topicField.setRequired(true);
        TextField messageField = new TextField("Message");
        messageField.setRequired(true);
        Button publishButton = new Button("Publish");
        publishButton.addClickListener(click -> {
            createStatusInfo();
            if(topicField.isEmpty() || messageField.isEmpty()) {
                if(topicField.isEmpty())
                    topicField.setInvalid(true);
                if(messageField.isEmpty())
                    messageField.setInvalid(true);
                return;
            }
            if(!mqttConnectionService.getMqttClient().isConnected()) {
                NotificationUtil.create(true, "Could not publish message: Client is not connected!");
                return;
            }
            MqttValue mqttValue = new MqttValue(topicField.getValue(), messageField.getValue());
            mqttConnectionService.publish(mqttValue);
            topicField.setValue("");
            topicField.setInvalid(false);
            messageField.setValue("");
            messageField.setInvalid(false);
            NotificationUtil.create(false, "Message has been published!");
        });
        publishLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END, publishButton);
        publishLayout.add(topicField, messageField, publishButton);

        Button refreshButton = new Button("Refresh");        
        refreshButton.addClickListener(click -> {
            createStatusInfo();
            populateGrid();
        });
        
        HorizontalLayout wrapperLayout = new HorizontalLayout(publishLayout, refreshButton);
        wrapperLayout.setWidthFull();
        wrapperLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        wrapperLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END, refreshButton);
        add(wrapperLayout);
    }

    public void createGrid() {
        mqttValueGrid.setColumns("id", "topic", "message", "dateTime");
        populateGrid();
        add(mqttValueGrid);
    }

    public void populateGrid() {
        List<MqttValue> messages = mqttValueService.findAll();
        mqttValueGrid.setItems(messages);
    }
}
