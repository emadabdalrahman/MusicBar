package com.oze.music.musicbar;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MusicBar extends View {

    String TAG = "MusicBar";
    int[] mBarHeight;
    int mTrackDurationInMilliSec;
    int mMaxDataPerBar = 0;
    int mSpaceBetweenBar = 2;
    int mSeekToPosition = -1;
    int mMaxBarHeight = 0;
    int mMinBarHeight;
    int mMaxNumOfBar = 0;
    int mBarDuration = 1000;
    float mBarWidth = 2;
    float mPlaybackSpeed = 1.0f;
    boolean isNewLoad;
    boolean isHide = false;
    boolean isShow = true;
    boolean isAnimated;
    boolean isTracking = false;
    boolean isAutoProgress;
    Paint mLoadedBarPrimeColor;
    Paint mBackgroundBarPrimeColor;
    OnMusicBarProgressChangeListener mMusicBarChangeListener;
    OnMusicBarAnimationChangeListener mMusicBarAnimationChangeListener;
    int mAnimatedValue = 0;
    ValueAnimator mBarAnimator;
    ValueAnimator mProgressAnimator;
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
        loadAttribute(context, attrs);
    }

    public MusicBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        loadAttribute(context, attrs);
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
        mBackgroundBarPrimeColor = new Paint();
        this.mBackgroundBarPrimeColor.setColor(getResources().getColor(R.color.BackgroundBarPrimeColor));
        this.mBackgroundBarPrimeColor.setStrokeCap(Paint.Cap.SQUARE);
        this.mBackgroundBarPrimeColor.setStrokeWidth(mBarWidth);

        mLoadedBarPrimeColor = new Paint();
        this.mLoadedBarPrimeColor.setColor(getResources().getColor(R.color.LoadedBarPrimeColor));
        this.mLoadedBarPrimeColor.setStrokeCap(Paint.Cap.SQUARE);
        this.mLoadedBarPrimeColor.setStrokeWidth(mBarWidth);
    }

    private void loadAttribute(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MusicBar, 0, 0);

        try {

            mSpaceBetweenBar = typedArray.getInteger(R.styleable.MusicBar_spaceBetweenBar, 2);

            mBarWidth = typedArray.getFloat(R.styleable.MusicBar_barWidth, 2);
            mBarDuration = typedArray.getInt(R.styleable.MusicBar_barDuration, 1000);
            mLoadedBarPrimeColor.setStrokeWidth(mBarWidth);
            mBackgroundBarPrimeColor.setStrokeWidth(mBarWidth);

            mLoadedBarPrimeColor.setColor(typedArray.getColor(R.styleable.MusicBar_LoadedBarPrimeColor,
                    getResources().getColor(R.color.LoadedBarPrimeColor)));
            mBackgroundBarPrimeColor.setColor(typedArray.getColor(R.styleable.MusicBar_backgroundBarPrimeColor,
                    getResources().getColor(R.color.BackgroundBarPrimeColor)));

        } finally {
            typedArray.recycle();
        }
    }

    /**
     * load the music file from file InputStream and music duration in millisecond
     * @param stream music file InputStream
     * @param duration music duration in millisecond
     */
    public void loadFrom(InputStream stream, int duration) {
        this.mStream = stream;
        this.mTrackDurationInMilliSec = duration;
        try {
            this.mStreamLength = stream.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isNewLoad = true;
        mSeekToPosition = -1;
        isAutoProgress = false;
        invalidate();
    }

    /**
     * load the music file from file path and music duration in millisecond
     * @param pathname music file path
     * @param duration music duration in millisecond
     */
    public void loadFrom(String pathname, int duration) {
        File file = new File(pathname);
        try {
            InputStream stream = new FileInputStream(file);
            loadFrom(stream, duration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int[] getBitPerSec() {
        int[] data = getBitPer(mTrackDurationInMilliSec / (mBarDuration / 2));
        int[] dataPerSec = new int[mTrackDurationInMilliSec / mBarDuration];
        for (int i = 0; i < mTrackDurationInMilliSec / mBarDuration; i++) {
            dataPerSec[i] = (data[i * 2] + data[i * 2 + 1]) / 2;
        }
        return dataPerSec;
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
            isTracking = true;
        }
    }

    void onStopTrackingTouch() {
        if (mMusicBarChangeListener != null) {
            mMusicBarChangeListener.onStopTrackingTouch(this);
            isTracking = false;
            if (isAutoProgress) {
                initProgressAnimator(mPlaybackSpeed, getPosition(), mTrackDurationInMilliSec);
            }
        }
    }

    private void initBarAnimator(int start, int end, final int animationType) {
        if (mBarAnimator == null) {
            mBarAnimator = ValueAnimator.ofInt(start, end);
            mBarAnimator.setDuration(mBarDuration);
            mBarAnimator.addUpdateListener(mBarAnimatorUpdateListener);

            mBarAnimator.addListener(new Animator.AnimatorListener() {

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

                    clearBarAnimator();
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
                    clearBarAnimator();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            mBarAnimator.start();
        }
    }

    ValueAnimator.AnimatorUpdateListener mBarAnimatorUpdateListener= new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mAnimatedValue = (int) animation.getAnimatedValue();
            invalidate();
        }
    };

    private void clearBarAnimator() {
        if (mBarAnimator != null) {
            mBarAnimator.removeAllUpdateListeners();
            mBarAnimator.removeAllListeners();
            mBarAnimator.cancel();
            mBarAnimator = null;
        }
    }

    private void initProgressAnimator(float playbackSpeed, int progress, int max) {
        int timeToEnd = (int) ((max - progress) / playbackSpeed);
        if (timeToEnd > 0) {
            mProgressAnimator = ValueAnimator.ofInt(progress, max).setDuration(timeToEnd);
            mProgressAnimator.setInterpolator(new LinearInterpolator());
            mProgressAnimator.addUpdateListener(mProgressAnimatorUpdateListener);
            mProgressAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    clearProgressAnimator();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mProgressAnimator.start();
        }
    }

    ValueAnimator.AnimatorUpdateListener mProgressAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if (isTracking || !isAutoProgress) {
                clearProgressAnimator();
            } else {
                setAutoProgressPosition((int) animation.getAnimatedValue());
            }
        }
    };

    /**
     * update view position when auto progress work
     * @param position
     */
    private void setAutoProgressPosition(int position) {
        if (position >= 0 && position <= (mTrackDurationInMilliSec)) {
            if (mSeekToPosition != position / mBarDuration) {
                mSeekToPosition = position / mBarDuration;
                if (mMusicBarChangeListener != null) {
                    mMusicBarChangeListener.onProgressChanged(MusicBar.this, mSeekToPosition, false);
                }
                invalidate();
            }
        }
    }

    /**
     * clear auto progress animator
     */
    private void clearProgressAnimator() {
        if (mProgressAnimator != null) {
            mProgressAnimator.removeAllUpdateListeners();
            mProgressAnimator.removeAllListeners();
            mProgressAnimator.cancel();
            mProgressAnimator = null;
        }
    }

    /**
     * start auto play animation should be called after
     * loadFrom() and media player finished prepare
     *
     * if startAutoProgress() called before loadFrom()
     * it will throw exception because duration is 0
     *
     * @param playbackSpeed playback speed from media player default value 1.0F for MediaPlayer and ExoPlayer
     */
    public void startAutoProgress(float playbackSpeed) {
        if (mTrackDurationInMilliSec > 0) {
            this.isAutoProgress = true;
            this.mPlaybackSpeed = playbackSpeed;
            initProgressAnimator(playbackSpeed, 0, mTrackDurationInMilliSec);
        }else {
            try {
                throw new  Exception("track duration less than 0 note startAutoProgress() should be called after loadFrom()");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * stop auto progress animation
     */
    public void stopAutoProgress() {
        this.isAutoProgress = false;
        clearProgressAnimator();
    }

    /**
     * Is auto progress.
     *
     * @return the boolean
     */
    public boolean isAutoProgress() {
        return isAutoProgress;
    }

    /**
     * Start Hide Animation. if view is hide nothing will happened
     */
    public void hide() {
        if (!isHide && mMaxBarHeight != 0) {
            isAnimated = true;
            initBarAnimator(0, mMaxBarHeight, ANIMATION_TYPE_HIDE);
        }
    }

    /**
     * Start Show Animation. if view is Show nothing will happened
     */
    public void show() {
        if (!isShow && mMaxBarHeight != 0) {
            isAnimated = true;
            initBarAnimator(mMaxBarHeight, 0, ANIMATION_TYPE_SHOW);
        }
    }

    /**
     * Is hide .
     *
     * @return boolean true if hide
     */
    public boolean isHide() {
        return isHide;
    }

    /**
     * Is show .
     *
     * @return boolean  true if show
     */
    public boolean isShow() {
        return isShow;
    }

    /**
     * move music bar to specified position
     * should be between 0 and duration of sound file in millisecond
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
                if (isAutoProgress) {
                    clearProgressAnimator();
                    initProgressAnimator(mPlaybackSpeed, position, mTrackDurationInMilliSec);
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
     * Set Prime loaded bar color. Default Value #fb4c01
     *
     * @param color the color
     */
    public void setLoadedBarPrimeColor(int color) {
        mLoadedBarPrimeColor.setColor(color);
    }

    /**
     * Set Prime background bar color. Default Value #dfd6d6
     *
     * @param color the color
     */
    public void setBackgroundBarPrimeColor(int color) {
        mBackgroundBarPrimeColor.setColor(color);
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
     * Set bar width.
     * for FixedMusicBar Default Value 2
     * for ScrollableMusicBar Default Value 3
     * Recommend to make barWidth equal spaceBetweenBar
     *
     * @param barWidth the bar width
     */
    public void setBarWidth(float barWidth) {
        if (barWidth > 0) {
            this.mBarWidth = barWidth;
            this.mBackgroundBarPrimeColor.setStrokeWidth(barWidth);
            this.mLoadedBarPrimeColor.setStrokeWidth(barWidth);
        }
    }

    /**
     * Set bar duration in milliseconds.
     * Default Value 1000 (1s)
     *
     * @param barDuration the bar duration in milliseconds
     */
    public void setBarDuration(int barDuration) {
        if (barDuration > 0) {
            this.mBarDuration = barDuration;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeAllListener();
        if (mProgressAnimator != null) {
            clearProgressAnimator();
        }
        if (mBarAnimator != null) {
            clearBarAnimator();
        }

    }
}
