package org.Landen.main.events;

import org.Landen.engine.objects.GameObject;

public interface HierarchyListener {
    void onHierarchyChanged(GameObject parent);
}