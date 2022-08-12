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


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.scankitdemo.scanpharm.Data.SessionManager;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.example.scankitdemo.scanpharm.util.Constant;

import android.device.ScanManager;
import android.device.scanner.configuration.PropertyID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class AddProductActivity extends AppCompatActivity {
    public String party = null;
    public String barcode = null;
    private byte[] barcode2 = null;
    public Integer tip = 0,barcodeType = 0;
    private String barcodeTypeZebra = null;
    private String path = null;
    private String url = null;
    private String urlset = null;
    private ImageView preview;
    int[] idbuf = new int[]{PropertyID.WEDGE_INTENT_ACTION_NAME, PropertyID.WEDGE_INTENT_DATA_STRING_TAG};
    String[] action_value_buf = new String[]{ScanManager.ACTION_DECODE, ScanManager.BARCODE_STRING_TAG};
    private RelativeLayout  scan,relativeLayoutScan, relativeLayoutTakePhoto, relativeLayoutSave;
    private int REQUEST_CODE_SCAN_ALL = 1;
    private int REQUEST_TAKE_PHOTO = 2;
    private TextView txtW,txtW1,txtW2,txtW3,txtW4,txtW5,txtW6;
    private Button btnscan,mh,btnOk,kostl;
    public String dev = "0";
    Intent intent;
    private ScanManager mScanManager;
    //BroadcastReceiver mScanReceiver;
    private IntentFilter filter;
    BroadcastReceiver intentReceiver;
    SessionManager session;
    StringBuffer sbb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_addproduct);
            this.initView();
            this.initAction();
        btnOk = this.findViewById(R.id.pref);
        kostl = this.findViewById(R.id.kostl);
        txtW = this.findViewById(R.id.text123);
        View.OnClickListener Ok  = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        };
        kostl.setOnClickListener(Ok);
        View.OnClickListener oclBtnOk  = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(AddProductActivity.this, Mesto.class);
                intent1.putExtra("barcode", party);
                if (party != null) {
                    startActivityForResult(intent1,1);
                }
            }
        };
        btnOk.setOnClickListener(oclBtnOk);
        }

    private void initView() {
         txtW1 = this.findViewById(R.id.textView);
         txtW2 = this.findViewById(R.id.textView2);
         txtW3 = this.findViewById(R.id.textView3);
         txtW4 = this.findViewById(R.id.textView4);
         txtW5 = this.findViewById(R.id.textView5);
         txtW6 = this.findViewById(R.id.textView1);
        txtW6.setText("Отсканируйте штрих-код");
            this.mh = this.findViewById(R.id.pref);
        this.scan = this.findViewById(R.id.relativate_scan);

    }
    @Override
    protected void onResume() {
        super.onResume();
        String www = getDeviceName();
        int index = www.indexOf("DT50");
        if(index == 6) {
            IntentFilter filter = new IntentFilter();// DT-50 ZEBRA
            filter.addCategory(Intent.CATEGORY_DEFAULT);// DT-50 ZEBRA
            filter.addAction(getResources().getString(R.string.activity_intent_filter_action));// DT-50 ZEBRA
            registerReceiver(myBroadcastReceiver, filter);// DT-50 ZEBRA
           // mScanManager = new ScanManager();
            //action_value_buf = mScanManager.getParameterString(idbuf);
            //IntentFilter filter = new IntentFilter();
            //filter.addAction(action_value_buf[0]);
           // registerReceiver(mScanReceiver, filter);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        String www = getDeviceName();
        int index = www.indexOf("DT50");
        if(index == 6) {
            //if (mScanManager != null) {
            //    mScanManager.stopDecode();
            //}
           // unregisterReceiver(mScanReceiver);
            unregisterReceiver(myBroadcastReceiver); //DT-50 ZEBRA
        }
    }


    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            byte[] barcodeData = intent.getByteArrayExtra("barcode");
            int dataLength = intent.getIntExtra("length", 0);
            String result = intent.getStringExtra(action_value_buf[1]);
            barcode = result;
            int sendTokensFormat = intent.getIntExtra("sendTokensFormat", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            barcodeType = Integer.parseInt(String.valueOf(temp));
            if (barcode != null) {
                if (barcodeType == 100)
                {  Toast.makeText(getApplicationContext(), "Отсканирован обычный штрихкод!", Toast.LENGTH_SHORT).show();}
                if (barcodeType == 119 | barcodeType == 73) {
                    txtW.setText(barcode);
                    UpdateProcess();
                }
            }

        }
    };


    private void initAction() {
        session = new SessionManager(AddProductActivity.this);
        if (session.isLoggedIn()) { getSupportActionBar().setTitle("Пользователь: " + session.loginin()); }
        String www = getDeviceName();
        int indexJava = www.indexOf("Newland");
        int index = www.indexOf("DT50");
        if(index == 6) {
            IntentFilter filter = new IntentFilter();// DT-50 ZEBRA
            filter.addCategory(Intent.CATEGORY_DEFAULT);// DT-50 ZEBRA
            filter.addAction(getResources().getString(R.string.activity_intent_filter_action));// DT-50 ZEBRA
            registerReceiver(myBroadcastReceiver, filter);// DT-50 ZEBRA
            //ReadUID1();
        }
        if(indexJava ==0) {
            intent = new Intent("ACTION_BAR_SCANCFG");
            intent.putExtra("EXTRA_SCAN_POWER", 1);
            this.sendBroadcast(intent);
            ReadUID(intent);
        }

        if(indexJava == - 1 & index == - 1) {
            scan.setOnClickListener(view -> {
               AddProductActivity.this.scanBarcode(AddProductActivity.this.REQUEST_CODE_SCAN_ALL);
            });
        }
        else {
            this.findViewById(R.id.linear_views).setVisibility(View.INVISIBLE);
            this.findViewById(R.id.sc).setVisibility(View.INVISIBLE);
            }
    }
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() { // DT-50 ZEBRA
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();
            if (action.equals(getResources().getString(R.string.activity_intent_filter_action))) {
                //  Received a barcode scan
                try {
                    displayScanResult(intent, "via Broadcast");
                } catch (Exception e) {
                    //  Catch if the UI does not exist when we receive the broadcast
                }
            }
        }
    };
    private void displayScanResult(Intent initiatingIntent, String howDataReceived) throws UnsupportedEncodingException// DT-50 ZEBRA
    {
        String decodedSource = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_source));
        String decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
        String decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type));
        String decodedLabelTypeHON = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_type_hon));
        Log.e("-------------", "!!!!!!!!!!!!!!!!!!!!!!!!! " + decodedData);
        Log.e("-------------1", "!!!!!!!!!!!!!!!!!!!!!!!!! " + decodedLabelType);
        Log.e("-------------2", "!!!!!!!!!!!!!!!!!!!!!!!!! " + decodedLabelTypeHON);
        Log.e("-------------3", "!!!!!!!!!!!!!!!!!!!!!!!!! " + decodedData+"    "+decodedLabelType);
        if(decodedLabelType!=null) {
            barcodeTypeZebra = decodedLabelType;
        } else {
            barcodeTypeZebra = decodedLabelTypeHON;
        }
        if(barcodeTypeZebra.equals("DataMatrix") || barcodeTypeZebra.equals("Undefined") || decodedLabelTypeHON.equals("DataMatrix")) {
            barcodeType = 119;
        }
        if(barcodeTypeZebra.equals("EAN-128")||barcodeTypeZebra.equals("Code128") || decodedLabelTypeHON.equals("EAN-128")|| decodedLabelTypeHON.equals("Code128")){
            barcodeType = 73;
        }
        barcode = decodedData;
        txtW.setText(barcode);
        if(barcodeTypeZebra!=null || barcode!=null){
            Log.e("asdasdasdad", "!!!!!!!!!!!!!!!!!!!!!!!!! " + barcodeTypeZebra+"    "+barcode);
            UpdateProcess();
        }
    }
    private void scanBarcode(int requestCode) {
        HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.DATAMATRIX_SCAN_TYPE,
                HmsScan.CODE128_SCAN_TYPE).create();
        ScanUtil.startScan(this, requestCode, options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView txtW = this.findViewById(R.id.text123);
        if (data == null) {
            return;
        }
        if ((requestCode == this.REQUEST_CODE_SCAN_ALL)
                && (resultCode == Activity.RESULT_OK)) {
            HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj != null && obj.getOriginalValue() != null) {
                this.barcode = obj.getShowResult();
                tip  = obj.getScanType();
                UpdateProcess();
                txtW.setText(barcode);
            }
        } else if ((requestCode == this.REQUEST_TAKE_PHOTO)
                && (resultCode == Activity.RESULT_OK)) {
            this.path = data.getStringExtra(Constant.IMAGE_PATH_VALUE);
            this.loadCameraImage();
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
    private void UpdateProcess() {
        if (isOnline(this)) {
            Log.e("----asd----", "!!!!!!!!!!!!!!!!!!!!!!!!! " + barcodeTypeZebra);
            RequestQueue queue = Volley.newRequestQueue(this);
            if (tip == 4 | barcodeType == 261 | barcodeType == 119) {
                Log.e("-------------", "!!!!!!!!!!!!!!!!!!!!!!!!!============ ");
                url = "http://" + session.ipin() + "/api/cdb/infobarcode?barcode=]d1" + barcode;
            }
            if (tip == 64 | barcodeType == 3 | barcodeType == 73) {
                Log.e("-------------", "!!!!!!!!!!!!!!!!!!!!!!!!! -----------");
                url = "http://" + session.ipin() + "/api/cdb/infobarcode?barcode=]c1" + barcode;
            }
            if (url != null) {
                Log.e("-------------", "!!!!!!!!!!!!!!!!!!!!!!!!! " + url);
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (url != null) {
                            try {
                                if (tip == 4 | barcodeType == 261 | barcodeType == 119 | barcodeTypeZebra == "DataMatrix") {
                                    Log.e("-----66666--------", "!!!!!!!!!!!!!!!!!!!!!!!!! " + response);
                                    JSONObject json_user = response.getJSONObject("InfoSGTINParty");
                                    Log.e("-----55555--------", "!!!!!!!!!!!!!!!!!!!!!!!!! " + json_user);
                                    for (int i = 0; i < response.length(); i++) {
                                        String www = json_user.getString("founded");
                                        int indexJava = www.indexOf("НЕ");
                                        if (indexJava == -1) {
                                            txtW1.setText("SGTIN: " + json_user.getString("sgtin"));
                                            txtW2.setText("Партия: " + json_user.getString("barcode_party"));
                                            party = json_user.getString("barcode_party");
                                            txtW3.setText("Серия: " + json_user.getString("ser_party"));
                                            txtW4.setText("Кол-во: " + json_user.getString("quant"));
                                            txtW5.setText("Движение: " + json_user.getString("income_doc"));
                                            txtW6.setText("Datamatrix");
                                        } else {
                                            txtW6.setText("Datamatrix - НЕ НАЙДЕН");
                                            txtW1.setText("SGTIN:");
                                            txtW2.setText("Партия:");
                                            txtW3.setText("Серия:");
                                            txtW4.setText("Кол-во:");
                                            txtW5.setText("Движение:");
                                            party = null;
                                        }
                                    }
                                }
                                if (tip == 64 | barcodeType == 3 | barcodeType == 73 | barcodeTypeZebra == "EAN-128") {
                                    JSONObject json_user = response.getJSONObject("InfoSSCCParty");
                                    for (int i = 0; i < response.length(); i++) {
                                        String www = json_user.getString("founded");
                                        int indexJava = www.indexOf("НЕ");
                                        if (indexJava == -1) {
                                            txtW1.setText("SSCC: " + json_user.getString("sscc"));
                                            txtW2.setText("Партия: " + json_user.getString("barcode_party"));
                                            party = json_user.getString("barcode_party");
                                            txtW3.setText("Серия: " + json_user.getString("ser_party"));
                                            txtW4.setText("Упаковка: " + json_user.getString("unpack"));
                                            txtW5.setText("Кол-во: " + json_user.getString("quant_max"));
                                            txtW6.setText("SSCC");
                                        } else {
                                            txtW1.setText("SSCC:");
                                            txtW2.setText("Партия:");
                                            txtW3.setText("Серия:");
                                            txtW4.setText("Упаковка:");
                                            txtW5.setText("Кол-во:");
                                            txtW6.setText("SSCC - НЕ НАЙДЕН");
                                            party = null;
                                        }
                                    }
                                }
                                url = null;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        txtW6.setText("НЕТ СВЯЗИ С СЕРВЕРОМ");
                    }
                });

                queue.add(jsObjRequest);
            }
        }else {Toast.makeText(getApplicationContext(), "Нет интернета, проверьте подключение", Toast.LENGTH_LONG).show();}
    }
    /** Returns the consumer friendly device name */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }
    @Override
    protected void onDestroy() {

        if (intentReceiver != null) {
            unregisterReceiver(intentReceiver);
            intentReceiver = null;
        }
        super.onDestroy();
    }
    public void ReadUID(Intent intent) {
        dev = getDeviceName();
        TextView txtW = this.findViewById(R.id.text123);
        this.sendBroadcast(intent);
        IntentFilter filter  = new IntentFilter("nlscan.action.SCANNER_RESULT");
        intentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    barcode = intent.getStringExtra("SCAN_BARCODE1");
                    barcodeType = intent.getIntExtra("SCAN_BARCODE_TYPE", -1);
                    if (barcode != null) {
                        if (barcodeType == 3 | barcodeType == 261) {
                            txtW.setText(barcode);
                            UpdateProcess();
                        } else {
                            txtW.setText("Отсканируйте SSCC или Datamatrix");
                            txtW1.setText("");
                            txtW2.setText("");
                            txtW3.setText("");
                            txtW4.setText("");
                            txtW5.setText("");
                            txtW6.setText("");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Barcode Not getting.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        registerReceiver(intentReceiver,filter);
    }

    private void loadCameraImage() {
        if (this.path == null) {
            return;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            Bitmap originBitmap = BitmapFactory.decodeStream(fis);
            originBitmap = originBitmap.copy(Bitmap.Config.ARGB_4444, true);
            this.preview.setImageBitmap(originBitmap);
        } catch (IOException error) {
            error.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
        }
    }
}
