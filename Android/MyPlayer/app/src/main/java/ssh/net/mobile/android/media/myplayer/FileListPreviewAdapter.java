package ssh.net.mobile.android.media.myplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sudhesha on 5/22/2015.
 */
public class FileListPreviewAdapter extends ArrayAdapter<String> {
    private final Context context;
    View.OnTouchListener mTouchListener;


    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
    private MediaPlayer mp = new MediaPlayer();

    public FileListPreviewAdapter(Context context, String[] values) {
        super(context, R.layout.list_preview_layout, values);
        this.context = context;
        for (int i = 0; i < values.length; ++i) {
            mIdMap.put(values[i], i);
        }

    }

    public FileListPreviewAdapter(Context context, int textViewResourceId, List<String> objects, View.OnTouchListener listener) {
        super(context, textViewResourceId, objects);
        this.context = context;
        mTouchListener = listener;

        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_preview_layout, parent, false);
        LinearLayout filePreviewMain = (LinearLayout) rowView.findViewById(R.id.filePreviewMain);

        LinearLayout filePreviewMainImage = (LinearLayout) rowView.findViewById(R.id.filePreviewMainImage);
        ImageView fileThumbnail = (ImageView) rowView.findViewById(R.id.fileThumbnail);
        LinearLayout filePreviewMainDetails = (LinearLayout) rowView.findViewById(R.id.filePreviewMainDetails);
        TextView fileNameView = (TextView) rowView.findViewById(R.id.fileNameView);
        TextView fileDetailsView = (TextView) rowView.findViewById(R.id.fileDetailsView);
        int duration;

        String str = Util.replace32L(getItem(position));
        if (str.length() > 0 && str != null && !str.equals("")) {
            try {
                mp = new MediaPlayer();
                mp.setDataSource(str);
                mp.prepare();
                duration = mp.getDuration();
                mp.release();
            } catch (Exception e) {
                duration = 0;
            }
            if (rowView != convertView) {
                if(Util.preview) {
                    Bitmap bMap = ThumbnailUtils.createVideoThumbnail(str, MediaStore.Video.Thumbnails.MINI_KIND);
                    bMap = scaleImage(bMap);
                    filePreviewMainImage.setId(str.hashCode());
                    fileThumbnail.setImageBitmap(bMap);
                }

                fileNameView.setText(str.substring(str.lastIndexOf("/") + 1, str.lastIndexOf(".")));

                Double mb = (double) (new File(str)).length() / (1024 * 1024) / 10;
                DecimalFormat df = new DecimalFormat("0.00");

                fileDetailsView.setText(String.valueOf(df.format(mb)) + " MB | " + Util.getTimeFromMilliSeconds(duration) + "mins");
                rowView.setOnTouchListener(mTouchListener);
            }
        }
        return rowView;
    }

    private Bitmap scaleImage(Bitmap bMap) {
        if(1079 > Util.dpToPx(getContext().getResources().getConfiguration().screenWidthDp))
        {
            bMap = Bitmap.createScaledBitmap(bMap, getContext().getResources().getConfiguration().screenWidthDp / 2, getContext().getResources().getConfiguration().screenHeightDp / 5, true);
        }
        return bMap;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        String item = getItem(position);
        return mIdMap.get(item);
    }

}

