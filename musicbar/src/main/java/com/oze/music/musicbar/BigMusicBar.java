package com.oze.music.musicbar;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class BigMusicBar extends MusicBar implements ValueAnimator.AnimatorUpdateListener {

    public BigMusicBar(Context context) {
        super(context);
    }

    public BigMusicBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BigMusicBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (isNewLoad && mFile != null && mFile.length > 0) {
            mBarHeight = getBarHeight(fixData(getBitPerSec()));
            isNewLoad = false;
        }

        if (mMaxNumOfBar == 0)
            mMaxNumOfBar = (int) (getWidth() / (mBarWidth + mSpaceBetweenBar));

        if (mBarHeight != null && mBarHeight.length > 0) {

            int baseLine = getHeight() / 2;


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

                float startX;
                if (mSeekToPosition < mMaxNumOfBar / 2)
                    startX = (2 + (getWidth() / 2) - (mSeekToPosition * (mBarWidth + mSpaceBetweenBar))
                            + (i - (startBar)) * (mBarWidth + mSpaceBetweenBar));
                else
                    startX = 2 + (i - (startBar)) * (mBarWidth + mSpaceBetweenBar);


                float startY = baseLine + (height / 2);
                float top = startY - height;

                if (startX < getWidth() / 2) {
                    canvas.drawLine(startX, startY, startX, top, mLoadedPaint);
                } else {
                    canvas.drawLine(startX, startY, startX, top, mBackgroundPaint);
                }
            }
        }

        super.onDraw(canvas);
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
}
