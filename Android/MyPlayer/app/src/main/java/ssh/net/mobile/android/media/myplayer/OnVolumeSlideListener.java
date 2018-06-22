package ssh.net.mobile.android.media.myplayer;

import android.widget.SeekBar;

import java.util.ArrayList;

/**
 * Created by sudhesha on 5/20/2015.
 */
public class OnVolumeSlideListener implements SeekBar.OnSeekBarChangeListener {


    protected static int mVolumeLevel = 0;
    private ArrayList<VolumeChangeListener> listeners = new ArrayList<VolumeChangeListener>();

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        for (VolumeChangeListener l : listeners) {
            l.getVolumeLevel();
        }
        mVolumeLevel = seekBar.getProgress();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        for (VolumeChangeListener l : listeners) {
            l.getVolumeLevel();
        }
        mVolumeLevel = seekBar.getProgress();
    }

    public void addVolumeChangeListener(VolumeChangeListener l) {
        listeners.add(l);
    }

    public void removeVolumeChangeListener(VolumeChangeListener l) {
        listeners.remove(l);
    }

    public static interface VolumeChangeListener {
        int getVolumeLevel();
    }
}
