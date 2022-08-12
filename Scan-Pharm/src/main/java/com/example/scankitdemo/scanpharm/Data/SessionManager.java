package com.example.scankitdemo.scanpharm.Data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.scankitdemo.scanpharm.Login;
import com.example.scankitdemo.scanpharm.MainActivity;
import com.example.scankitdemo.scanpharm.Mesto;
import com.example.scankitdemo.scanpharm.SettingsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SessionManager extends AppCompatActivity {
    private static String TAG = SessionManager.class.getSimpleName();
    public static String token = "";
    SharedPreferences pref;
    Integer mStatusCode;
    Editor editor;
    Context _context;
    Login log;
    int PRIVATE_MODE = 0;
    Intent intent;
    private static final String PREF_NAME = "Name";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final int REQUEST_N = 999;
    private static final String TOK = "token";
    private static final String NL = "nakl";
    private static final String LOG = "login";
    private static final String IPS = "ip";
    public SessionManager(Context context) {
        log = new Login();
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void setIP(String url) {
        editor.putString(IPS, url);
        editor.commit();
        Log.d(TAG, "User setIP session modified!"+url);

    }
    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.commit();
        Log.d(TAG, "User isLoggedIn session modified!"+isLoggedIn);
    }
    public void setLogin(String log) {
        editor.putString(LOG, log);
        editor.commit();
        Log.d(TAG, "User setLogin session modified!"+log);

    }
    public void setNakl(String nl) {
        editor.putString(NL, nl);
        editor.commit();
        Log.d(TAG, "User setNakl session modified!"+nl);

    }
    public void setToken(String tok) {
        editor.putString(TOK, tok);
        editor.commit();
        Log.d(TAG, "User setToken session modified!"+tok);

    }
    public String naklin(){
        return pref.getString(NL,"");
    }
    public String loginin(){
        return pref.getString(LOG,"");
    }
    public String tokenin(){
        return pref.getString(TOK,"");
    }
    public String ipin(){
        return pref.getString(IPS,"");
    }
    public void avto(String name,String url){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(_context);
            Log.e("URLLLLLLLLLLLL", url.toString());
            String URL = "http://"+url+"/auth/sign-in";
                    /*/api/cdb/assembly?id_tsd=123";*/
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.i("statusCode-------------------------", String.valueOf(mStatusCode));
                        if(mStatusCode == 200){
                            setLogin(true);
                            setToken(token);
                            setLogin(name);
                            setIP(url);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
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
                    params.put("Authorization", " Bearer "+token);
                    return params;
                }
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response)
                {
                    if (response != null) {
                        mStatusCode = response.statusCode;
                    } return super.parseNetworkResponse(response);
                }
            };
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void parseVolleyError(VolleyError error) {
        try {

            String responseBody = new String(error.getMessage());
            Log.e("00", "!!!!!!!!!!!!!!!!!!!!!!!!! " + responseBody);
            log.showToast(responseBody);
            log.hideDialog();
        } catch (Exception e) {
        }
    }
    public String LoginProcess(String email, String password,String url) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(_context);
            String URL = "http://"+url+"/auth/sign-in";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", email);
            jsonBody.put("password", password);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Log.e("4", "!!!!!!!!!!!!!!!!!!!!!!!!! " + response);
                        Log.e("4", "!!!!!!!!!!!!!!!!!!!!!!!!! " + obj.getString("token"));
                        token = obj.getString("token");
                        setLogin(true);
                        setToken(token);
                        setLogin(email);
                        setIP(url);
                        //avto(email,url);
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("444444", "!!!!!!!!!!!!!!!!!!!!!!!!! " + token);
        return  token;
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
}