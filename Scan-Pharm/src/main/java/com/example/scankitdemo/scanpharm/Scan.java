package com.example.scankitdemo.scanpharm;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.scankitdemo.scanpharm.Data.DatabaseHandler;
import com.example.scankitdemo.scanpharm.Data.SessionManager;

import android.device.ScanManager;
import android.device.scanner.configuration.PropertyID;

import java.util.HashMap;
import java.util.List;

public class Scan  extends AppCompatActivity {
    DatabaseHandler db;
    Sbor sb;
    Intent intent;
    BroadcastReceiver intentReceiver;
    SessionManager session;
    public String barcode = null;
    public String barcodeTypeZebra = null;
    public Integer tip = 0, barcodeType = 0;
    private MediaPlayer yes, no;
    private HashMap<String,String> docw = new HashMap<>();
    TextView txtApteka,txtSeriaTotal;
    private SimpleAdapter adapter2;
    ListView messages_list2,list;
    Integer gh = 0;
    StringBuffer sbb;
    private static long back_pressed;

    public Integer dev = 2; // 0 - телефон, 2 - Newland, 3 - Urovo
    int[] idbuf = new int[]{PropertyID.WEDGE_INTENT_ACTION_NAME, PropertyID.WEDGE_INTENT_DATA_STRING_TAG};
    String[] action_value_buf = new String[]{ScanManager.ACTION_DECODE, ScanManager.BARCODE_STRING_TAG};
    private ScanManager mScanManager;
    AlertDialog alertDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initAction();
        String www = getDeviceName();
        int index = www.indexOf("DT50");
        if(index == 6) {
        dev = 3;
        }
        setContentView(R.layout.activity_scan);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        session = new SessionManager(Scan.this);
        db = new DatabaseHandler(Scan.this);
        no = MediaPlayer.create(this, R.raw.no2);
        sb = new Sbor();

        txtApteka = findViewById(R.id.apt);
        txtSeriaTotal = findViewById(R.id.seria);
        messages_list2 = (ListView) findViewById(R.id.nakl);
        simpleArray();
        sbr();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {                                         //Обработка клавиши на корпусе (правая выше кнопки сканера)


        switch (keyCode) {
            case 515:
                if (back_pressed + 500 > System.currentTimeMillis()){
                    //super.onBackPressed();
                    NextPrep();
                }
                else
                    Toast.makeText(getBaseContext(), "Нажмите еще раз для перехода!",
                            Toast.LENGTH_SHORT).show();
                back_pressed = System.currentTimeMillis();

        }
        return super.onKeyDown(keyCode, event);

    }
    //@Override
   // public boolean onKeyLongPress(int keyCode, KeyEvent event) {
   //     if (keyCode == 515) {
   //         String id = db.getidposdoc(session.naklin());
   //         db.updeteposdocNext(id, 1);
   //         simpleArray();
   //         return true;
   //     }
   //     return super.onKeyLongPress(keyCode, event);
   // }

    public void sbr() {
        docw = db.getDocDetails(session.naklin());
        //Log.e("VOLLEY1111111",  docw.get("serial_numb") + " из " + docw.get("total_parts"));
        String ap = docw.get("agent");
        String rrr = "Часть "+docw.get("serial_numb") + " из " + docw.get("total_parts");


        if (ap != null |docw.get("serial_numb") != null | docw.get("total_parts") != null) {
            txtApteka.setText(ap);
            txtSeriaTotal.setText(rrr);

        } else {
            txtApteka.setText("ERROR");
        }
        //final View view = getLayoutInflater().inflate(R.layout.text, null);
        //simpleArray(st);
    }
    private void initAction() {
        if(dev == 2) {
            intent = new Intent("ACTION_BAR_SCANCFG");
            intent.putExtra("EXTRA_SCAN_POWER", 1);
            this.sendBroadcast(intent);
            ReadUID(intent);
        }
    }
    private void showDialog(String title) {
        db.showProgressDialog(Scan.this, title);
    }

