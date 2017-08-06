package com.learn2crack.nfc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nfc.adapter.CustomAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by basssrongsil on 8/5/2017 AD.
 */

public class UsersListActivity extends AppCompatActivity {

    public static final String TAG = UsersListActivity.class.getSimpleName();

    private EditText mSearch;

    static List<String> USERS;

    private String register_flag = "";
    private String firstName = "";
    private String lastName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_list);

        mSearch = (EditText) findViewById(R.id.search);

        USERS = queryAllUser();

        CustomAdapter adapter = new CustomAdapter(getApplicationContext(), USERS);

        ListView listView = (ListView)findViewById(R.id.listViewUsers);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String user = USERS.get(position);

                Toast.makeText(UsersListActivity.this, "Click item " + position + " which is " + USERS.get(position), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(UsersListActivity.this, TopupActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();

            }
        });

        mSearch.addTextChangedListener(
                new TextWatcher() {
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    private Timer timer=new Timer();
                    private final long DELAY = 1000; // milliseconds

                    @Override
                    public void afterTextChanged(final Editable s) {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                System.out.println("SEND AJAX SEARCH");
                                                String searchText = mSearch.getText().toString();
                                                USERS = searchList(searchText);

                                                adapter.setItems(USERS);
                                                adapter.notifyDataSetChanged();

                                            }
                                        });

                                    }
                                },
                                DELAY
                        );
                    }
                }
        );
    }

    @Override
    public void onResume(){
        super.onResume();
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
    }

    public void searchByScanNfc(View v) {
        Intent intent = new Intent(this, NFCActivity.class);
        startActivity(intent);
        finish();
    }

    public List<String> queryAllUser(){
        List<String> allUser = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://philandeznetwork.000webhostapp.com/test_query_sql.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("JsonObject Response",response.toString());
                Toast.makeText(UsersListActivity.this,response.toString(),Toast.LENGTH_LONG).show();
                try {
                    JSONObject obj = new JSONObject(response.toString());
                    JSONArray dataArray = obj.getJSONArray("data");
                    //JSONObject finalObject = dataArray.getJSONObject(0);
                    for(int i = 0; i < dataArray.length(); i++){
                        JSONObject finalObject = dataArray.getJSONObject(i);
                        firstName = finalObject.getString("f_name");
                        Log.d("Test Json Firstname", firstName);
                        lastName = finalObject.getString("l_name");
                        register_flag = finalObject.getString("register_flag");
                        allUser.add(firstName + " "  + lastName);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UsersListActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("action", "QUERYALLUSER");
                //Log.d("ShowTag", "Value: " + tagId );
                return params;
            }
        };

        //stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        return allUser;
    }

    public List<String> searchList(String textSearch){
        List<String> filterUser = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://philandeznetwork.000webhostapp.com/test_query_sql.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("JsonObject Response",response.toString());
                Toast.makeText(UsersListActivity.this,response.toString(),Toast.LENGTH_LONG).show();
                try {
                    JSONObject obj = new JSONObject(response.toString());
                    JSONArray dataArray = obj.getJSONArray("data");
                    //JSONObject finalObject = dataArray.getJSONObject(0);
                    for(int i = 0; i < dataArray.length(); i++){
                        JSONObject finalObject = dataArray.getJSONObject(i);
                        firstName = finalObject.getString("f_name");
                        Log.d("Test Json Firstname", firstName);
                        lastName = finalObject.getString("l_name");
                        register_flag = finalObject.getString("register_flag");
                        filterUser.add(firstName + " "  + lastName);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UsersListActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("action", "QUERYALLUSERFILTER");
                params.put("textSearch", textSearch);
                //Log.d("ShowTag", "Value: " + tagId );
                return params;
            }
        };

        //stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        return filterUser;
    }
}
