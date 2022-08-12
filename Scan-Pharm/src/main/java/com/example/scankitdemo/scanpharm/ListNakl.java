package com.example.scankitdemo.scanpharm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.scankitdemo.scanpharm.Data.DatabaseHandler;
import com.example.scankitdemo.scanpharm.Data.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListNakl extends AppCompatActivity {
    DatabaseHandler db;
    private HashMap<String, String> doc = new HashMap<>();
    private HashMap<String, String> posdoc = new HashMap<>();
    SessionManager session;
    Sbor sbor;
    int mStatusCode = 0;
    int servererror = 0;
    private List<View> allEds;
    private SimpleAdapter adapter;
    ListView messages_list, list;
    String statusdoc = "Error";
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nakl);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = new Intent ("ACTION_BAR_SCANCFG");
        intent.putExtra("EXTRA_SCAN_POWER", 0);
        this.sendBroadcast(intent);
        sbor = new Sbor();
        session = new SessionManager(ListNakl.this);
        db = new DatabaseHandler(ListNakl.this);
        getSupportActionBar().setTitle("Пользователь: " + session.loginin());
        allEds = new ArrayList<View>();
        messages_list = (ListView) findViewById(R.id.nakl);
        messages_list.setBackgroundColor(1);
        simpleArray();
        hideDialog();

    }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {                                                 //Создание меню
       MenuInflater inflater = getMenuInflater();
       inflater.inflate(R.menu.menu_l, menu);
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
   public void main1(MenuItem item) {                                                               // Выход в главное меню
        intent = new Intent(ListNakl.this, MainActivity.class);
       startActivityForResult(intent, 1);
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
    public void down1(MenuItem item) {
        if (isOnline(this)) {

            AlertDialog alertDialog = new AlertDialog.Builder(ListNakl.this)        // Дополнительное диалоговое окно
                    .setMessage("Вы действительно хотите загрузить? Все данные удалятся")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                           //StatusJson("NEW");
                            String ff = db.getALLPOSDOC();
                            if(Integer.parseInt(ff) == 0) {
                                Updatenew();
                                statusdoc = "Error";
                                hideDialog();
                                return;
                            }
                            else  { Toast.makeText(getApplicationContext(), "Отправте или Отмените сборочный лист!!!", Toast.LENGTH_LONG).show();
                            return; }
                        }
                    })
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            statusdoc = "Error";
                            hideDialog();
                            return;
                        }
                    })
                    .show();
        }
        else{ Toast.makeText(getApplicationContext(), "Нет интернета! ", Toast.LENGTH_LONG).show();}
    }
    @Override
    public void onBackPressed() {
        hideDialog();
    }
    private void simpleArray() {                                                                    //ОБНОВЛЕНИЕ ДАННЫХ НА АКТИВИТИ
        String[] from = new String[]{"agent", "pref", "numb","id","otdel","serial_numb","date"};
        int[] to = new int[]{R.id.agent, R.id.pref, R.id.numb,R.id.idnakl,R.id.otdel,R.id.sertot,R.id.daten};
        adapter = new SimpleAdapter(this, db.getDocDet(), R.layout.list_nakl, from, to) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                Button b = (Button) v.findViewById(R.id.prosmotr);
                LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.str);
                final TextView tv = (TextView) v.findViewById(R.id.idnakl);
                final String id = (String) tv.getText();
                String status = db.getKOLPOSDOC2(id);
                String allkol = db.getKOLPOSDOC3(id);
                if(Integer.parseInt(status)==Integer.parseInt(allkol)) {
                    linearLayout.setBackgroundResource(R.color.str);
                }
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        final TextView tv = (TextView) v.findViewById(R.id.idnakl);
                        final String id = (String) tv.getText();
                        session.setNakl(id);
                        showDialog("Обновление..");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                Intent intent = new Intent(ListNakl.this, Sbor.class);
                                startActivityForResult(intent, 1);
                            } }, 100); //specify the number of milliseconds
                    }
                });
                return v;
            }
        };
        messages_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
    private void showDialog(String title) {
        db.showProgressDialog(ListNakl.this, title);
    }

    private void hideDialog() { db.hideProgressDialog(ListNakl.this); }

    public String StatusJson(String status) {
        JSONObject jsonObject = new JSONObject();
        List<String> getgtin = db.getnumb();
        // задаем идентификатор
        try {
            jsonObject.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(JSONObject.quote(jsonObject.toString()));
        sendstatus(jsonObject);
        return jsonObject.toString();
    }
    public void parseVolleyError(VolleyError error) {                                               //ПАРСЕР ОШИБОК
        try {
            String responseBody = new String(error.networkResponse.data);
            String responseBody1 = new String(error.networkResponse.headers.toString());
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("message");
            int parus = message.indexOf("PARUS");
            if(parus>0) { servererror = 1; }

            int index2 = message.indexOf("Empty");
            //Log.e("-------------1", "!!!!!!!!!!!!!!!!!!!!!!!!! " + message);
            //Log.e("-------------2", "!!!!!!!!!!!!!!!!!!!!!!!!! " + index2);
            if(index2 == 0 ) {
                //Log.e("-------------2", "!!!!!!!!!!!!!!!!!!!!!!!!! " + index2);
                Toast.makeText(getApplicationContext(), "!НЕТ СБОРОЧНЫХ ДЛЯ ЗАГРУЗКИ!" + message, Toast.LENGTH_LONG).show();
            }
            int index = message.indexOf("token");
            if(index ==0 | message.equals("invalid auth header")) {
                Toast.makeText(getApplicationContext(), "Ошибка! Авторизации" + message, Toast.LENGTH_LONG).show();
                session.setLogin(false);
                Intent intent = new Intent(ListNakl.this, First.class);
                startActivityForResult(intent, 0);
            }
            if(index !=0 & index2 != 0){
            Toast.makeText(getApplicationContext(), "Ошибка! " + message, Toast.LENGTH_LONG).show();}
        } catch (JSONException e) {
        }
    }
    public void sendstatus(JSONObject st) {                                                         //ИЗМЕНЕНИЕ СТАТУСА
        if (isOnline(this)) {
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String URL = "http://" + session.ipin() + "/api/cdb/assembly/status/" + db.getiddoc();
                final String requestBody = st.toString();
                StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String data = "Успешно!";
                        if (mStatusCode == 200) {
                            Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.e("VOLLEY", error.toString());
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{ Toast.makeText(getApplicationContext(), "Нет интернета! ", Toast.LENGTH_LONG).show();}
    }

    public static JSONObject sscc(String lastName) {                                                //ПЕРЕЧИСЛЕНИЕ SSCC ДЛЯ JSON
        JSONObject person = new JSONObject();
        try {
            person.put("value", lastName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return person;
    }

    public static JSONObject sgtin(String lastName) {                                               //ПЕРЕЧИСЛЕНИЕ SGTIN ДЛЯ JSON
        JSONObject person = new JSONObject();
        try {
            person.put("value", lastName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return person;
    }

    public String buildWeatherJson() {                                                               //СОЗДАНИЕ JSON
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String dat = dateFormat.format(date);
        String tim = timeFormat.format(date);
        JSONObject jsonObject = new JSONObject();
        List<String> doc = db.getdoccend();
        try {
            jsonObject.put("rn", db.getiddoc());
            jsonObject.put("datecreate", dat);
            jsonObject.put("timecreate", tim);
            // создаем поле с именем
            JSONArray docc = new JSONArray();
            for (int s = 0;  s < doc.size(); s++) {
                JSONObject ss = new JSONObject();
                ss.put("rn", Long.parseLong(doc.get(s)));
                List<String> getss = db.getssccend(doc.get(s));
                List<String> getgtin = db.getsgtinend(doc.get(s));
                JSONArray ssar = new JSONArray();
                for (int i = 0; i < getss.size(); i++) {
                    ssar.put(sscc("" + getss.get(i) + ""));
                }
                ss.put("sscc", ssar);
                JSONArray sgar = new JSONArray();
                for (int i = 0; i < getgtin.size(); i++) {
                    sgar.put(sgtin("" + getgtin.get(i) + ""));
                }
                ss.put("sgtin", sgar);
                docc.put(ss);
            }
            jsonObject.put("documents", docc);
        } catch (JSONException e) {

            e.printStackTrace();
        }

        System.out.println(JSONObject.quote(jsonObject.toString()));
        send(jsonObject);
        return jsonObject.toString();
    }
    public void send(JSONObject all) {                                                              //ОТПРАВКА СБОРОЧНОГО ЛИСТА
        if (isOnline(this)) {
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String URL = "http://" + session.ipin() + "/api/cdb/assembly/upload?id_tsd="+Build.SERIAL;
                final String requestBody = all.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String data = "Успешно!";
                        //Log.e("-------------", "!!!!!!!!!!!!!!!!!!!!!!!!! " + mStatusCode);
                        if (mStatusCode == 200) {
                            //Log.e("-------------1", "!!!!!!!!!!!!!!!!!!!!!!!!! aaaaaaaaaaaaaaaaaa" + mStatusCode);
                            db.resetTables();
                            simpleArray();
                            Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.e("VOLLEY", error.toString());
                        //   parseVolleyError(error);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{ Toast.makeText(getApplicationContext(), "Нет интернета! ", Toast.LENGTH_LONG).show();}

    }
    public void send(MenuItem item) {                                                               //КНОПКА Отправить сборочный лист
        if (db.getRowCount() != 0) {
            if (isOnline(this)) {
                infostatus();
                showDialog("Отправка...");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run () {
                        Integer st = 0;
                        if (servererror != 1) {
                            if (statusdoc == "Error") {
                                Toast.makeText(getApplicationContext(), "Соединение отсутствует!", Toast.LENGTH_LONG).show();
                                statusdoc = "Error";
                                hideDialog();
                            } else {
                                String kol = String.valueOf(db.getKOLPOSDOC());
                                statusdoc = "Error";
                                hideDialog();
                                String k = String.valueOf(db.POSDOC());
                                //------------------------------------------------------------------
                                List<String> doc = db.getdoccend();
                                for (int s = 0;  s < doc.size(); s++) {
                                    List<String> getss = db.checkposdoc(doc.get(s));
                                    for (int i = 0; i < getss.size(); i++) {
                                        Integer koldm = Integer.parseInt(db.getKOL(getss.get(i)));
                                        Integer quant = Integer.parseInt(db.getQuant(getss.get(i), doc.get(s)));
                                        if(koldm>quant) {
                                            Toast.makeText(getApplicationContext(), "Ошибка! кол-во scan:"+ koldm +" кол-во по документу:"+quant, Toast.LENGTH_LONG).show();
                                            st = 1;
                                        }
                                    }
                                }
                                if(st == 0) {
                                    //--------------------------------------------------------------------
                                    if (0 == Integer.parseInt(k)) {
                                        try {
                                            buildWeatherJson();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        AlertDialog alertDialog = new AlertDialog.Builder(ListNakl.this)        // Дополнительное диалоговое окно
                                                .setMessage("Не все позиции отсканированы! Вы действительно хотите отправить?")
                                                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        try {
                                                            buildWeatherJson();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                        statusdoc = "Error";
                                                        hideDialog();
                                                        return;
                                                    }
                                                })
                                                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        statusdoc = "Error";
                                                        hideDialog();
                                                        return;
                                                    }
                                                })
                                                .show();
                                    }
                                }
                                else { Toast.makeText(getApplicationContext(), "Проверьте позиции документа!!!", Toast.LENGTH_LONG).show();}
                            }
                        } else{
                            servererror = 0;
                            Toast.makeText(getApplicationContext(), "Ошибка на сервере !!!", Toast.LENGTH_LONG).show();
                        }
                    }
                }, 2000); //specify the number of milliseconds
            }else{ Toast.makeText(getApplicationContext(), "Нет интернета! ", Toast.LENGTH_LONG).show();}
        } else {
            hideDialog();
            //statusdoc = "Error";
            Toast.makeText(getApplicationContext(), "Загрузите сборочный лист!", Toast.LENGTH_LONG).show();
        }
    }
    public void claer1(MenuItem item) {                                                             // КНОПКА ОЧИСТКИ
        if (isOnline(this)) {
            showDialog("ОЧИСТКА...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                        AlertDialog alertDialog = new AlertDialog.Builder(ListNakl.this)    // Дополнительное диалоговое окно
                                .setMessage("Вы уверены что хотите очистить? (ВСЕ ДАННЫЕ УДАЛЯТЬСЯ)")
                                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (db.getRowCount() != 0 && servererror == 404) {
                                            hideDialog();
                                            //StatusJson("NEW");
                                            db.resetTables();
                                            simpleArray();
                                        } else {
                                            if (servererror != 404) {
                                                hideDialog();
                                                statusdoc = "Error";
                                                Toast.makeText(getApplicationContext(), "Сначала попробуйте сделать ОТМЕНУ!", Toast.LENGTH_LONG).show();
                                            }
                                            if(db.getRowCount() == 0) {
                                                hideDialog();
                                                statusdoc = "Error";
                                                Toast.makeText(getApplicationContext(), "Загрузите сборочный лист!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        return;
                                    }
                                })
                                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        hideDialog();
                                        return;
                                    }
                                })
                                .show();
                }
            }, 1500); //specify the number of milliseconds

        }else {
            Toast.makeText(getApplicationContext(), "Нет интернета! ", Toast.LENGTH_LONG).show();
        }
    }
    public void cenc1(MenuItem item) {                                                              // КНОПКА ОТМЕНЫ
        if (db.getRowCount() != 0) {
            if (isOnline(this)) {
                infostatus();
                showDialog("Отмена...");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (servererror != 1) {
                            AlertDialog alertDialog = new AlertDialog.Builder(ListNakl.this)    // Дополнительное диалоговое окно
                                    .setMessage("Вы уверены что хотите отменить?")
                                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (statusdoc == "Error") {
                                                Toast.makeText(getApplicationContext(), "Соединение отсутствует!", Toast.LENGTH_LONG).show();
                                                statusdoc = "Error";
                                                hideDialog();
                                            }
                                            simpleArray();
                                            hideDialog();
                                            if (db.getRowCount() != 0) {
                                                hideDialog();
                                                StatusJson("NEW");
                                                db.resetTables();
                                                simpleArray();
                                            } else {
                                                hideDialog();
                                                statusdoc = "Error";
                                                Toast.makeText(getApplicationContext(), "Загрузите сборочный лист!", Toast.LENGTH_LONG).show();
                                            }
                                            return;
                                        }
                                    })
                                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            hideDialog();
                                            return;
                                        }
                                    })
                                    .show();

                        } else {
                            servererror = 404;
                            Toast.makeText(getApplicationContext(), "Ошибка на сервере !!!", Toast.LENGTH_LONG).show();
                        }
                    }
                }, 1500); //specify the number of milliseconds

            }else {
                Toast.makeText(getApplicationContext(), "Нет интернета! ", Toast.LENGTH_LONG).show();
            }
        } else {
            hideDialog();
            statusdoc = "Error";
            Toast.makeText(getApplicationContext(), "Загрузите сборочный лист!", Toast.LENGTH_LONG).show();
        }

    }
    public void Updatenew() {                                                                       //ЗАГРУЗКА СБОРОЧНОГО ЛИСТА
        try {
            showDialog("Загрузка ...");
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "http://" + session.ipin() + "/api/cdb/assembly?id_tsd="+Build.SERIAL;
              StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
            try {
                db.resetTables();
                JSONObject obj = new JSONObject(response);
                JSONArray docc = obj.getJSONArray("Documents");
                for (int p = 0; p < docc.length(); p++) {
                    JSONObject doc = docc.getJSONObject(p);

                    db.addDOC(doc.getString("rn"), doc.getString("date"), doc.getString("zone"), doc.getString("agent"), doc.getString("pref"), doc.getString("numb"),obj.getString("rn"),0,obj.getString("serial_numb"),obj.getString("total_parts"));
                    String iddoc = doc.getString("rn");
                    JSONArray jArray = doc.getJSONArray("positions");
                    for (int j = 0; j < jArray.length(); j++) {
                        JSONObject ps = jArray.getJSONObject(j);
                        String rn = ps.getString("rn");
                        Integer numb = 0;// ps.getInt("numb");
                        Integer mrk = ps.getInt("mrk");
                        String mh = ps.getString("mh");
                        String mh_ord = ""; //ps.getString("mh_ord");
                        String name = ps.getString("name");
                        String ser = ps.getString("ser");
                        String dexp = ps.getString("dexp");
                        String barcode = ps.getString("barcode");
                        String party = ps.getString("party");
                        String quant = "";//ps.getString("quant");
                        String quant_pack = ps.getString("quant_pack");
                        String meas = ps.getString("meas");
                        String trc = ps.getString("trans_cond");
                        db.addPOSDOC(iddoc, rn, name, mrk, mh_ord, mh, dexp, ser, party, barcode, meas, quant_pack, 0, 0, trc);
                        if(mrk == 1) {
                            JSONArray ss = ps.getJSONArray("sscc");
                            for (int n = 0; n < ss.length(); n++) {
                                StringBuilder sbSql = new StringBuilder("Insert Into BARCODE (id_pos_bar, sscc, rs, gtin, status) VALUES");
                                JSONObject ccss = ss.getJSONObject(n);
                                String code = ccss.getString("code");
                                JSONArray sgtin = ccss.getJSONArray("sgtin");
                                for (int y = 0; y < sgtin.length(); y++) {
                                    JSONObject rr = sgtin.getJSONObject(y);
                                    Log.e("qqwqw", rr.toString());
                                    Integer rs = rr.getInt("rs");
                                    String value = rr.getString("value");
                                    db.addBARCODE(rn, code, rs, value, 0);
                                    sbSql.append(" ('").append(rn);
                                    sbSql.append("', '").append(code);
                                    sbSql.append("', '").append(rs);
                                    sbSql.append("', '").append(value);
                                    sbSql.append("', '").append(0);

                                    if (y == sgtin.length() - 1) {
                                        sbSql.append("')");
                                    } else {
                                        sbSql.append("'),");
                                    }
                                }
                                String sql = sbSql.toString();
                                Log.e("qqwqw", sql.toString());
                            }
                        }

                    }
                }
                hideDialog();
            } catch (JSONException e) {
                e.printStackTrace();
                hideDialog();
            }
            simpleArray();
            StatusJson("DOWNLOADED");
            hideDialog();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideDialog();
                    try {
                        parseVolleyError(error);
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
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", " Bearer " + session.tokenin());
                    return params;
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
    public void infostatus() {                                                                      // ЗАПРОС СТАТУСА СБОРОЧНОГО ЛИСТА
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            List<String> getgtin = db.getnumb();
            String URL = "http://" + session.ipin() + "/api/cdb/assembly/status/" + db.getiddoc();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        statusdoc =  obj.getString("result");
                        hideDialog();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    hideDialog();
                    try {
                        parseVolleyError(error);
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
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", " Bearer " + session.tokenin());
                    return params;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {

                    if (response != null) {
                        mStatusCode = response.statusCode;
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
}
