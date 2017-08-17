package com.learn2crack.nfc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
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
import com.learn2crack.nfc.androidbluetoothprint.DeviceListActivity;
import com.learn2crack.nfc.androidbluetoothprint.UnicodeFormatter;
import com.learn2crack.nfc.shopowneractivity.ConfirmSellActivity;
import com.learn2crack.nfc.shopowneractivity.SellfoodActivity;
import com.learn2crack.nfc.shopowneractivity.TopupActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.SimpleFormatter;

public class NFCScanToConfirmActivity extends AppCompatActivity implements Runnable{
    
    public static final String TAG = NFCScanToConfirmActivity.class.getSimpleName();

    private TextView mTextScan;
    private Button mBtnProceed;

    private NFCWriteFragment mNfcWriteFragment;
    private NFCReadFragment mNfcReadFragment;

    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;

    private String currentBalance;
    private String mCurrentNfcId;
    private TextView mTextConfirm;

    private NfcAdapter mNfcAdapter;

    private String status;
    private String action;
    private String firstName, lastName, userName, mNfcId, totalAmountString;

    protected static final String TAG2 = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;


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
        mTextConfirm = (TextView) findViewById(R.id.text_confirm);

        mNfcId = getIntent().getStringExtra("NFCID");
        action = getIntent().getStringExtra("ACTION");
        firstName = getIntent().getStringExtra("FIRST_NAME");
        lastName = getIntent().getStringExtra("LAST_NAME");
        totalAmountString = getIntent().getStringExtra("TOTAL_AMOUNT");
        userName = getIntent().getStringExtra("USER_NAME");
        Integer totalAmount = Integer.valueOf(totalAmountString);

        //Toast.makeText(NFCScanToConfirmActivity.this, "Check if the nfc id is " + mNfcId, Toast.LENGTH_SHORT).show();

        if(action.equals("topup")){
            //Toast.makeText(NFCScanToConfirmActivity.this, "TOP UP!!!", Toast.LENGTH_SHORT).show();
            mTextConfirm.setText("TOP UP WITH AMOUNT: " + totalAmountString + " BAHT?");
        }else if(action.equals("deduct")){
            mTextConfirm.setText("DEDUCT WITH AMOUNT: " + totalAmountString + " BAHT?");
        }else if(action.equals("sellitem")){
            mTextConfirm.setText("BUY ITEM WITH AMOUNT: " + totalAmountString + " BAHT?");
        }else{
            Toast.makeText(NFCScanToConfirmActivity.this, "Not topup or deduct: " + action, Toast.LENGTH_SHORT).show();
        }

        mBtnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mNfcId.equals(mCurrentNfcId)) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(NFCScanToConfirmActivity.this);
                    alertDialog.setTitle("SCAN");
                    alertDialog.setMessage("NFC does not match :" + mNfcId + ": AND :" + mCurrentNfcId + ":");
                    //alertDialog.setMessage("Print: " + mNfcId.equals(mCurrentNfcId));
                    alertDialog.setNegativeButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    alertDialog.show();
                } else {

                    mBtnProceed.setEnabled(false);

                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter == null) {
                        Toast.makeText(NFCScanToConfirmActivity.this, "Message1", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!mBluetoothAdapter.isEnabled()) {
                            Intent enableBtIntent = new Intent(
                                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent,
                                    REQUEST_ENABLE_BT);
                        } else {
                            ListPairedDevices();
                            Intent connectIntent = new Intent(NFCScanToConfirmActivity.this,
                                    DeviceListActivity.class);
                            startActivityForResult(connectIntent,
                                    REQUEST_CONNECT_DEVICE);
                        }
                    }

                }
            }
        });
    }

    private void initNFC(){

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
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

    private void topupToDatabase(String nfcId, String amount, String firstName, String lastName, String userName)
    {

        Log.d("Top Up Amount ",  ":" + amount + " with NFC id: " + nfcId + " and username: " + userName);

        /*final Integer totalAmount = amount;
        final String nfcId = nfcid;
        final String firstName = first_name;
        final String lastName = last_name;*/

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://nfcregis.tkhomeservice.co.th/api_nfc.php", new Response.Listener<String>() {
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
                        Toast.makeText(NFCScanToConfirmActivity.this, "Update topup name " + firstName + " " + lastName + " with amount " + currentBalance, Toast.LENGTH_SHORT).show();
                        printBill();
                        Intent intent = new Intent(NFCScanToConfirmActivity.this, TopupActivity.class);
                        intent.putExtra("NFCID", nfcId);
                        intent.putExtra("FIRST_NAME", firstName);
                        intent.putExtra("LAST_NAME", lastName);
                        intent.putExtra("USER_NAME", userName);
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

    private void deductToDatabase(String nfcId, String amount, String firstName, String lastName, String userName)
    {

        Log.d("Deduct Amount ",  ":" + amount);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://nfcregis.tkhomeservice.co.th/api_nfc.php", new Response.Listener<String>() {
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
                        printBill();
                        Intent intent = new Intent(NFCScanToConfirmActivity.this, TopupActivity.class);
                        intent.putExtra("NFCID", nfcId);
                        intent.putExtra("FIRST_NAME", firstName);
                        intent.putExtra("LAST_NAME", lastName);
                        intent.putExtra("USER_NAME", userName);
                        intent.putExtra("CURRENT_BALANCE", currentBalance);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(NFCScanToConfirmActivity.this, "Invalid update deduct, please try again.", Toast.LENGTH_SHORT).show();
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

    private void printBill(){
        String strReceipt = getStringReceipt();
        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = mBluetoothSocket
                            .getOutputStream();
                    os.write(strReceipt.getBytes());

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
                    os.write(intToByteArray(n_width));
                } catch (Exception e) {
                    Log.e("MainActivity", "Exe ", e);
                }
            }
        };
        t.start();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(NFCScanToConfirmActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(NFCScanToConfirmActivity.this, "Message", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG2, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG2, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG2, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG2, "CouldNotCloseSocket");
        }
    }

    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(NFCScanToConfirmActivity.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
            if (action.equals("topup")) {
                //Toast.makeText(NFCScanToConfirmActivity.this, "TOP UP!!!", Toast.LENGTH_SHORT).show();
                topupToDatabase(mNfcId, totalAmountString, firstName, lastName, userName);
            } else if (action.equals("deduct")) {
                deductToDatabase(mNfcId, totalAmountString, firstName, lastName, userName);
            } else {
                Toast.makeText(NFCScanToConfirmActivity.this, "Not topup or deduct: " + action, Toast.LENGTH_SHORT).show();
            }
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }

    private String getStringReceipt() {

        String BILL = "";
        try {
            String testText = "Name: " + firstName + " " + lastName + "Balance: " + currentBalance;
            Toast.makeText(NFCScanToConfirmActivity.this, testText, Toast.LENGTH_LONG).show();

            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dt.format(new Date());

            // OutputStream os = mBluetoothSocket.getOutputStream();

            BILL =  "CASHLESS PROJECT    \n" +
                    strDate + "  \n";
            BILL = BILL
                    + "--------------------------------\n\n";

            BILL = BILL + "Name:" + firstName + " " + lastName + "\n";
            BILL = BILL + "Total Balance Left:" + "  " + currentBalance + "\n\n";

            BILL = BILL
                    + "--------------------------------\n";
            BILL = BILL + "\n\n ";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return BILL;
    }
}
