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

    public static void loadTest() {
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

    public static Scene loadDragableArrows(Vector3f center) {
        ArrayList<GameObject> arrows = new ArrayList<>();

        Material l_r_material = new Material(new Vector4f(1f, 0f, 0f, .5f), 1f, 0.5f);
        Material f_b_material = new Material(new Vector4f(0f, 0f, 1f, .5f), 1f, 0.5f);
        Material u_d_material = new Material(new Vector4f(1f, 1f, 0f, .5f), 1f, 0.5f);

        GameObject l_r = MeshManager.createGameObjectFromMesh(
                "left/right-arrows",
                "models/models/arrows.obj",
                l_r_material,
                center,
                new Vector3f(0,0,0),
                new Vector3f(1,1,1)
        );

        GameObject f_b = MeshManager.createGameObjectFromMesh(
                "front/back-arrows",
                "models/models/arrows.obj",
                f_b_material,
                center,
                new Vector3f(0,90,0),
                new Vector3f(1,1,1)
        );

        GameObject u_d = MeshManager.createGameObjectFromMesh(
                "up/down-arrows",
                "models/models/arrows.obj",
                u_d_material,
                center,
                new Vector3f(90,0,0),
                new Vector3f(1,1,1)
        );

        arrows.add(l_r);
        arrows.add(u_d);
        arrows.add(f_b);

        Scene s = new Scene(arrows);
        return s;
    }

    public static ArrayList<Scene> getScenes() {
        return scenes;
    }
}
