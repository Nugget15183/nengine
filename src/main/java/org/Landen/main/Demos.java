package org.Landen.main;

import org.Landen.engine.graphics.Material;
import org.Landen.engine.maths.Vector3f;
import org.Landen.engine.maths.Vector4f;
import org.Landen.engine.objects.Animation.Animation;
import org.Landen.engine.objects.Animation.Keyframe;
import org.Landen.engine.objects.nonliteral.GameObject;
import org.Landen.engine.objects.nonliteral.Scene;
import org.Landen.main.Managers.AnimationManager;
import org.Landen.main.Managers.MeshManager;
import org.Landen.main.Managers.SceneManager;

import java.util.ArrayList;

public class Demos {
    public void runall() {
        exampleSceneAndObject();
    }
    public void exampleAnimation(GameObject targetobj){
        ArrayList<Keyframe> keyframes = new ArrayList<>();

        keyframes.add(new Keyframe(
                new Vector3f(0,0,0),
                new Vector3f(0,0,0),
                targetobj,
                0f
        ));

        keyframes.add(new Keyframe(
                new Vector3f(0,0,0),
                new Vector3f(0,100,0),
                targetobj,
                50f
        ));

        Animation anim = new Animation(keyframes);

        AnimationManager.register(anim);

        anim.play();
    }
    public void exampleSceneAndObject() {
        ArrayList<GameObject> objs = new ArrayList<>();

        Material mat = new Material(new Vector4f(0.5f,0.5f,0.5f,1f),1f,0.5f);

        GameObject g = MeshManager.createGameObjectFromMesh("models/usermodels/monkey.obj",
                mat,
                new Vector3f(0,0,-5),
                new Vector3f(0,0,0),
                new Vector3f(1,1,1)
        );

        objs.add(g);

        Scene s = new Scene(objs);
        SceneManager.insert(s, true);

    }
}
