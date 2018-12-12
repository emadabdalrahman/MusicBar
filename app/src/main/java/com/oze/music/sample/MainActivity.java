package com.oze.music.sample;

import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import com.oze.music.musicbar.BigMusicBar;
import com.oze.music.musicbar.MiniMusicBar;
import com.oze.music.musicbar.MusicBar;

public class MainActivity extends AppCompatActivity implements ValueAnimator.AnimatorUpdateListener {

    public static final String TAG = "MainActivity";
    BigMusicBar musicBar;
    MiniMusicBar miniMusicBar;
    Button hide;
    Button show;
    MediaPlayer mediaPlayer;
    boolean mSeekBarIsTracking = false;
    ValueAnimator mValueAnimator;

    MusicBar.OnMusicBarAnimationChangeListener onMusicBarAnimationChangeListener = new MusicBar.OnMusicBarAnimationChangeListener() {
        @Override
        public void onHideAnimationStart() {
            Log.i(TAG, "onHideAnimationStart");
        }

        @Override
        public void onHideAnimationEnd() {
            Log.i(TAG, "onHideAnimationEnd");
        }

        @Override
        public void onShowAnimationStart() {
            Log.i(TAG, "onShowAnimationStart");

        }

        @Override
        public void onShowAnimationEnd() {
            Log.i(TAG, "onShowAnimationEnd");
        }
    };

    MusicBar.OnMusicBarProgressChangeListener onMusicBarProgressChangeListener = new MusicBar.OnMusicBarProgressChangeListener() {
        @Override
        public void onProgressChanged(MusicBar musicBar, int progress, boolean fromUser) {
            if (fromUser)
                mSeekBarIsTracking = true;

        }

        @Override
        public void onStartTrackingTouch(MusicBar musicBar) {
            mSeekBarIsTracking = true;

        }

        @Override
        public void onStopTrackingTouch(MusicBar musicBar) {
            mSeekBarIsTracking = false;
            initValueAnimator(1.0f, musicBar.getPosition(), mediaPlayer.getDuration());
            mediaPlayer.seekTo(musicBar.getPosition());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicBar = findViewById(R.id.BigMusicBar);
        miniMusicBar = findViewById(R.id.MiniMusicBar);

        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.start();

        musicBar.loadFrom(getResources().openRawResource(R.raw.music), mediaPlayer.getDuration());
        miniMusicBar.loadFrom(getResources().openRawResource(R.raw.music),mediaPlayer.getDuration());

        initValueAnimator(1.0f, 0, mediaPlayer.getDuration());

        musicBar.setAnimationChangeListener(onMusicBarAnimationChangeListener);
        musicBar.setProgressChangeListener(onMusicBarProgressChangeListener);

        miniMusicBar.setAnimationChangeListener(onMusicBarAnimationChangeListener);
        miniMusicBar.setProgressChangeListener(onMusicBarProgressChangeListener);

        initButton();
    }

    void initButton() {
        hide = findViewById(R.id.hide);
        show = findViewById(R.id.show);
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oHideClick();
            }
        });
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowClick();
            }
        });
    }

    private void initValueAnimator(float playbackSpeed, int progress, int max) {
        int timeToEnd = (int) ((max - progress) / playbackSpeed);
        if (timeToEnd > 0) {
            mValueAnimator = ValueAnimator.ofInt(progress, max).setDuration(timeToEnd);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.addUpdateListener(this);
            mValueAnimator.start();
        }
    }

    private void oHideClick() {
        musicBar.hide();
        miniMusicBar.hide();
    }

    private void onShowClick() {
        musicBar.show();
        miniMusicBar.show();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

        if (mSeekBarIsTracking) {
            animation.cancel();
        } else {
            musicBar.setProgress((int) animation.getAnimatedValue());
            miniMusicBar.setProgress((int) animation.getAnimatedValue());
        }
    }
}
