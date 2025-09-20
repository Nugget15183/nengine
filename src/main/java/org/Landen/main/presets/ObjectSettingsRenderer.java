package org.Landen.main.presets;

import org.Landen.engine.objects.GameObject;

@FunctionalInterface
public interface ObjectSettingsRenderer {
    void render(GameObject object);
}
