package ssh.net.mobile.android.media.myplayer;

/**
 * Created by sudhesha on 5/3/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class FileMenuListViewAdapter extends BaseExpandableListAdapter {

    private final Context context;
    public ArrayList<String> groupItem, tempChild;
    public ArrayList<Object> Childtem = new ArrayList<Object>();
    public LayoutInflater minflater;
    public Activity activity;
    public View childView;

    public FileMenuListViewAdapter(Context context, ArrayList<String> grList, ArrayList<Object> childItem) {
        this.context = context;
        groupItem = grList;
        this.Childtem = childItem;
    }

    public void setInflater(LayoutInflater mInflater, Activity act) {
        this.minflater = mInflater;
        activity = act;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        tempChild = (ArrayList<String>) Childtem.get(groupPosition);
        String childName = null;
        if (convertView == null) {
            convertView = new TextView(context);
        }

        TextView text = (TextView) convertView;
        text.setTextSize(18);

        childName = tempChild.get(childPosition);
        if (childName.contains("#H")) {
            text.setBackgroundColor(Color.LTGRAY);
            childName = childName.replace("#H", "");
        } else {
            text.setBackgroundColor(Color.WHITE);
        }

        text.setText("    " + childName);

        convertView.setTag(tempChild.get(childPosition));
        return convertView;
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        return ((ArrayList<String>) Childtem.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return groupItem.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new TextView(context);
        }
        ((TextView) convertView).setText(groupItem.get(groupPosition));
        convertView.setTag(groupItem.get(groupPosition));
        ((TextView) convertView).setTextSize(22);
        ((TextView) convertView).setTextColor(Color.DKGRAY);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
