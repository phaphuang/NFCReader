package com.learn2crack.nfc.shopowneractivity;

/*import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;*/

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.learn2crack.nfc.Listener;
import com.learn2crack.nfc.MainActivity;
import com.learn2crack.nfc.NFCReadFragment;
import com.learn2crack.nfc.NFCWriteFragment;
import com.learn2crack.nfc.R;
import com.learn2crack.nfc.UsersListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//import android.view.View.OnClickListener;

public class NFCSellActivity extends AppCompatActivity implements Listener {
    
    public static final String TAG = NFCSellActivity.class.getSimpleName();

    private EditText mEtMessage;
    private Button mBtWrite;
    private Button mBtRead;
    private TextView mNfcId;
    private Button mBtnDoSearch;

    private NFCWriteFragment mNfcWriteFragment;
    private NFCReadFragment mNfcReadFragment;

    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;
    private boolean isRegister = false;

    private NfcAdapter mNfcAdapter;

    private String currentBalance = "";
    private String firstName = "";
    private String lastName = "";
    private String currentTagId = "";
    private String userName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_sell);

        initViews();
        initNFC();
    }

    private void initViews() {

        mNfcId = (TextView) findViewById(R.id.text_scan_to_search_nfc);
        mBtnDoSearch = (Button) findViewById(R.id.btn_search_by_nfc_id);

        String totalSellAmount = getIntent().getStringExtra("TOTAL_SELL_AMOUNT");

        Toast.makeText(this, "TOTAL SELL AMOUNT : " + totalSellAmount, Toast.LENGTH_SHORT).show();

        mBtnDoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isRegister) {

                    Intent intent = new Intent(NFCSellActivity.this, ConfirmSellActivity.class);
                    intent.putExtra("FIRST_NAME", firstName);
                    intent.putExtra("LAST_NAME", lastName);
                    intent.putExtra("CURRENT_BALANCE", currentBalance);
                    intent.putExtra("NFCID", currentTagId);
                    intent.putExtra("USER_NAME", userName);
                    intent.putExtra("TOTAL_SELL_AMOUNT", totalSellAmount);
                    startActivity(intent);
                    finish();

                } else {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(NFCSellActivity.this);
                    alertDialog.setTitle("CONFIRM");
                    alertDialog.setMessage("This user hasn't registered yet, please contact staff");
                    //alertDialog.setMessage("Print: " + mNfcId.equals(mCurrentNfcId));
                    alertDialog.setNegativeButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    alertDialog.show();
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
            Ndef ndef = Ndef.get(tag);

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
            mNfcId.setText(hexdump);
            sendRequestJson(hexdump);
        }
    }

    private void sendRequestJson(String mId)
    {

        Log.d("ADebugTag", "Value: " + mId );

        final String tagId = mId;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://philandeznetwork.000webhostapp.com/test_query_sql.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("JsonObject Response",response.toString());
                Toast.makeText(NFCSellActivity.this,response,Toast.LENGTH_LONG).show();
                try {
                    JSONObject obj = new JSONObject(response.toString());
                    JSONArray dataArray = obj.getJSONArray("data");

                    if (dataArray.length() > 0) {
                        JSONObject finalObject = dataArray.getJSONObject(0);
                        firstName = finalObject.getString("f_name");
                        lastName = finalObject.getString("l_name");
                        currentBalance = finalObject.getString("current_amt");
                        currentTagId = finalObject.getString("tag_id");
                        userName = finalObject.getString("username");
                        //Toast.makeText(NFCSellActivity.this, currentAmount, Toast.LENGTH_SHORT).show();
                        isRegister = true;
                    } else {
                        isRegister = false;
                    }
                    mBtnDoSearch.setEnabled(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NFCSellActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("action", "QUERYTOPUP");
                params.put("sendId" ,tagId);
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
