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
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class NFCActivity extends AppCompatActivity implements Listener{
    
    public static final String TAG = NFCActivity.class.getSimpleName();

    private EditText mEtMessage;
    private Button mBtWrite;
    private Button mBtRead;
    private TextView mNfcId;
    private Button mBtnDoSearch;
    private Button mBtnRegister;

    private NFCWriteFragment mNfcWriteFragment;
    private NFCReadFragment mNfcReadFragment;

    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;

    private String currentBalance = "";
    private String firstName = "";
    private String lastName = "";
    private String userName = null;

    private NfcAdapter mNfcAdapter;

    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_register);

        initViews();
        initNFC();
    }

    private void initViews() {

        mNfcId = (TextView) findViewById(R.id.text_scan_to_search_nfc);
        mBtnDoSearch = (Button) findViewById(R.id.btn_search_by_nfc_id);
        mBtnRegister = (Button) findViewById(R.id.btn_register_nfc_user);

        userName = getIntent().getStringExtra("USER_NAME");

        if (userName != null) {
            mNfcId.setText("PLEASE SCAN NFC TO REGISTER");
        }


        mBtnDoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NFCActivity.this, TopUpDeductActivity.class);
                intent.putExtra("NFCID", mNfcId.getText().toString());
                intent.putExtra("FIRST_NAME", firstName);
                intent.putExtra("LAST_NAME", lastName);
                intent.putExtra("CURRENT_BALANCE", currentBalance);
                intent.putExtra("USER_NAME", userName);
                startActivity(intent);
                finish();
            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(NFCActivity.this);
                alertDialog.setTitle("REGISTER");
                alertDialog.setMessage("Enter Username");

                EditText input = new EditText(NFCActivity.this);
                if (userName != null) {
                    input.setText(userName);
                }
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mBtnRegister.setEnabled(false);
                                String username = input.getText().toString();
                                String nfcId = mNfcId.getText().toString();
                                checkDuplicatedUsername(username, nfcId);
                            }
                        });

                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();


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
            //Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();

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
                //Toast.makeText(NFCActivity.this,response,Toast.LENGTH_LONG).show();
                try {
                    JSONObject obj = new JSONObject(response.toString());
                    JSONArray dataArray = obj.getJSONArray("data");

                    if (dataArray.length() > 0) {
                        JSONObject finalObject = dataArray.getJSONObject(0);
                        firstName = finalObject.getString("f_name");
                        lastName = finalObject.getString("l_name");
                        currentBalance = finalObject.getString("current_amt");
                        userName = finalObject.getString("username");
                        //Toast.makeText(NFCSellActivity.this, currentAmount, Toast.LENGTH_SHORT).show();
                        mBtnDoSearch.setVisibility(View.VISIBLE);
                    } else {

                        mBtnRegister.setVisibility(View.VISIBLE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NFCActivity.this,error.toString(),Toast.LENGTH_LONG).show();
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

    private String checkDuplicatedUsername(String username, String nfcid)
    {

        Log.d("Username: ",  username );

        final String userName = username;
        final String nfcId = nfcid;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://philandeznetwork.000webhostapp.com/test_query_sql.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("JsonObject Response",response.toString());
                //Toast.makeText(NFCActivity.this,response,Toast.LENGTH_LONG).show();
                try {
                    JSONObject nameObj = new JSONObject(response.toString());
                    JSONArray dataArray = nameObj.getJSONArray("data");
                    for(int i = 0; i < dataArray.length(); i++) {
                        JSONObject finalObject = dataArray.getJSONObject(i);
                        firstName = finalObject.getString("f_name");
                        Log.d("Test Json Firstname", firstName);
                        lastName = finalObject.getString("l_name");
                    }


                    JSONObject obj = new JSONObject(response.toString());
                    status = obj.getString("status");
                    if (status.equals("true")) {
                        //Toast.makeText(NFCActivity.this, "Register user " + username + " with nfcId " + nfcId, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(NFCActivity.this, TopUpDeductActivity.class);
                        intent.putExtra("NFCID", nfcId);
                        intent.putExtra("FIRST_NAME", firstName);
                        intent.putExtra("LAST_NAME", lastName);
                        intent.putExtra("CURRENT_BALANCE", currentBalance);
                        startActivity(intent);
                        finish();
                    } else {
                        mBtnRegister.setEnabled(true);
                        Toast.makeText(NFCActivity.this, "Invalid username, please try again.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NFCActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("action", "CHECKUSERNAME");
                params.put("username" ,userName);
                params.put("nfcId" ,nfcId);
                //Log.d("ShowTag", "Value: " + tagId );
                return params;
            }
        };

        //stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        return status;
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
