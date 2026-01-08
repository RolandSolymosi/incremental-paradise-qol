package com.incrementalclient.common.utils;

public class Vector2f {
    public float x;
    public float y;

    public Vector2f() {
        this(0, 0);
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(Vector2f other) {
        this(other.x, other.y);
    }

    public Vector2f add(Vector2f other) {
        return new Vector2f(this.x + other.x, this.y + other.y);
    }

    public Vector2f subtract(Vector2f other) {
        return new Vector2f(this.x - other.x, this.y - other.y);
    }

    public Vector2f multiply(float scalar) {
        return new Vector2f(this.x * scalar, this.y * scalar);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2f copy() {
        return new Vector2f(this.x, this.y);
    }

    @Override
    public String toString() {
        return "Vector2f(" + x + ", " + y + ")";
    }
}

