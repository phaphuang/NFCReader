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
import android.widget.Toast;

import com.learn2crack.nfc.MainActivity;
import com.learn2crack.nfc.R;
import com.learn2crack.nfc.UsersListActivity;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.Locale;

public class BaverageMenuActivity extends AppCompatActivity {

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

    public static final String TAG = BaverageMenuActivity.class.getSimpleName();

    private Spinner spinner1;
    private Spinner spinner2;
    private Spinner spinner3;
    private Spinner spinner4;
    private Spinner spinner5;
    private Spinner spinner6;
    private Spinner spinner7;
    private Spinner spinner8;
    private Button btnProceed;
    private TextView text;
    private int[] totalMenu = new int[8];
    private int[] amountMenu = new int[8];

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
        spinner6 = (Spinner) findViewById(R.id.spinner_amount6);
        spinner7 = (Spinner) findViewById(R.id.spinner_amount7);
        spinner8 = (Spinner) findViewById(R.id.spinner_amount8);
        btnProceed = (Button)  findViewById(R.id.btn_proceed);
        text = (TextView) findViewById(R.id.total);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.textview_spinner, spinnerItems);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.textview_spinner);

        spinner1.setAdapter(spinnerArrayAdapter);
        spinner2.setAdapter(spinnerArrayAdapter);
        spinner3.setAdapter(spinnerArrayAdapter);
        spinner4.setAdapter(spinnerArrayAdapter);
        spinner5.setAdapter(spinnerArrayAdapter);
        spinner6.setAdapter(spinnerArrayAdapter);
        spinner7.setAdapter(spinnerArrayAdapter);
        spinner8.setAdapter(spinnerArrayAdapter);

        totalAmount = 0;

        btnProceed.setEnabled(false);
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String testText = amountMenu[5] + "" + amountMenu[6] + amountMenu[7];
                //Toast.makeText(BaverageMenuActivity.this, testText, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(BaverageMenuActivity.this, NFCSellActivity.class);
                intent.putExtra("TOTAL_SELL_AMOUNT", totalAmount + "");
                intent.putExtra("SHOP", "BEVERAGE");
                intent.putExtra("AMOUNT_1", amountMenu[0] + "");
                intent.putExtra("AMOUNT_2", amountMenu[1] + "");
                intent.putExtra("AMOUNT_3", amountMenu[2] + "");
                intent.putExtra("AMOUNT_4", amountMenu[3] + "");
                intent.putExtra("AMOUNT_5", amountMenu[4] + "");
                intent.putExtra("AMOUNT_6", amountMenu[5] + "");
                intent.putExtra("AMOUNT_7", amountMenu[6] + "");
                intent.putExtra("AMOUNT_8", amountMenu[7] + "");
                startActivity(intent);
                finish();

                /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(BaverageMenuActivity.this);
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

                alertDialog.show();*/
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

                totalMenu[1] = 78 * position;
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

        spinner6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                totalMenu[5] = 20 * position;
                amountMenu[5] = position;
                calculateTotalAmount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        spinner7.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                totalMenu[6] = 30 * position;
                amountMenu[6] = position;
                calculateTotalAmount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        spinner8.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                totalMenu[7] = 138 * position;
                amountMenu[7] = position;
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
