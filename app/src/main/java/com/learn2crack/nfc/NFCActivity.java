package com.learn2crack.nfc;

/*import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;*/

import android.app.PendingIntent;
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

import java.util.HashMap;
import java.util.Map;

//import android.view.View.OnClickListener;

public class NFCActivity extends AppCompatActivity implements Listener{
    
    public static final String TAG = NFCActivity.class.getSimpleName();

    private EditText mEtMessage;
    private Button mBtWrite;
    private Button mBtRead;
    private TextView mNfcId;
    private Button mBtnDoSearch;

    private NFCWriteFragment mNfcWriteFragment;
    private NFCReadFragment mNfcReadFragment;

    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;

    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_register);

        initViews();
        initNFC();
    }

    private void initViews() {

        // mEtMessage = (EditText) findViewById(R.id.et_message);
        // mBtWrite = (Button) findViewById(R.id.btn_write);
        // mBtRead = (Button) findViewById(R.id.btn_read);
        //mBtnMenu = (Button) findViewById(R.id.btn_mainmenu);
        mNfcId = (TextView) findViewById(R.id.text_scan_to_search_nfc);
        mBtnDoSearch = (Button) findViewById(R.id.btn_search_by_nfc_id);

        mBtnDoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NFCActivity.this, TopupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //mBtWrite.setOnClickListener(view -> showWriteFragment());
        //mBtRead.setOnClickListener(view -> showReadFragment());
//        mBtRead.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String mId = mEtMessage.getText().toString();
//                trySend(mId);
//            }
//        });
    }

    private void initNFC(){

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }


    private void showWriteFragment() {

        isWrite = true;

        mNfcWriteFragment = (NFCWriteFragment) getFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);

        if (mNfcWriteFragment == null) {

            mNfcWriteFragment = NFCWriteFragment.newInstance();
        }
        mNfcWriteFragment.show(getFragmentManager(),NFCWriteFragment.TAG);

    }

    private void showReadFragment() {

        mNfcReadFragment = (NFCReadFragment) getFragmentManager().findFragmentByTag(NFCReadFragment.TAG);

        if (mNfcReadFragment == null) {

            mNfcReadFragment = NFCReadFragment.newInstance();
        }
        mNfcReadFragment.show(getFragmentManager(),NFCReadFragment.TAG);

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
            //mEtMessage.setText(tag.getId().toString());
            // mEtMessage.setText(hexdump);
            mNfcId.setText(hexdump);
            mBtnDoSearch.setEnabled(true);
//            mBtRead.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String mId = mEtMessage.getText().toString();
//                    trySend(mId);
//                }
//            });
            /*if (isDialogDisplayed) {

                if (isWrite) {

                    String messageToWrite = mEtMessage.getText().toString();
                    mNfcWriteFragment = (NFCWriteFragment) getFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);
                    mNfcWriteFragment.onNfcDetected(ndef,messageToWrite);

                } else {

                    mNfcReadFragment = (NFCReadFragment)getFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
                    mNfcReadFragment.onNfcDetected(ndef);
                }
            }*/
        }
    }

    private void trySend(String mId)
    {

        Log.d("ADebugTag", "Value: " + mId );

        final String tagId = mId;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://philandeznetwork.000webhostapp.com/test_query_sql.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("JsonObject Response",response.toString());
                Toast.makeText(NFCActivity.this,response,Toast.LENGTH_LONG).show();
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
