package org.Landen.engine.maths;

public class Vector4f {
    private float x, y, z, w;

    public Vector4f() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
    }

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f(Vector4f other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }

    // Getters
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getW() {
        return w;
    }

    // Setters
    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void setW(float w) {
        this.w = w;
    }

    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    // Math operations
    public static Vector4f add(Vector4f left, Vector4f right) {
        return new Vector4f(left.x + right.x, left.y + right.y, left.z + right.z, left.w + right.w);
    }

    public static Vector4f subtract(Vector4f left, Vector4f right) {
        return new Vector4f(left.x - right.x, left.y - right.y, left.z - right.z, left.w - right.w);
    }

    public static Vector4f multiply(Vector4f vector, float scalar) {
        return new Vector4f(vector.x * scalar, vector.y * scalar, vector.z * scalar, vector.w * scalar);
    }

    public static Vector4f divide(Vector4f vector, float scalar) {
        return new Vector4f(vector.x / scalar, vector.y / scalar, vector.z / scalar, vector.w / scalar);
    }

    public static float dot(Vector4f left, Vector4f right) {
        return left.x * right.x + left.y * right.y + left.z * right.z + left.w * right.w;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public float lengthSquared() {
        return x * x + y * y + z * z + w * w;
    }

    public Vector4f normalize() {
        float len = length();
        if (len == 0) {
            return new Vector4f(0, 0, 0, 0);
        }
        return new Vector4f(x / len, y / len, z / len, w / len);
    }

    public Vector4f negate() {
        return new Vector4f(-x, -y, -z, -w);
    }

    // Utility methods
    public Vector3f toVector3f() {
        return new Vector3f(x, y, z);
    }

    public float[] toArray() {
        return new float[]{x, y, z, w};
    }

    @Override
    public String toString() {
        return "Vector4f{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj);
    }
}