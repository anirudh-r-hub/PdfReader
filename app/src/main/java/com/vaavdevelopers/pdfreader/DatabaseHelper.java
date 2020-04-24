package com.vaavdevelopers.pdfreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(Context context) {
        super(context, "recent_files.db", null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table recent (ID INTEGER PRIMARY KEY AUTOINCREMENT, PATH TEXT)");
        db.execSQL("create table recent_page (ID INTEGER PRIMARY KEY AUTOINCREMENT, PATH TEXT, PAGE INTEGER)");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }

    public long addRecentPage(String path, int page) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues;
        contentValues = new ContentValues();
        contentValues.put("PATH", path);
        contentValues.put("PAGE",page);
        long res;

        //check for already present

        Cursor search = db.rawQuery("select * from recent_page where path = '"+path+"'",null);

        if(!search.moveToNext()) {

           res = db.insert("recent_page", null, contentValues);
           db.close();
            return res;
        } else {
            //update the page no.
            String update_id = new Integer(search.getInt(0)).toString();

            db.update("recent_page", contentValues, "ID = ?", new String[] {update_id});

        }
        db.close();
        return -1;

    }

    public int getRecentPage(String path) {
        //check for already present

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor search = db.rawQuery("select * from recent_page where path = '"+path+"'",null);

        if(search.moveToNext()) {
            db.close();

            return search.getInt(2);//recent page

        }

        db.close();

        return 1;
    }


    public long insertSingle(String path) {
        //check for duplicate
        ContentValues contentValues;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor search = getSingle(path);

        if(!search.moveToNext()) {
            contentValues = new ContentValues();
            contentValues.put("PATH", path);
            long res = db.insert("recent", null, contentValues);
            db.close();
            return res;
        }
        db.close();
        return -1;

    }

    public Cursor getSingle(String path) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("select * from recent where path = '"+path+"'",null);
        //System.err.println("res status : "+res.toString());
        if(res.moveToNext()){
            //System.err.println("res path: "+res.getString(1)+ " index: "+res.getString(0));

        }
        return res;
    }

    public ArrayList<String> getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<String> resultset = new ArrayList<>();

        Cursor res = db.rawQuery("select * from recent",null);

        while(res.moveToNext()) {

            resultset.add(res.getString(1));

        }
        db.close();
        return resultset;

    }


    public int clearAllData() {
        SQLiteDatabase db = this.getWritableDatabase();

        int rows_deleted = db.delete("recent", null, null);
        db.close();
        return rows_deleted;
    }




}
