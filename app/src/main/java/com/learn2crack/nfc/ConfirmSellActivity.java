package com.learn2crack.nfc;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmSellActivity extends AppCompatActivity {
    
    public static final String TAG = ConfirmSellActivity.class.getSimpleName();

    private TextView mCurrentBalance;
    private TextView mCurrentSell;
    private TextView mCurrentLeft;
    private Button mProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmsell);

        String nfcid = getIntent().getStringExtra("NFCID");
        String totalSellAmount = getIntent().getStringExtra("TOTAL_SELL_AMOUNT");

        Toast.makeText(this, "Confirm sell nfc id : " + nfcid + " with amount : " + totalSellAmount, Toast.LENGTH_SHORT).show();

        mCurrentBalance = (TextView) findViewById(R.id.current_balance);
        mCurrentSell = (TextView) findViewById(R.id.current_sell);
        mCurrentLeft = (TextView) findViewById(R.id.current_left);
        mProceed = (Button) findViewById(R.id.btn_proceed);

        // SEND AJAX TO GET CURRENT BALANCE

        mCurrentBalance.setText("CURRENT BALANCE : 2800");

        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmSellActivity.this, MainActivity.class);
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
