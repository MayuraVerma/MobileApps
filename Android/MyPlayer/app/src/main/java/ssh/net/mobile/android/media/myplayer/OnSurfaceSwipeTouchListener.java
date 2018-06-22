package ssh.net.mobile.android.media.myplayer;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupMenu;

import java.util.ArrayList;

/**
 * Created by sudhesha on 5/16/2015.
 */
public class OnSurfaceSwipeTouchListener implements View.OnTouchListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private static final int SWIPE_DISTANCE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private static final int SWIPE_THRESHOLD = 50;
    private static int LAST_ACTION = 0;
    private final GestureDetector gestureDetector;
    private ArrayList<TapListener> listeners = new ArrayList<TapListener>();
    private ArrayList<SurfacePopUps> popUpListeners = new ArrayList<SurfacePopUps>();
    private ArrayList<SurfaceSwipe> swipeListeners = new ArrayList<SurfaceSwipe>();

    public OnSurfaceSwipeTouchListener(Context ctx) {
        gestureDetector = new GestureDetector(ctx, this);
    }

    public static int getLastAction() {
        return LAST_ACTION;
    }

    public static void setLastAction(int LAST_ACTION) {
        Util.setLastAction(LAST_ACTION);
        OnSurfaceSwipeTouchListener.LAST_ACTION = LAST_ACTION;
    }

    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
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
    public boolean onSingleTapUp(MotionEvent e) {
        for (TapListener l : listeners) {
            l.onTap(e);
        }
        return (true);
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        for (SurfacePopUps l : popUpListeners) {
            l.showSurfacePopUps();
        }
    }

    @Override
    public boolean onFling(MotionEvent me1, MotionEvent me2, float v1, float v2) {
        boolean result = false;
        try {
            float diffY = me2.getY() - me1.getY();
            float diffX = me2.getX() - me1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(v1) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        //result = onSwipeRight();
                        Util.setLastAction(Util.SWIPE_RIGHT);
                    } else {
                        //result = onSwipeLeft();
                        Util.setLastAction(Util.SWIPE_LEFT);
                    }
                }
            } else {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(v2) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        //result = onSwipeBottom();
                        Util.setLastAction(Util.SWIPE_DOWN);
                    } else {
                        //result = onSwipeTop();
                        Util.setLastAction(Util.SWIPE_UP);
                    }

                    Util.swipeLength = Math.abs(diffY / 100) * 3;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        for (SurfaceSwipe l : swipeListeners) {
            l.onScreenSwipe();
        }

        return result;
    }


    public void addTapListener(TapListener l) {
        listeners.add(l);
    }

    public void removeTapListener(TapListener l) {
        listeners.remove(l);
    }

    public void addPopUpListener(SurfacePopUps l) {
        popUpListeners.add(l);
    }

    public void removePopUpListener(SurfacePopUps l) {
        popUpListeners.remove(l);
    }


    public void addSwipeListeners(SurfaceSwipe l) {
        swipeListeners.add(l);
    }

    public void removeSwipeListeners(SurfaceSwipe l) {
        swipeListeners.remove(l);
    }

    public interface TapListener {
        void onTap(MotionEvent event);
    }

    public interface SurfacePopUps extends PopupMenu.OnMenuItemClickListener {
        void showSurfacePopUps();
    }

    public interface SurfaceSwipe {
        void onScreenSwipe();
    }
}
