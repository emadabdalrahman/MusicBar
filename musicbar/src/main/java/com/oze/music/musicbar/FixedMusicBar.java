package com.oze.music.musicbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class FixedMusicBar extends MusicBar {

    public FixedMusicBar(Context context) {
        super(context);
    }

    public FixedMusicBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedMusicBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mMaxNumOfBar == 0)
            mMaxNumOfBar = (int) ((getWidth() - getPaddingLeft() - getPaddingRight()) / (mBarWidth + mSpaceBetweenBar));


        if (isNewLoad && mStream != null && mStreamLength > 0) {
            mBarHeight = getBarHeight(fixData(getBitPer(mMaxNumOfBar)));
            isNewLoad = false;

            mBarDuration = ((mStreamLength / mMaxNumOfBar) * 1000) / (mStreamLength / mTrackDurationInSec);
        }

        if (mBarHeight != null && mBarHeight.length > 0) {

            int baseLine = getHeight() / 2;
            for (int i = 0; i < mBarHeight.length; i++) {
                int height = mBarHeight[i];

                if (isAnimated) {
                    height = height - mAnimatedValue;
                    if (height <= 0) height = mMinBarHeight;
                }

                float startX = getPaddingLeft() + i * (mBarWidth + mSpaceBetweenBar);
                float startY = baseLine + (height / 2);
                float top = startY - height;

                if (i <= mSeekToPosition) {
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
                updateView(event);
                setPressed(true);
                break;
            case MotionEvent.ACTION_MOVE:
                setPressed(true);
                updateView(event);
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

    private void updateView(MotionEvent event) {
        mSeekToPosition = (int) (event.getX() / (mBarWidth + mSpaceBetweenBar));
        Log.i(TAG, "updateView: " + mSeekToPosition);
        if (mSeekToPosition < 0) {
            mSeekToPosition = 0;
        } else if (mSeekToPosition > mMaxNumOfBar) {
            mSeekToPosition = mMaxNumOfBar;
        } else {
            invalidate();
            if (mMusicBarChangeListener != null)
                mMusicBarChangeListener.onProgressChanged(this, mSeekToPosition, true);
        }
    }
}
