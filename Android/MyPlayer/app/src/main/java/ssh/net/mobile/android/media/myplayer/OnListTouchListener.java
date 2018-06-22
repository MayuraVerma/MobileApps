package ssh.net.mobile.android.media.myplayer;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by sudhesha on 5/30/2015.
 */
public class OnListTouchListener implements View.OnTouchListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private final GestureDetector gestureDetector;
    private ArrayList<TouchListener> touchListeners = new ArrayList<TouchListener>();
    private ArrayList<TapListener> listeners = new ArrayList<TapListener>();

    public OnListTouchListener(Context ctx) {
        gestureDetector = new GestureDetector(ctx, this);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        for (TapListener l : listeners) {
            l.onTap(motionEvent);
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {


        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        boolean retVal = false;
        for (TouchListener l : touchListeners) {
            retVal = l.onItemTouch(view, motionEvent);
        }
        return gestureDetector.onTouchEvent(motionEvent);
    }

    public void addOnTouchListener(TouchListener l) {
        touchListeners.add(l);
    }

    public void removeOnTouchListener(TouchListener l) {
        touchListeners.remove(l);
    }

    public void addTapListener(TapListener l) {
        listeners.add(l);
    }

    public void removeTapListener(TapListener l) {
        listeners.remove(l);
    }

    public interface TouchListener {
        boolean onItemTouch(View view, MotionEvent motionEvent);
    }

    public interface TapListener {
        void onTap(MotionEvent event);
    }

}