    public void SBROS(View view) {                                                                    // КНОПКА СБРОС
        AlertDialog alertDialog = new AlertDialog.Builder(Scan.this)    // Дополнительное диалоговое окно
                .setMessage("Вы уверены что хотите сбросить все отсканированные упаковки на этой позиции?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String id = db.getidposdoc(session.naklin());
                        db.deletposdoc(id);
                        db.deletebar(id);
                        Toast.makeText(getApplicationContext(), "Позиция сброшена",
                                Toast.LENGTH_SHORT).show();
                        simpleArray();
                        return;
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {

                        return;
                    }
                })
                .show();

    }

    public void NextPrep() {                                                                    // КНОПКА NEXT

            String id = db.getidposdoc(session.naklin());
            Integer bar = db.checkmrk(id);
            if(bar==1) {
                Integer koldm = Integer.parseInt(db.getKOL(id));
                Integer quant = Integer.parseInt(db.getQuant(id, session.naklin()));
                if (koldm.equals(quant)) {
                    db.updeteposdocNext(id, 1);
                    Grey();
                    simpleArray();
                }
                if (koldm < quant) {
                    db.updeteposdocNext(id, 2);
                    Grey();
                    simpleArray();
                    /*AlertDialog alertDialog = new AlertDialog.Builder(Scan.this)    // Дополнительное диалоговое окно
                            .setMessage("Вы уверенны что хотите перейти к след?")
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    db.updeteposdocNext(id, 2);
                                    Grey();
                                    simpleArray();
                                    return;
                                }
                            })
                            .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    return;
                                }
                            })
                            .show();

                        */
                }
                if (koldm > quant) {
                    //db.updeteposdocNext(id, 2);
                    Grey();
                    simpleArray();
                    Toast.makeText(getApplicationContext(), "Отсканировано больше упаковок чем нужно!", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                db.updeteposdocNext(id, 1);
                simpleArray();
                /*alertDialog = new AlertDialog.Builder(Scan.this)    // Дополнительное диалоговое окно
                        .setMessage("Это не маркированный препарат, вы уверенны что хотите перейти к след?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db.updeteposdocNext(id, 1);
                                simpleArray();
                                return;
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {

                                return;
                            }
                        })
                        .show();
                        */

            }

    }

    public void Next(View view) {                                                                    // КНОПКА NEXT
        NextPrep();
    }
    public void Sborka(View view) {                                                                 // КНОПКА ВЫЙТИ
        AlertDialog alertDialog = new AlertDialog.Builder(Scan.this)    // Дополнительное диалоговое окно
                .setMessage("Вы уверены что хотите выйти?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String id = db.getidposdoc(session.naklin());
                        Integer koldm = Integer.parseInt(db.getKOL(id));
                        Integer quant = Integer.parseInt(db.getQuant(id, session.naklin()));
                        if (koldm > quant) {
                            db.deletposdoc(id);
                            db.deletebar(id);
                        }
                        db.deletposdocs();
                        Intent intent = new Intent(Scan.this, Sbor.class);
                        showDialog("Обновление..");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                startActivityForResult(intent, 0);
                            } }, 100); //specify the number of milliseconds
                        return;
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {

                        return;
                    }
                })
                .show();

    }
    private void simpleArray(){

        String[] from2 = new String[] {"id", "mh", "name","kol","lol","ser","par","meas","trc"};
        int[] to2 = new int[] { R.id.idnakl, R.id.pref, R.id.agent,R.id.kol,R.id.lol,R.id.ser,R.id.numb,R.id.meas,R.id.trc};
        adapter2 = new SimpleAdapter(this,db.getDetailsPOSDOC20(session.naklin()),R.layout.list_item20,from2, to2);
        messages_list2.setAdapter(adapter2);
        adapter2.notifyDataSetChanged();

    }
    @Override
    public void onBackPressed() {

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
                    Parse();
                }
            }

        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        if(dev == 3) {
            IntentFilter filter = new IntentFilter();// DT-50 ZEBRA
            filter.addCategory(Intent.CATEGORY_DEFAULT);// DT-50 ZEBRA
            filter.addAction(getResources().getString(R.string.activity_intent_filter_action));// DT-50 ZEBRA
            registerReceiver(myBroadcastReceiver, filter);// DT-50 ZEBRA
           // mScanManager = new ScanManager(); //DT50 - H
           // action_value_buf = mScanManager.getParameterString(idbuf);
           // IntentFilter filter = new IntentFilter();
           // filter.addAction(action_value_buf[0]);
           // registerReceiver(mScanReceiver, filter);
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
    private void displayScanResult(Intent initiatingIntent, String howDataReceived)// DT-50 ZEBRA
    {
        String decodedSource = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_source));
        String decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
        String decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type));
        String decodedLabelTypeHON = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_type_hon));
        //Log.e("-------------", "!!!!!!!!!!!!!!!!!!!!!!!!! " + decodedData);
       // Log.e("-------------1", "!!!!!!!!!!!!!!!!!!!!!!!!! " + decodedLabelType);
        //Log.e("-------------2", "!!!!!!!!!!!!!!!!!!!!!!!!! " + decodedLabelTypeHON);
       // Log.e("-------------3", "!!!!!!!!!!!!!!!!!!!!!!!!! " + decodedData+"    "+decodedLabelType);
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
        if(barcodeTypeZebra!=null || barcode!=null){
            Parse();

        }



    }
    @Override
    protected void onPause() {
        if (dev == 2) {
            super.onPause();
            try {
                unregisterReceiver(intentReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (dev == 3) {
            super.onPause();
            //if (mScanManager != null) { // DT-50-H
            //     mScanManager.stopDecode();
            // }
            // unregisterReceiver(mScanReceiver);
            unregisterReceiver(myBroadcastReceiver);
        }
    }
   @Override
    protected void onDestroy() {
        if (intentReceiver != null) {
            unregisterReceiver(intentReceiver);
            intentReceiver = null;
        }
        super.onDestroy();
    }
    public void Grey() {
        ListView view2 = (ListView) findViewById(R.id.nakl);
        view2.setBackgroundColor(Color.rgb(51,71,86));
    }
    public void Red() {
        ListView view2 = (ListView) findViewById(R.id.nakl);
        view2.setBackgroundColor(Color.rgb(202,0,46));
    }
    public void Parse() {
        if (intent != null) {
            if (barcode != null) {
                if (db.getRowCount() != 0) {
                    if (barcodeType == 3 | barcodeType == 261|barcodeType == 119|barcodeType == 73) {
                        if ((barcodeType == 261 | barcodeType == 119) & barcode.length() > 30) {
                            String ww = barcode.substring(2, 16);
                            String kk = barcode.substring(18, 31);
                            Integer check = 0;
                            if (barcode.length() < 40){
                                try {
                                    sbb = new StringBuffer(barcode);
                                    //Log.e("3----------", "!!!!!!!!!!!!!!!!!!!!!!!!! " + sbb);
                                    //sbb.delete(31, 32);
                                    //sbb.delete(37, 38);
                                } catch (Exception e) {
                                    check = 1;
                                    Toast.makeText(getApplicationContext(), "Ошибка!!! с DATAMATRIX", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                            //----------------------------------------------------------------------
                            if (barcode.length() > 40){
                                try {
                                    sbb = new StringBuffer(barcode);
                                    sbb.delete(31, 32);
                                    sbb.delete(37, 38);
                                    //Log.e("4----------", "!!!!!!!!!!!!!!!!!!!!!!!!! " + sbb);
                                } catch (Exception e) {
                                    check = 1;
                                    Toast.makeText(getApplicationContext(), "Ошибка!!! с DATAMATRIX", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                            //----------------------------------------------------------------------
                            if (check == 0) {
                                int status = db.checkbar(ww + kk);
                                simpleArray();
                                String id = db.getidposdoc(session.naklin());
                                if (id  == "") {
                                    Toast.makeText(getApplicationContext(), "ВСЕ позиции отсканированы", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    List<String> bb = db.getbar(ww + kk, id);
                                    try {
                                        if (bb.get(0).length() < 5) {
                                            Integer gh = Integer.parseInt(bb.get(0));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (gh != 99) {
                                        if (bb.size() != 0) {

                                            Integer qu = 0;
                                            Integer koldm = Integer.parseInt(db.getKOL(bb.get(0)));
                                            String idprep = bb.get(0);
                                            try {
                                                qu = Integer.parseInt(db.getQuant(bb.get(0), session.naklin()));// нужен id
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            if (qu != 0) {
                                               /* if (db.getStatus(idprep, koldm) == 0) {   ограничение сканирования*/
                                                    if (status == 10) {
                                                        no.start();
                                                        Toast.makeText(getApplicationContext(), "Упаковка отсутствует на остатке!", Toast.LENGTH_SHORT).show();
                                                    }
                                                    if (status == 0) {
                                                        Grey();
                                                        //Log.e("-------------2", "!!!!!!!!!!!!!!!!!!!!!!!!! " + ww + kk);
                                                        //Log.e("-------------12", "!!!!!!!!!!!!!!!!!!!!!!!!! " + sbb.toString());
                                                        db.updetebar(ww + kk, sbb.toString().replaceAll("^(\\r\\n|[\\n\\x0B\\x0C\\r\\u0085\\u2028\\u2029])|(\\r\\n|[\\n\\x0B\\x0C\\r\\u0085\\u2028\\u2029])$", ""), id);
                                                        koldm = Integer.parseInt(db.getKOL(bb.get(0)));
                                                        if (qu.equals(koldm)) {
                                                            //db.updeteposdoc(bb.get(0));
                                                        }
                                                        simpleArray();
                                                        adapter2.notifyDataSetChanged();
                                                    }
                                                    if (status == 1) {
                                                        Red();
                                                        no.start();
                                                        simpleArray();
                                                        Toast.makeText(getApplicationContext(), "Упаковка уже отсканирована!", Toast.LENGTH_SHORT).show();
                                                    }
                                                    if (status == 2) {
                                                        no.start();
                                                        Toast.makeText(getApplicationContext(), "Упаковка уже отсканирована в коробке!", Toast.LENGTH_SHORT).show();
                                                        Red();
                                                    }
                                               /* } else {  //ограничение сканирования
                                                    if (status == 1) {
                                                        no.start();
                                                        Toast.makeText(getApplicationContext(), "Упаковка уже отсканирована!", Toast.LENGTH_SHORT).show();
                                                        Red();
                                                    }
                                                    if (status == 0) {
                                                        no.start();
                                                        Toast.makeText(getApplicationContext(), "Этот препарат отсканирован!", Toast.LENGTH_SHORT).show();
                                                        Red();
                                                    }
                                                }*/
                                            } else {
                                                no.start();
                                                Toast.makeText(getApplicationContext(), "Не тот препарат!", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            no.start();
                                           simpleArray();
                                            Toast.makeText(getApplicationContext(), "Упаковки нет в базе!", Toast.LENGTH_SHORT).show();
                                            Red();
                                        }
                                    } else {
                                        no.start();
                                        simpleArray();
                                        Toast.makeText(getApplicationContext(), "Не этот препарат!!!", Toast.LENGTH_SHORT).show();
                                        Red();
                                    }
                                }
                            }
                        }
                        if (barcodeType == 3|barcodeType == 73) {
                            try {
                                sbb = new StringBuffer(barcode);
                                sbb.delete(0, 2);
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Ошибка!!! с Коробкой!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                            String id = db.getidposdoc(session.naklin());
                            List<String> ss = db.getsscc(sbb.toString(),id); //все упаковки по SSCC
                            if (ss.size() != 0) {
                                Integer kolq = Integer.parseInt(db.getQuant(ss.get(0),session.naklin()));  // нужное кол-во препаратов
                                Integer kolb = Integer.parseInt(db.getKOL(ss.get(0)));//
                                Integer sscc = Integer.parseInt(db.getKOLSSCC(sbb.toString(),id));
                                String[] array = new String[ss.size()];
                                ss.toArray(array);
                                int status = 0;
                                for (int i = 1; i < array.length; i = i + 3) {
                                    Integer r = Integer.parseInt(ss.get(i));
                                    if (r == 1) {
                                        status = 1;
                                        break;
                                    }
                                    if (r == 2) {
                                        status = 2;
                                        break;
                                    }
                                    System.out.println(array[i]);
                                }
                                if (kolq - kolb >= sscc) {
                                    if (status == 0) {
                                        db.updetebarSSCC(sbb.toString(),id,ss.get(0));
                                        Integer kk = Integer.parseInt(db.getQuant(ss.get(0),session.naklin()));  // нужное кол-во препаратов
                                        Integer oo = Integer.parseInt(db.getKOL(ss.get(0)));//
                                        simpleArray();
                                        adapter2.notifyDataSetChanged();
                                    }
                                    if (status == 1) {
                                        no.start();
                                        Toast.makeText(getApplicationContext(), "Из этой коробки уже отсканировали упаковку!", Toast.LENGTH_SHORT).show();
                                        Red();
                                    }
                                    if (status == 2) {
                                        no.start();
                                        Toast.makeText(getApplicationContext(), "Коробка уже отсканирована!", Toast.LENGTH_SHORT).show();
                                        Red();
                                    }
                                } else {
                                    no.start();
                                    Toast.makeText(getApplicationContext(), "Кол-во в коробке превышает нужное кол-во упаковок!", Toast.LENGTH_SHORT).show();
                                    Red();
                                }
                            } else {
                                no.start();
                                Toast.makeText(getApplicationContext(), "Коробки нет на остатках!", Toast.LENGTH_SHORT).show();
                                Red();
                            }

                        }
                    }

                } else {
                }
            } else {
                no.start();
                Toast.makeText(getApplicationContext(), "Загрузите сборочный лист", Toast.LENGTH_SHORT).show();
            }
        } else {
            no.start();
            Toast.makeText(getApplicationContext(), "Barcode Not getting.", Toast.LENGTH_SHORT).show();
        }
    }
    public void ReadUID(Intent intent) {
        try {
            intentReceiver = null;
            this.sendBroadcast(intent);
            IntentFilter filter = new IntentFilter("nlscan.action.SCANNER_RESULT");
            intentReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    barcode = intent.getStringExtra("SCAN_BARCODE1");
                    barcodeType = intent.getIntExtra("SCAN_BARCODE_TYPE", -1);
                    Parse();
                    }
            };
            registerReceiver(intentReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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
}
