package com.nfc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.learn2crack.nfc.R;

/**
 * Created by basssrongsil on 8/5/2017 AD.
 */

public class CustomAdapter extends BaseAdapter {

    private Context mContext;
    private String[] strName;
    private int[] resId;

    public CustomAdapter(Context context, String[] strName) {
        this.mContext= context;
        this.strName = strName;
        // this.resId = resId;
    }

    public int getCount() {
        return strName.length;
    }

    public Object getItem(int position) {
        return strName[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    public void setItems(String[] items) {
        this.strName = items;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            view = layoutInflater.inflate(R.layout.users_row, parent, false);
        }

        TextView textView = (TextView)view.findViewById(R.id.textViewUserRow);
        textView.setText(strName[position]);

        ImageView imageView = (ImageView)view.findViewById(R.id.imageViewUserRow);
        // imageView.setBackgroundResource(resId[position]);

        return view;
    }
}
