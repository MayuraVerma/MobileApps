package ssh.net.mobile.android.media.myplayer;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by sudhesha on 5/30/2015.
 */

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window w = getWindow();
        w.requestFeature(Window.FEATURE_LEFT_ICON);

        setContentView(R.layout.activity_about);

        w.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_info);


        TextView versionText = (TextView) this.findViewById(R.id.AboutActivity_VersionText);
        //versionText.setTypeface(tf);
        versionText.setText(this.getString(R.string.AboutActivity_VersionText) + " " + getVersion());

        TextView licenseText = (TextView) this.findViewById(R.id.AboutActivity_LicenseText);
        //licenseText.setTypeface(tf);
        licenseText.setText(this.getString(R.string.AboutActivity_LicenseText) + " " + this.getString(R.string.AboutActivity_LicenseTextValue));

        TextView urlText = (TextView) this.findViewById(R.id.AboutActivity_UrlText);
        //urlText.setTypeface(tf);
        urlText.setText(this.getString(R.string.AboutActivity_UrlTextValue));

        Button closeBtn = (Button) this.findViewById(R.id.AboutActivity_CloseBtn);
        //closeBtn.setTypeface(tf);
        closeBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();
            }

        });
}

    private String getVersion() {
        String result = "";
        try {

            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);

            result = info.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            Log.w(AboutActivity.class.toString(), "Unable to get application version: " + e.getMessage());
            result = "Unable to get application version.";
        }

        return result;
    }

}
