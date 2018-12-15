package com.oze.music.musicbar;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class ScrollableMusicBar extends MusicBar implements ValueAnimator.AnimatorUpdateListener {

    private float startY;
    private float stopY;
    private float startX;
    private boolean isDivided;
    private float mDividerSize = 4;
    private Paint mDarkLoadedPaint;
    private Paint mDarkBackgroundPaint;
    private int topBar;
    private int bottomBar;


    public ScrollableMusicBar(Context context) {
        super(context);
        init();
    }

    public ScrollableMusicBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        loadAttribute(context,attrs);
    }

    public ScrollableMusicBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        loadAttribute(context,attrs);
    }

    private void init() {
        mDarkBackgroundPaint = new Paint();
        this.mDarkBackgroundPaint.setColor(getResources().getColor(R.color.DarkBackgroundBarColor));
        this.mDarkBackgroundPaint.setStrokeCap(Paint.Cap.SQUARE);
        this.mDarkBackgroundPaint.setStrokeWidth(mBarWidth);

        mDarkLoadedPaint = new Paint();
        this.mDarkLoadedPaint.setColor(getResources().getColor(R.color.DarkLoadedBarColor));
        this.mDarkLoadedPaint.setStrokeCap(Paint.Cap.SQUARE);
        this.mDarkLoadedPaint.setStrokeWidth(mBarWidth);
    }

    private void loadAttribute(Context context,AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MusicBar, 0, 0);

        try {

            mDarkLoadedPaint.setColor(typedArray.getColor(R.styleable.MusicBar_darkLoadedBarColor,
                    getResources().getColor(R.color.DarkLoadedBarColor)));
            mDarkBackgroundPaint.setColor(typedArray.getColor(R.styleable.MusicBar_darkBackgroundBarColor,
                    getResources().getColor(R.color.DarkBackgroundBarColor)));
            isDivided = typedArray.getBoolean(R.styleable.MusicBar_divided,false);
            mDividerSize = typedArray.getFloat(R.styleable.MusicBar_dividerSize,4);

        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (isNewLoad && mStream != null && mStreamLength > 0) {
            mBarHeight = getBarHeight(fixData(getBitPerSec()));
            isNewLoad = false;
        }

        if (mMaxNumOfBar == 0)
            mMaxNumOfBar = (int) (getWidth() / (mBarWidth + mSpaceBetweenBar));

        if (mBarHeight != null && mBarHeight.length > 0) {

            int baseLine = getBaseLine();

            int startBar = mSeekToPosition - (mMaxNumOfBar / 2);
            if (startBar < 0) startBar = 0;
            int endBar = startBar + mMaxNumOfBar;
            if (endBar > mBarHeight.length) endBar = mBarHeight.length;


            for (int i = startBar; i < endBar; i++) {
                int height = mBarHeight[i];

                if (isAnimated) {
                    height = height - mAnimatedValue;
                    if (height <= 0) height = mMinBarHeight;
                }

                if (mSeekToPosition < mMaxNumOfBar / 2)
                    startX = (2 + (getWidth() / 2) - (mSeekToPosition * (mBarWidth + mSpaceBetweenBar))
                            + (i - (startBar)) * (mBarWidth + mSpaceBetweenBar));
                else
                    startX = 2 + (i - (startBar)) * (mBarWidth + mSpaceBetweenBar);

                if (isDivided) {
                    drawDividedBar(canvas, height, baseLine);
                } else {
                    drawStraightBar(canvas, height, baseLine);
                }
            }
        }

        super.onDraw(canvas);
    }

    private void drawDividedBar(Canvas canvas, int height, int baseLine) {

        bottomBar = (height / 8) * 3;
        if (bottomBar < mMinBarHeight) bottomBar = mMinBarHeight;
        startY = baseLine + (mDividerSize / 2) + bottomBar;

        topBar = (height / 8) * 5;
        if (topBar < mMinBarHeight) topBar = mMinBarHeight;
        stopY = baseLine - (mDividerSize / 2) - topBar;

        if (startX < getWidth() / 2) {
            canvas.drawLine(startX, startY, startX, baseLine + (mDividerSize / 2), mDarkLoadedPaint);
            canvas.drawLine(startX, baseLine - (mDividerSize / 2), startX, stopY, mLoadedPaint);
        } else {
            canvas.drawLine(startX, startY, startX, baseLine + (mDividerSize / 2), mDarkBackgroundPaint);
            canvas.drawLine(startX, baseLine - (mDividerSize / 2), startX, stopY, mBackgroundPaint);
        }
    }

    private void drawStraightBar(Canvas canvas, int height, int baseLine) {
        startY = baseLine + (height / 2);
        stopY = startY - height;
        if (startX < getWidth() / 2) {
            canvas.drawLine(startX, startY, startX, stopY, mLoadedPaint);
        } else {
            canvas.drawLine(startX, startY, startX, stopY, mBackgroundPaint);
        }
    }

    private int getBaseLine() {
        if (isDivided) {
            return (getHeight() / 8) * 5;
        } else {
            return (getHeight() / 2);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onStartTrackingTouch();
                setPressed(true);
                mFirstTouchX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                setPressed(true);
                updateView(event, mFirstTouchX);
                mFirstTouchX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                onStopTrackingTouch();
                this.getParent().requestDisallowInterceptTouchEvent(false);
                setPressed(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                onStopTrackingTouch();
                this.getParent().requestDisallowInterceptTouchEvent(false);
                setPressed(false);
                break;
        }

        return true;
    }

    private void updateView(MotionEvent event, float firstTouchX) {

        Log.i(TAG, "updateView: " + mSeekToPosition);

        float secondTouch = event.getX();
        int distance = (int) ((secondTouch - firstTouchX) / (mBarWidth + mSpaceBetweenBar));

        mSeekToPosition = mSeekToPosition - distance;
        if (mSeekToPosition < 0) {
            mSeekToPosition = 0;
        } else if (mSeekToPosition > mBarHeight.length) {
            mSeekToPosition = mBarHeight.length;
        } else {
            if (mMusicBarChangeListener != null)
                mMusicBarChangeListener.onProgressChanged(this, mSeekToPosition, true);
        }
        invalidate();
    }

    public void setDivided(boolean divided) {
        this.isDivided = divided;
    }

    public void setDividerSize(float size) {
        this.mDividerSize = size;
    }

    public void setDarkLoadedColor(int color) {
        mDarkLoadedPaint.setColor(color);
    }

    public void setDarkBackgroundColor(int color) {
        mDarkBackgroundPaint.setColor(color);
    }
}
