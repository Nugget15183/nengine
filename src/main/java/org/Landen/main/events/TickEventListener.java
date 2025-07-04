package org.Landen.main.events;

import org.Landen.main.events.EventListener;

public class TickEventListener extends EventListener {
    private Runnable callback;

    public TickEventListener(Runnable callback) {
        super(callback);
        this.callback = callback;
    }

    @Override
    public void tick() {
        if (callback != null) {
            callback.run();
        }
    }
}