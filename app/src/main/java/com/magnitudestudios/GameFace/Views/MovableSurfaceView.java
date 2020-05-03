package com.magnitudestudios.GameFace.Views;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import org.webrtc.SurfaceViewRenderer;

public class MovableSurfaceView extends SurfaceViewRenderer {
    float dX, dY;
    float maxwidth, maxheight;

    boolean stateConnected = false;
    boolean stateFull = true;

    public MovableSurfaceView(Context context) {
        super(context);

    }

    public MovableSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void getParentDimens() {
        maxwidth = ((View) getParent()).getWidth();
        maxheight = ((View) getParent()).getHeight();
        Log.e("INIT", "initView: "+ maxwidth + " " + maxheight );
    }

    public void setCalling() {
        setSmallScreen();
        stateConnected = true;
    }

    public void setLocal() {
        setFullScreen();
        stateConnected = false;
    }

    private void setSmallScreen() {
        setDimensionsSmallScreen();
        stateFull = false;
    }

    private void setFullScreen() {
        reposition(null, 0,0);
        setDimensionsFullScreen();
        stateFull = true;
    }

    private void setDimensionsSmallScreen() {
        getParentDimens();
        scaleAnimator(getWidth(), maxwidth/3, getHeight(), maxheight/3);
    }

    private void setDimensionsFullScreen() {
        getParentDimens();
        scaleAnimator(getWidth(), maxwidth, getHeight(), maxheight);
    }

    private void scaleAnimator(float startX, float endX, float startY, float endY) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofInt("x", Math.round(startX), Math.round(endX));
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofInt("y", Math.round(startY), Math.round(endY));
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY);
        animator.addUpdateListener(animation -> {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = (int) animation.getAnimatedValue("x");
            layoutParams.height = (int) animation.getAnimatedValue("y");
            setLayoutParams(layoutParams);
        });
        animator.setDuration(300);
        animator.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        super.surfaceChanged(holder, format, width, height);
        Log.e("LOCAL", "surfaceChanged: " + width + " " + height);
    }

    private static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParentDimens();
                dX = this.getX() - event.getRawX();
                dY = this.getY() - event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                this.animate()
                    .translationX(Math.max(0, Math.min(event.getRawX() + dX, maxwidth - getWidth())))
                    .translationY(Math.max(0, Math.min(event.getRawY() + dY, maxheight - getHeight())))
                    .setDuration(0)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
                break;

            case MotionEvent.ACTION_UP:
                Log.e("Event", "onTouchEvent: " + (event.getEventTime() - event.getDownTime()));
                if (event.getEventTime() - event.getDownTime() < 100 && stateConnected
                        && closeEnough(this.getX() - event.getRawX(), dX) && closeEnough(this.getY() - event.getRawY(), dY)) {
                    if (stateFull) {
                        setSmallScreen();
                    } else {
                        setFullScreen();
                    }
                } else {
                    reposition(event);
                }
                break;
            default:
                return false;
        }
        return true;
    }

    private boolean closeEnough(float n1, float n2) {
        return Math.abs(n1-n2) < 10;
    }

    private void reposition(MotionEvent event, float... args) {
        float positionx = 0.0f, positiony = 0.0f;
        if (event != null) {
            positionx = event.getRawX() + dX;
            positiony = event.getRawY() + dY;
        }
        float toX;
        float toY;

        float horizontal_boundary = maxwidth/2 - getWidth()/2.0f;
        float vertical_boundary = maxheight/2 - getHeight()/2.0f;

        //Top-Left
        if (args != null && args.length == 2) {
            toX = args[0];
            toY = args[1];
        }
        else if (positionx <= horizontal_boundary && positiony <= vertical_boundary) {
            toX = 0;
            toY = 0;
        }
        //Bottom-Left
        else if (positionx <= horizontal_boundary && positiony >= vertical_boundary) {
            toX = 0;
            toY = maxheight - getHeight();
        }
        //Top-Right
        else if (positionx >= horizontal_boundary && positiony <= vertical_boundary) {
            toX = maxwidth - getWidth();
            toY = 0;
        }
        //Bottom-Right
        else {
            toX = maxwidth - getWidth();
            toY = maxheight - getHeight();
        }
        this.animate()
                .translationX(toX)
                .translationY(toY)
                .setDuration(400)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }
}
