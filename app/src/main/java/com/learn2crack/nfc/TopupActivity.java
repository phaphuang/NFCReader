package com.learn2crack.nfc;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class TopupActivity extends AppCompatActivity {
    
    public static final String TAG = TopupActivity.class.getSimpleName();

    private TextView mCurrentBalance;
    private EditText mAmountTopup;
    private EditText mAmountDeduct;
    private TextView mUserName;
    private Button mProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topup);

        mCurrentBalance = (TextView) findViewById(R.id.current_balance);
        mAmountTopup = (EditText) findViewById(R.id.amount_top_up);
        mAmountDeduct = (EditText) findViewById(R.id.amount_deduct);
        mProceed = (Button) findViewById(R.id.btn_proceed);
        mUserName = (TextView) findViewById(R.id.username);

        String nfcId = getIntent().getStringExtra("NFCID");
        String firstName = getIntent().getStringExtra("FIRST_NAME");
        String lastName = getIntent().getStringExtra("LAST_NAME");
        String currentBalance = getIntent().getStringExtra("CURRENT_BALANCE");

        if (nfcId != null) {
            Toast.makeText(this, "Top up with nfc id: " + nfcId, Toast.LENGTH_SHORT).show();
        }

        if (firstName != null) {
            Toast.makeText(this, "Top up with user id: " + firstName, Toast.LENGTH_SHORT).show();
        }

        // SEND AJAX TO GET CURRENT BALANCE
        mUserName.setText(firstName + " " + lastName);
        mCurrentBalance.setText("CURRENT BALANCE : " + currentBalance);

        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amountAdd = mAmountTopup.getText().toString();
                String amountDeduct = mAmountDeduct.getText().toString();

                Toast.makeText(TopupActivity.this, "ADD MONEY : " + amountAdd + ", DEDUCT MONEY:" + amountDeduct, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(TopupActivity.this, MainActivity.class);
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
