package com.example.scankitdemo.scanpharm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.scankitdemo.scanpharm.Data.Auth;
import com.example.scankitdemo.scanpharm.Data.DatabaseHandler;
import com.example.scankitdemo.scanpharm.Data.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    private int REQUEST_N = 1005;
    public String tok;
    EditText log, pas, url;
    Button but;
    JSONObject obj1;
    Intent intent;
    Auth auth;
    DatabaseHandler db;
    SessionManager session;
    Spinner spinner;
    First start;
    ArrayAdapter<String> adapter;
    String[] count ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //log = findViewById(R.id.loginT);
        url = findViewById(R.id.url1);
        pas = findViewById(R.id.PassW);
        but = findViewById(R.id.button2);
        spinner = (Spinner) findViewById(R.id.spinner2);
        start = new First();
        try {
            count = getIntent().getExtras().getStringArray("testNameData");
        }catch (Exception e){}
        db = new DatabaseHandler(Login.this);
        session = new SessionManager(this);
        if (session.ipin() == "" | session.ipin() == null) {
        } else {
            url.setText(session.ipin());
        }
        this.initAction();
        hideDialog();
        if (session.isLoggedIn()) {
            intent = new Intent(Login.this, MainActivity.class);
            startActivityForResult(intent, REQUEST_N);
        }
    }

    public void upd(){
        if(count != null) {
            adapter = new ArrayAdapter<String>(this, R.layout.spin, count);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }
    public void showToast(final String toast)
    { Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show(); }

    public void showDialog(String title) {
        db.showProgressDialog(Login.this, title);
    }

    public void hideDialog() {
        db.hideProgressDialog(Login.this);
    }
    public void Sborka(View view) {
        intent = new Intent(Login.this, MainActivity.class);
        showDialog("Авторизация...");
        tok = session.LoginProcess(spinner.getSelectedItem().toString(),pas.getText().toString(),url.getText().toString());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(session.isLoggedIn()) {
                    hideDialog();
                    startActivityForResult(intent, REQUEST_N);
                }
                else {hideDialog();
                    Toast.makeText(Login.this, "Ошибка! Проверьте IP, login или password!", Toast.LENGTH_LONG).show();}
            }
        }, 5000); //specify the number of milliseconds
    }
    private void initAction() {
        upd();
    }
}
