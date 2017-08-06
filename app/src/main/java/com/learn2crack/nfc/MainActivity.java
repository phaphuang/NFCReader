package com.learn2crack.nfc;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    
    public static final String TAG = MainActivity.class.getSimpleName();

    private Button mBtnAllUsers;
    private Button mBtnSell;
    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnAllUsers = (Button) findViewById(R.id.btn_allusers);
        mBtnSell = (Button) findViewById(R.id.btn_sell);
        mBtnLogin = (Button) findViewById(R.id.btn_login_staff);

        SharedPreferences pref = getSharedPreferences("permission", 0);
        String prefRole = pref.getString("role", null);
        String role = prefRole == null ? "shop owner" : "staff" ;
        boolean isStaff = role.equals("staff");

        Toast.makeText(MainActivity.this, "You are " + role, Toast.LENGTH_SHORT).show();

        mBtnAllUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UsersListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mBtnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UsersListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (!isStaff) {
            mBtnLogin.setText("LOG IN AS STAFF");
        } else {
            mBtnLogin.setText("REMOVE MYSELF FROM ROLE STAFF");
        }


        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStaff) {
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    SharedPreferences pref = getSharedPreferences("permission", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.remove("role");
                    editor.commit();

                    finish();
                    startActivity(getIntent());
                }
            }
        });
    }
}
