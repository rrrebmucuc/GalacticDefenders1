package com.example.myapplication;

public class Bullet {
    public int x, y;
    private int speed = 45;

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        y -= speed;
    }
}
