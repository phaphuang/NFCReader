package com.learn2crack.nfc;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class TopUpDeductActivity extends AppCompatActivity {

    public static final String TAG = TopUpDeductActivity.class.getSimpleName();

    private TextView mCurrentBalance;
    private TextView mTextAdd;
    private TextView mTextDeduct;
    private TextView mUserName;
    private Button mProceed;
    private Button mTopup;
    private Button mDeduuct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topupdeduct);

        mCurrentBalance = (TextView) findViewById(R.id.current_balance);
        mTextAdd = (TextView) findViewById(R.id.add_money);
        mTextDeduct = (TextView) findViewById(R.id.deduct_money);
        mProceed = (Button) findViewById(R.id.btn_proceed);
        mUserName = (TextView) findViewById(R.id.username);
        mTopup = (Button) findViewById(R.id.add_btn);
        mDeduuct = (Button) findViewById(R.id.deduct_btn);

        String nfcId = getIntent().getStringExtra("NFCID");
        String firstName = getIntent().getStringExtra("FIRST_NAME");
        String lastName = getIntent().getStringExtra("LAST_NAME");
        String currentBalance = getIntent().getStringExtra("CURRENT_BALANCE");

        SharedPreferences pref = getSharedPreferences("permission", 0);
        String prefRole = pref.getString("role", null);
        String role = prefRole == null ? "shop owner" : "staff" ;
        boolean isStaff = role.equals("staff");

        if (nfcId != null) {
            Toast.makeText(this, "Top up with nfc id: " + nfcId, Toast.LENGTH_SHORT).show();
        }

        if (firstName != null) {
            Toast.makeText(this, "Top up with user id: " + firstName, Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(this, "Your role is: " + role, Toast.LENGTH_SHORT).show();

        // SEND AJAX TO GET CURRENT BALANCE
        mUserName.setText(firstName + " " + lastName);
        mCurrentBalance.setText("CURRENT BALANCE : " + currentBalance);

        if (!isStaff) {
            mTopup.setEnabled(false);
            mTopup.setVisibility(View.INVISIBLE);

            mDeduuct.setEnabled(false);
            mDeduuct.setVisibility(View.INVISIBLE);

            mProceed.setEnabled(false);
            mProceed.setVisibility(View.INVISIBLE);

            mTextAdd.setVisibility(View.INVISIBLE);
            mTextDeduct.setVisibility(View.INVISIBLE);
        }

        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String amountAdd = mAmountTopup.getText().toString();
                //String amountDeduct = mAmountDeduct.getText().toString();

                //Toast.makeText(TopUpDeductActivity.this, "ADD MONEY : " + amountAdd + ", DEDUCT MONEY:" + amountDeduct, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(TopUpDeductActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(TopUpDeductActivity.this, UpdateTopupActivity.class);
                intent.putExtra("FIRST_NAME", getIntent().getStringExtra("FIRST_NAME"));
                intent.putExtra("LAST_NAME", getIntent().getStringExtra("LAST_NAME"));
                intent.putExtra("CURRENT_BALANCE", getIntent().getStringExtra("CURRENT_BALANCE"));
                startActivity(intent);
                finish();
            }
        });

        mDeduuct.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(TopUpDeductActivity.this, UpdateDeductActivity.class);
                intent.putExtra("FIRST_NAME", getIntent().getStringExtra("FIRST_NAME"));
                intent.putExtra("LAST_NAME", getIntent().getStringExtra("LAST_NAME"));
                intent.putExtra("CURRENT_BALANCE", getIntent().getStringExtra("CURRENT_BALANCE"));
                startActivity(intent);
                finish();
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
