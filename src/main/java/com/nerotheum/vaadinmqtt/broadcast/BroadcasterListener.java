package com.nerotheum.vaadinmqtt.broadcast;

public interface BroadcasterListener {
    void receiveBroadcast(BroadcastMessage message);
}
