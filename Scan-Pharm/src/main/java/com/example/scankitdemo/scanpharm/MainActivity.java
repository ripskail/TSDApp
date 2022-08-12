/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.example.scankitdemo.scanpharm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.button.MaterialButton;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
//import com.huawei.hms.mlsdk.common.internal.client.SmartLog;
import com.example.scankitdemo.scanpharm.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private int REQUEST_ADD_PRODUCT = 1002;
    private int REQUEST_SETTINGS = 1003;
    private int REQUEST_SBOR = 1001;
    private int REQUEST_MX = 1004;
    private static final int PERMISSION_REQUESTS = 1;
    SessionManager session;
    DatabaseHandler db;
    String[] countries;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new SessionManager(MainActivity.this);
        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }
        hideDialog();
        db = new DatabaseHandler(MainActivity.this);
        String  rty = Build.PRODUCT;
        String  qwe = Build.DISPLAY;
        String  boa = Build.BOARD;
        String  ty = Build.MODEL;
        String manufacturer = Build.DEVICE;
        String qqq = Build.SERIAL;
        String aaa = Build.SERIAL;
        String rrrr = Build.USER;
        Log.e("DEVICE", "!DEVICE!"+ty+"  "+rty+"  "+qwe+" "+boa+" "+ manufacturer+"    "+qqq+"    "+rrrr+"   "+aaa);
        Intent intent = new Intent ("ACTION_BAR_SCANCFG");
        intent.putExtra("EXTRA_SCAN_POWER", 0);
        this.sendBroadcast(intent);
        if (session.isLoggedIn()) {
            getSupportActionBar().setTitle("Пользователь: "+session.loginin());
        }
        else { Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
            finish();}
    }

    private void getRuntimePermissions() {

        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }
        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return new String[0];
        }
    }
    public void logoutUser(View view) {
        if (isOnline(this)) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)        // Дополнительное диалоговое окно
                    .setMessage("Вы действительно хотите сменить пользователя?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            session.setLogin(false);
                            Updatenew();
                            showDialog("Загрузка ...");
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    Intent intent = new Intent(MainActivity.this, First.class);
                                    intent.putExtra("testNameData", countries);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 1000); //specify the number of milliseconds
                            return;
                        }
                    })
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            hideDialog();
                            return;
                        }
                    })
                    .show();//Загрузка сборочного листа
        } else { Toast.makeText(getApplicationContext(), "Нет интернета, проверьте подключение", Toast.LENGTH_LONG).show();} }

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
    public void addProduct(View view) {
        Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
        startActivityForResult(intent, REQUEST_ADD_PRODUCT);
    }

    public void Settings(View view) {
    }
    public void Sborka(View view) {
        Intent intent = new Intent(MainActivity.this, ListNakl.class);
            startActivityForResult(intent, REQUEST_SBOR);
    }
    public void MestoX(View view) {
        Intent intent = new Intent(MainActivity.this, Mesto.class);
        startActivityForResult(intent, REQUEST_MX);
    }
    private void showDialog(String title) {
        db.showProgressDialog(MainActivity.this, title);
    }

    private void hideDialog() {
        db.hideProgressDialog(MainActivity.this);
    }
    public void Updatenew() {
        try {
            showDialog("Загрузка ...");
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "http://" + session.ipin() + "/auth/userslist";
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
                        hideDialog();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideDialog();
                    }
                    hideDialog();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideDialog();
                    try {
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
    @Override
    public void onBackPressed() {
        hideDialog();
    }
}
