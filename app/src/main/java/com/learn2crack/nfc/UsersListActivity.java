package com.learn2crack.nfc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.nfc.adapter.CustomAdapter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by basssrongsil on 8/5/2017 AD.
 */

public class UsersListActivity extends AppCompatActivity {

    public static final String TAG = UsersListActivity.class.getSimpleName();

    private EditText mSearch;

    static String[] USERS = new String[] {
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

        mSearch = (EditText) findViewById(R.id.search);

        CustomAdapter adapter = new CustomAdapter(getApplicationContext(), USERS);

        ListView listView = (ListView)findViewById(R.id.listViewUsers);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String user = USERS[position];

                Toast.makeText(UsersListActivity.this, "Click item " + position + " which is " + USERS[position], Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(UsersListActivity.this, TopupActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();

            }
        });

        mSearch.addTextChangedListener(
                new TextWatcher() {
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    private Timer timer=new Timer();
                    private final long DELAY = 1000; // milliseconds

                    @Override
                    public void afterTextChanged(final Editable s) {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                System.out.println("SEND AJAX SEARCH");

                                                USERS = new String[] {
                                                        "BASS one",
                                                        "BASS two",
                                                        "BASS three",
                                                        "BASS four",
                                                        "BASS five",
                                                        "BASS six",
                                                        "BASS seven",
                                                        "BASS eight",
                                                        "BASS nine",
                                                        "BASS ten"
                                                };

                                                adapter.setItems(USERS);
                                                adapter.notifyDataSetChanged();

                                            }
                                        });

                                    }
                                },
                                DELAY
                        );
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {

    }

    public void backToMainMenu(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void backToAllUsers(View v) {
    }

    public void searchByScanNfc(View v) {
        Intent intent = new Intent(this, NFCActivity.class);
        startActivity(intent);
        finish();
    }
}
