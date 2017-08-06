package com.learn2crack.nfc;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SellfoodActivity extends AppCompatActivity {
    
    public static final String TAG = SellfoodActivity.class.getSimpleName();

    private EditText mAmountSell;
    private Button mProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sellfood);

        mAmountSell = (EditText) findViewById(R.id.amount_sell);
        mProceed = (Button) findViewById(R.id.btn_proceed);

        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amountSell = mAmountSell.getText().toString();

                Toast.makeText(SellfoodActivity.this, "TOTAL SELL AMOUNT : " + amountSell, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SellfoodActivity.this, NFCSellActivity.class);
                intent.putExtra("TOTAL_SELL_AMOUNT", mAmountSell.getText().toString());
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
