package com.learn2crack.nfc.shopowneractivity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.learn2crack.nfc.MainActivity;
import com.learn2crack.nfc.R;
import com.learn2crack.nfc.UsersListActivity;

import java.text.NumberFormat;
import java.util.Locale;

public class FoodMenuActivity extends AppCompatActivity {
    
    public static final String TAG = FoodMenuActivity.class.getSimpleName();

    private Spinner spinner1;
    private Spinner spinner2;
    private Button btnProceed;
    private TextView text;
    private int amountMenu1, amountMenu2;

    private int totalAmount, totalMenu1, totalMenu2;

    String[] spinnerItems = new String[]{
            "x0",
            "x1",
            "x2",
            "x3",
            "x4",
            "x5",
            "x6",
            "x7",
            "x8",
            "x9",
            "x10",
            "x11",
            "x12",
            "x13",
            "x14",
            "x15",
            "x16",
            "x17",
            "x18",
            "x19",
            "x20"

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_menu);

        spinner1 = (Spinner) findViewById(R.id.spinner_amount1);
        spinner2 = (Spinner) findViewById(R.id.spinner_amount2);
        text = (TextView) findViewById(R.id.total);
        btnProceed = (Button)  findViewById(R.id.btn_proceed);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.textview_spinner, spinnerItems);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.textview_spinner);

        spinner1.setAdapter(spinnerArrayAdapter);
        spinner2.setAdapter(spinnerArrayAdapter);

        totalAmount = 0;
        amountMenu1 = 0;
        amountMenu2 = 0;

        btnProceed.setEnabled(false);
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FoodMenuActivity.this, NFCSellActivity.class);
                intent.putExtra("TOTAL_SELL_AMOUNT", totalAmount + "");
                intent.putExtra("SHOP", "FOOD");
                intent.putExtra("AMOUNT_1", amountMenu1 + "");
                intent.putExtra("AMOUNT_2", amountMenu2 + "");
                startActivity(intent);
                finish();

                /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodMenuActivity.this);
                alertDialog.setTitle("CONFIRM");
                alertDialog.setMessage("Are you sure to sell food for total " + totalAmount + " bath ?");
                //alertDialog.setMessage("Print: " + mNfcId.equals(mCurrentNfcId));
                alertDialog.setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(FoodMenuActivity.this, NFCSellActivity.class);
                                intent.putExtra("TOTAL_SELL_AMOUNT", totalAmount + "");
                                intent.putExtra("SHOP", "FOOD");
                                intent.putExtra("AMOUNT_1", amountMenu1 + "");
                                intent.putExtra("AMOUNT_2", amountMenu2 + "");
                                startActivity(intent);
                                finish();
                            }
                        });

                alertDialog.setPositiveButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();*/
            }
        });

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                amountMenu1 = position;
                totalMenu1 = 88 * position;
                totalAmount = totalMenu1 + totalMenu2;
                String formatAmoutn = NumberFormat.getNumberInstance(Locale.US).format(totalAmount);
                text.setText("Total : " + formatAmoutn + " บาท");
                btnProceed.setEnabled(totalAmount > 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                amountMenu2 = position;
                totalMenu2 = 88 * position;
                totalAmount = totalMenu1 + totalMenu2;
                String formatAmoutn = NumberFormat.getNumberInstance(Locale.US).format(totalAmount);
                text.setText("Total : " + formatAmoutn + " บาท");
                btnProceed.setEnabled(totalAmount > 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
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
