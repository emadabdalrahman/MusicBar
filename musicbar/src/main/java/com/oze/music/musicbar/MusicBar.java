package com.oze.music.musicbar;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MusicBar extends View implements ValueAnimator.AnimatorUpdateListener {

    String TAG = "MusicBar";
    int[] mBarHeight;
    int mTrackDurationInSec;
    int mTrackDurationInMilliSec;
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
    InputStream mStream;
    int mStreamLength = 0;

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

    /**
     * A callback that notifies clients when the progress level has been changed.
     * This includes changes that were initiated by the user through a touch gesture
     * or changes that were initiated programmatically.
     */
    public interface OnMusicBarProgressChangeListener {


        /**
         * Notification that the progress level has changed.
         *
         * @param musicBar The MusicBar whose progress has changed
         * @param progress The current progress level.
         * @param fromUser True if the progress change was initiated by the user.
         */
        void onProgressChanged(MusicBar musicBar, int progress, boolean fromUser);

        /**
         * Notification that the user has started a touch gesture.
         *
         * @param musicBar The MusicBar in which the touch gesture began
         */
        void onStartTrackingTouch(MusicBar musicBar);

        /**
         * Notification that the user has finished a touch gesture.
         *
         * @param musicBar The MusicBar in which the touch gesture began
         */
        void onStopTrackingTouch(MusicBar musicBar);


    }

    /**
     * A callback that notifies the hide and the show animation states.
     * notifies when animation start or end
     */
    public interface OnMusicBarAnimationChangeListener {



        /**
         * Notification that Hide Animation Start
         */
        void onHideAnimationStart();

        /**
         * Notification that Hide Animation End
         */
        void onHideAnimationEnd();

        /**
         * Notification that Show Animation Start
         */
        void onShowAnimationStart();

        /**
         * Notification that Show Animation End
         */
        void onShowAnimationEnd();


    }

    private void init() {
        mBackgroundPaint = new Paint();
        this.mBackgroundPaint.setColor(Color.parseColor("#dfd6d6"));
        this.mBackgroundPaint.setStrokeCap(Paint.Cap.SQUARE);
        this.mBackgroundPaint.setStrokeWidth(mBarWidth);

        mLoadedPaint = new Paint();
        this.mLoadedPaint.setColor(Color.RED);
        this.mLoadedPaint.setStrokeCap(Paint.Cap.SQUARE);
        this.mLoadedPaint.setStrokeWidth(mBarWidth);
    }

    public void loadFrom(InputStream stream, int duration) {
        this.mStream = stream;
        this.mTrackDurationInMilliSec = duration;
        this.mTrackDurationInSec = duration / 1000;
        try {
            this.mStreamLength = stream.available();
//            this.mActualBitRate = stream.available() / duration;
        } catch (IOException e) {
            e.printStackTrace();
        }
        isNewLoad = true;
        mSeekToPosition = -1;
        invalidate();
    }

    public void loadFrom(String pathname, int duration) {
        File file = new File(pathname);
        try {
            InputStream stream = new FileInputStream(file);
            loadFrom(stream,duration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int[] getBitPerSec() {
        int[] data = getBitPer(mTrackDurationInSec*2);
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
        try {
            int bitRate = mStream.available() / (numOfBar);
            byte[] buffer = new byte[bitRate];
            for (int i = 0; i < data.length; i++) {
                int temp = 0;
                mStream.read(buffer, 0, bitRate);
                for (int j = 0; j < buffer.length; j++) {
                    temp = temp + buffer[j];
                }
                data[i] = Math.abs(temp);
            }
            mStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
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

    /**
     * Start Hide Animation. if view is hide nothing will happened
     */
    public void hide() {
        if (!isHide && mMaxBarHeight != 0) {
            isAnimated = true;
            initAnimator(0, mMaxBarHeight, ANIMATION_TYPE_HIDE);
        }
    }

    /**
     * Start Show Animation. if view is Show nothing will happened
     */
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

    /**
     * Is hide boolean.
     *
     * @return boolean true if hide
     */
    public boolean isHide() {
        return isHide;
    }

    /**
     * Is show boolean.
     *
     * @return boolean  true if show
     */
    public boolean isShow() {
        return isShow;
    }

    /**
     * move music bar to specified position
     *
     * @param position time in millisecond
     */
    public void setProgress(int position) {
        if (position >= 0 && position <= (mTrackDurationInMilliSec)) {
            if (mSeekToPosition != position / mBarDuration) {
                mSeekToPosition = position / mBarDuration;
                Log.i(TAG, "setProgress: " + mSeekToPosition);
                if (mMusicBarChangeListener != null) {
                    mMusicBarChangeListener.onProgressChanged(this, mSeekToPosition, false);
                }
                invalidate();
            }
        }
    }

    /**
     * Get current position.
     *
     * @return the position time in millisecond
     */
    public int getPosition() {
        return mSeekToPosition * mBarDuration;
    }

    /**
     * Set a listener to receive notifications of changes to the MusicBar's progress level.
     * Also provides notifications of when the user starts and stops a touch gesture within the SeekBar..
     *
     * @param musicBarChangeListener the music bar Progress change notification listener
     */
    public void setProgressChangeListener(OnMusicBarProgressChangeListener
                                                  musicBarChangeListener) {
        this.mMusicBarChangeListener = musicBarChangeListener;
    }

    /**
     * Set a listener to receive notifications about MusicBar's animation state.
     *
     * @param musicBarAnimationChangeListener the music bar animation change notification listener
     */
    public void setAnimationChangeListener(OnMusicBarAnimationChangeListener
                                                   musicBarAnimationChangeListener) {
        this.mMusicBarAnimationChangeListener = musicBarAnimationChangeListener;
    }

    /**
     * Remove AnimationChangeListener and ProgressChangeListener
     */
    public void removeAllListener() {
        this.mMusicBarAnimationChangeListener = null;
        this.mMusicBarChangeListener = null;
    }

    /**
     * Set loaded bar color. Default Value Color.RED
     *
     * @param color the color
     */
    public void setLoadedBarColor(int color) {
        mLoadedPaint.setColor(color);
    }

    /**
     * Set background bar color. Default Value #dfd6d6
     *
     * @param color the color
     */
    public void setBackgroundBarColor(int color) {
        mBackgroundPaint.setColor(color);
    }

    /**
     * Set space between bar. Default Value 2
     * Recommend to make spaceBetweenBar equal barWidth
     *
     * @param spaceBetweenBar the space between bar
     */
    public void setSpaceBetweenBar(int spaceBetweenBar) {
        if (spaceBetweenBar > 0) {
            this.mSpaceBetweenBar = spaceBetweenBar;
        }
    }

    /**
     * Set bar width. Default Value 2
     * Recommend to make barWidth equal spaceBetweenBar
     *
     * @param barWidth the bar width
     */
    public void setBarWidth(float barWidth) {
        if (barWidth > 0) {
            this.mBarWidth = barWidth;
            this.mBackgroundPaint.setStrokeWidth(barWidth);
            this.mLoadedPaint.setStrokeWidth(barWidth);
        }
    }
}
