package com.example.scankitdemo.scanpharm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.scankitdemo.scanpharm.Data.DatabaseHandler;
import com.example.scankitdemo.scanpharm.Data.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class First extends AppCompatActivity {
    DatabaseHandler db;
    public String[] countries;
    Login log;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Updatenew();
        try {
        super.onCreate(savedInstanceState);
        //savedInstanceState

        setContentView(R.layout.activity_start);
        log = new Login();

            Updatenew();
        }
        catch (Exception e){
            }
        Intent intent = new Intent(this, Login.class);
        Handler handler = new Handler();
        showDialog("Загрузка ...");
        handler.postDelayed(new Runnable() {
            public void run() {
                intent.putExtra("testNameData", countries);
                startActivity(intent);
                finish();
              } }, 3000); //specify the number of milliseconds
    }
    public void showDialog(String title) {
//        db.showProgressDialog(First.this, title);
    }
    public void hideDialog() {
        db.hideProgressDialog(First.this);
    }
    public void Updatenew() {
        try {
            showDialog("Загрузка ...");
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "http://********";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray docc = obj.getJSONArray("users");
                        String myArray[] = new String[docc.length()];

                        countries = new String[docc.length()];
                        for (int p = 0; p < docc.length(); p++) {
                            JSONObject doc = docc.getJSONObject(p);
                            countries[p] = doc.getString("login");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideDialog();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideDialog();
                    try {
                        //Log.e("VOLLEY", error.toString());
                        //parseVolleyError(error);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    if (response != null) {
                        int mStatusCode = response.statusCode;
                        String message = response.data.toString();
                        //Log.e("YYYYYYYYYYYYYYYYY", "!!!!!!!!!!!!!!!!!!!!!!!!! " + mStatusCode);
                        //Log.e("YYYYYYYYYYYYYYYYY", "!!!!!!!!!!!!!!!!!!!!!!!!! " + message);
                    }
                    return super.parseNetworkResponse(response);
                }
            };
            int socketTimeout = 100000;//10 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}