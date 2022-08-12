package com.example.scankitdemo.scanpharm;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.scankitdemo.scanpharm.Data.DatabaseHandler;
import com.example.scankitdemo.scanpharm.Data.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Mesto extends AppCompatActivity {
    DatabaseHandler db ;
    SessionManager session;
    AddProductActivity add;
    EditText mes,bar;
    int mStatusCode;
    public  int REQUEST_N = 00;
    public String strok = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesto);
        bar = findViewById(R.id.bar);
        mes= findViewById(R.id.mes);
        db = new DatabaseHandler(Mesto.this);
        session = new SessionManager(this);
        add = new AddProductActivity();
        Intent intent = getIntent();
        strok = intent.getStringExtra("barcode");
        bar.setText(strok);
        session = new SessionManager(Mesto.this);
        if (session.isLoggedIn()) {
            getSupportActionBar().setTitle("Пользователь: " + session.loginin());
        }
    }
    public void izmenit(View view) {
        if(bar.getText().toString()!=null&&bar.getText().toString()!=""&&mes.getText().toString()!=null&&mes.getText().toString()!=""){
        izm(bar.getText().toString(),mes.getText().toString());}
    }
    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data);
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("message");
            if(message.equals("Ошибка запроса! обратитесь в службу поддержки!")){
                session.setLogin(false);
                Intent intent = new Intent(Mesto.this, Login.class);
                startActivityForResult(intent, REQUEST_N);
            }
            Toast.makeText(getApplicationContext(), "Ошибка! "+message, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
        }
    }
    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }
    public void izm(String barcode, String mh) {
        if (isOnline(this)) {
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String URL = "http://" + session.ipin() + "/api/cdb/mh/";
                String parts[] = barcode.split(" ", 2);
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("barcode", parts[0]);
                jsonBody.put("name_mh", mh);
                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String data = "Успешно! Место хранения изменено";
                        if (mStatusCode == 200) {
                            Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", error.toString());
                        parseVolleyError(error);
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Authorization", " Bearer " + session.tokenin());
                        return params;
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return requestBody == null ? null : requestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        if (response != null) {
                            mStatusCode = response.statusCode;
                        }
                        return super.parseNetworkResponse(response);
                    }
                };
                requestQueue.add(stringRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {Toast.makeText(getApplicationContext(), "Нет интернета, проверьте подключение", Toast.LENGTH_LONG).show();}
    }
}
