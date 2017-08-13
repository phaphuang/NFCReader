package com.learn2crack.nfc;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NFCScanToConfirmActivity extends AppCompatActivity implements Listener{
    
    public static final String TAG = NFCScanToConfirmActivity.class.getSimpleName();

    private TextView mTextScan;
    private Button mBtnProceed;

    private NFCWriteFragment mNfcWriteFragment;
    private NFCReadFragment mNfcReadFragment;

    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;

    private String currentBalance = "";
    private String userName = null;
    private String mCurrentNfcId;

    private NfcAdapter mNfcAdapter;

    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_scan_to_confirm);

        initViews();
        initNFC();
    }

    private void initViews() {

        mTextScan = (TextView) findViewById(R.id.text_scan_to_search_nfc);
        mBtnProceed = (Button) findViewById(R.id.btn_search_by_nfc_id);

        String mNfcId = getIntent().getStringExtra("NFCID");
        String action = getIntent().getStringExtra("ACTION");
        String firstName = getIntent().getStringExtra("FIRST_NAME");
        String lastName = getIntent().getStringExtra("LAST_NAME");
        String totalAmountString = getIntent().getStringExtra("TOTAL_AMOUNT");
        Integer totalAmount = Integer.valueOf(totalAmountString);

        Toast.makeText(NFCScanToConfirmActivity.this, "Check if the nfc id is " + mNfcId, Toast.LENGTH_SHORT).show();

        mBtnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mNfcId.equals(mCurrentNfcId)) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(NFCScanToConfirmActivity.this);
                    alertDialog.setTitle("SCAN");
                    alertDialog.setMessage("NFC does not match " + mNfcId + " AND " + mCurrentNfcId);
                    //alertDialog.setMessage("Print: " + mNfcId.equals(mCurrentNfcId));
                    alertDialog.setNegativeButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    alertDialog.show();
                } else {

                    Toast.makeText(NFCScanToConfirmActivity.this, "MATCH!!!! " + action + " , Amount: " + totalAmountString, Toast.LENGTH_SHORT).show();
                    if(action.equals("topup")){
                        //Toast.makeText(NFCScanToConfirmActivity.this, "TOP UP!!!", Toast.LENGTH_SHORT).show();
                        topupToDatabase(mNfcId, totalAmountString, firstName, lastName);
                    }else if(action.equals("deduct")){
                        deductToDatabase(mNfcId, totalAmountString, firstName, lastName);
                    }else{
                        Toast.makeText(NFCScanToConfirmActivity.this, "Not topup or deduct: " + action, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    private void initNFC(){

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    public void onDialogDisplayed() {

        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {

        isDialogDisplayed = false;
        isWrite = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected,tagDetected,ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if(mNfcAdapter!= null)
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mNfcAdapter!= null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Log.d(TAG, "onNewIntent: "+intent.getAction());

        if(tag != null) {
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();

            //Log.d(TAG, "tag ID = " + tag.getId().toString());
            //String textId = tag.getId().toString();
            byte[] tagId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            String hexdump = new String();
            for (int i = 0; i < tagId.length; i++) {
                String x = Integer.toHexString(((int) tagId[i] & 0xff));
                if (x.length() == 1) {
                    x = '0' + x;
                }
                hexdump += x + ' ';
            }
            mTextScan.setText(hexdump);
            mCurrentNfcId = hexdump;
            mBtnProceed.setVisibility(View.VISIBLE);
        }
    }

    private void topupToDatabase(String nfcId, String amount, String firstName, String lastName)
    {

        Log.d("Top Up Amount ",  ":" + amount);

        /*final Integer totalAmount = amount;
        final String nfcId = nfcid;
        final String firstName = first_name;
        final String lastName = last_name;*/

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://philandeznetwork.000webhostapp.com/test_query_sql.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("JsonObject Response",response.toString());
                Toast.makeText(NFCScanToConfirmActivity.this,response,Toast.LENGTH_LONG).show();
                try {
                    JSONObject nameObj = new JSONObject(response.toString());
                    JSONArray dataArray = nameObj.getJSONArray("data");
                    for(int i = 0; i < dataArray.length(); i++) {
                        JSONObject finalObject = dataArray.getJSONObject(i);
                        currentBalance = finalObject.getString("current_amt");
                        Log.d("Test Json Firstname", currentBalance);
                    }

                    JSONObject obj = new JSONObject(response.toString());
                    status = obj.getString("status");

                    if (status.equals("true")) {
                        //Toast.makeText(NFCScanToConfirmActivity.this, "Update topup name " + firstName + " " + lastName + " with amount " + currentBalance, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(NFCScanToConfirmActivity.this, TopUpDeductActivity.class);
                        intent.putExtra("NFCID", nfcId);
                        intent.putExtra("FIRST_NAME", firstName);
                        intent.putExtra("LAST_NAME", lastName);
                        intent.putExtra("CURRENT_BALANCE", currentBalance);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(NFCScanToConfirmActivity.this, "Invalid update top up, please try again.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NFCScanToConfirmActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("action", "TOPUPAMOUNT");
                params.put("firstName" ,firstName);
                params.put("lastName" ,lastName);
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

    private void deductToDatabase(String nfcId, String amount, String firstName, String lastName)
    {

        Log.d("Deduct Amount ",  ":" + amount);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://philandeznetwork.000webhostapp.com/test_query_sql.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("JsonObject Response",response.toString());
                Toast.makeText(NFCScanToConfirmActivity.this,response,Toast.LENGTH_LONG).show();
                try {
                    JSONObject nameObj = new JSONObject(response.toString());
                    JSONArray dataArray = nameObj.getJSONArray("data");
                    for(int i = 0; i < dataArray.length(); i++) {
                        JSONObject finalObject = dataArray.getJSONObject(i);
                        currentBalance = finalObject.getString("current_amt");
                        Log.d("Test Json Firstname", currentBalance);
                    }

                    JSONObject obj = new JSONObject(response.toString());
                    status = obj.getString("status");

                    if (status.equals("true")) {
                        //Toast.makeText(NFCScanToConfirmActivity.this, "Update topup name " + firstName + " " + lastName + " with amount " + currentBalance, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(NFCScanToConfirmActivity.this, TopUpDeductActivity.class);
                        intent.putExtra("NFCID", nfcId);
                        intent.putExtra("FIRST_NAME", firstName);
                        intent.putExtra("LAST_NAME", lastName);
                        intent.putExtra("CURRENT_BALANCE", currentBalance);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(NFCScanToConfirmActivity.this, "Invalid update top up, please try again.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NFCScanToConfirmActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("action", "DEDUCTAMOUNT");
                params.put("firstName" ,firstName);
                params.put("lastName" ,lastName);
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
