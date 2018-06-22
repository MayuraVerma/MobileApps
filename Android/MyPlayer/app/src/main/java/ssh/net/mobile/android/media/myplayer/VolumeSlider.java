package ssh.net.mobile.android.media.myplayer;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Created by sudhesha on 5/19/2015.
 */
public class VolumeSlider extends SeekBar {
    public VolumeSlider(Context context) {
        super(context);
    }

    public VolumeSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VolumeSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VolumeSlider(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
