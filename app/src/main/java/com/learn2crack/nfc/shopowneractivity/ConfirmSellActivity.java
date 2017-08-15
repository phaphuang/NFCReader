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
import com.learn2crack.nfc.NFCScanToConfirmActivity;
import com.learn2crack.nfc.R;
import com.learn2crack.nfc.UsersListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
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

    private String status;

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
                    sellitemToDatabase(mNfcId, totalSellAmount, firstName, lastName, userName);
                    /*Intent intent = new Intent(ConfirmSellActivity.this, NFCScanToConfirmActivity.class);
                    intent.putExtra("NFCID", mNfcId);
                    intent.putExtra("TOTAL_AMOUNT", totalSellAmount);
                    intent.putExtra("FIRST_NAME", firstName);
                    intent.putExtra("LAST_NAME", lastName);
                    intent.putExtra("USER_NAME", userName);
                    intent.putExtra("CURRENT_BALANCE", currentBalance);
                    intent.putExtra("ACTION", "sellitem");
                    startActivity(intent);
                    finish();*/
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
}
