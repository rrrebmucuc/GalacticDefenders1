package com.example.myapplication;

public class Enemy {
    public int x, y;
    public int type, reward;
    public int size;
    public int speed;
    public int hp;
    private boolean movingRight = true;

    public Enemy(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        switch(type){
            case 0: //normal
                speed = 5;
                hp = 15;
                reward = 100;
                size = 100;
                break;
            case 1: //speedy
                speed = 10;
                hp = 10;
                reward = 200;
                size = 90;
                break;
            case 2: //tank
                speed = 2;
                hp = 30;
                reward = 300;
                size = 150;
                break;
            case 3: //boss
                speed = 1;
                hp = 400;
                reward = 2000;
                size = 400;
        }
    }

    public void update() {
        if (type!=3) {
            if (movingRight) {
                x += speed;
                if (x > 1000) {
                    movingRight = false;
                    y += size;
                }
            } else {
                x -= speed;
                if (x < 0) {
                    movingRight = true;
                    y += size;
                }
            }
        } else{
            if (y<400) {
                y += 5;
            }
        }
    }
}
