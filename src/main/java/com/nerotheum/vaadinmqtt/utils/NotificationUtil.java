package com.nerotheum.vaadinmqtt.utils;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class NotificationUtil {
    public static void create(boolean error, String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(error ? NotificationVariant.LUMO_ERROR : NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.BOTTOM_START);
        notification.setDuration(5000);
    }
}
