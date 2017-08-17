package com.learn2crack.nfc;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    
    public static final String TAG = RegisterActivity.class.getSimpleName();

    private Button mBtnLogin;
    private EditText mUsername;
    private EditText mPassword;
    private Button mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_login);

        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mBack = (Button) findViewById(R.id.btn_register_back);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                boolean isLoginSuccess = userName.equals("admin") && password.equals("nfc1234");
                if (isLoginSuccess) {

                    SharedPreferences pref = getSharedPreferences("permission", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("role", "staff");
                    editor.apply();

                    Toast.makeText(RegisterActivity.this, "YOU'VE BEEN GRANTED ROLE STAFF RIGHT NOW.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("Error occurs while processing login");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    builder.show();

                }
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }

    @Override
    public void onBackPressed() {

    }
}
