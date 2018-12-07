package com.oze.music.musicbar;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class MusicBar extends View implements ValueAnimator.AnimatorUpdateListener {

    String TAG = "MusicBar";
    byte[] mFile;
    int[] mBarHeight;
    int mTrackDurationInSec;
    int mActualBitRate;
    int mMaxDataPerBar = 0;
    int mSpaceBetweenBar = 2;
    int mSeekToPosition = -1;
    int mMaxBarHeight = 0;
    int mMinBarHeight;
    int mMaxNumOfBar = 0;
    int mBarDuration = 1000;
    float mBarWidth = 2;
    boolean isNewLoad;
    boolean isHide = false;
    boolean isShow = true;
    boolean isAnimated;
    Paint mLoadedPaint;
    Paint mBackgroundPaint;
    OnMusicBarProgressChangeListener mMusicBarChangeListener;
    OnMusicBarAnimationChangeListener mMusicBarAnimationChangeListener;
    int mAnimatedValue = 0;
    ValueAnimator mValueAnimator;
    float mFirstTouchX = 0;
    final int ANIMATION_TYPE_SHOW = 0;
    final int ANIMATION_TYPE_HIDE = 1;

    public MusicBar(Context context) {
        super(context);
        init();
    }

    public MusicBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MusicBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public interface OnMusicBarProgressChangeListener {


        void onProgressChanged(MusicBar musicBar, int progress, boolean fromUser);

        void onStartTrackingTouch(MusicBar musicBar);

        void onStopTrackingTouch(MusicBar musicBar);

    }

    public interface OnMusicBarAnimationChangeListener {


        void onHideAnimationStart();

        void onHideAnimationEnd();

        void onShowAnimationStart();

        void onShowAnimationEnd();


    }

    private void init() {
        mBackgroundPaint = new Paint();
        this.mBackgroundPaint.setColor(Color.YELLOW);
        this.mBackgroundPaint.setStrokeCap(Paint.Cap.SQUARE);
        this.mBackgroundPaint.setStrokeWidth(mBarWidth);

        mLoadedPaint = new Paint();
        this.mLoadedPaint.setColor(Color.RED);
        this.mLoadedPaint.setStrokeCap(Paint.Cap.SQUARE);
        this.mLoadedPaint.setStrokeWidth(mBarWidth);
    }

    public void loadFrom(byte[] file, int durationInSec) {
        this.mFile = file;
        this.mTrackDurationInSec = durationInSec;
        this.mActualBitRate = file.length / durationInSec;
        isNewLoad = true;
        mSeekToPosition = -1;
        invalidate();
    }

    int[] getBitPerHalfSec() {
        int[] data = new int[mTrackDurationInSec * 2];
        int bitRate = mFile.length / (mTrackDurationInSec * 2);
        for (int i = 0; i < mTrackDurationInSec * 2; i++) {
            int temp = 0;
            for (int j = i * bitRate; j < (i + 1) * bitRate; j++) {
                temp = temp + mFile[j];
            }
            data[i] = Math.abs(temp);
        }
        return data;
    }

    int[] getBitPerSec() {
        int[] data = getBitPerHalfSec();
        int[] dataPerSec = new int[mTrackDurationInSec];
        for (int i = 0; i < mTrackDurationInSec; i++) {
            dataPerSec[i] = (data[i * 2] + data[i * 2 + 1]) / 2;
        }
        return dataPerSec;
    }

    int[] getBitPer2Sec() {
        int[] dataPerSec = getBitPerSec();
        int[] dataPer2Sec = new int[dataPerSec.length / 2];
        for (int i = 0; i < dataPer2Sec.length; i++) {
            dataPer2Sec[i] = (dataPerSec[i * 2] + dataPerSec[i * 2 + 1]) / 2;
        }
        return dataPer2Sec;
    }

    int[] getBarHeight(int[] data) {
        int[] barHeight = new int[data.length];
        mMaxBarHeight = getHeight() - getPaddingBottom() - getPaddingTop();
        mMinBarHeight = mMaxBarHeight / 100;
        if (mMinBarHeight == 0) mMinBarHeight = 1;
        for (int i = 0; i < data.length; i++) {
            barHeight[i] = (data[i] * mMaxBarHeight) / mMaxDataPerBar;
        }

        for (int i = 0; i < barHeight.length; i++) {
            if (barHeight[i] == 0) {
                if (i == 0) barHeight[i] = barHeight[i + 1] / 2;
                else if (i == barHeight.length - 1) barHeight[i] = barHeight[i - 1] / 2;
                else {
                    barHeight[i] = barHeight[i - 1] + barHeight[i + 1];
                }
            }
            if (barHeight[i] == 0) barHeight[i] = mMinBarHeight;

            if (barHeight[i] > mMaxBarHeight) barHeight[i] = mMaxBarHeight;

            if (barHeight[i] % 2 != 0) barHeight[i] = barHeight[i] + 1;
        }

        return barHeight;
    }

    int[] getBitPer(int numOfBar) {
        int[] data = new int[numOfBar];
        int bitRate = mFile.length / numOfBar;

        for (int i = 0; i < data.length; i++) {
            int temp = 0;
            for (int j = i * bitRate; j < (i + 1) * bitRate; j++) {
                temp = temp + mFile[j];
            }
            data[i] = Math.abs(temp);
        }
        return data;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private int getMedium(int[] data) {
        int total = 0;
        for (int i = 0; i < data.length; i++) {
            total = total + data[i];
        }
        return total / data.length;
    }

    int[] fixData(int[] data) {
        int mid = getMedium(data);
        ArrayList<Integer> test = new ArrayList<>();

        mMaxDataPerBar = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] >= mid * 2) data[i] = mid;
            if (i + 1 < data.length && data[i + 1] >= mid * 2) data[i + 1] = mid;

            if (data[i] >= mid) {
                test.add(data[i]);
                if (i == data.length - 1) {
                    data[i] = data[i - 1] / 2;
                } else if (i == 0) {
                    data[i] = data[i + 1] / 2;
                } else {
                    data[i] = (data[i + 1] + data[i - 1]) / 2;
                }
            }

            if (data[i] > mid * 1.0) data[i] = (int) (mid * 1.0);

            if (mMaxDataPerBar < data[i]) mMaxDataPerBar = data[i];
        }
        return data;
    }

    void onStartTrackingTouch() {
        if (mMusicBarChangeListener != null) {
            mMusicBarChangeListener.onStartTrackingTouch(this);
        }
    }

    void onStopTrackingTouch() {
        if (mMusicBarChangeListener != null) {
            mMusicBarChangeListener.onStopTrackingTouch(this);
        }
    }

    public void hide() {
        if (!isHide && mMaxBarHeight != 0) {
            isAnimated = true;
            initAnimator(0, mMaxBarHeight, ANIMATION_TYPE_HIDE);
        }
    }

    public void show() {
        if (!isShow && mMaxBarHeight != 0) {
            isAnimated = true;
            initAnimator(mMaxBarHeight, 0, ANIMATION_TYPE_SHOW);
        }
    }

    private void initAnimator(int start, int end, final int animationType) {
        if (mValueAnimator == null) {
            mValueAnimator = ValueAnimator.ofInt(start, end);
            mValueAnimator.setDuration(1000);
            mValueAnimator.addUpdateListener(this);

            mValueAnimator.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                    if (animationType == ANIMATION_TYPE_SHOW) {
                        if (mMusicBarAnimationChangeListener != null)
                            mMusicBarAnimationChangeListener.onShowAnimationStart();
                    } else {
                        if (mMusicBarAnimationChangeListener != null)
                            mMusicBarAnimationChangeListener.onHideAnimationStart();
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (animationType == ANIMATION_TYPE_SHOW) {
                        isShow = true;
                        isHide = false;
                        if (mMusicBarAnimationChangeListener != null)
                            mMusicBarAnimationChangeListener.onShowAnimationEnd();
                    } else {
                        isHide = true;
                        isShow = false;
                        if (mMusicBarAnimationChangeListener != null)
                            mMusicBarAnimationChangeListener.onHideAnimationEnd();
                    }

                    clearAnimator();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (animationType == ANIMATION_TYPE_SHOW) {
                        isShow = true;
                        isHide = false;
                        if (mMusicBarAnimationChangeListener != null)
                            mMusicBarAnimationChangeListener.onShowAnimationEnd();
                    } else {
                        isHide = true;
                        isShow = false;
                        if (mMusicBarAnimationChangeListener != null)
                            mMusicBarAnimationChangeListener.onHideAnimationEnd();
                    }
                    clearAnimator();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            mValueAnimator.start();
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mAnimatedValue = (int) animation.getAnimatedValue();
        invalidate();
    }

    private void clearAnimator() {
        if (mValueAnimator != null) {
            mValueAnimator.removeAllUpdateListeners();
            mValueAnimator.removeAllListeners();
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
    }

    public boolean isHide() {
        return isHide;
    }

    public boolean isShow() {
        return isShow;
    }

    public void seekTo(int position) {
        position = position / mBarDuration;
        if (position >= 0 && position <= (mTrackDurationInSec / 1)) {
            mSeekToPosition = position;
            if (mMusicBarChangeListener != null) {
                mMusicBarChangeListener.onProgressChanged(this, mSeekToPosition, false);
            }
            invalidate();
        }
    }

    public int getPosition() {
        return mSeekToPosition * mBarDuration;
    }

    public void setProgressChangeListener(OnMusicBarProgressChangeListener
                                                  musicBarChangeListener) {
        this.mMusicBarChangeListener = musicBarChangeListener;
    }

    public void setAnimationChangeListener(OnMusicBarAnimationChangeListener
                                                   musicBarAnimationChangeListener) {
        this.mMusicBarAnimationChangeListener = musicBarAnimationChangeListener;
    }

    public void removeAllListener() {
        this.mMusicBarAnimationChangeListener = null;
        this.mMusicBarChangeListener = null;
    }

    public void setLoadedBarColor(int color) {
        mLoadedPaint.setColor(color);
    }

    public void setBackgroundBarColor(int color) {
        mBackgroundPaint.setColor(color);
    }

    public void setSpaceBetweenBar(int spaceBetweenBar) {
        this.mSpaceBetweenBar = spaceBetweenBar;
    }

    public void setBarWidth(float barWidth) {
        this.mBarWidth = barWidth;
    }
}
