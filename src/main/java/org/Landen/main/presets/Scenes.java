package org.Landen.main.presets;

import org.Landen.engine.graphics.Material;
import org.Landen.engine.graphics.Mesh;
import org.Landen.engine.maths.Vector3f;
import org.Landen.engine.maths.Vector4f;
import org.Landen.engine.objects.GameObject;
import org.Landen.engine.objects.Scene;
import org.Landen.main.Managers.MeshManager;
import org.Landen.main.Managers.SceneManager;

import java.util.ArrayList;

public class Scenes {
    public static ArrayList<Scene> scenes = new ArrayList<>();

    public static void load() {
        ArrayList<GameObject> s1_objs = new ArrayList<>();

        Material m = new Material(new Vector4f(0.5f,0.5f,0.5f,1f),1f,0.5f);

        GameObject g = MeshManager.createGameObjectFromMesh(
                "monkey",
                "models/usermodels/monkey.obj",
                m,
                new Vector3f(0,0,-5),
                new Vector3f(0,0,0),
                new Vector3f(1,1,1)
        );

        s1_objs.add(g);

        Scene s1 = new Scene(s1_objs);

        scenes.add(s1);

        SceneManager.insert(s1, true);
    }

    public static ArrayList<Scene> getScenes() {
        return scenes;
    }
}
