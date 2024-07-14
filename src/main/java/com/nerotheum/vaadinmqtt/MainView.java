package com.nerotheum.vaadinmqtt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.nerotheum.vaadinmqtt.broadcast.Broadcaster;
import com.nerotheum.vaadinmqtt.broadcast.BroadcasterListener;
import com.nerotheum.vaadinmqtt.mqtt.MqttConnectionService;
import com.nerotheum.vaadinmqtt.mqtt.MqttValue;
import com.nerotheum.vaadinmqtt.mqtt.MqttValueService;
import com.nerotheum.vaadinmqtt.utils.NotificationUtil;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import jakarta.annotation.PostConstruct;

@Route
@UIScope
@SpringComponent
public class MainView extends VerticalLayout implements BroadcasterListener {
    private final MqttValueService mqttValueService;
    private final MqttConnectionService mqttConnectionService;
    private Registration broadcasterRegistration;

    // Components for status info
    private Span spanStatusInfo = new Span("Placeholder");
    private Button btnReconnect = new Button("Reconnect");

    // Components for toolbar
    private TextField fieldTopic = new TextField("Topic");
    private TextField fieldMessage = new TextField("Message");
    private Button btnPublish = new Button("Publish");

    // Components for grid
    private Grid<MqttValue> mqttValueGrid = new Grid<>(MqttValue.class);

    @Autowired
    public MainView(MqttValueService mqttValueService, MqttConnectionService mqttConnectionService) {
        this.mqttValueService = mqttValueService;
        this.mqttConnectionService = mqttConnectionService;
        this.broadcasterRegistration = Broadcaster.register(this);
    }

    @PostConstruct
    public void init() {
        createStatusInfo();
        createToolbar();
        createGrid();
    }

    public void createStatusInfo() {
        spanStatusInfo.setHeight("40px");
        btnReconnect.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnReconnect.addClickListener(click -> {
            mqttConnectionService.connect();
        });

        boolean initialStatus = mqttConnectionService.getMqttClient() == null ? false : mqttConnectionService.getMqttClient().isConnected();
        HorizontalLayout layoutWrapper = new HorizontalLayout(spanStatusInfo, btnReconnect);
        layoutWrapper.setWidthFull();
        layoutWrapper.expand(spanStatusInfo);
        spanStatusInfo.getStyle().set("flex-grow", "1");
        enableDisableComponents(initialStatus);
        add(layoutWrapper);
    }

    public void createToolbar() {
        fieldTopic.setRequired(true);
        fieldMessage.setRequired(true);
        btnPublish.addClickListener(click -> {
            if(fieldTopic.isEmpty() || fieldMessage.isEmpty()) {
                if(fieldTopic.isEmpty())
                    fieldTopic.setInvalid(true);
                if(fieldMessage.isEmpty())
                    fieldMessage.setInvalid(true);
                return;
            }
            if(!mqttConnectionService.getMqttClient().isConnected()) {
                NotificationUtil.create(true, "Could not publish message: Client is not connected!");
                enableDisableComponents(false);
            } else {
                MqttValue mqttValue = new MqttValue(fieldTopic.getValue(), fieldMessage.getValue());
                mqttConnectionService.publish(mqttValue);
                fieldTopic.setValue("");
                fieldTopic.setInvalid(false);
                fieldMessage.setValue("");
                fieldMessage.setInvalid(false);
                NotificationUtil.create(false, "Message has been published!");
            }
        });

        HorizontalLayout layoutPublish = new HorizontalLayout();
        layoutPublish.setVerticalComponentAlignment(FlexComponent.Alignment.END, btnPublish);
        layoutPublish.add(fieldTopic, fieldMessage, btnPublish);
     
        Button btnRefresh = new Button("Refresh");
        btnRefresh.addClickListener(click -> {
            populateGrid();
        });

        HorizontalLayout layoutWrapper = new HorizontalLayout(layoutPublish, btnRefresh);
        layoutWrapper.setWidthFull();
        layoutWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        layoutWrapper.setVerticalComponentAlignment(FlexComponent.Alignment.END, btnRefresh);
        add(layoutWrapper);
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

    public void enableDisableComponents(boolean connected) {
        String strConnected = connected ? "success" : "error";

        spanStatusInfo.setText("MQTT connection status: " + strConnected);
        spanStatusInfo.getElement().getThemeList().clear();
        spanStatusInfo.getElement().getThemeList().add("badge " + strConnected);

        fieldTopic.setReadOnly(!connected);
        fieldMessage.setReadOnly(!connected);
        btnPublish.setEnabled(connected);
    }

    @Override
    public void receiveBroadcast(String message) {
        getUI().ifPresent(ui -> ui.access((Command) () -> {
            switch(message) {
                case "RefreshConnectionStatus":
                    enableDisableComponents(mqttConnectionService.getMqttClient().isConnected());
                    ui.push();
                    break;
                case "RefreshGrid":
                    populateGrid();
                    ui.push();
                    break;
                default:
                    break;
            }
        }));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        broadcasterRegistration.remove();
        super.onDetach(detachEvent);
    }
}
