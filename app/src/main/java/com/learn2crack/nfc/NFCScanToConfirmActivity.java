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

public class NFCScanToConfirmActivity extends AppCompatActivity implements Listener{
    
    public static final String TAG = NFCScanToConfirmActivity.class.getSimpleName();

    private TextView mTextScan;
    private Button mBtnProceed;

    private NFCWriteFragment mNfcWriteFragment;
    private NFCReadFragment mNfcReadFragment;

    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;

    private String currentBalance = "";
    private String firstName = "";
    private String lastName = "";
    private String userName = null;
    private String mNfcId;
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

        mNfcId = getIntent().getStringExtra("NFCID");

        Toast.makeText(NFCScanToConfirmActivity.this, "Check if the nfc id is " + mNfcId, Toast.LENGTH_SHORT).show();

        mBtnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mNfcId.equals(mCurrentNfcId)) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(NFCScanToConfirmActivity.this);
                    alertDialog.setTitle("SCAN");
                    alertDialog.setMessage("NFC does not match");
                    alertDialog.setNegativeButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    alertDialog.show();
                } else {

                    Toast.makeText(NFCScanToConfirmActivity.this, "MATCH!!!!", Toast.LENGTH_SHORT).show();

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
