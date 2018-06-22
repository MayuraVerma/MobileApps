package ssh.net.mobile.android.media.myplayer;

import java.util.ArrayList;
import java.util.List;


public class PlaylistObj {
    String name;
    List<String> fileNames;

    public PlaylistObj() {
        fileNames = new ArrayList<String>();
    }

    //getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    @Override
    public String toString() {
        return "PlaylistObj [name=" + name + ", files=" + fileNames + "]";
    }


}