package com.imaginetventures.masterkey.adapterClasses;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imaginetventures.masterkey.R;

/**
 * Created by Sanjay on 3/29/16.
 */
public class MenuListViewAdapter extends BaseAdapter {
    private Activity activity;
    public static String[] menuArray = {"Dashboard", "Transaction", "Add Lead", "Resource", "Record Expense", "Logout"};
    private int[] iconArray = {R.drawable.dashboard, R.drawable.transaction, R.drawable.enquiry, R.drawable.resource, R.drawable.record_expense, R.drawable.logout};
    private int disablePosition = -1;

    public MenuListViewAdapter(Activity activity, int disablePosition) {
        this.activity = activity;
        this.disablePosition = disablePosition;
    }

    @Override
    public boolean isEnabled(int position) {
        if (disablePosition == position)
            return false;
        else
            return true;

    }

    @Override
    public int getCount() {
        return menuArray.length;
    }

    @Override
    public Object getItem(int position) {
        return menuArray[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.menu_item, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.menuImageView);
        TextView textView = (TextView) convertView.findViewById(R.id.menuTextView);
        imageView.setImageResource(iconArray[position]);
        textView.setText(menuArray[position]);
        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.menuItemLinearLayout);
        if (disablePosition == position)
            linearLayout.setBackgroundResource(R.color.colorPrimaryDark);
        else
            linearLayout.setBackgroundResource(R.drawable.menu_list_content_color);

        return convertView;
    }
}
