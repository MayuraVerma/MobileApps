package ssh.net.mobile.android.media.myplayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Playlists {

    private final static Pattern LTRIM = Pattern.compile("^\\s+");
    private ArrayList<Object> Lists;
    private Map<String, ArrayList<String>> plist;

    public Playlists(ArrayList<Object> lists,
                     Map<String, ArrayList<String>> plist) {
        super();
        Lists = lists;
        this.plist = plist;
    }

    public Playlists() {
        Lists = new ArrayList<Object>();
        plist = new HashMap<>();
    }

    public static String ltrim(String s) {
        return LTRIM.matcher(s).replaceAll("");
    }

    public void setList() {

    }

    public Map<String, ArrayList<String>> getPlist() {
        return plist;
    }

    public void setPlist(Map<String, ArrayList<String>> plist) {
        this.plist = plist;
    }

    public Set<String> getListNames() {

        return plist.keySet();
    }

    public void add(String listName, String fileName) {
        ArrayList<String> tempList = new ArrayList<String>();
        String[] fileNames = fileName.split(",");
        try {
            tempList = plist.get(listName);
            plist.remove(listName);
        } catch (Exception ex) {

        } finally {
            if ((tempList == null ? tempList = new ArrayList<String>() : tempList != null) != null)
                for (String str : fileNames)
                    tempList.add(str);
            plist.put(listName, tempList);
        }
    }

    public void add(String listName, String[] files) {
        Collection filesCollection = new ArrayList<>();

        for (String str : files) {
            filesCollection.add(str);
        }
        add(listName, filesCollection);
    }

    public void add(String listName, ArrayList<String> files) {
        ArrayList<String> tempList = null;
        try {
            tempList = plist.get(listName);
            plist.remove(listName);
        } catch (Exception ex) {

        } finally {
            if ((tempList == null ? tempList = new ArrayList<String>() : tempList != null) != null)
                tempList.addAll(files);
            plist.put(listName, tempList);
        }
    }

    public void add(String listName, Collection<String> files) {
        ArrayList<String> tempList = null;
        try {
            tempList = plist.get(listName);
            plist.remove(listName);
        } catch (Exception ex) {

        } finally {
            if ((tempList == null ? tempList = new ArrayList<String>() : tempList != null) != null)
                tempList.addAll(files);
            plist.put(listName, tempList);
        }
    }

    public String[] getPlist(String listName) {

        ArrayList<String> list = plist.get(listName);

        String[] files = new String[list.size()];
        files = list.toArray(files);

        for (String s : files) {
            s = ltrim(s);
        }
        return files;
    }

    public boolean removeList(String listName) {
        boolean retVal = false;
        if (!plist.isEmpty()) {
            plist.remove(listName);
            retVal = true;
        }
        return retVal;

    }
}
