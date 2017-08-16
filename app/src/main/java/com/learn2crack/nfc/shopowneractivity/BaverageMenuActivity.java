package com.learn2crack.nfc.shopowneractivity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.learn2crack.nfc.MainActivity;
import com.learn2crack.nfc.R;
import com.learn2crack.nfc.UsersListActivity;

import java.text.NumberFormat;
import java.util.Locale;

public class BaverageMenuActivity extends AppCompatActivity {
    
    public static final String TAG = BaverageMenuActivity.class.getSimpleName();

    private Spinner spinner1;
    private Spinner spinner2;
    private Spinner spinner3;
    private Spinner spinner4;
    private Spinner spinner5;
    private Button btnProceed;
    private TextView text;
    private int[] totalMenu = new int[5];
    private int[] amountMenu = new int[5];

    private int totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bav_menu);

        spinner1 = (Spinner) findViewById(R.id.spinner_amount1);
        spinner2 = (Spinner) findViewById(R.id.spinner_amount2);
        spinner3 = (Spinner) findViewById(R.id.spinner_amount3);
        spinner4 = (Spinner) findViewById(R.id.spinner_amount4);
        spinner5 = (Spinner) findViewById(R.id.spinner_amount5);
        btnProceed = (Button)  findViewById(R.id.btn_proceed);
        text = (TextView) findViewById(R.id.total);

        totalAmount = 0;

        btnProceed.setEnabled(false);
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(BaverageMenuActivity.this);
                alertDialog.setTitle("CONFIRM");
                alertDialog.setMessage("Are you sure to sell food for total " + totalAmount + " bath ?");
                //alertDialog.setMessage("Print: " + mNfcId.equals(mCurrentNfcId));
                alertDialog.setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(BaverageMenuActivity.this, NFCSellActivity.class);
                                intent.putExtra("TOTAL_SELL_AMOUNT", totalAmount + "");
                                intent.putExtra("SHOP", "BEVERAGE");
                                intent.putExtra("AMOUNT_1", amountMenu[0] + "");
                                intent.putExtra("AMOUNT_2", amountMenu[1] + "");
                                intent.putExtra("AMOUNT_3", amountMenu[2] + "");
                                intent.putExtra("AMOUNT_4", amountMenu[3] + "");
                                intent.putExtra("AMOUNT_5", amountMenu[4] + "");
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

                alertDialog.show();
            }
        });

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                totalMenu[0] = 68 * position;
                amountMenu[0] = position;
                calculateTotalAmount();
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

                totalMenu[1] = 98 * position;
                amountMenu[1] = position;
                calculateTotalAmount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                totalMenu[2] = 148 * position;
                amountMenu[2] = position;
                calculateTotalAmount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                totalMenu[3] = 188 * position;
                amountMenu[3] = position;
                calculateTotalAmount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        spinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                totalMenu[4] = 188 * position;
                amountMenu[4] = position;
                calculateTotalAmount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    private void calculateTotalAmount() {
        totalAmount = 0;
        for (int price : totalMenu) {
            totalAmount += price;
        }
        String formatAmount = NumberFormat.getNumberInstance(Locale.US).format(totalAmount);
        text.setText("Total : " + formatAmount + " บาท");
        btnProceed.setEnabled(totalAmount > 0);
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
