package com.learn2crack.nfc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nfc.adapter.CustomAdapter;

/**
 * Created by basssrongsil on 8/5/2017 AD.
 */

public class UsersListActivity extends AppCompatActivity {

    public static final String TAG = UsersListActivity.class.getSimpleName();

    static final String[] USERS = new String[] {
            "User one",
            "User two",
            "User three",
            "User four",
            "User five",
            "User six",
            "User seven",
            "User eight",
            "User nine",
            "User ten"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_list);

        CustomAdapter adapter = new CustomAdapter(getApplicationContext(), USERS);

        ListView listView = (ListView)findViewById(R.id.listViewUsers);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            }
        });
    }
}
