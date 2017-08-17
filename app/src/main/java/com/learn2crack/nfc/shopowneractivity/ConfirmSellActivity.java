package com.learn2crack.nfc.shopowneractivity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.learn2crack.nfc.RegisterActivity;
import com.learn2crack.nfc.UsersListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.learn2crack.nfc.androidbluetoothprint.DeviceListActivity;
import com.learn2crack.nfc.androidbluetoothprint.UnicodeFormatter;

public class ConfirmSellActivity extends AppCompatActivity implements Runnable {
    
    public static final String TAG = ConfirmSellActivity.class.getSimpleName();

    private TextView mCurrentBalance;
    private TextView mCurrentSell;
    private TextView mCurrentLeft;
    private TextView mUserName;
    private Button mProceed;
    private int amountLeft = 0;
    private String currentBalance;
    private String currentSell;
    private String mNfcId;
    private String totalSellAmount;
    private String firstName;
    private String lastName;
    private String userName;

    private String status;

    private String shopType;
    private String[] amounts = new String[5];

    protected static final String TAG2 = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;

    private Button mBack;

    //Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmsell);

        mNfcId = getIntent().getStringExtra("NFCID");
        totalSellAmount = getIntent().getStringExtra("TOTAL_SELL_AMOUNT");
        firstName = getIntent().getStringExtra("FIRST_NAME");
        lastName = getIntent().getStringExtra("LAST_NAME");
        userName = getIntent().getStringExtra("USER_NAME");
        currentBalance = getIntent().getStringExtra("CURRENT_BALANCE");
        currentSell = totalSellAmount;

        mBack = (Button) findViewById(R.id.btn_back);

        //Toast.makeText(this, "Confirm sell nfc id : " + mNfcId + " with amount : " + totalSellAmount, Toast.LENGTH_SHORT).show();

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
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter == null) {
                        //Toast.makeText(ConfirmSellActivity.this, "Message1", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!mBluetoothAdapter.isEnabled()) {
                            Intent enableBtIntent = new Intent(
                                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent,
                                    REQUEST_ENABLE_BT);
                        } else {
                            ListPairedDevices();
                            Intent connectIntent = new Intent(ConfirmSellActivity.this,
                                    DeviceListActivity.class);
                            startActivityForResult(connectIntent,
                                    REQUEST_CONNECT_DEVICE);
                        }
                    }
                }
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ConfirmSellActivity.this, SellfoodActivity.class);
                startActivity(intent);
                finish();

            }
        });

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

    private void sellitemToDatabase(String nfcId, String amount, String firstName, String lastName, String userName)
    {

        Log.d("Deduct Amount ",  ":" + amount);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://nfcregis.tkhomeservice.co.th/api_nfc.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("JsonObject Response",response.toString());
                //Toast.makeText(ConfirmSellActivity.this,response,Toast.LENGTH_LONG).show();
                try {

                    JSONObject obj = new JSONObject(response.toString());
                    status = obj.getString("status");

                    if (status.equals("true")) {
                        //Toast.makeText(ConfirmSellActivity.this, "Update amount of: " + firstName + " " + lastName + " and current balance: " + currentBalance, Toast.LENGTH_SHORT).show();
                        printBill();
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
                    Intent connectIntent = new Intent(ConfirmSellActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(ConfirmSellActivity.this, "Message", Toast.LENGTH_SHORT).show();
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(ConfirmSellActivity.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
            sellitemToDatabase(mNfcId, totalSellAmount, firstName, lastName, userName);
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
            BILL = "0000000000000000000000000000\n";
            BILL = BILL + "000000000               000000\n";
            BILL = BILL + "000000000                00000\n";
            BILL = BILL + "000000000                 0000\n";
            BILL = BILL + "000000000                   0000\n";
            BILL = BILL + "000000000                 000000\n";
            BILL = BILL + "000000000                  00000\n";
            BILL = BILL + "000000000                 000000\n";
            BILL = BILL + "000000000                0000000\n";
            BILL = BILL + "0000000000000000000000000000\n";
            BILL = BILL + "000000000000000000000000000\n";
            BILL = BILL + "000000000              000000\n";
            BILL = BILL + "000000000               00000000\n";
            BILL = BILL + "000000000                 000000\n";
            BILL = BILL + "000000000                   0000\n";
            BILL = BILL + "000000000                 000000\n";
            BILL = BILL + "000000000                 000000\n";
            BILL = BILL + "000000000                 00000\n";
            BILL = BILL + "000000000                0000\n";
            BILL = BILL + "0000000000000000000000000000\n";
            BILL = BILL + "0000000000000000000000000\n";
            BILL = BILL
                    + "--------------------------------\n";
            BILL = BILL + "CASHLESS PROJECT    \n" +
                    strDate + "  \n";
            BILL = BILL
                    + "--------------------------------\n";


            BILL = BILL + String.format("%1$-10s %2$-5s %3$-7s %4$-5s", "Item", "Qty", "Price", "Total");
            BILL = BILL + "\n";
            BILL = BILL
                    + "--------------------------------\n";
            if (shopType.equals("FOOD")) {
                if (amounts[0] != null) {
                    int amount = Integer.parseInt(amounts[0]);
                    BILL = BILL + String.format("%1$-10s %2$-5s %3$-7s %4$5s", "Kon Lek", amounts[0], 88 + "", (amount * 88) + "\n");
                }

                if (amounts[1] != null) {
                    int amount = Integer.parseInt(amounts[1]);
                    BILL = BILL + String.format("%1$-10s %2$-5s %3$-7s %4$5s", "Song Kon", amounts[1], 88 + "",(amount * 88) + "\n");
                }
            } else if (shopType.equals("BEVERAGE")) {
                if (amounts[0] != null) {
                    int amount = Integer.parseInt(amounts[0]);
                    BILL = BILL + String.format("%1$-10s %2$-5s %3$-7s %4$5s", "Leo", amounts[0], 68 + "", (amount * 68) + "\n");
                }

                if (amounts[1] != null) {
                    int amount = Integer.parseInt(amounts[1]);
                    BILL = BILL + String.format("%1$-10s %2$-5s %3$-7s %4$5s", "Singha", amounts[1], 98 + "", (amount * 98) + "\n");
                }

                if (amounts[2] != null) {
                    int amount = Integer.parseInt(amounts[2]);
                    BILL = BILL + String.format("%1$-10s %2$-5s %3$-7s %4$5s", "Tsingtao", amounts[2], 148 + "", (amount * 148) + "\n");
                }

                if (amounts[3] != null) {
                    int amount = Integer.parseInt(amounts[3]);
                    BILL = BILL + String.format("%1$-10s %2$-5s %3$-7s %4$5s", "Signature1", amounts[3], 188 + "", (amount * 188) + "\n");
                }

                if (amounts[4] != null) {
                    int amount = Integer.parseInt(amounts[4]);
                    BILL = BILL + String.format("%1$-10s %2$-5s %3$-7s %4$5s", "Signature2", amounts[4], 188 + "", (amount * 188) + "\n");
                }
            }

            BILL = BILL
                    + "--------------------------------";
            BILL = BILL + "\n\n";

            BILL = BILL + "Total Value:" + "  " + currentSell + "\n";
            BILL = BILL + "Total Balance Left:" + "  " + amountLeft + "\n";

            BILL = BILL
                    + "--------------------------------\n";
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
