package org.Landen.main.presets;

import org.Landen.engine.objects.Scene;

public class presets {
    public static void loadAll() {
        Guis.LoadSideEditorMenu();
        Guis.loadTopBarDefaults();
        Scenes.loadTest();
    }

    public static void updateHierarchy(Scene scene) {
        Guis.loadHierarchy(scene);
    }
}
