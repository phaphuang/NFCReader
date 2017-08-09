package com.nfc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.learn2crack.nfc.NfcUser;
import com.learn2crack.nfc.R;

import java.util.List;

/**
 * Created by basssrongsil on 8/5/2017 AD.
 */

public class CustomAdapter extends BaseAdapter {

    private Context mContext;
    private List<NfcUser> items;
    private int[] resId;

    public CustomAdapter(Context context, List<NfcUser> items) {
        this.mContext= context;
        this.items = items;
        // this.resId = resId;
    }

    public int getCount() {
        return items.size();
    }

    public Object getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public void setItems(List<NfcUser> items) {
        this.items = items;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            view = layoutInflater.inflate(R.layout.users_row, parent, false);
        }

        TextView textView = (TextView)view.findViewById(R.id.textViewUserRow);
        textView.setText(items.get(position).firstName + " " + items.get(position).lastName);

        ImageView imageView = (ImageView)view.findViewById(R.id.imageViewUserRow);

        ImageView imageViewRegis = (ImageView)view.findViewById(R.id.isRegis);
        if (items.get(position).nfcId != null && !items.get(position).nfcId.isEmpty()) {

            imageViewRegis.setBackgroundResource(R.drawable.correct_sign);
        } else {
            imageViewRegis.setBackgroundResource(R.drawable.incorrect_sign);
        }

        // imageView.setBackgroundResource(resId[position]);

        return view;
    }
}
