package org.Landen.engine.objects;

import org.Landen.main.Managers.SceneManager;

import java.util.ArrayList;

public class Scene {
    private final ArrayList<GameObject> objects;

    public Scene(ArrayList<GameObject> objects) {
        this.objects = objects;
    }

    public void addObject(GameObject obj) {
        objects.add(obj);
    }

    public void load(Boolean loaded) {
        SceneManager.update(this, loaded);
    }

    public ArrayList<GameObject> getGameObjects() {
        return objects;
    }
}
