package ssh.net.mobile.android.media.myplayer;

import android.content.res.AssetManager;
import android.content.res.Resources;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Util {

    protected static final int SWIPE_UP = 1;
    protected static final int SWIPE_DOWN = 2;
    protected static final int SWIPE_LEFT = 3;
    protected static final int SWIPE_RIGHT = 4;
    protected static final int RESULT_PLAY = 307;
    private final static String fileName = "listconfig.xml";
    public static int listPosition = 0;
    public static String filesList[];
    public static String appPropertiesFile = "myplayer.mp";
    public static String appExtDir = "/sdcard1";
    public static Properties prop;
    public static float swipeLength = 0;
    public static int layout = -1;
    protected static DecimalFormat timeFormat = new DecimalFormat("00");
    private static AssetManager assets;
    private static String playlistpath = "listconfig.xml";
    private static Map<Integer, String> menuMap = new HashMap<>();
    private static int LAST_ACTION = 0;

    public static int amplify = 0;
    public static boolean shuffle = false;
    public static boolean preview = false;


    public static String getAppPropertiesFile() {
        return appPropertiesFile;
    }

    public static void setAppPropertiesFile(String appPropertiesFile) {
        Util.appPropertiesFile = appPropertiesFile;
    }

    public static String getAppExtDir() {
        return appExtDir;
    }

    public static void setAppExtDir(String appExtDir) {
        Util.appExtDir = appExtDir;
    }

    public static Properties getProp() {
        return prop;
    }

    public static void setProp() {
        Util.prop = new Properties();
    }

    public static String getPlaylistfile() {
        return fileName;
    }

    public static String getPlaylistpath() {
        return playlistpath;
    }

    public static void setPlaylistpath(String fullpath) {
        playlistpath = fullpath;
    }

    public static Map<Integer, String> getMenuMap() {
        return menuMap;
    }

    public static void setMenuMap(Map<Integer, String> menuMap) {
        Util.menuMap = menuMap;
    }

    public static String getMenuName(Integer name) {
        return menuMap.get(name);
    }

    public static int getLastAction() {
        return LAST_ACTION;
    }

    public static void setLastAction(int lastAction) {
        LAST_ACTION = lastAction;
    }

    public static String replace32L(String s) {
        String retaVal = null;
        char c[] = s.toCharArray();
        StringBuilder ret = new StringBuilder();
        int j = 0;
        boolean isbspace = true;

        for (int i = 0; i < s.length(); i++) {
            if (!((c[i] == 32) && isbspace)) {
                if (c[i] != 0) {
                    ret.append(c[i]);
                    isbspace = false;
                }
            }
        }
        //ret[j]='\0';
        retaVal = ret.toString();
        return retaVal;
    }

    public static String getTimeFromMilliSeconds(int milliSeconds) {
        double secO = 0;
        Double sec = null;
        Double minute = null;
        Double hour = null;
        Double day = null;

        secO = milliSeconds / 1000;
        sec = (Double) (Math.floor((milliSeconds / 1000) % 60));
        minute = (Double) (Math.floor(secO / 60));
        hour = (Double) (Math.floor(secO / 3600));
        day = (Double) (Math.floor(secO / (3600 * 24)));

        return (day >= 1 ? day + ":" : "") + "" + (hour >= 1 ? String.format("%2.0f", hour) + ":" : "") + "" + (minute >= 1 ? timeFormat.format(minute) : "00") + ":" + (sec >= 1 ? timeFormat.format(sec) : "00");
    }

    public static AssetManager getAssets() {
        return assets;
    }

    public static void setAssets(AssetManager assets) {
        Util.assets = assets;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
