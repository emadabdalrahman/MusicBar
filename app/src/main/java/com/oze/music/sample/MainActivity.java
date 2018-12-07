package com.oze.music.sample;

import android.animation.ValueAnimator;
import android.media.MediaMetadataRetriever;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ValueAnimator.AnimatorUpdateListener {

    public static final String TAG = "MainActivity";
    BigMusicBar musicBar;
    MiniMusicBar miniMusicBar;
    Button next;
    Button previous;
    ArrayList<Integer> music = new ArrayList<>();
    MediaPlayer mediaPlayer;
    int i;
    boolean mSeekBarIsTracking = false;
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

        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);

        music.add(R.raw.music);
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.start();

        initValueAnimator(1.0f, 0, mediaPlayer.getDuration());

        File file = new File(getCacheDir(), "music.mp3");
        byte[] buffer = null;
        if (i < music.size()) {
            buffer = getBytes(file, music.get(0));
            i++;
        } else {
            i = 0;
            buffer = getBytes(file, music.get(0));
            i++;
        }

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(file.getPath());

        int duration = Integer.parseInt(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;


        if (buffer != null){
            musicBar.loadFrom(buffer, duration);
            miniMusicBar.loadFrom(buffer,duration);
        }


        musicBar.setAnimationChangeListener(onMusicBarAnimationChangeListener);
        musicBar.setProgressChangeListener(onMusicBarProgressChangeListener);

        miniMusicBar.setAnimationChangeListener(onMusicBarAnimationChangeListener);
        miniMusicBar.setProgressChangeListener(onMusicBarProgressChangeListener);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClick();
            }
        });
        next.setText("Hide");

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPreviousClick();
            }
        });
        previous.setText("Show");

    }

    ValueAnimator mValueAnimator;

    private void initValueAnimator(float playbackSpeed, int progress, int max) {
        int timeToEnd = (int) ((max - progress) / playbackSpeed);
        if (timeToEnd > 0) {
            mValueAnimator = ValueAnimator.ofInt(progress, max).setDuration(timeToEnd);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.addUpdateListener(this);
            mValueAnimator.start();
        }
    }

    private void onNextClick() {
        musicBar.hide();
        miniMusicBar.hide();
    }

    private void onPreviousClick() {
        musicBar.show();
        miniMusicBar.show();
    }

    private byte[] getBytes(File file, int id) {
        byte[] buffer = null;
        try {
            InputStream stream = getResources().openRawResource(id);
            OutputStream output = new FileOutputStream(file);
            buffer = new byte[stream.available()];
            stream.read(buffer);
            output.write(buffer);
            stream.close();
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (mSeekBarIsTracking) {
            animation.cancel();
        } else {
            musicBar.seekTo((int) animation.getAnimatedValue());
            miniMusicBar.seekTo((int) animation.getAnimatedValue());
        }
    }
}
