package org.Landen.main.Managers;


import org.Landen.main.events.EventListener;

import java.util.ArrayList;

public class EventListenerManager {
    public static ArrayList<EventListener> listeners = new ArrayList<>();

    public static void register(EventListener listener) {
        listeners.add(listener);
    }

    public static void tick() {
        for(int i=0; i < listeners.toArray().length; i++) {
            if (listeners.get(i) != null) {
                listeners.get(i).tick();
            }
        }
    }
}
