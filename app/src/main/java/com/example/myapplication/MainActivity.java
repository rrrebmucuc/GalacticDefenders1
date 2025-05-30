package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    private GameView gameView;
    Button startButton, quitButton;
    private AudioPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = findViewById(R.id.startButton);
        quitButton = findViewById(R.id.quitButton);
        mPlayer = new AudioPlayer(this);
        mPlayer.play();
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView = new GameView(MainActivity.this);
                setContentView(gameView);
                gameView.resume();
            }
        });
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer.pause();
        if (gameView!=null) gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayer.play();
        if (gameView!=null) gameView.resume();
    }
}
