package ssh.net.mobile.android.media.myplayer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements OnChildClickListener, PopupMenu.OnMenuItemClickListener {

    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;
    private static final String FILES_TO_UPLOAD = "upload";
    final Context context = this;
    public View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //fileToRemove = files[i];
            showPopUp();
        }
    };
    public View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            //fileToRemove = files[i];
            showPopUp();
            return true;
        }
    };
    protected String newPlayListName;
    protected String listName = null;
    protected ArrayList<String> filesInList = new ArrayList<String>();
    protected String fileToRemove = null;
    protected String files[];
    private OnListTouchListener.TapListener onListSingleClick = new OnListTouchListener.TapListener() {

        @Override
        public void onTap(MotionEvent event) {
            Intent pIntent = new Intent("media.app.my.com.myplayer.Player");
            pIntent.putExtra("MEDIA_FILES", files);
            startActivity(pIntent);
            finish();
        }
    };
    ImageView imageView;
    EditText input;
    boolean mListNew = false;
    String lastChildOnLongClick;
    ArrayList<String> groupItem = new ArrayList<String>();
    ArrayList<Object> childItem = new ArrayList<Object>();
    FileListPreviewAdapter mAdapter;
    ImageButton mEditButton = null;
    /*    private OnChildClickListener onListChildClick=new ExpandableListView.OnChildClickListener(){
            //Toast.makeText(MainActivity.this, "Time for an upgrade 2!", Toast.LENGTH_SHORT).show();
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {
                int oi;
                i = 0;
                return false;
            }
        };*/
    ListView mListView;
    BackgroundContainer mBackgroundContainer;
    boolean mSwiping = false;
    boolean mItemPressed = false;
    private OnListTouchListener.TouchListener onListTouch = new OnListTouchListener.TouchListener() {
        float mDownX;
        private int mSwipeSlop = -1;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public boolean onItemTouch(final View v, MotionEvent event) {
            if (mSwipeSlop < 0) {
                mSwipeSlop = ViewConfiguration.get(MainActivity.this).
                        getScaledTouchSlop();
            }

            Util.listPosition = mListView.getPositionForView(v);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mItemPressed) {
                        // Multi-item swipes not handled
                        return false;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1);
                    v.setTranslationX(0);
                    mItemPressed = false;
                    break;
                case MotionEvent.ACTION_MOVE: {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    boolean xRight = deltaX > 0 ? true : false;
                    if (!mSwiping) {
                        if (deltaXAbs > mSwipeSlop) {
                            mSwiping = true;
                            mListView.requestDisallowInterceptTouchEvent(true);
                            if (!xRight) {
                                mBackgroundContainer.setBackGround(R.drawable.list_remove);
                            } else {
                                mBackgroundContainer.setBackGround(R.drawable.list_background);
                            }
                            mBackgroundContainer.showBackground(v.getTop(), v.getHeight());
                        }
                    }
                    if (mSwiping) {
                        v.setTranslationX((x - mDownX));
                        v.setAlpha(1 - deltaXAbs / v.getWidth());
                    }
                }
                break;
                case MotionEvent.ACTION_UP: {
                    // User let go - figure out whether to animate the view out, or back into place
                    if (mSwiping) {
                        float x = event.getX() + v.getTranslationX();
                        float deltaX = x - mDownX;
                        final boolean xRight = deltaX > 0 ? true : false;
                        float deltaXAbs = Math.abs(deltaX);
                        float fractionCovered;
                        float endX;
                        float endAlpha;
                        final boolean remove;

                        if (deltaXAbs > v.getWidth() / 4) {
                            // Greater than a quarter of the width - animate it out
                            fractionCovered = deltaXAbs / v.getWidth();
                            endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                            endAlpha = 0;
                            remove = true;
                        } else {
                            // Not far enough - animate it back
                            fractionCovered = 1 - (deltaXAbs / v.getWidth());
                            endX = 0;
                            endAlpha = 1;
                            remove = false;
                        }
                        // Animate position and alpha of swiped item
                        // NOTE: This is a simplified version of swipe behavior, for the
                        // purposes of this demo about animation. A real version should use
                        // velocity (via the VelocityTracker class) to send the item off or
                        // back at an appropriate speed.
                        long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                        mListView.setEnabled(false);
                        v.animate().setDuration(duration).
                                alpha(endAlpha).translationX(endX).
                                withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Restore animated values
                                        v.setAlpha(1);
                                        v.setTranslationX(0);
                                        if (remove) {
                                            animateRemoval(mListView, v, xRight);
                                            if (xRight)
                                                deleteFile();
                                        } else {
                                            mBackgroundContainer.hideBackground();
                                            mSwiping = false;
                                            mListView.setEnabled(true);
                                        }
                                    }
                                });
                    }
                }
                mItemPressed = false;
                mBackgroundContainer.setBackGround(R.drawable.shadowed_background);
                break;
                default:
                    return false;
            }
            return true;
        }
    };

    HashMap<Long, Integer> mItemIdTopMap = new HashMap();
    private ExpandableListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    //private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private String[] files_paths; //string array

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeAppSettings();

        mDrawerList = (ExpandableListView) findViewById(R.id.navList);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        mEditButton = (ImageButton) findViewById(R.id.button_edit);
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewFiles(view);
            }
        });

        mEditButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                createNewPlaylist();
                return true;
            }
        });
        File file = new File(Util.getAppExtDir(), Util.getPlaylistfile());
        Util.setPlaylistpath(file.getAbsolutePath().toString());

        mBackgroundContainer = (BackgroundContainer) findViewById(R.id.listViewBackground);
        mListView = (ListView) findViewById(R.id.listPreview);

        try {
            Intent pIntent = getIntent();
            listName = (String) pIntent.getSerializableExtra("LIST_TO_EDIT"); //file array list
        } catch (Exception e) {
            listName = null;
        }

        setGroupData();
        setChildGroupData();
        addDrawerItems();
        setupDrawer();


        loadDefaultPlaylistThumbnails();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


    }

    private void initializeAppSettings() {
        Util.setAssets(getAssets());
        Util.setAppExtDir(getExternalFilesDir(null).getAbsolutePath());
        Util.setProp();
        (new MySetting()).readSettings();
    }

    private void loadDefaultPlaylistThumbnails() {
        Playlists plyList = new Playlists();

        try {

            readPlayList(plyList);
            if (listName == null) {
                listName = "MyPlaylist";
            }

            files = plyList.getPlist(listName);


//            final ListView listview = (ListView) findViewById(R.id.listPreview);
            if (mAdapter != null && mAdapter.getCount() > 0) {
                mAdapter.clear();
            }

            for (String s : files)
                filesInList.add(s);

            OnListTouchListener onListTouchListener = new OnListTouchListener(MainActivity.this);
            onListTouchListener.addOnTouchListener(onListTouch);
            onListTouchListener.addTapListener(onListSingleClick);
            mAdapter = new FileListPreviewAdapter(this, R.layout.list_preview_layout, filesInList,
                    onListTouchListener/*,mOnClickListener,mOnLongClickListener*/);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch ((int) id) {
                        case 0:
                            break;
                        case 1:
                            Toast.makeText(MainActivity.this, "Time for an upgrade 1!", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(MainActivity.this, "Time for an upgrade 2!", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
            mListView.setAdapter(mAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addDrawerItems() {
        mDrawerList.setAdapter(new FileMenuListViewAdapter(this, groupItem, childItem));
        mDrawerList.setOnChildClickListener(this);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch ((int) id) {
                    case 0:
                        imageView.setImageResource(R.drawable.batdroid);
                    case 1:
                        Toast.makeText(MainActivity.this, "Time for an upgrade 1!", Toast.LENGTH_SHORT).show();
                    default:
                        Toast.makeText(MainActivity.this, "Time for an upgrade 2!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mDrawerList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long packedPosition = mDrawerList.getExpandableListPosition(position);
                if (ExpandableListView.getPackedPositionType(packedPosition) ==
                        ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    // get item ID's
                    int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
                    int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);
                    lastChildOnLongClick = getMenuName(groupPosition, childPosition);
                    showPopup(view);
                    // return true as we are handling the event.
                    return true;
                }
                return false;
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                showSettingsDialog();
                break;
            case R.id.about:
                showAboutDialog();
                break;
        }

        // Activate the navigation drawer toggle

        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    private void showSettingsDialog() {
        Intent i = new Intent(this, MySettingsEditor.class);
        startActivity(i);
    }

    private void showAboutDialog() {
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {

        super.onKeyDown(keyCode, event);

        switch(keyCode) {
            case KeyEvent.KEYCODE_HOME:
                //holder.setKeepScreenOn(false);
                return true;

            case KeyEvent.KEYCODE_MENU:
               // holder.setKeepScreenOn(false);
                return true;

            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    public void createNewPlaylist() {
        mListNew = true;
        createPlayList();
    }

    public void modifyPlaylist(final String plyListName) {

        finish();
        Intent intent = new Intent("media.app.my.com.myplayer.EditPlayList");
        intent.putExtra("LIST_TO_EDIT", plyListName);
        //setResult(Activity.RESULT_OK, result);
        startActivity(intent);

    }

    public void deletePlayList(String listName) throws IOException {
        Playlists plyLst = new Playlists();
        readPlayList(plyLst);
        plyLst.removeList(listName);
        writePlayList(plyLst);
    }

    public boolean createPlayList() {
        //editTextMainScreen = (EditText) findViewById(R.id.editTextResult);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View promptView = layoutInflater.inflate(R.layout.list_name_input_popup, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        input = (EditText) promptView.findViewById(R.id.newplaylistname);

        alertDialogBuilder.setCancelable(false).setTitle("New Playlist").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // get user input and set it to result
                newPlayListName = input.getText().toString();

                //newPlayListName = input.getText().toString();
                Intent intent = new Intent("media.app.my.com.myplayer.FileSelectionActivity");
                startActivityForResult(intent, 0);
                //finish();
            }
        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        // create an alert dialog
        AlertDialog alertD = alertDialogBuilder.create();

        alertD.show();

        return true;
    }

    private void writePlayList(Playlists plyList) throws IOException {
        WriteXML plyLstWriter = new WriteXML();
        plyLstWriter.write2(plyList);
    }

    private void readPlayList(Playlists plyList) throws IOException {
        (new ReadXML()).readXML2(plyList);
    }

    public void setOnCompletionListener(android.media.MediaPlayer.OnCompletionListener listener) {
        int i;
    /* compiled code */
    }

    public void setGroupData() {
        String[] osArray = getResources().getStringArray(R.array.options_list);
        for (String str : osArray)
            groupItem.add(" " + str);
    }

    public void setChildGroupData() {
        /**
         * Add Data For TecthNology
         */
        ArrayList<String> child;
        int i = 0, j = 0;
        Map m = new HashMap();

        String[] osArray = getResources().getStringArray(R.array.options_list);
        for (String str : osArray) {
            int id = getResources().getIdentifier(str, "array", getPackageName());
            String[] childValues = getResources().getStringArray(id);
            child = new ArrayList();
            for (String cStr : childValues) {
                child.add(cStr);
                Integer ix = Integer.parseInt(new String(i + "" + j));
                m.put(ix, cStr);
                Util.setMenuMap(m);
                j++;
            }
            try {
                if (id == R.array.Playlists) {
                    Playlists plyLists = new Playlists();
                    (new ReadXML()).readXML2(plyLists);
                    Set<String> s = plyLists.getListNames();
                    Iterator<String> it = s.iterator();
                    if (s.size() > 0) {
                        child.add("#HSelect Playlist");
                        j++;
                        String next;
                        while (it.hasNext()) {
                            next = it.next();
                            child.add(next);
                            Integer ix = Integer.parseInt(new String(i + "" + j));
                            m.put(ix, next);
                            Util.setMenuMap(m);

                            j++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            childItem.add(child);

            i++;
        }

    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {

        String menuItem = getMenuName(i, i2);

        mDrawerLayout.closeDrawers();

        switch (menuItem) {
            case "New":
                createNewPlaylist();
                break;
            case "Folders":
                Toast.makeText(MainActivity.this, "Showing Folders", Toast.LENGTH_SHORT).show();
                showFilesFolders("Folders");
                break;
            case "Files":
                Toast.makeText(MainActivity.this, "Showing Files", Toast.LENGTH_SHORT).show();
                showFilesFolders("Files");
                break;
            default:
                Playlists plyList = new Playlists();
                try {
                    readPlayList(plyList);
                    if (plyList.getListNames().contains(menuItem)) playFromPlayList(menuItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(MainActivity.this, "Time for an upgrade 2!", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private void showFilesFolders(String filesFolders) {

    }

    public void playFromPlayList(String menuItem) {
        Playlists plyList = new Playlists();
        try {
            (new ReadXML()).readXML2(plyList);
            startPlay(menuItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startPlay(String cListName) {
        Util.filesList = null;
        listName = cListName;
        loadDefaultPlaylistThumbnails();
    }

    public void startPlay(String[] filesPath) {
        //Common.filesList = null;
        // listName =menuItem;
        Intent pIntent = new Intent("ssh.net.mobile.android.media.myplayer.Player");
        pIntent.putExtra("MEDIA_FILES", filesPath);
        startActivity(pIntent);
        finish();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        if (lastChildOnLongClick.equals("MyPlaylist")) {
            inflater.inflate(R.menu.menu_popup_trunc, popup.getMenu());
        } else {
            inflater.inflate(R.menu.menu_popup, popup.getMenu());
        }
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                try {
                    deletePlayList(lastChildOnLongClick);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.action_edit:
                modifyPlaylist(lastChildOnLongClick);
                return true;
            case R.id.action_rename:
                try {
                    renamePlaylist(lastChildOnLongClick);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            case R.id.action_play:
                //playFile(lastItemLongClicked);
                break;
            default:
                return false;
        }
        return false;
    }

    private void renamePlaylist(final String plyListName) throws IOException {
        //editTextMainScreen = (EditText) findViewById(R.id.editTextResult);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View promptView = layoutInflater.inflate(R.layout.list_name_input_popup, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        input = (EditText) promptView.findViewById(R.id.newplaylistname);

        alertDialogBuilder.setCancelable(false).setTitle("Playlist Name").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // get user input and set it to result
                newPlayListName = input.getText().toString();
                Playlists plyList = new Playlists();
                try {
                    readPlayList(plyList);

                    String[] files = plyList.getPlist(plyListName);
                    plyList.add(newPlayListName, files);
                    plyList.removeList(plyListName);
                    writePlayList(plyList);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //newPlayListName = input.getText().toString();
                //Intent intent = new Intent("media.app.my.com.myplayer.FileSelectionActivity");
                ///startActivityForResult(intent, 0);
                //finish();
            }
        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        // create an alert dialog
        AlertDialog alertD = alertDialogBuilder.create();

        alertD.show();
    }

    public String getMenuName(int i, int j) {
        return Util.getMenuName(Integer.parseInt(new String(i + "" + j)));
    }

    private void showPopUp() {
        PopupMenu popup = new PopupMenu(getBaseContext(), findViewById(R.id.editlistpopup));
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_file_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    /**
     * This method animates all other views in the ListView container (not including ignoreView)
     * into their final positions. It is called after ignoreView has been removed from the
     * adapter, but before layout has been run. The approach here is to figure out where
     * everything is now, then allow layout to run, then figure out where everything is after
     * layout, and then to run animations between all of those start/end positions.
     */
    private void animateRemoval(final ListView listview, View viewToRemove, boolean xRight) {
        int firstVisiblePosition = listview.getFirstVisiblePosition();
        for (int i = 0; i < listview.getChildCount(); ++i) {
            View child = listview.getChildAt(i);
            if (child != viewToRemove) {
                int position = firstVisiblePosition + i;
                long itemId = mAdapter.getItemId(position);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }
        // Delete the item from the adapter
        int position = mListView.getPositionForView(viewToRemove);
        fileToRemove = mAdapter.getItem(position);
        mAdapter.remove(mAdapter.getItem(position));

        final ViewTreeObserver observer = listview.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @SuppressLint("NewApi")
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = listview.getFirstVisiblePosition();
                for (int i = 0; i < listview.getChildCount(); ++i) {
                    final View child = listview.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = mAdapter.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(MOVE_DURATION).translationY(0);
                            if (firstAnimation) {
                                child.animate().withEndAction(new Runnable() {
                                    public void run() {
                                        mBackgroundContainer.hideBackground();
                                        mSwiping = false;
                                        mListView.setEnabled(true);
                                    }
                                });
                                firstAnimation = false;
                            }
                        }
                    } else {
                        // Animate new views along with the others. The catch is that they did not
                        // exist in the start state, so we must calculate their starting position
                        // based on neighboring views.
                        int childHeight = child.getHeight() + listview.getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(MOVE_DURATION).translationY(0);
                        if (firstAnimation) {
                            child.animate().withEndAction(new Runnable() {
                                public void run() {
                                    mBackgroundContainer.hideBackground();
                                    mSwiping = false;
                                    mListView.setEnabled(true);
                                }
                            });
                            firstAnimation = false;
                        }
                    }
                }
                mItemIdTopMap.clear();

                return true;
            }
        });
    }

    public void deleteFile() {
        filesInList.remove(fileToRemove);
        saveList();
    }

    @Override
    public void onBackPressed() {
        saveList();
        finish();
    }

    public void saveList() {
        Playlists plyList = new Playlists();
        (new ReadXML()).readXML2(plyList);
        plyList.removeList(listName);
        plyList.add(listName, filesInList);
        try {
            writePlayList(plyList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNewFiles(View view) {
        Intent intent = new Intent("media.app.my.com.myplayer.FileSelectionActivity");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == 0 && resultCode == RESULT_OK) {
                ArrayList<File> filesList = (ArrayList<File>) data.getSerializableExtra(FILES_TO_UPLOAD); //file array list
                int i = 0;
                files_paths = new String[filesList.size()];

                for (File file : filesList) {
                    //String fileName = file.getName();
                    if (file != null) {
                        String uri = file.getAbsolutePath();
                        files_paths[i] = uri; //storing the selected file's paths to string array files_paths
                        i++;
                    }
                }

                Playlists plyList = new Playlists();
                readPlayList(plyList);

                if (mListNew) {
                    plyList.add(newPlayListName, files_paths);
                } else {
                    plyList.add(listName, files_paths);
                }
                writePlayList(plyList);

                startPlay(files_paths);
            } else if (requestCode == 0 && resultCode == Util.RESULT_PLAY) {
                String file = (String) data.getSerializableExtra(FILES_TO_UPLOAD); //file array list
                files_paths = new String[1];
                files_paths[0] = file; //storing the selected file's paths to string array files_paths
                startPlay(files_paths);
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reloadPlayList() {
        loadDefaultPlaylistThumbnails();
    }
}
