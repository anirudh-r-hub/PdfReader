package com.example.pdfreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(Context context) {
        super(context, "recent_files.db", null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table recent (ID INTEGER PRIMARY KEY AUTOINCREMENT, PATH TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }

    public void insertData() {
        ContentValues contentValues;
        SQLiteDatabase db = this.getWritableDatabase();

        contentValues = new ContentValues();
        contentValues.put("PATH", "/storage/abc.pdf");
        db.insert("recent", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("PATH", "/storage/def.pdf");
        db.insert("recent", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("PATH", "/storage/ghi.pdf");
        db.insert("recent", null, contentValues);


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
            return res;
        }
        return -1;

    }

    public Cursor getSingle(String path) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("select * from recent where path = '"+path+"'",null);
        System.err.println("res status : "+res.toString());
        if(res.moveToNext()){
            System.err.println("res path: "+res.getString(1)+ " index: "+res.getString(0));

        }
        return res;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("select * from recent",null);
        return res;

    }


    public int clearAllData() {
        SQLiteDatabase db = this.getWritableDatabase();

        int rows_deleted = db.delete("recent", null, null);
        return rows_deleted;
    }


}
