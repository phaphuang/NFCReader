package com.learn2crack.nfc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Aniwat on 8/7/2017.
 */

public class UpdateTopupActivity extends AppCompatActivity{
    private Button topupBtn;
    private Integer totalAmt = 0;
    private String display = "";
    private ImageButton plus20;
    private ImageButton plus50;
    private ImageButton plus100;
    private ImageButton plus500;
    private ImageButton plus1000;
    private ImageButton minus20;
    private ImageButton minus50;
    private ImageButton minus100;
    private ImageButton minus500;
    private ImageButton minus1000;

    private int amt = 0;
    private int temp = 0;
    private Boolean checkNumber = true;

    private TextView mCurrentBalance;
    private TextView mUserName;

    private String mNfcId;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);

        mCurrentBalance = (TextView) findViewById(R.id.userBalance);
        mUserName = (TextView) findViewById(R.id.nameOfUser);

        //String firstName = getIntent().getStringExtra("FIRST_NAME");
        //String lastName = getIntent().getStringExtra("LAST_NAME");
        String currentBalance = getIntent().getStringExtra("CURRENT_BALANCE");
        //mNfcId = getIntent().getStringExtra("NFCID");

        mUserName.setText("NAME: " + getIntent().getStringExtra("FIRST_NAME") + " " + getIntent().getStringExtra("LAST_NAME"));
        mCurrentBalance.setText("CURRENT BALANCE : " + currentBalance);


        topupBtn = (Button) findViewById(R.id.topup_btn);
        display = "TOP UP " + totalAmt + " BAHT";
        topupBtn.setText(display);

        initCalculate();

        topupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(UpdateTopupActivity.this, "ADD MONEY : " + totalAmt, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(UpdateTopupActivity.this, NFCScanToConfirmActivity.class);
                intent.putExtra("NFCID", getIntent().getStringExtra("NFCID"));
                intent.putExtra("TOTAL_AMOUNT", totalAmt.toString());
                intent.putExtra("FIRST_NAME", getIntent().getStringExtra("FIRST_NAME"));
                intent.putExtra("LAST_NAME", getIntent().getStringExtra("LAST_NAME"));
                intent.putExtra("USER_NAME", getIntent().getStringExtra("USER_NAME"));
                intent.putExtra("ACTION", "topup");
                startActivity(intent);
                finish();
            }
        });
    }

    protected void updateNumber(){
        display = "TOP UP " + totalAmt + " BAHT";
        topupBtn.setText(display);
    }

    protected boolean checkNegativeNumber(int number){
        if(number < 0){
            Toast.makeText(this, "Invalid number!!", Toast.LENGTH_SHORT).show();
            return true;
        }else{
            return false;
        }
    }

    protected void initCalculate()
    {
        plus20 = (ImageButton) findViewById(R.id.imagePlus20);
        plus50 = (ImageButton) findViewById(R.id.imagePlus50);
        plus100 = (ImageButton) findViewById(R.id.imagePlus100);
        plus500 = (ImageButton) findViewById(R.id.imagePlus500);
        plus1000 = (ImageButton) findViewById(R.id.imagePlus1000);

        minus20 = (ImageButton) findViewById(R.id.imageMinus20);
        minus50 = (ImageButton) findViewById(R.id.imageMinus50);
        minus100 = (ImageButton) findViewById(R.id.imageMinus100);
        minus500 = (ImageButton) findViewById(R.id.imageMinus500);
        minus1000 = (ImageButton) findViewById(R.id.imageMinus1000);


        plus20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("Add 20:", "Value: " + amt);
                amt = 20;
                totalAmt += amt;
                updateNumber();
            }
        });

        plus50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amt = 50;
                totalAmt += amt;
                updateNumber();
            }
        });

        plus100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amt = 100;
                totalAmt += amt;
                updateNumber();
            }
        });

        plus500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amt = 500;
                totalAmt += amt;
                updateNumber();
            }
        });

        plus1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amt = 1000;
                totalAmt += amt;
                updateNumber();
            }
        });

        minus20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amt = 20;
                temp = totalAmt - amt;
                checkNumber = checkNegativeNumber(temp);
                if(checkNumber == false){
                    totalAmt -= amt;
                    updateNumber();
                }
            }
        });

        minus50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amt = 50;
                temp = totalAmt - amt;
                checkNumber = checkNegativeNumber(temp);
                if(checkNumber == false){
                    totalAmt -= amt;
                    updateNumber();
                }
            }
        });

        minus100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amt = 100;
                temp = totalAmt - amt;
                checkNumber = checkNegativeNumber(temp);
                if(checkNumber == false){
                    totalAmt -= amt;
                    updateNumber();
                }
            }
        });

        minus500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amt = 500;
                temp = totalAmt - amt;
                checkNumber = checkNegativeNumber(temp);
                if(checkNumber == false){
                    totalAmt -= amt;
                    updateNumber();
                }
            }
        });

        minus1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amt = 1000;
                temp = totalAmt - amt;
                checkNumber = checkNegativeNumber(temp);
                if(checkNumber == false){
                    totalAmt -= amt;
                    updateNumber();
                }
            }
        });
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
