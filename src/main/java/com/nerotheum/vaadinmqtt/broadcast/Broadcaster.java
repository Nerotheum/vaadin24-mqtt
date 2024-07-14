package com.nerotheum.vaadinmqtt.broadcast;

import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Broadcaster {
    static ExecutorService executorService = Executors.newSingleThreadExecutor();
    static List<BroadcasterListener> listeners = new ArrayList<>();

    public static synchronized Registration register(BroadcasterListener listener) {
        listeners.add(listener);

        return () -> {
            synchronized (Broadcaster.class) {
                listeners.remove(listener);
            }
        };
    }

    public static synchronized void broadcast(String message) {
        for (BroadcasterListener listener : listeners) {
            executorService.execute(() -> listener.receiveBroadcast(message));
        }
    }
}
