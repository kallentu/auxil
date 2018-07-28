package com.auxil.auxil.mapmarker;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.google.android.gms.maps.model.Marker;

public abstract class OnInfoWindowTouchListener implements OnTouchListener {
    private final View view;
    private final Drawable drawableNormal;
    private final Drawable drawablePressed;
    private final Handler handler = new Handler();

    private Marker marker;
    private boolean isPressed = false;

    public OnInfoWindowTouchListener(View view,
                                     Drawable drawableNormal,
                                     Drawable drawablePressed) {
        this.view = view;
        this.drawableNormal = drawableNormal;
        this.drawablePressed = drawablePressed;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (0 <= event.getX() && event.getX() <= view.getWidth() &&
                0 <= event.getY() && event.getY() <= view.getHeight()) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    startPress();
                    break;
                case MotionEvent.ACTION_UP:
                    // Delay to show pressed state
                    handler.postDelayed(confirmClickRunnable, 150);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    endPress();
                    break;
                default:
                    break;
            }
        }
        else {
            // Press is released
            endPress();
        }

        return false;
    }

    private void startPress() {
        if (!isPressed) {
            isPressed = true;
            handler.removeCallbacks(confirmClickRunnable);
            view.setBackground(drawablePressed);
            if (marker != null) {
                marker.showInfoWindow();
            }
        }
    }

    private boolean endPress() {
        if (isPressed) {
            isPressed = false;
            handler.removeCallbacks(confirmClickRunnable);
            view.setBackground(drawableNormal);
            if (marker != null) {
                marker.showInfoWindow();
            }
            return true;
        }
        else {
            return false;
        }
    }

    private final Runnable confirmClickRunnable = new Runnable() {
        @Override
        public void run() {
            if (endPress()) {
                onClickConfirmed(view, marker);
            }
        }
    };

    /**
     * This is called after a successful click
     */
    protected abstract void onClickConfirmed(View v, Marker marker);
}
