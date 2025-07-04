package org.Landen.engine.maths;

public class Ray {
    private Vector3f origin;
    private Vector3f direction;

    public Ray(Vector3f origin, Vector3f direction) {
        this.origin = origin;
        this.direction = Vector3f.normalize(direction); // Ensure direction is normalized
    }

    public Vector3f getOrigin() {
        return origin;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public Vector3f getPointAt(float t) {
        return Vector3f.add(origin, Vector3f.multiply(direction, t));
    }
}