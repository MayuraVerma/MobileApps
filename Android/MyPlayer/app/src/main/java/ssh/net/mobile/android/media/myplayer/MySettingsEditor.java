package ssh.net.mobile.android.media.myplayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * Created by sudhesha on 5/30/2015.
 */
public class MySettingsEditor extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window w = getWindow();
        w.requestFeature(Window.FEATURE_LEFT_ICON);

        setContentView(R.layout.app_settings);

        Button closeBtn = (Button) this.findViewById(R.id.AppSettings_CloseBtn);
        //closeBtn.setTypeface(tf);
        closeBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();
            }

        });
    }
}
