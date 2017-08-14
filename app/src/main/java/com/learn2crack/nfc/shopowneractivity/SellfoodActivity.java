package com.learn2crack.nfc.shopowneractivity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.learn2crack.nfc.MainActivity;
import com.learn2crack.nfc.R;
import com.learn2crack.nfc.UsersListActivity;

public class SellfoodActivity extends AppCompatActivity {
    
    public static final String TAG = SellfoodActivity.class.getSimpleName();

    private Button mFood;
    private Button mBeverage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sellfood);

        mFood = (Button) findViewById(R.id.btn_food);
        mBeverage = (Button) findViewById(R.id.btn_bev);

        mFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SellfoodActivity.this, FoodMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mBeverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SellfoodActivity.this, BaverageMenuActivity.class);
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
