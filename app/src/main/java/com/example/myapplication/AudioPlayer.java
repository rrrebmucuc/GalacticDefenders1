package com.example.myapplication;

import android.content.Context;
import android.media.MediaPlayer;

public class AudioPlayer {
    MediaPlayer mPlayer;
    public AudioPlayer(Context context){
        mPlayer = MediaPlayer.create(context, R.raw.through_space);
        mPlayer.setVolume(60,60);
    }

    public void play(){
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()  {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer){
                mPlayer.start();
            }
        });
        mPlayer.start();
    }

    public void stop(){
        if(mPlayer!=null){
            mPlayer.release();
        }
    }

    public void pause(){
        if(mPlayer!=null)
            mPlayer.pause();
    }
}
