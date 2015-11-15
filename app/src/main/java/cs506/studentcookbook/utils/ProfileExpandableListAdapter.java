package cs506.studentcookbook.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cs506.studentcookbook.R;

/**
 * Code adapted from:
 * Ravi Tamada
 * http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
 */
public class ProfileExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private String[] headerList; // Titles of the headers
    // child data in format of header title, child title
    private ArrayList<ArrayList<String>> childList;

    public ProfileExpandableListAdapter(Context context, String[] listDataHeader, ArrayList<ArrayList<String>> listChildData) {
        this._context = context;
        this.headerList = listDataHeader;
        this.childList = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.childList.get(groupPosition)
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.profile_list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.profile_list_exp_ListItem);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.childList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.headerList[groupPosition];
    }

    @Override
    public int getGroupCount() {
        return this.headerList.length;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.profile_list_groups, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.profile_list_exp_ListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

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
