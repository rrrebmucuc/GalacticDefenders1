package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;

public class GameView extends SurfaceView implements Runnable {
    private Thread gameThread;
    private SurfaceHolder holder;
    private boolean isPlaying;
    private Paint paint;
    private View gameOverScreen;
//    private SoundPool soundPool;

    private final Bitmap playerbit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.playertexture),150,150,false);
    private final Bitmap back = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.myback3),getScreenWidth(),getScreenHeight()*2,false);
    private final Bitmap normalenemy = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.normalenemy),100,100,false);
    private final Bitmap speedenemy = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.speedenemy),90,90,false);
    private final Bitmap tankenemy = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.tankenemy),150,100,false);
    private final Bitmap bossenemy = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.bossenemy),450,600,false);
    private final Bitmap[] bits = {normalenemy,speedenemy,tankenemy,bossenemy};
    private int backY = -getScreenHeight();

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> bullets;

    private int score = 0;
    private int rowsNum = 1, colNum = 5;
    private int enemiesKilled = 0;
    private int totalEnemies;
    private int waves = 1;
    private long lastShotTime = 0;
    int[] sizes = {100,90,150,500};

//    AudioAttributes attributes = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_GAME).build();

//    int firingSound = soundPool.load(getContext(),R.raw.laser, 1);

    public GameView(Context context) {
        super(context);
        holder = getHolder();
        paint = new Paint();

        player = new Player();
        player.x=getScreenWidth()/2;
        player.y=getScreenHeight()-200;

        enemies = new ArrayList<>();
        createEnemies(rowsNum, colNum, 0, 0, 0);

        bullets = new ArrayList<>();
//        soundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(attributes).build();
    }

    private void createEnemies(int row, int col, int type, int startX, int startY) {
        int size = sizes[type];
        int margin = 20;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                enemies.add(new Enemy(j * (size + margin) + margin + startX, i * (size + margin) + startY, type));
            }
        }
        totalEnemies = row*col;
    }

    //todo WAVES
    private void spawnWave(){
        waves++;
        if (waves%10==0){createEnemies(1,1,3, getScreenWidth()/2-20, 100);}
        if (waves<4) {createEnemies(waves,5,0, 0, 100);}
        if (waves>3 && waves<7) {createEnemies(waves-3,5,1, 0, 100);}
        if (waves>6 && waves<10){createEnemies(waves-6,5,2, 0, 100);}
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
        }
    }

    //todo 'backend'
    public void update() {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            bullets.get(i).update();
            if (bullets.get(i).y < 0) {
                bullets.remove(i);
            }
        }

        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.update();

             //todo HIT CHECK
            Iterator<Bullet> bulletIterator = bullets.iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = bulletIterator.next();
                if (Math.abs(bullet.x-enemy.x) < enemy.size/2 && Math.abs(bullet.y-enemy.y) < enemy.size/2) {
                    bulletIterator.remove();
                    score +=10;
                    enemy.hp-=20;
                    if (enemy.hp<=0){
                        enemiesKilled++;
                        score+=enemy.reward;
                        enemyIterator.remove();
                    }
                    break;
                }
            }

            //loss check
            if (enemy.y> player.y){
                player.isDead = true;
            }
            //new wave
            if (enemiesKilled == totalEnemies){
                enemiesKilled=0;
                spawnWave();
            }
        }
    }

    //todo 'frontend'
    private void draw() {
        if (!holder.getSurface().isValid()) return;

        Canvas canvas = holder.lockCanvas();

        // background
        if (backY<0) backY+=4;
        else backY= -getScreenHeight();
        canvas.drawBitmap(back,0,backY,paint);

        // enemies
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            canvas.drawBitmap(bits[enemy.type],enemy.x - enemy.size/2,enemy.y - enemy.size/2,paint);
        }

        // bullets
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            paint.setColor(Color.GREEN);
            canvas.drawCircle(bullet.x, bullet.y, 5, paint);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(bullet.x, bullet.y, 3, paint);
        }

        // player
        canvas.drawBitmap(playerbit,player.x-75,player.y-75,paint);

        // score
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        canvas.drawText("Score: " + score, 70, 70, paint);
        canvas.drawText("Wave:" + waves, getScreenWidth()-220, 70, paint);

        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            player.x = (int) event.getX();
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShotTime > 250) {
                player.x = (int) event.getX();
//                soundPool.play(firingSound, 1 , 1, 1, 0, 1);
                bullets.add(new Bullet(player.x, getScreenHeight() - 150));
                lastShotTime = currentTime;
            }
        }
        return true;
    }

    public void pause () {
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void resume () {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}
