package org.Landen.engine.maths;

public class Randf {
    public static float range(float min, float max) {
        if (min >= max) {
            throw new IllegalArgumentException("Min must be less than max");
        }
        return min + (float) Math.random() * (max - min);
    }
}
