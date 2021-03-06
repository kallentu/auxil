package com.auxil.auxil.map;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/** Customizes the info window in {@link FoodBankMapActivity}. */
public class MapWrapperLayout extends RelativeLayout {
    private GoogleMap map;
    private Marker marker;
    private View infoWindow;

    public MapWrapperLayout(Context context) {
        super(context);
    }

    public MapWrapperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapWrapperLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /** Must be called before performing any other actions */
    public void initializeMap(GoogleMap map) {
        this.map = map;
    }

    /** Called from infoWindowAdapter.getInfoContents */
    public void setMarkerWithInfoWindow(Marker marker, View infoWindow) {
        this.marker = marker;
        this.infoWindow = infoWindow;
    }

    /** Registering the info window touch event */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean ret = false;
        if (marker != null && marker.isInfoWindowShown() && map != null && infoWindow != null) {
            Point markerPosition = map.getProjection().toScreenLocation(marker.getPosition());
            MotionEvent copyEv = MotionEvent.obtain(ev);

            // Adjusting location so it is relative to infoWindow left top corner
            copyEv.offsetLocation(
                    -markerPosition.x + (infoWindow.getWidth() / 2),
                    -markerPosition.y + (infoWindow.getHeight()));

            ret = infoWindow.dispatchTouchEvent(copyEv);
        }

        // If the infoWindow consumed the touch event, then just return true.
        // Otherwise pass this event to the super class and return it's result
        return ret || super.dispatchTouchEvent(ev);
    }
}
