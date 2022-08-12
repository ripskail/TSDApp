package com.example.scankitdemo.scanpharm.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.scankitdemo.scanpharm.Sbor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "Android";
    // Login table name
    private static final String TABLE_SBORDOC = "SBORDOC";
    private static final String TABLE_DOC = "DOC";
    private static final String TABLE_POSDOC = "POSDOC";
    private static final String TABLE_BARCODE = "BARCODE";
    // SBORDOC Columns names
    private static final String KEY_IID = "id";
    private static final String KEY_N = "numb";
    // DOC Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_OTDEL = "otdel";
    private static final String KEY_AP = "agent";
    private static final String KEY_PREF = "pref";
    private static final String KEY_NUMB = "numb";
    private static final String KEY_SBOR = "rn";
    private static final String KEY_ST_DOC = "stdoc";
    private static final String KEY_SERIAL = "serial_numb";
    private static final String KEY_TOTAL = "total_parts";
    // POSDOC Columns names
    private static final String KEY_ID_DOC = "id_doc";
    private static final String KEY_ID_POS = "id_pos";
    private static final String KEY_NAME_PREP = "name";
    private static final String KEY_MARK = "mrk";
    private static final String KEY_MH_ORD = "mh_ord";
    private static final String KEY_MH = "mh";
    private static final String KEY_DEXP = "dexp";
    private static final String KEY_SER = "seria";
    private static final String KEY_PARTY = "party";
    private static final String KEY_BARCODE = "barcode";
    private static final String KEY_MEAS = "meas";
    private static final String KEY_QUANT = "quant";
    private static final String KEY_ST = "st";
    private static final String KEY_NM = "numb";
    private static final String KEY_TRC = "trans_cond";

    // BARCODE Columns names
    private static final String KEY_ID_POS_BAR = "id_pos_bar";
    private static final String KEY_SSCC = "sscc";
    private static final String KEY_NAL= "rs";
    private static final String KEY_GTIN = "gtin";
    private static final String KEY_STATUS = "status";
    private static final String KEY_DATA = "datamatrix";






    public DatabaseHandler(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    private static final String CREATE_DOC_TABLE = "CREATE TABLE " + TABLE_DOC + "("
            + KEY_ID + " TEXT ,"
            + KEY_DATE + " DATE,"
            + KEY_OTDEL + " TEXT,"
            + KEY_AP + " TEXT,"
            + KEY_PREF + " TEXT,"
            + KEY_NUMB + " TEXT,"
            + KEY_SBOR + " TEXT,"
            + KEY_ST_DOC + " INTEGER,"
            + KEY_SERIAL + " TEXT,"
            + KEY_TOTAL + " TEXT"+")";
    // Table POSDOC
    private static final String CREATE_POSDOC_TABLE = "CREATE TABLE " + TABLE_POSDOC + "("
            + KEY_ID_DOC + " TEXT,"
            + KEY_ID_POS + " TEXT,"
            + KEY_NAME_PREP + " TEXT,"
            + KEY_MARK + " INTEGER,"
            + KEY_MH_ORD + " TEXT,"
            + KEY_MH + " TEXT,"
            + KEY_DEXP + " TEXT,"
            + KEY_SER + " TEXT,"
            + KEY_PARTY + " TEXT,"
            + KEY_BARCODE + " TEXT,"
            + KEY_MEAS + " TEXT,"
            + KEY_QUANT + " TEXT,"//КОЛ-ВО

            + KEY_ST + " INTEGER,"//СТАТУС
            + KEY_NM + " INTEGER,"
            + KEY_TRC + " TEXT"+")";
    // Table BARCODE
    private static final String CREATE_BARCODE_TABLE = "CREATE TABLE " + TABLE_BARCODE + "("
            + KEY_ID_POS_BAR + " TEXT,"
            + KEY_SSCC + " TEXT,"
            + KEY_NAL + " INTEGER,"
            + KEY_GTIN + " TEXT,"
            + KEY_STATUS + " INTEGER,"
            + KEY_DATA + " TEXT"+")"; //DATAMATRIX
    // Creating Tables

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DOC_TABLE);
        db.execSQL(CREATE_POSDOC_TABLE);
        db.execSQL(CREATE_BARCODE_TABLE);
        db.execSQL("CREATE UNIQUE INDEX q1 ON DOC (id)");
        db.execSQL("CREATE UNIQUE INDEX q2 ON POSDOC (id_pos,seria)");
        db.execSQL("CREATE UNIQUE INDEX q5 ON BARCODE (gtin,id_pos_bar)");
    }
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSDOC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BARCODE);
        // Create tables again
        onCreate(db);
    }
    public void addBARCODE(String id,String ss,int nal, String gtin,int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("PRAGMA synchronous=OFF");
        ContentValues values = new ContentValues();
        values.put(KEY_ID_POS_BAR, id);
        values.put(KEY_SSCC, ss);
        values.put(KEY_NAL, nal);
        values.put(KEY_GTIN, gtin);
        values.put(KEY_STATUS, status);
        values.put(KEY_DATA, "");
        db.insert(TABLE_BARCODE, null, values);

    }
    public void addPOSDOC(String id,String idd, String name,int mark,String mho,String mh,String dexp,String ser, String part, String barcode, String mess,String quant,int status,int nm,String trc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_DOC, id);
        values.put(KEY_ID_POS, idd);
        values.put(KEY_NAME_PREP, name);
        values.put(KEY_MARK, mark);
        values.put(KEY_MH_ORD, mho);
        values.put(KEY_MH, mh);
        values.put(KEY_SER, ser);
        values.put(KEY_DEXP, dexp);
        values.put(KEY_PARTY, part);
        values.put(KEY_BARCODE, barcode);
        values.put(KEY_MEAS, mess);
        values.put(KEY_QUANT, quant);
        values.put(KEY_ST, status);
        values.put(KEY_NM, nm);
        values.put(KEY_TRC, trc);
        db.insert(TABLE_POSDOC, null, values);
        db.close(); // Closing database connection
    }
    public  String getidposdoc(String nak) {
        String query = String.format("SELECT id_pos FROM POSDOC WHERE st != 1 and id_doc ="+nak+" ORDER BY st");
        SQLiteDatabase db  = this.getReadableDatabase();
        String  li ="";
        Cursor cursor      = db.rawQuery(query, null);
        String[] data      = null;
        if (cursor.moveToFirst()) {
            do {
                String  name=cursor.getString(0);
                li=name;
                System.out.println("long l = " + li);
                break;
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();
        return li;
    }
    public  long getiddoc() {
        String query = String.format("SELECT rn FROM DOC GROUP BY rn");
        SQLiteDatabase db  = this.getReadableDatabase();
        String  li ="";
        Cursor cursor      = db.rawQuery(query, null);
        String[] data      = null;
        if (cursor.moveToFirst()) {
            do {
                String  name=cursor.getString(0);
                li=name;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Long lll = null;
        try {
           lll = Long.parseLong(li);
            System.out.println("long l = " + lll);
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: " + nfe.getMessage());
        }
        return lll;
    }
    public  String getQuant(String id,String nakl) {

        String query = String.format("SELECT quant FROM POSDOC WHERE  id_pos='%s'",id);
        SQLiteDatabase db  = this.getReadableDatabase();
        String li ="";
        Cursor cursor      = db.rawQuery(query, null);
        String[] data      = null;
        if (cursor.moveToFirst()) {
            do {
                String  name=cursor.getString(0);
                li=name;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }
    public  int getStatus(String id,Integer kol) {

        String query = String.format("SELECT quant FROM POSDOC WHERE id_pos ='%s'",id);
        SQLiteDatabase db  = this.getReadableDatabase();
        String li ="0";
        Cursor cursor      = db.rawQuery(query, null);
        String[] data      = null;
        if (cursor.moveToFirst()) {
            do {
                String  name=cursor.getString(0);
                li=name;
            } while (cursor.moveToNext());
        }
        cursor.close();
        Integer rr = Integer.parseInt(li);
        db.close();
        if(rr.equals(kol))
        {
            return 1;
        }
        else
        {
            return 0;
        }

    }
    public  String getKOL(String id) {
        String query = String.format("SELECT COUNT(*) FROM BARCODE WHERE (status = 1 or status = 2) and id_pos_bar='%s'",id);
        SQLiteDatabase db  = this.getReadableDatabase();
        String li ="";
        Cursor cursor      = db.rawQuery(query, null);
        String[] data      = null;
        if (cursor.moveToFirst()) {
            do {
                String  name=cursor.getString(0);
                li=name;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }

    public  String getALLPOSDOC() {

        String query = String.format("SELECT COUNT(*) FROM POSDOC");
        SQLiteDatabase db  = this.getReadableDatabase();
        String li ="";
        Cursor cursor      = db.rawQuery(query, null);
        String[] data      = null;
        if (cursor.moveToFirst()) {
            do {
                String  name=cursor.getString(0);
                li=name;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }

    public  String POSDOC() {
        String query = String.format("SELECT COUNT(*) FROM POSDOC WHERE st = 0 or st = 2");
        SQLiteDatabase db  = this.getReadableDatabase();
        String li ="";
        Cursor cursor      = db.rawQuery(query, null);
        String[] data      = null;
        if (cursor.moveToFirst()) {
            do {
                String  name=cursor.getString(0);
                li=name;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }

    public  String getKOLPOSDOC3(String id) {
        String query = String.format("SELECT COUNT(*) FROM POSDOC WHERE id_doc ="+id);
        SQLiteDatabase db  = this.getReadableDatabase();
        String li ="";
        Cursor cursor      = db.rawQuery(query, null);
        String[] data      = null;
        if (cursor.moveToFirst()) {
            do {
                String  name=cursor.getString(0);
                li=name;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }

    public  String getKOLPOSDOC2(String id) {
        String query = String.format("SELECT COUNT(*) FROM POSDOC WHERE st = 1 and id_doc ="+id);
        SQLiteDatabase db  = this.getReadableDatabase();
        String li ="";
        Cursor cursor      = db.rawQuery(query, null);
        String[] data      = null;
        if (cursor.moveToFirst()) {
            do {
                String  name=cursor.getString(0);
                li=name;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }

    public  String getKOLPOSDOC() {
        String query = String.format("SELECT COUNT(*) FROM POSDOC WHERE st = 1");
        SQLiteDatabase db  = this.getReadableDatabase();
        String li ="";
        Cursor cursor      = db.rawQuery(query, null);
        String[] data      = null;
        if (cursor.moveToFirst()) {
            do {
                String  name=cursor.getString(0);
                li=name;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }

    public  List getdoccend() {
        String query = String.format("SELECT id FROM DOC GROUP BY id");
        SQLiteDatabase db  = this.getReadableDatabase();
        List<String> li = new ArrayList<String>();
        Cursor cursor      = db.rawQuery(query, null);
        String[] data      = null;
        if (cursor.moveToFirst()) {
            do {
                String  name=cursor.getString(0);
                li.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }

    public  List getssccend(String iddoc) {
        String query = String.format("SELECT sscc FROM BARCODE br,POSDOC pd WHERE  br.status = 2 and br.id_pos_bar=pd.id_pos and pd.id_doc ="+ iddoc +" GROUP BY sscc");
        SQLiteDatabase db  = this.getReadableDatabase();
        List<String> li = new ArrayList<String>();
        Cursor cursor      = db.rawQuery(query, null);
        String[] data      = null;
        if (cursor.moveToFirst()) {
            do {
                String  name=cursor.getString(0);
                li.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }

    public List getnumb() {
        String query = String.format("SELECT numb FROM POSDOC  GROUP BY numb");
        SQLiteDatabase db  = this.getReadableDatabase();
        List<String> li = new ArrayList<String>();
        Cursor cursor      = db.rawQuery(query, null);
        String[] data      =new String[5];
        if (cursor.moveToFirst()) {
            do {
                String  name=cursor.getString(0);
                li.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }

    public  List checkposdoc(String iddoc) {
        String query = String.format("select id_pos from POSDOC where  id_doc ="+ iddoc);
        SQLiteDatabase db  = this.getReadableDatabase();
        List<String> li = new ArrayList<String>();
        Cursor cursor      = db.rawQuery(query, null);
        String[] data      = null;
        if (cursor.moveToFirst()) {
            do {
                String  name=cursor.getString(0);
                li.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }

    public  List getsgtinend(String iddoc) {
        String query = String.format("SELECT datamatrix,gtin FROM BARCODE WHERE status = 1 and id_pos_bar in (select id_pos from POSDOC where  id_doc ="+ iddoc+")");
        SQLiteDatabase db  = this.getReadableDatabase();
        List<String> li = new ArrayList<String>();
        Cursor cursor      = db.rawQuery(query, null);
        String[] data      = null;
        if (cursor.moveToFirst()) {
            do {
                String  name=cursor.getString(0);
                li.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }

    public  String getKOLSSCC(String id,String nakl) {
        String query = String.format("SELECT COUNT(*) FROM BARCODE WHERE id_pos_bar ="+nakl+" and status = 0 and rs = 1 and sscc='%s'",id);
        SQLiteDatabase db  = this.getReadableDatabase();
        String li ="";
        Cursor cursor      = db.rawQuery(query, null);
        String[] data      = null;
        if (cursor.moveToFirst()) {
            do {
                String  name=cursor.getString(0);
                li=name;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }

    public  int updetebarSSCC(String barcode,String id,String idd) {
        String query = String.format("UPDATE BARCODE SET status = 2 WHERE id_pos_bar ="+id+" and  rs = 1 and sscc='%s'",barcode);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        int li = 0;
        db.close();
        Integer kk = Integer.parseInt(getQuant(idd,id));  // нужное кол-во препаратов
        Integer oo = Integer.parseInt(getKOL(idd));//
        if(kk.intValue()==oo.intValue()){
            updeteposdoc(idd);}
        return li;
    }

    public  List getsscc(String barcode,String id) {
        String query = String.format("SELECT  * FROM BARCODE WHERE id_pos_bar ="+id+" and rs = 1 and sscc='%s'",barcode);
        SQLiteDatabase db  = this.getReadableDatabase();
        List<String> li = new ArrayList<String>();
        Cursor cursor      = db.rawQuery(query, null);
        String[] data      = null;
        if (cursor.moveToFirst()) {
            do {
                String  name=cursor.getString(0);
                String  st=cursor.getString(4);
                String  gt=cursor.getString(3);
                li.add(name);
                li.add(st);
                li.add(gt);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }

    public List getbarcode(String bb) {
        String query = String.format("SELECT * FROM BARCODE WHERE gtin='%s' LIMIT 1",bb);
        SQLiteDatabase db  = this.getReadableDatabase();
        List<String> li = new ArrayList<String>();
        Cursor cursor      = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String  name = cursor.getString(0);
                li.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }

    public  List getbar(String barcode,String id) {
        String query = String.format("SELECT * FROM BARCODE WHERE id_pos_bar ="+id+" and gtin='%s'",barcode);
        SQLiteDatabase db  = this.getReadableDatabase();
        List<String> li = new ArrayList<String>();
        Cursor cursor      = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String  name=cursor.getString(0);
                String  st=cursor.getString(4);
                String  ss=cursor.getString(1);
                li.add(name);
                li.add(st);
                li.add(ss);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        if(li.size() >0) {
            try {
                Long ss = Long.parseLong(li.get(0));
                Long ff = Long.parseLong(id);
                if (ss.equals(ff)) {
                    if (li.size() == 0) {
                        System.out.println("array is empty");
                    }
                } else {
                    li.clear();
                }
            } catch (Exception e) {
                li.add("99");
                e.printStackTrace();
            }
        }
        else{li.add("99");}
                return li;
    }

    public  int checkRS(String barcode) {
        String query = String.format("SELECT  rs FROM BARCODE WHERE gtin='%s'",barcode);
        SQLiteDatabase db  = this.getReadableDatabase();
        Integer li = 0;
        Cursor cursor      = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                li = Integer.parseInt(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }

    public  int checkbar(String barcode) {
        String query = String.format("SELECT  status FROM BARCODE WHERE gtin='%s'",barcode);
        SQLiteDatabase db  = this.getReadableDatabase();
        Integer li = 5;
        Cursor cursor      = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                li = Integer.parseInt(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Integer rs = checkRS(barcode);
        if(rs == 0) {
        li = 10;
        }
        return li;
    }

    public  int checkST(String barcode) {
        String query = String.format("select st from POSDOC where  id_pos ="+ barcode);
        SQLiteDatabase db  = this.getReadableDatabase();
        Integer li = 5;
        Cursor cursor      = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                li = Integer.parseInt(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }

    public  int checkmrk(String barcode) {
        String query = String.format("select mrk from POSDOC where  id_pos ="+ barcode);
        SQLiteDatabase db  = this.getReadableDatabase();
        Integer li = 5;
        Cursor cursor      = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                li = Integer.parseInt(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }

    public  int deletebar(String barcode) {
        String query = String.format("UPDATE BARCODE SET status = 0 WHERE id_pos_bar='%s'",barcode);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        int li = 0;
        return li;
    }

    public  int TEST(String ins) {
        String query = String.format(ins);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        int li = 0;
        return li;
    }

    public  int updetebar(String barcode,String data,String iddoc) {
        String query = String.format("UPDATE BARCODE SET status = 1 , datamatrix ='"+data+"' WHERE id_pos_bar = "+iddoc+" and gtin='%s'",barcode);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        int li = 0;
        return li;
    }

    public  int deletposdocs() {
        String query = String.format("UPDATE POSDOC SET st = 0 WHERE st=2");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        int li = 0;
        return li;
    }

    public  int deletposdoc(String barcode) {
        String query = String.format("UPDATE POSDOC SET st = 0 WHERE id_pos='%s'",barcode);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        int li = 0;
        return li;
    }

    public  String getSTATUSposdoc(String id) {
        String query = String.format("SELECT st FROM POSDOC WHERE id_pos ="+id);
        SQLiteDatabase db  = this.getReadableDatabase();
        String li ="";
        Cursor cursor      = db.rawQuery(query, null);
        String[] data      = null;
        if (cursor.moveToFirst()) {
            do {
                String  name=cursor.getString(0);
                li=name;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }

    public  int updeteposdocNext(String barcode,Integer s) {
        String query = String.format("UPDATE POSDOC SET st = "+s+" WHERE id_pos='%s'",barcode);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        int li = 0;
        return li;
    }

    public  int updeteposdoc(String barcode) {
        String query = String.format("UPDATE POSDOC SET st = 1 WHERE id_pos='%s'",barcode);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        int li = 0;
        return li;
    }

    public List<HashMap<String, String>> getDocDet(){
        List<HashMap<String,String>> posdocold = new ArrayList<HashMap<String,String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_DOC;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            for (int n = 0; n < cursor.getCount(); n++) {
                HashMap<String,String> user = new HashMap<String,String>();
                user.put("id", cursor.getString(0));
                user.put("date", cursor.getString(1));
                user.put("otdel", cursor.getString(2));
                user.put("agent", cursor.getString(3));
                user.put("pref", cursor.getString(4));
                user.put("numb", cursor.getString(5));
                user.put("serial_numb", "Часть "+cursor.getString(8)+" из "+cursor.getString(9));
                posdocold.add(user);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return posdocold;
    }

    public List<HashMap<String, String>> getPosDocDetails(String nakl,int stata,boolean y){
        List<HashMap<String,String>> posdocold = new ArrayList<HashMap<String,String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_POSDOC+" where id_doc="+nakl;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            for (int n = 0; n < cursor.getCount(); n++) {
                HashMap<String, String> posdoc = new HashMap<String, String>();
                Integer ll = Integer.parseInt(getKOL(cursor.getString(1)));
                Integer yy = cursor.getInt(11);
                if( yy.equals(ll) & stata == 0 & y != true)
                {
                    updeteposdoc( cursor.getString(1));
                    posdoc.put("id_doc", cursor.getString(0));
                    posdoc.put("rowid", cursor.getString(1));
                    posdoc.put("name", cursor.getString(2));
                    if(Integer.parseInt(cursor.getString(3))==1) {
                        posdoc.put("mrk", "М");
                        posdoc.put("kol", cursor.getString(11)+"/");
                        posdoc.put("status",getKOL(cursor.getString(1)));
                    }else {
                        posdoc.put("mrk", "");
                        posdoc.put("kol", cursor.getString(11));
                        posdoc.put("status","");
                    }
                    posdoc.put("mh_ord", cursor.getString(4));
                    posdoc.put("mh", "М/Х-"+cursor.getString(5));
                    posdoc.put("seria","Серия-"+cursor.getString(7));
                    posdoc.put("party", "Партия-"+cursor.getString(9));
                    posdoc.put("mess", cursor.getString(10));

                    posdocold.add(posdoc);
                }
                else {
                posdoc.put("id_doc", cursor.getString(0));
                posdoc.put("rowid", cursor.getString(1));
                posdoc.put("status", getKOL(cursor.getString(1)));
                posdoc.put("name", cursor.getString(2));
                    if(Integer.parseInt(cursor.getString(3))==1) {
                        posdoc.put("mrk", "М");
                        posdoc.put("kol", cursor.getString(11) + "/");
                        posdoc.put("status", getKOL(cursor.getString(1)));
                    }else {
                        posdoc.put("mrk", "");
                        posdoc.put("kol", cursor.getString(11));
                        posdoc.put("status", "");
                    }
                posdoc.put("mh_ord", cursor.getString(4));
                posdoc.put("mh", "М/Х-" + cursor.getString(5));
                posdoc.put("seria", "Серия-" + cursor.getString(7));
                posdoc.put("party", "Партия-" + cursor.getString(9));
                posdoc.put("mess", cursor.getString(10));
                posdocold.add(posdoc);
            }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return posdocold;
    }

    public void addDOC(String id, String d, String otdel,String ap, String pr, String nb,String rn,int std,String ser,String tot) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_DATE, d);
        values.put(KEY_OTDEL, otdel);
        values.put(KEY_AP, ap);
        values.put(KEY_PREF, pr);
        values.put(KEY_NUMB, nb);
        values.put(KEY_SBOR, rn);
        values.put(KEY_ST_DOC, std);
        values.put(KEY_SERIAL, ser);
        values.put(KEY_TOTAL, tot);
        db.insert(TABLE_DOC, null, values);
        db.close();
    }
    public List<HashMap<String, String>> getDetailsPOSDOC20(String nakl){
        List<HashMap<String,String>> posdocold = new ArrayList<HashMap<String,String>>();
        String query = String.format("SELECT * FROM POSDOC WHERE (st = 0 or st = 2) and id_doc="+nakl+" ORDER BY st");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            for (int n = 0; n < 1; n++) {
                HashMap<String, String> posdoc = new HashMap<String, String>();
                Integer  ll = Integer.parseInt(getKOL(cursor.getString(1)));
                Integer  yy =  cursor.getInt(11);

                posdoc.put("id", cursor.getString(1));
                posdoc.put("mh", cursor.getString(5));
                posdoc.put("name", cursor.getString(2));
                Integer plus = Integer.parseInt(cursor.getString(11))- ll; // - кол-во упак минус кол-во отсканирован
                if(Integer.parseInt(cursor.getString(3))==1) {
                    posdoc.put("mrk", "М");
                    posdoc.put("kol", cursor.getString(11));
                    posdoc.put("lol", ll.toString());
                }else {
                    posdoc.put("mrk", "");
                    posdoc.put("kol", cursor.getString(11));
                    posdoc.put("lol","");
                }
                posdoc.put("ser", cursor.getString(7));
                posdoc.put("par", cursor.getString(9));
                posdoc.put("meas", cursor.getString(10));
                posdoc.put("trc", cursor.getString(14));

                    posdocold.add(posdoc);

                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return posdocold;
    }
    public HashMap<String, String> getDetailsPOSDOC(){
        HashMap<String,String> user = new HashMap<String,String>();
        String selectQuery = "SELECT  * FROM " + TABLE_POSDOC;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            user.put("id", cursor.getString(1));
            user.put("mh", cursor.getString(5));
            user.put("name", cursor.getString(2));
            user.put("kol", cursor.getString(11));
            user.put("ser", cursor.getString(6));
            user.put("par", cursor.getString(8));
        }
        cursor.close();
        db.close();
        return user;
    }
    public HashMap<String, String> getDocDetails(String nakl){
        HashMap<String,String> user = new HashMap<String,String>();
        String query = String.format("Select * from DOC WHERE id='%s'",nakl);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            user.put("id", cursor.getString(0));
            user.put("date", cursor.getString(1));
            user.put("otdel", cursor.getString(2));
            user.put("agent", cursor.getString(3));
            user.put("pref", cursor.getString(4));
            user.put("numb", cursor.getString(5));
            user.put("serial_numb", cursor.getString(8));
            user.put("total_parts", cursor.getString(9));
        }
        cursor.close();
        db.close();
        return user;
    }

    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_DOC;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        return rowCount;
    }

    public int getRowCountPOSDOC() {
        String countQuery = "SELECT  * FROM " + TABLE_POSDOC;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        return rowCount;
    }

    public  void up(){
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db,0,1);
    }

    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DOC, null, null);
        db.delete(TABLE_POSDOC, null, null);
        db.delete(TABLE_BARCODE, null, null);
        db.close();
    }
    public static void showProgressDialog(Context context, String title) {
        FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
        DialogFragment newFragment = ProgressBarDialog.newInstance(title);
        newFragment.show(fm, "dialog");
    }

    public static void hideProgressDialog(Context context) {
        FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
    }
}
