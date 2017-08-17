package com.learn2crack.nfc.shopowneractivity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.learn2crack.nfc.MainActivity;
import com.learn2crack.nfc.NFCScanToConfirmActivity;
import com.learn2crack.nfc.R;
import com.learn2crack.nfc.UsersListActivity;

import org.w3c.dom.Text;

public class TopupActivity extends AppCompatActivity {
    
    public static final String TAG = TopupActivity.class.getSimpleName();

    private Button mBtnTopup;
    private Button mBtnDeduct;
    private TextView mBalance;
    private TextView mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topup);

        mBtnTopup = (Button) findViewById(R.id.btn_topup);
        mBtnDeduct = (Button) findViewById(R.id.btn_deduct);
        mName = (TextView) findViewById(R.id.username);
        mBalance = (TextView) findViewById(R.id.current_balance);
        String currentBalance = getIntent().getStringExtra("CURRENT_BALANCE");
        mName.setText("NAME: " + getIntent().getStringExtra("FIRST_NAME") + " " + getIntent().getStringExtra("LAST_NAME"));
        mBalance.setText("CURRENT BALANCE : " + currentBalance);

        SharedPreferences pref = getSharedPreferences("permission", 0);
        String prefRole = pref.getString("role", null);
        String role = prefRole == null ? "shop owner" : "staff" ;
        boolean isStaff = role.equals("staff");

        if (!isStaff) {
            mBtnTopup.setEnabled(false);
            mBtnTopup.setVisibility(View.INVISIBLE);

            mBtnDeduct.setEnabled(false);
            mBtnDeduct.setVisibility(View.INVISIBLE);
        }


        mBtnTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(TopupActivity.this);
                alertDialog.setTitle("TOPUP");
                alertDialog.setMessage("Enter amount to topup");

                EditText input = new EditText(TopupActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                try {
                                    int totalAmount = Integer.parseInt(input.getText().toString());
                                    if (totalAmount > 0) {

                                        Intent intent = new Intent(TopupActivity.this, NFCScanToConfirmActivity.class);
                                        intent.putExtra("NFCID", getIntent().getStringExtra("NFCID"));
                                        intent.putExtra("TOTAL_AMOUNT", totalAmount + "");
                                        intent.putExtra("FIRST_NAME", getIntent().getStringExtra("FIRST_NAME"));
                                        intent.putExtra("LAST_NAME", getIntent().getStringExtra("LAST_NAME"));
                                        intent.putExtra("USER_NAME", getIntent().getStringExtra("USER_NAME"));
                                        intent.putExtra("CURRENT_BALANCE", getIntent().getStringExtra("CURRENT_BALANCE"));
                                        intent.putExtra("ACTION", "topup");
                                        startActivity(intent);
                                        finish();

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }
        });

        mBtnDeduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(TopupActivity.this);
                alertDialog.setTitle("DEDUCT");
                alertDialog.setMessage("Enter amount to deduct");

                EditText input = new EditText(TopupActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                try {
                                    int totalAmount = Integer.parseInt(input.getText().toString());
                                    int balance = Integer.parseInt(currentBalance);
                                    if (totalAmount > 0 && balance - totalAmount >= 0) {

                                        Intent intent = new Intent(TopupActivity.this, NFCScanToConfirmActivity.class);
                                        intent.putExtra("NFCID", getIntent().getStringExtra("NFCID"));
                                        intent.putExtra("TOTAL_AMOUNT", totalAmount + "");
                                        intent.putExtra("FIRST_NAME", getIntent().getStringExtra("FIRST_NAME"));
                                        intent.putExtra("LAST_NAME", getIntent().getStringExtra("LAST_NAME"));
                                        intent.putExtra("USER_NAME", getIntent().getStringExtra("USER_NAME"));
                                        intent.putExtra("ACTION", "deduct");
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Toast.makeText(TopupActivity.this, "Deduct must be les then balance", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }
        });

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
        Intent intent = new Intent(this, UsersListActivity.class);
        startActivity(intent);
        finish();
    }
}
