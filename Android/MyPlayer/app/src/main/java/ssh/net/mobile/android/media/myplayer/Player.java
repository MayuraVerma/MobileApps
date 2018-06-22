/***
 Copyright (c) 2008-2009 CommonsWare, LLC

 Licensed under the Apache License, Version 2.0 (the "License"); you may
 not use this file except in compliance with the License. You may obtain
 a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package ssh.net.mobile.android.media.myplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

//import javax.xml.stream.XMLStreamException;

public class Player extends Activity implements
        /* OnBufferingUpdateListener, */ OnCompletionListener,
        MediaPlayer.OnPreparedListener, SurfaceHolder.Callback, SeekBar.OnSeekBarChangeListener {
    private static final int REQUEST_PICK_FILE = 1;
    private static final String FILES_TO_UPLOAD = "upload";
    private static String TAG = "MyPlayer";
    private Thread.UncaughtExceptionHandler onBlooey =
            new Thread.UncaughtExceptionHandler() {
                public void uncaughtException(Thread thread, Throwable ex) {
                    Log.e(TAG, "Uncaught exception", ex);
                    goBlooey(ex);
                }
            };
    private static String HISTORY_FILE = "history.json";
    protected boolean hasActiveHolder = false;
    //protected int Common.listPosition = 0;
    /*	public void onBufferingUpdate(MediaPlayer arg0, int percent) {
        } */
    protected String mfullScreenOption = "FullScreen";
    private OnSurfaceSwipeTouchListener.SurfacePopUps showSurfacePopUps =
            new OnSurfaceSwipeTouchListener.SurfacePopUps() {

                @Override
                public void showSurfacePopUps() {
                    PopupMenu popup = new PopupMenu(getBaseContext(), (View) findViewById(R.id.screenpopup));
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.menu_onscreen_popup, popup.getMenu());
                    popup.getMenu().getItem(0).setTitle(mfullScreenOption);
                    popup.setOnMenuItemClickListener(this);
                    popup.show();
                }

                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.action_screen_size:
                            try {
                                switchViewMode(menuItem, null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                return true;
                            }
                        case R.id.zoomin:
                            OnZoomIn();
                            break;
                        case R.id.zoomout:
                            OnZoomOut();
                            break;
                        default:
                            return false;
                    }
                    return true;
                }
            };
    protected boolean autoPlay = true;
    protected OnVolumeSlideListener mVolumeChangeListener = null;

    OnSurfaceSwipeTouchListener surfaceSwipeTouchListener = null;
    AudioManager audioManager;
    int maxVolume = 0;
    int curVolume = 0;
    OnVolumeSlideListener.VolumeChangeListener onVolumeSlideChange = new OnVolumeSlideListener.VolumeChangeListener() {
        @Override
        public int getVolumeLevel() {
            curVolume = OnVolumeSlideListener.mVolumeLevel;
            setVolume();
            return 1;
        }
    };
    VolumeSlider mVolumeSlider = null;
    int playerWidth = 1;
    int playerHeight = 1;
    ArrayList<Integer> playedItems = new ArrayList<>();
    private OnClickListener onPlay = new OnClickListener() {
        public void onClick(View v) {
            String url = null;
            switch (v.getId()) {
                case R.id.play:
                    url = address.getText().toString();
                    if (url != null && url.length() > 0 || Util.filesList.length != 0) {
                        playVideo(url);
                        clearPanels(true);
                        //bottomPanel.setVisibility(View.VISIBLE);
                        history.update(url);
                    } else {
                        Toast.makeText(Player.this, "No File Selected!", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    };
    private int width = 0;
    private int height = 0;
    private MediaPlayer player;
    private SurfaceView surface;
    private SurfaceHolder holder;
    private OnClickListener onGo = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.go:
                    clearPanels(false);

                    player.stop();
                    player.reset();

                    player = null;
                    holder = null;
                    surface = null;
                    finish();
                    Intent intent = new Intent("media.app.my.com.myplayer.FileSelectionActivity");
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };
    private View topPanel = null;
    private View bottomPanel = null;
    private long lastActionTime = 0L;
    private OnSurfaceSwipeTouchListener.TapListener onTap =
            new OnSurfaceSwipeTouchListener.TapListener() {
                public void onTap(MotionEvent event) {
                    lastActionTime = SystemClock.elapsedRealtime();

                    if (event.getY() > surface.getHeight() / 3
                            &&
                            event.getY() < surface.getHeight() - surface.getHeight() / 3
                            &&
                            event.getX() > surface.getWidth() / 3
                            &&
                            event.getX() < surface.getWidth() - surface.getWidth() / 3
                            ) {
                        playPause();
                    }
                    //isPaused = !isPaused;
                    if (event.getY() < surface.getHeight() / 2) {
                        topPanel.setVisibility(View.VISIBLE);
                    } else {
                        bottomPanel.setVisibility(View.VISIBLE);
                    }
                }
            };
    private OnClickListener onMedia = new OnClickListener() {
        public void onClick(View v) {
            lastActionTime = SystemClock.elapsedRealtime();
            playPause();
        }
    };
    private TextWatcher addressChangeWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            lastActionTime = SystemClock.elapsedRealtime();
            go.setEnabled(s.length() > 0);
        }

        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) {
            // no-op
        }

        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {
            // no-op
        }
    };
    private OnSurfaceSwipeTouchListener.SurfaceSwipe onScreenSwipe =
            new OnSurfaceSwipeTouchListener.SurfaceSwipe() {

                @Override
                public void onScreenSwipe() {
                    int lastAction = Util.getLastAction();

                    switch (lastAction) {
                        case Util.SWIPE_LEFT:
                            playNext();
                            Toast.makeText(Player.this, "Playing Next", Toast.LENGTH_SHORT).show();
                            break;
                        case Util.SWIPE_RIGHT:
                            playPrevious();
                            Toast.makeText(Player.this, "Playing Previous", Toast.LENGTH_SHORT).show();
                            break;
                        case Util.SWIPE_DOWN:
                            //volumeDown(Common.swipeLength);
                            lastActionTime = SystemClock.elapsedRealtime();
                            mVolumeSlider.setVisibility(View.VISIBLE);
                            Toast.makeText(Player.this, "Volume Down", Toast.LENGTH_SHORT).show();
                            break;
                        case Util.SWIPE_UP:
                            //volumeUp(Common.swipeLength);
                            lastActionTime = SystemClock.elapsedRealtime();
                            mVolumeSlider.setVisibility(View.VISIBLE);
                            Toast.makeText(Player.this, "Voume Up", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(Player.this, lastAction, Toast.LENGTH_SHORT).show();
                            break;
                    }

                }
            };
    private boolean isPaused = false;
    private Button go = null;
    private Button play = null;
    private AutoCompleteTextView address = null;
    private URLHistory history = null;
    // private ProgressBar timeline = null;
    private SeekBar timelineSeek = null;
    private TextView timer = null;
    // private ImageButton media = null;
    private ImageView imagePlayPause;
    private boolean manualSeek = false;
    private TextView filePath;
    private Button Browse;
    private File selectedFile;
    private int currentScreenOrientation = Configuration.ORIENTATION_PORTRAIT;
    private String totalLength = "00:00";
    private String[] files_paths; //string array



    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Thread.setDefaultUncaughtExceptionHandler(onBlooey);


        initSurfaceHolder();
        initAudioSettings();


        try {
            File historyFile = new File(getFilesDir(), HISTORY_FILE);

            if (historyFile.exists()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(openFileInput(HISTORY_FILE)));
                String str;
                StringBuilder buf = new StringBuilder();

                while ((str = in.readLine()) != null) {
                    buf.append(str);
                    buf.append("\n");
                }

                in.close();
                history.load(buf.toString());
            }
        } catch (Throwable t) {
            Log.e(TAG, "Exception in loading history", t);
            goBlooey(t);
        }

        address.setAdapter(history);

        try {
            if (Util.filesList == null) {
                Intent pIntent = getIntent();
                Util.filesList = (String[]) pIntent.getSerializableExtra("MEDIA_FILES"); //file array list
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                history.load(Util.filesList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

/*        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(filesList[0], MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        ImageView iv = (ImageView)findViewById(R.id.ic_play_pause);
        iv.setVisibility(View.VISIBLE);
        iv.setImageBitmap(bMap);*/
    }

    private void initSurfaceHolder() {
        setContentView(R.layout.activity_player);

        surfaceSwipeTouchListener = new OnSurfaceSwipeTouchListener(Player.this);
        surfaceSwipeTouchListener.addTapListener(onTap);
        surfaceSwipeTouchListener.addPopUpListener(showSurfacePopUps);
        surfaceSwipeTouchListener.addSwipeListeners(onScreenSwipe);

        if (surface == null) {
            surface = (SurfaceView) findViewById(R.id.surface);
            surface.setOnTouchListener(surfaceSwipeTouchListener);
        }

        holder = surface.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        topPanel = findViewById(R.id.top_panel);
        bottomPanel = findViewById(R.id.bottom_panel);

        //timeline = (ProgressBar) findViewById(R.id.timeline);
        timelineSeek = (SeekBar) findViewById(R.id.seekBar);
        timer = (TextView) findViewById(R.id.timer);

        // media = (ImageButton) findViewById(R.id.media);
        //media.setOnClickListener(onMedia);

        go = (Button) findViewById(R.id.go);
        go.setOnClickListener(onGo);

        imagePlayPause = (ImageView) findViewById(R.id.ic_play_pause);
        imagePlayPause.setVisibility(View.INVISIBLE);

        play = (Button) findViewById(R.id.play);
        play.setOnClickListener(onPlay);

        address = (AutoCompleteTextView) findViewById(R.id.address);
        address.addTextChangedListener(addressChangeWatcher);
        history = new URLHistory(this, android.R.layout.simple_dropdown_item_1line);
    }

    private void initAudioSettings() {
        mVolumeChangeListener = new OnVolumeSlideListener();
        mVolumeChangeListener.addVolumeChangeListener(onVolumeSlideChange);

        mVolumeSlider = (VolumeSlider) findViewById(R.id.volumeSlider);
        mVolumeSlider.setVisibility(View.INVISIBLE);
        mVolumeSlider.setOnSeekBarChangeListener(mVolumeChangeListener);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        OnVolumeSlideListener.mVolumeLevel = curVolume;
        mVolumeSlider.setProgress(curVolume);
        mVolumeSlider.setMax(maxVolume + Util.amplify);
    }

    private void adjustVolumeSlider() {

        /*ViewGroup.LayoutParams lp = new ActionBar.LayoutParams(new ViewGroup.LayoutParams(
                pxToDp(activity_player.getVideoWidth()),
                30));
        if(activity_player!=null)
            mVolumeSlider.setLayoutParams(lp);
        else
            mVolumeSlider.setMax(0);*/
    }

    private void setmVolumeSlider(int maxVolume) {
        mVolumeSlider.setProgress(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //playPlayList(null,null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        isPaused = false;
        surface.postDelayed(onEverySecond, 1000);
    }    private Runnable onEverySecond = new Runnable() {
        public void run() {
            if (lastActionTime > 0 &&
                    SystemClock.elapsedRealtime() - lastActionTime > 3000) {
                clearPanels(true);
            }

            if (player != null) {
                setTimer(player.getCurrentPosition());
            } else if (Util.filesList.length > 0 && autoPlay) {
                playVideo(Util.filesList[Util.listPosition]);
            }

            if (!isPaused) {
                surface.postDelayed(onEverySecond, 1000);
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();

        isPaused = true;

        try {
            OutputStream out = openFileOutput(HISTORY_FILE,
                    MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(out);

            history.save(writer);
            writer.close();
        } catch (Throwable t) {
            Log.e(TAG, "Exception writing history", t);
            goBlooey(t);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (player != null) {
            player.release();
            player = null;
        }

        surfaceSwipeTouchListener.removeTapListener(onTap);
        surfaceSwipeTouchListener.removePopUpListener(showSurfacePopUps);
        surfaceSwipeTouchListener.removeSwipeListeners(onScreenSwipe);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean isScreenOn = powerManager.isScreenOn();
        //isPaused = isPaused^isPaused;
        lastActionTime = SystemClock.elapsedRealtime();

        super.onKeyDown(keyCode, event);

        switch(keyCode) {

            case KeyEvent.KEYCODE_BACK:
                if (
                        (topPanel.getVisibility() == View.VISIBLE ||
                                bottomPanel.getVisibility() == View.VISIBLE)) {
                    clearPanels(false);
                    if (player.isPlaying())
                        player.stop();
                    surface = null;
                    holder = null;
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    return (true);
                }
                break;
            case KeyEvent.KEYCODE_HOME:
                holder.setKeepScreenOn(false);
                return true;

            case KeyEvent.KEYCODE_MENU:
                holder.setKeepScreenOn(false);
                return true;

        }

        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP) && !isScreenOn && event.isLongPress()) {
            // Do something
            playNext();
            // return true because we want handle this key
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) && !isScreenOn && event.isLongPress()) {
            // Do something
            playPrevious();
            // return true because we want handle this key
            return true;
        }

        return (super.onKeyDown(keyCode, event));
    }

    public void onCompletion(MediaPlayer arg0) {

        //media.setEnabled(false);
    }

    public void onPrepared(MediaPlayer mediaplayer) {
        playerWidth = playerWidth == 1 ? player.getVideoWidth() : playerWidth;
        playerHeight = playerHeight == 1 ? player.getVideoHeight() : playerHeight;

        if (playerWidth != 0 && playerHeight != 0) {
            holder.setFixedSize(playerWidth, playerHeight);
            timelineSeek.setProgress(0);
            timelineSeek.setOnSeekBarChangeListener(this);
            timelineSeek.setMax(player.getDuration());
            totalLength = Util.getTimeFromMilliSeconds(player.getDuration());
            timer.setText("00:00 / " + totalLength);
            player.start();
        }

        //media.setEnabled(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
        int ch;
        // no-op
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        int yl;
        synchronized (this) {
            hasActiveHolder = false;

            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int io;
        synchronized (this) {
            hasActiveHolder = true;
            this.notifyAll();
        }
    }

    private void playVideo(String url) {
        FileInputStream f = null;
        try {
            f = new FileInputStream(new File(Util.replace32L(url.trim())));
            FileDescriptor fd = f.getFD();
            if (player == null) {
                player = new MediaPlayer();
            } else {
                player.stop();
                player.reset();
            }


            setMediaTitle(url);
            player.setDataSource(fd);
            holder.setKeepScreenOn(true);
            player.setDisplay(holder);
            player.setScreenOnWhilePlaying(false);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnPreparedListener(this);


            adjustVolumeSlider();
            //setVideoResolution();
            //setPlayerScreenResolution(playerWidth, playerHeight);
            //switchViewMode(null,currentScreenOrientation);
            player.prepareAsync();
            player.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    playVideo(Util.filesList[getNextItem()]);
                    history.update(Util.filesList[Util.listPosition]);

                }
            });
        } catch (Exception t) {
            //Log.e(TAG, "Exception in media prep", t);
            t.printStackTrace();

            goBlooey(t);
        } finally {

            try {
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setMediaTitle(String url) {
        String title = url;
        TextView t = (TextView) findViewById(R.id.mediaTitle);
        t.setText(title.substring(title.lastIndexOf("/") + 1, title.lastIndexOf(".")));
        t.setAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
    }

    private void clearPanels(boolean both) {
        lastActionTime = 0;

        if (both) {
            topPanel.setVisibility(View.INVISIBLE);
            mVolumeSlider.setVisibility(View.INVISIBLE);
            bottomPanel.setVisibility(View.INVISIBLE);
        } else {
            topPanel.setVisibility(View.VISIBLE);
            bottomPanel.setVisibility(View.VISIBLE);
        }
    }

    private void goBlooey(Throwable t) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        t.printStackTrace();
        builder
                .setTitle("Exception!")
                .setMessage(t.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private void switchViewMode(MenuItem menuItem, Integer orientation) {
        if (menuItem == null) {
            // menuItem = (new Menu()).findItem(R.id.action_screen_size);
        }

        if (orientation != null) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                holder.setFixedSize(player.getVideoWidth(), player.getVideoHeight());
                changeOnScreenPopUpText(menuItem, "FullScreen");
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                holder.setFixedSize(Util.dpToPx(getResources().getConfiguration().screenWidthDp), Util.dpToPx(getResources().getConfiguration().screenHeightDp));
                changeOnScreenPopUpText(menuItem, "ExitFullScreen");
            }
        } else {
            if (currentScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                holder.setFixedSize(Util.dpToPx(getResources().getConfiguration().screenWidthDp), Util.dpToPx(getResources().getConfiguration().screenHeightDp));
                currentScreenOrientation = Configuration.ORIENTATION_LANDSCAPE;
                changeOnScreenPopUpText(menuItem, "ExitFullScreen");
            } else if (currentScreenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                holder.setFixedSize(player.getVideoWidth(), player.getVideoHeight());
                currentScreenOrientation = Configuration.ORIENTATION_PORTRAIT;
                changeOnScreenPopUpText(menuItem, "FullScreen");
            }

        }
        if (orientation != null)
            currentScreenOrientation = orientation;
    }

    private void setTimerAndSeek(Integer sec) {
        player.seekTo(sec);
        // setTimer(sec);
    }

    private void setTimer(Integer sec) {
        if (sec == null) {
            sec = player.getCurrentPosition();
        }
        timelineSeek.setProgress(player.getCurrentPosition());
        timer.setText(Util.getTimeFromMilliSeconds(sec) + " / " + totalLength);
    }

    private void playPause() {
        if (player != null) {
            if (player.isPlaying()) {
                imagePlayPause.setImageResource(R.drawable.ic_media_play);
                imagePlayPause.setVisibility(View.VISIBLE);
                player.pause();
            } else {
                imagePlayPause.setImageResource(R.drawable.ic_media_pause);
                imagePlayPause.setVisibility(View.GONE);
                player.start();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == Util.RESULT_PLAY) {
            String file = (String) data.getSerializableExtra(FILES_TO_UPLOAD); //file array list
            int i = 0;
            files_paths = new String[1];

            files_paths[0] = file; //storing the selected file's paths to string array files_paths

            initSurfaceHolder();

            Util.filesList = files_paths;
            Util.listPosition = 0;
            // playVideo(Common.filesList[Common.listPosition]);
        }

/*        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case REQUEST_PICK_FILE:

                    if (data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {

                        selectedFile = new File
                                (data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
                        address.setText(selectedFile.getPath());
                    }
                    break;
            }
        }*/
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        currentScreenOrientation = newConfig.orientation;
        switchViewMode(null, newConfig.orientation);
        // Checks the orientation of the screen

    }

    public boolean playPlayList(String Name, Integer order) {
        Playlists plyList = new Playlists();
        List<String> allFiles = new ArrayList<String>();

        Set s = new HashSet<>();
        String next;
        ReadXML readXML = new ReadXML();

        try {
            readXML.readXML2(plyList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        s = plyList.getListNames();

        if (Name == null || Name.length() < 1 || Name == "NOLIST") {
            Iterator<String> it = s.iterator();
            while (it.hasNext()) {
                next = it.next();
                //System.out.println(next+" --> "+ for( String Ls :plyList.getPlist(next).toString().split(",")) System.out.print(Ls));
                String Ls[] = plyList.getPlist(next);//.toString().split(",");
                for (String ls : Ls) {
                    //System.out.println(next+" --> "+ls.replace('[', ' ').replace(']', ' '));
                    allFiles.add(ls/*.replace('[', ' ').replace(']', ' ')*/);
                }
            }
        }

        while (allFiles.listIterator().hasNext()) {
            playVideo(allFiles.listIterator().next());
        }

        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (manualSeek) {
            setTimerAndSeek(seekBar.getProgress());
            player.setVolume(0, 0);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        manualSeek = true;
        setTimerAndSeek(seekBar.getProgress());
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        manualSeek = false;
        setTimerAndSeek(seekBar.getProgress());
        player.setVolume(1, 1);
    }

    private void changeOnScreenPopUpText(MenuItem menuItem, String fullScreen) {
        mfullScreenOption = fullScreen;
    }

    public void playNext() {
        playVideo(Util.filesList[getNextItem()]);
    }

    public void playPrevious() {
        if (Util.listPosition > 0)
            playVideo(Util.filesList[--Util.listPosition]);
        else {
            Util.listPosition = Util.filesList.length - 1;
            playVideo(Util.filesList[Util.listPosition]);
        }
    }

    public void volumeUp(float units) {
        if (curVolume + (int) units <= maxVolume) {
            curVolume = curVolume + (int) units;
        } else {
            curVolume = maxVolume;
        }
        setVolume();
    }

    public void volumeDown(float units) {
        if (curVolume - (int) units > 0) {
            curVolume = curVolume - (int) units;
        } else {
            curVolume = 0;
        }

        setVolume();
    }

    private void setVolume() {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, 0);
    }

    public void OnZoomIn() {
        playerHeight = Util.dpToPx(getResources().getConfiguration().screenHeightDp);
        playerWidth = Util.dpToPx(getResources().getConfiguration().screenWidthDp);

        setPlayerScreenResolution(playerWidth, playerHeight);
    }

    public void OnZoomOut() {
        setVideoResolution();
        setPlayerScreenResolution(playerWidth, playerHeight);
    }

    public void setVideoResolution() {

       // playerHeight = Util.dpToPx(getResources().getConfiguration().screenHeightDp);
        playerWidth = Util.dpToPx(getResources().getConfiguration().screenWidthDp);

       // playerWidth = player.getVideoWidth();
        playerHeight =  Util.dpToPx(getResources().getConfiguration().screenHeightDp)/3;//player.getVideoHeight();
    }

    public void setPlayerScreenResolution(int playerWidth, int playerHeight) {
        holder.setFixedSize(playerWidth, playerHeight);
    }

    public boolean isLoop() {
        boolean retVal = true;

        if (!Util.shuffle) retVal = false;

        return retVal;
    }

    public int getNextItem() {
        int nextItem = 0;
        if (!isLoop()) {
            if (++Util.listPosition < Util.filesList.length)
                nextItem = Util.listPosition;
            else {
                Util.listPosition = 0;
                nextItem = Util.listPosition;
            }
        } else {
            double rand = Math.random() * 10000000;

            nextItem = (int) rand % Util.filesList.length;

            if (playedItems.size() >= Util.filesList.length)
                playedItems.clear();

            if (playedItems.contains(nextItem) || playedItems.contains(Util.listPosition)) {
                nextItem = nextItem(nextItem);
            }

        }

        playedItems.add(nextItem);

        return nextItem;
    }

    private int nextItem(int i) {
        int nextItem = 0;
        double rand;
        if (!playedItems.contains(i))
            return i;
        else {
            rand = Math.random() * 10000000;
            nextItem = (int) rand % Util.filesList.length;
            return nextItem(nextItem);
        }
        //return 0;
    }
}
