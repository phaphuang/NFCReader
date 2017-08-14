package com.learn2crack.nfc.shopowneractivity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.learn2crack.nfc.MainActivity;
import com.learn2crack.nfc.NFCScanToConfirmActivity;
import com.learn2crack.nfc.R;
import com.learn2crack.nfc.UsersListActivity;

import java.text.NumberFormat;
import java.util.Locale;

public class ConfirmSellActivity extends AppCompatActivity {
    
    public static final String TAG = ConfirmSellActivity.class.getSimpleName();

    private TextView mCurrentBalance;
    private TextView mCurrentSell;
    private TextView mCurrentLeft;
    private TextView mUserName;
    private Button mProceed;
    private int amountLeft = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmsell);

        String mNfcId = getIntent().getStringExtra("NFCID");
        String totalSellAmount = getIntent().getStringExtra("TOTAL_SELL_AMOUNT");
        String firstName = getIntent().getStringExtra("FIRST_NAME");
        String lastName = getIntent().getStringExtra("LAST_NAME");
        String currentBalance = getIntent().getStringExtra("CURRENT_BALANCE");

        Toast.makeText(this, "Confirm sell nfc id : " + mNfcId + " with amount : " + totalSellAmount, Toast.LENGTH_SHORT).show();

        mCurrentBalance = (TextView) findViewById(R.id.current_balance);
        mCurrentSell = (TextView) findViewById(R.id.current_sell);
        mCurrentLeft = (TextView) findViewById(R.id.current_left);
        mProceed = (Button) findViewById(R.id.btn_proceed);
        mUserName = (TextView) findViewById(R.id.username);

        // SEND AJAX TO GET CURRENT BALANCE

        Log.i("INFO TOTAL AMOUNT", "totalSellAmount:" + totalSellAmount);
        //mCurrentBalance.setText("CURRENT BALANCE : 2800");
        mCurrentBalance.setText("CURRENT BALANCE : " + currentBalance);
        mUserName.setText("NAME : " + firstName+ " " +lastName);

        try {

            int totalSell = Integer.parseInt(totalSellAmount);
            int balance = Integer.parseInt(currentBalance);
            amountLeft = balance - totalSell;

            String formatAmount = NumberFormat.getNumberInstance(Locale.US).format(totalSell);
            mCurrentSell.setText("CURRENT BALANCE : " + formatAmount);

            if (amountLeft < 0) {
                mCurrentLeft.setText("TOTAL LEFT AMOUNT : " + amountLeft);
            } else {
                String formatAmountLeft = NumberFormat.getNumberInstance(Locale.US).format(amountLeft);
                mCurrentLeft.setText("TOTAL LEFT AMOUNT : " + formatAmountLeft);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (amountLeft < 0) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConfirmSellActivity.this);
                    alertDialog.setTitle("CONFIRM");
                    alertDialog.setMessage("Please contact staff to top up before purchasing");
                    //alertDialog.setMessage("Print: " + mNfcId.equals(mCurrentNfcId));
                    alertDialog.setNegativeButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    alertDialog.show();

                } else {

                    Intent intent = new Intent(ConfirmSellActivity.this, NFCScanToConfirmActivity.class);
                    intent.putExtra("NFCID", mNfcId);
                    intent.putExtra("TOTAL_AMOUNT", totalSellAmount);
                    intent.putExtra("FIRST_NAME", firstName);
                    intent.putExtra("LAST_NAME", lastName);
                    intent.putExtra("CURRENT_BALANCE", currentBalance);
                    intent.putExtra("ACTION", "SELLITEM");
                    startActivity(intent);
                    finish();
                    startActivity(intent);
                    finish();
                }
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
