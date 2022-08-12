package com.example.scankitdemo.scanpharm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.scankitdemo.scanpharm.Data.DatabaseHandler;
import com.example.scankitdemo.scanpharm.Data.SessionManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Sbor extends AppCompatActivity {
    public static String URL = "********"+Build.SERIAL;
    private static boolean inet;
    DatabaseHandler db;
    public int mStatusCode, scan;
    private HashMap<String, String> doc = new HashMap<>();
    private HashMap<String, String> posdoc = new HashMap<>();
    TextView txtAp, txtDate, txtOtdel, rr, txtk, txtt, txtnum, txtkol, txtscankol;
    Button myButton, buttonexit, send, qq, test;
    SessionManager session;
    Switch sw;
    private List<View> allEds;
    private SimpleAdapter adapter;
    ListView messages_list, list;
    public String barcode = null;
    private int REQUEST_CODE_SCAN_ALL = 1;
    private int REQUEST_N = 1005;
    private int REQUEST_TAKE_PHOTO = 2;
    String kol = "0";
    String statusdoc = "Error";
    String k = "-1";
    public String idnakl;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sbor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtAp = findViewById(R.id.Aptek);
        txtDate = findViewById(R.id.dates);
        txtOtdel = findViewById(R.id.otdels);
        txtnum = findViewById(R.id.nomer);
        txtkol = findViewById(R.id.textView18);
        txtscankol = findViewById(R.id.textView19);
        test = findViewById(R.id.button5);
        myButton = findViewById(R.id.btn);
        send = findViewById(R.id.button3);
        buttonexit = findViewById(R.id.button2);
        sw = findViewById(R.id.switch1);
        rr = findViewById(R.id.lol);
        session = new SessionManager(Sbor.this);
        db = new DatabaseHandler(Sbor.this);
        scan = 0;
        getSupportActionBar().setTitle("Пользователь: " + session.loginin());
        allEds = new ArrayList<View>();
        messages_list = (ListView) findViewById(R.id.nakl);
        messages_list.setBackgroundColor(1);
        hideDialog();
        www(1);
        Intent intent = new Intent ("ACTION_BAR_SCANCFG");
        intent.putExtra("EXTRA_SCAN_POWER", 0);
        this.sendBroadcast(intent);


    }
    public void send(MenuItem item) {

    }
    public void down(MenuItem item) {                                                               //Загрузка сборочного листа\
    }
    public void main(MenuItem item) {                                                               // Выход в накл
        Intent intent = new Intent(Sbor.this, ListNakl.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {                                                 //Создание меню
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_s, menu);

        if (menu.getClass().getSimpleName()
                .equals("MenuBuilder")) {
            try {
                Method m = menu.getClass()
                        .getDeclaredMethod(
                                "setOptionalIconsVisible",
                                Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            } catch (NoSuchMethodException e) {
                System.err.println("onCreateOptionsMenu");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    public void Scan(View view) {                                                                   //переход на сканирование
        Intent intent = new Intent(Sbor.this, Scan.class);
        startActivityForResult(intent, REQUEST_N);
    }
    public static String removeLastChar(String s) {
        return (s == null || s.length() == 0) ? null : (s.substring(0, s.length() - 1));
    }
    private void simpleArray(int st) {
            String[] from = new String[]{"name", "party", "mh", "kol", "rowid", "status", "seria","mrk"};
            int[] to = new int[]{R.id.agent, R.id.numb, R.id.pref, R.id.kol, R.id.ID, R.id.lol, R.id.ser,R.id.mrk};
            adapter = new SimpleAdapter(this, db.getPosDocDetails(session.naklin(), st, sw.isChecked()), R.layout.list_item, from, to) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    Button b = (Button) v.findViewById(R.id.clearp);
                    LinearLayout linearLayout1 = (LinearLayout) v.findViewById(R.id.line);
                    final TextView tv = (TextView) v.findViewById(R.id.ID);
                    final String id = (String) tv.getText();
                    Integer status = db.checkST(id);
                    if(status==1) {
                        linearLayout1.setBackgroundResource(R.color.str);
                    }
                    else{
                        linearLayout1.setBackgroundResource(R.color.colorPrimaryDark);
                    }
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            final TextView tv = (TextView) v.findViewById(R.id.ID);
                            final String id = (String) tv.getText();
                            db.deletposdoc(id);
                            db.deletebar(id);
                            if (sw.isChecked()) {
                                www(0);
                            } else {
                                www(1);
                            }
                            Toast.makeText(getApplicationContext(), "Позиция сброшена",
                                    Toast.LENGTH_SHORT).show();
                            // www(1);
                        }
                    });
                    return v;
                }
            };
            txtscankol.setText("Скан - " + db.getKOLPOSDOC());
            messages_list.setAdapter(adapter);
            adapter.notifyDataSetChanged();
    }

    public void logoutUser(View view) {
        session.setLogin(false);
        Intent intent = new Intent(Sbor.this, Login.class);
        startActivity(intent);
        finish();
    }



    public void www(int st) {
        doc = db.getDocDetails(session.naklin());
        String ap = doc.get("agent");
        String num = doc.get("pref") + "-" + doc.get("numb");
        String date = doc.get("date");
        String otdel = doc.get("otdel");
        kol = String.valueOf(db.getKOLPOSDOC());
        k = String.valueOf(db.getALLPOSDOC());

        if (ap != null | date != null | otdel != null | doc.get("pref") != null | doc.get("numb") != null) {
            txtAp.setText(ap);
            txtDate.setText(date);
            txtOtdel.setText(otdel);
            txtnum.setText(num);
            txtkol.setText("Кол - " + kol);
        } else {
            txtAp.setText("Загрузите сборочный лист!");
        }
        final View view = getLayoutInflater().inflate(R.layout.text, null);
        simpleArray(st);
    }
    private void showDialog(String title) {
        db.showProgressDialog(Sbor.this, title);
    }
    private void hideDialog() { db.hideProgressDialog(Sbor.this); }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data);
            String responseBody1 = new String(error.networkResponse.headers.toString());
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("message");
            Toast.makeText(getApplicationContext(), "Ошибка! " + message, Toast.LENGTH_LONG).show();
            if (message.equals("invalid auth header")) {
                session.setLogin(false);
                Intent intent = new Intent(Sbor.this, Login.class);
                startActivityForResult(intent, REQUEST_N);
            }
        } catch (JSONException e) {
        }
    }
    @Override
    public void onBackPressed() {
        hideDialog();
    }
}