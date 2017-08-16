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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.learn2crack.nfc.MainActivity;
import com.learn2crack.nfc.R;
import com.learn2crack.nfc.UsersListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConfirmSellActivity extends AppCompatActivity {
    
    public static final String TAG = ConfirmSellActivity.class.getSimpleName();

    private TextView mCurrentBalance;
    private TextView mCurrentSell;
    private TextView mCurrentLeft;
    private TextView mUserName;
    private Button mProceed;
    private int amountLeft = 0;
    private String currentBalance;
    private String currentSell;

    private String status;

    private String shopType;
    private String[] amounts = new String[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmsell);

        String mNfcId = getIntent().getStringExtra("NFCID");
        String totalSellAmount = getIntent().getStringExtra("TOTAL_SELL_AMOUNT");
        String firstName = getIntent().getStringExtra("FIRST_NAME");
        String lastName = getIntent().getStringExtra("LAST_NAME");
        String userName = getIntent().getStringExtra("USER_NAME");
        currentBalance = getIntent().getStringExtra("CURRENT_BALANCE");
        currentSell = totalSellAmount;

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

        shopType = getIntent().getStringExtra("SHOP");
        amounts[0] = getIntent().getStringExtra("AMOUNT_1");
        amounts[1] = getIntent().getStringExtra("AMOUNT_2");

        if (shopType.equals("BEVERAGE")) {

            amounts[2] = getIntent().getStringExtra("AMOUNT_3");
            amounts[3] = getIntent().getStringExtra("AMOUNT_4");
            amounts[4] = getIntent().getStringExtra("AMOUNT_5");

        }

        try {

            int totalSell = Integer.parseInt(totalSellAmount);
            int balance = Integer.parseInt(currentBalance);
            amountLeft = balance - totalSell;

            String formatAmount = NumberFormat.getNumberInstance(Locale.US).format(totalSell);
            mCurrentSell.setText("TOTAL SELL AMOUNT : " + formatAmount);

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
                    String strReceipt = getStringReceipt();

                    //sellitemToDatabase(mNfcId, totalSellAmount, firstName, lastName, userName);
                }
            }
        });

    }

    private void sellitemToDatabase(String nfcId, String amount, String firstName, String lastName, String userName)
    {

        Log.d("Deduct Amount ",  ":" + amount);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://nfcregis.tkhomeservice.co.th/api_nfc.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("JsonObject Response",response.toString());
                Toast.makeText(ConfirmSellActivity.this,response,Toast.LENGTH_LONG).show();
                try {

                    JSONObject obj = new JSONObject(response.toString());
                    status = obj.getString("status");

                    if (status.equals("true")) {
                        Toast.makeText(ConfirmSellActivity.this, "Update amount of: " + firstName + " " + lastName + " and current balance: " + currentBalance, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ConfirmSellActivity.this, SellfoodActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ConfirmSellActivity.this, "Invalid update deduct, please try again.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ConfirmSellActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("action", "SELLITEMAMOUNT");
                //params.put("firstName" ,firstName);
                //params.put("lastName" ,lastName);
                params.put("userName", userName);
                params.put("amount" ,amount);
                params.put("nfcId" ,nfcId);
                //Log.d("ShowTag", "Value: " + tagId );
                return params;
            }
        };

        //stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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

    private String getStringReceipt() {

        String BILL = "";
        try {
            String testText = shopType + "," + amounts[0] + "," + amounts[1] + "," + amounts[2] + "," + amounts[3] + "," + amounts[4];
            Toast.makeText(ConfirmSellActivity.this, testText, Toast.LENGTH_LONG).show();

            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dt.format(new Date());

            // OutputStream os = mBluetoothSocket.getOutputStream();

            BILL =  "                   CASHLESS PROJECT    \n" +
                    "                      " + strDate + "  \n";
            BILL = BILL
                    + "-----------------------------------------------\n";


            BILL = BILL + String.format("%1$-10s %2$10s %3$13s %4$10s", "Item", "Qty", "Rate", "Totel");
            BILL = BILL + "\n";
            BILL = BILL
                    + "-----------------------------------------------\n";
            if (shopType.equals("FOOD")) {
                if (amounts[0] != null) {
                    int amount = Integer.parseInt(amounts[0]);
                    BILL = BILL + String.format("%1$-10s %2$10s %3$11s %4$10s", "คนเล็กกุ๊กเทวดา", "", amounts[0], (amount * 88) + "");
                }

                if (amounts[1] != null) {
                    int amount = Integer.parseInt(amounts[1]);
                    BILL = BILL + String.format("%1$-10s %2$10s %3$11s %4$10s", "สองคนสองคม", "", amounts[1], (amount * 88) + "");
                }
            } else if (shopType.equals("BEVERAGE")) {
                if (amounts[0] != null) {
                    int amount = Integer.parseInt(amounts[0]);
                    BILL = BILL + String.format("%1$-10s %2$10s %3$11s %4$10s", "leo", "", amounts[0], (amount * 68) + "");
                }

                if (amounts[1] != null) {
                    int amount = Integer.parseInt(amounts[1]);
                    BILL = BILL + String.format("%1$-10s %2$10s %3$11s %4$10s", "singha", "", amounts[1], (amount * 98) + "");
                }

                if (amounts[2] != null) {
                    int amount = Integer.parseInt(amounts[2]);
                    BILL = BILL + String.format("%1$-10s %2$10s %3$11s %4$10s", "singha", "", amounts[2], (amount * 148) + "");
                }

                if (amounts[3] != null) {
                    int amount = Integer.parseInt(amounts[3]);
                    BILL = BILL + String.format("%1$-10s %2$10s %3$11s %4$10s", "singha", "", amounts[3], (amount * 188) + "");
                }

                if (amounts[4] != null) {
                    int amount = Integer.parseInt(amounts[4]);
                    BILL = BILL + String.format("%1$-10s %2$10s %3$11s %4$10s", "singha", "", amounts[4], (amount * 188) + "");
                }
            }

            BILL = BILL
                    + "\n-----------------------------------------------";
            BILL = BILL + "\n\n ";

            BILL = BILL + "                   Total Value:" + "      " + currentSell + "\n";
            BILL = BILL + "                   Total Balance Left:" + "     " + amountLeft + "\n";

            BILL = BILL
                    + "-----------------------------------------------\n";
            BILL = BILL + "\n\n ";

            /*os.write(BILL.getBytes());
            //This is printer specific code you can comment ==== > Start

            // Setting height
            int gs = 29;
            os.write(intToByteArray(gs));
            int h = 104;
            os.write(intToByteArray(h));
            int n = 162;
            os.write(intToByteArray(n));

            // Setting Width
            int gs_width = 29;
            os.write(intToByteArray(gs_width));
            int w = 119;
            os.write(intToByteArray(w));
            int n_width = 2;
            os.write(intToByteArray(n_width));*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        return BILL;
    }
}
