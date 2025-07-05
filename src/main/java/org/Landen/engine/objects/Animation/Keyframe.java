package org.Landen.engine.objects.Animation;

import org.Landen.engine.maths.Vector3f;
import org.Landen.engine.objects.nonliteral.GameObject;

public class Keyframe {
    private final Vector3f rot;
    private final Vector3f pos;
    private final GameObject target;
    private final float time;

    public Keyframe(Vector3f rot, Vector3f pos, GameObject target, float time) {
        this.rot = rot;
        this.pos = pos;
        this.target = target;
        this.time = time;
    }

    public Vector3f getRot() {
        return rot;
    }

    public Vector3f getPos() {
        return pos;
    }

    public GameObject getTarget() {
        return target;
    }

    public float getTime() {
        return time;
    }
}
