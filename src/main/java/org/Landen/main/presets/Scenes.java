package org.Landen.main.presets;

import org.Landen.engine.graphics.Material;
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

        GameObject parent = MeshManager.createGameObjectFromMesh(
                "monkey",
                "models/models/monkey.obj",
                m,
                new Vector3f(-1.5f,0,-5),
                new Vector3f(0,0,0),
                new Vector3f(1,1,1)
        );

        GameObject child = MeshManager.createGameObjectFromMesh(
                "monkey",
                "models/models/monkey.obj",
                m,
                new Vector3f(-1.5f,-3,-5),
                new Vector3f(0,0,0),
                new Vector3f(.5f,.5f,.5f)
        );

        s1_objs.add(parent);

        Scene s1 = new Scene(parent,s1_objs);
        parent.addChild(child);

        scenes.add(s1);

        SceneManager.insert(s1, true);
    }

    public static Scene loadDragableArrows(Vector3f center) {
        ArrayList<GameObject> arrows = new ArrayList<>();

        center.print();

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

        return new Scene(f_b,arrows);
    }

    public static ArrayList<Scene> getScenes() {
        return scenes;
    }
}
