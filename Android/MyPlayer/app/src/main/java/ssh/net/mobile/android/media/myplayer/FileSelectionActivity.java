package ssh.net.mobile.android.media.myplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.commonsware.cwac.merge.MergeAdapter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class FileSelectionActivity extends Activity implements PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "FileSelection";
    private static final String FILES_TO_UPLOAD = "upload";
    protected String lastItemLongClicked = null;
    File mainPath = new File(Environment.getExternalStorageDirectory() + "");
    Button ok, all, cancel, storage, New;
    TextView path;
    Boolean Switch = false;
    Boolean switcher = false;
    String primary_sd;
    String secondary_sd;
    int index = 0;
    int top = 0;
    private ArrayList<File> resultFileList;
    private ListView directoryView;
    private ArrayList<File> directoryList = new ArrayList<File>();
    private ArrayList<String> directoryNames = new ArrayList<String>();
    //private ListView fileView;
    private ArrayList<File> fileList = new ArrayList<File>();
    private ArrayList<String> fileNames = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_selection);
        //getActionBar().setDisplayHomeAsUpEnabled(true);


        directoryView = (ListView) findViewById(R.id.directorySelectionList);
        ok = (Button) findViewById(R.id.ok);
        all = (Button) findViewById(R.id.all);
        cancel = (Button) findViewById(R.id.cancel);
        storage = (Button) findViewById(R.id.storage);
        New = (Button) findViewById(R.id.New);
        path = (TextView) findViewById(R.id.folderpath);


        loadLists();
        New.setEnabled(false);


        ExtStorageSearch();
        if (secondary_sd == null) {
            storage.setEnabled(false);
        }


        directoryView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                View v = directoryView.getChildAt(0);
                lastItemLongClicked = fileList.get(i).getAbsolutePath();
                showFilePopupMenu();
                return true;
            }
        });
        directoryView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                index = directoryView.getFirstVisiblePosition();
                View v = directoryView.getChildAt(0);
                top = (v == null) ? 0 : v.getTop();

                File lastPath = mainPath;
                try {
                    if (position < directoryList.size()) {
                        mainPath = directoryList.get(position);
                        loadLists();
                    }
                } catch (Throwable e) {
                    mainPath = lastPath;
                    loadLists();
                }

            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ok();
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        storage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (!switcher) {
                        mainPath = new File(secondary_sd);
                        loadLists();
                        switcher = true;
                        storage.setText(getString(R.string.Int));
                    } else {
                        mainPath = new File(primary_sd);
                        loadLists();
                        switcher = false;
                        storage.setText(getString(R.string.ext));
                    }
                } catch (Throwable e) {

                }
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!Switch) {
                    for (int i = directoryList.size(); i < directoryView.getCount(); i++) {
                        directoryView.setItemChecked(i, true);
                    }
                    all.setText(getString(R.string.none));
                    Switch = true;
                } else if (Switch) {
                    for (int i = directoryList.size(); i < directoryView.getCount(); i++) {
                        directoryView.setItemChecked(i, false);
                    }
                    all.setText(getString(R.string.all));
                    Switch = false;
                }
            }

        });
    }

    //protected String mfullScreenOption = "FullScreen";
    private void showFilePopupMenu() {
        PopupMenu popup = new PopupMenu(getBaseContext(), findViewById(R.id.screenfilepopup));
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_file_popup, popup.getMenu());
        //popup.getMenu().getItem(0).setTitle(mfullScreenOption);
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    public void onBackPressed() {
        try {
            if (mainPath.equals(Environment.getExternalStorageDirectory().getParentFile().getParentFile())) {
                finish();
            } else {
                File parent = mainPath.getParentFile();
                mainPath = parent;
                loadLists();
                directoryView.setSelectionFromTop(index, top);
            }

        } catch (Throwable e) {

        }
    }

    public void ok() {
        Log.d(TAG, "Upload clicked, finishing activity");


        resultFileList = new ArrayList<File>();

        for (int i = 0; i < directoryView.getCount(); i++) {
            if (directoryView.isItemChecked(i)) {
                resultFileList.add(fileList.get(i - directoryList.size()));
            }
        }
        if (resultFileList.isEmpty()) {
            Log.d(TAG, "Nada seleccionado");
            finish();
        }
        Log.d(TAG, "Files: " + resultFileList.toString());
        Intent result = this.getIntent();
        result.putExtra(FILES_TO_UPLOAD, resultFileList);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    private void loadLists() {
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        };
        FileFilter directoryFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };

        //if(mainPath.exists() && mainPath.length()>0){
        //Lista de directorios
        File[] tempDirectoryList = mainPath.listFiles(directoryFilter);

        if (tempDirectoryList != null && tempDirectoryList.length > 1) {
            Arrays.sort(tempDirectoryList, new Comparator<File>() {
                @Override
                public int compare(File object1, File object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });
        }

        directoryList = new ArrayList<File>();
        directoryNames = new ArrayList<String>();
        for (File file : tempDirectoryList) {
            directoryList.add(file);
            directoryNames.add(file.getName());
        }
        ArrayAdapter<String> directoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, directoryNames);


        //Lista de ficheros
        File[] tempFileList = mainPath.listFiles(fileFilter);

        if (tempFileList != null && tempFileList.length > 1) {
            Arrays.sort(tempFileList, new Comparator<File>() {
                @Override
                public int compare(File object1, File object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });
        }

        fileList = new ArrayList<File>();
        fileNames = new ArrayList<String>();
        for (File file : tempFileList) {
            fileList.add(file);
            fileNames.add(file.getName());
        }


        path.setText(mainPath.toString());
        iconload();
        setTitle(mainPath.getName());
        //}
    }

    /**
     * @Override public boolean onCreateOptionsMenu(Menu menu) {
     * getMenuInflater().inflate(R.menu.activity_file_selection, menu);
     * return true;
     * }
     * @Override public boolean onOptionsItemSelected(MenuItem item) {
     * switch (item.getItemId()) {
     * case android.R.id.home:
     * NavUtils.navigateUpFromSameTask(this);
     * return true;
     * }
     * return super.onOptionsItemSelected(item);
     * }*
     */

    public void iconload() {
        String[] foldernames = new String[directoryNames.size()];
        foldernames = directoryNames.toArray(foldernames);

        String[] filenames = new String[fileNames.size()];
        filenames = fileNames.toArray(filenames);

        CustomListSingleOnly adapter1 = new CustomListSingleOnly(FileSelectionActivity.this, directoryNames.toArray(foldernames), mainPath.getPath());
        CustomList adapter2 = new CustomList(FileSelectionActivity.this, fileNames.toArray(filenames), mainPath.getPath());


        MergeAdapter adap = new MergeAdapter();

        adap.addAdapter(adapter1);
        adap.addAdapter(adapter2);


        directoryView.setAdapter(adap);
    }

    public void ExtStorageSearch() {
        String[] extStorlocs = {"/storage/sdcard1", "/storage/extsdcard", "/storage/sdcard0/external_sdcard", "/mnt/extsdcard",
                "/mnt/sdcard/external_sd", "/mnt/external_sd", "/mnt/media_rw/sdcard1", "/removable/microsd", "/mnt/emmc",
                "/storage/external_SD", "/storage/ext_sd", "/storage/removable/sdcard1", "/data/sdext", "/data/sdext2",
                "/data/sdext3", "/data/sdext4", "/storage/sdcard0"};

        //First Attempt
        primary_sd = System.getenv("EXTERNAL_STORAGE");
        secondary_sd = System.getenv("SECONDARY_STORAGE");


        if (primary_sd == null) {
            primary_sd = Environment.getExternalStorageDirectory() + "";
        }
        if (secondary_sd == null) {//if fail, search among known list of extStorage Locations
            for (String string : extStorlocs) {
                if ((new File(string)).exists() && (new File(string)).isDirectory()) {
                    secondary_sd = string;
                    break;
                }
            }
        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_delete:
                deleteFile(lastItemLongClicked);
                return true;
            case R.id.action_rename:
                return true;
            case R.id.action_play:
                playFile(lastItemLongClicked);
                break;
            default:
                return false;
        }
        return true;
    }

    public void playFile(String lastItemLongClicked) {
        Intent result = this.getIntent();
        Util.listPosition = 0;

        // String file = (String) data.getSerializableExtra(FILES_TO_UPLOAD); //file array list
        String[] files_paths = new String[1];

        files_paths[0] = lastItemLongClicked; //storing the selected file's paths to string array files_paths

        Util.filesList = null;
        Intent pIntent = new Intent("media.app.my.com.myplayer.Player");
        pIntent.putExtra("MEDIA_FILES", files_paths);
        startActivity(pIntent);
        finish();

    }
}
