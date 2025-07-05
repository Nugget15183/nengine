package org.Landen.main.Managers;

import org.Landen.engine.objects.nonliteral.Scene;

import java.util.ArrayList;
import java.util.HashMap;

public class SceneManager {
    private static HashMap<Scene, Boolean> scenes = new HashMap<>();

    private static int maxLoadedScenes = 1;
    private static int loadedScenes = 0;

    private static ArrayList<Scene> loadedSceneObjects = new ArrayList<>();

    public static void insert(Scene s, Boolean load) {
        scenes.put(s,load);
        if(load) {
            loadScene(s);
        }
    }

    public static void update(Scene scene, Boolean loaded) {
        scenes.put(scene,loaded);

        if(loaded) {
            loadScene(scene);
        } else {
            unloadScene(scene);
        }
    }

    private static void loadScene(Scene s) {
        if(loadedScenes < maxLoadedScenes) {
            loadedScenes++;
            loadedSceneObjects.add(s);
            MeshManager.loadScene(s);
        }
        else {
            MeshManager.unloadScene(loadedSceneObjects.get(0));
            loadedSceneObjects.remove(0);
            loadedSceneObjects.add(s);
            MeshManager.loadScene(s);
        }
    }

    private static void unloadScene(Scene s) {
        loadedSceneObjects.remove(s);
        loadedScenes--;
    }
}
