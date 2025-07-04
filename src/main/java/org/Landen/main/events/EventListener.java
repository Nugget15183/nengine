package org.Landen.main.events;

public class EventListener {
    private Runnable callback;
    public EventListener(Runnable callback) {
        this.callback = callback;
    }

    public void tick() {
        //child listeners run checks here
    }
}
