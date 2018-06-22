package ssh.net.mobile.android.media.myplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sudhesha on 5/30/2015.
 */
public class MySetting {

    File file = null;

    public MySetting() {
        initializeFile();
    }

    public void initializeFile() {
        try {
            this.file = new File(Util.getAppExtDir(), Util.getAppPropertiesFile());
        } catch (Exception e) {
            file = null;
        }
    }
    //Properties prop = new Properties();
    // OutputStream output = null;
    //InputStream input = null;

    public void readSettings() {
        InputStream input = null;
        try {
            if (file == null || !file.canRead()) {
                input = Util.getAssets().open(Util.getAppPropertiesFile());
            } else {
                input = new FileInputStream(file);
            }
            Util.prop.load(input);

            Util.amplify  = Util.prop.getProperty("amplify") != null ? Math.min(Integer.parseInt(Util.prop.getProperty("amplify")), 5) : 0;
            Util.preview  = Util.prop.getProperty("preview") != null ? Boolean.valueOf(Util.prop.getProperty("preview")) : false;
            Util.shuffle  = Util.prop.getProperty("shuffle") != null ? Boolean.valueOf(Util.prop.getProperty("shuffle")) : false;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void writeSettings(String name, String value) {
        try {
            file = new File(Util.getAppExtDir(), Util.getAppPropertiesFile());

            //initializeFile();

            OutputStream output = new FileOutputStream(file);
            // set the properties value
            Util.prop.setProperty(name, value);

            // save properties to project root folder
            Util.prop.store(output, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            readSettings();
        }
    }

    public void writeSettings(Map<String, String> properties) {
        Map mp;
        String key;
        try {
            if (file == null) {
                initializeFile();
            }
            OutputStream output = new FileOutputStream(file);
            // set the properties value
            Iterator<String> it = properties.keySet().iterator();
            while (it.hasNext()) {
                key = it.next();
                Util.prop.setProperty(key, properties.get(key));
            }

            // save properties to project root folder
            Util.prop.store(output, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
