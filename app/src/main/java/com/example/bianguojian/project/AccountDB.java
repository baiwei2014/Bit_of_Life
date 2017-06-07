package com.example.bianguojian.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 84339 on 2016/12/23.
 */

public class AccountDB extends SQLiteOpenHelper {
    private SimpleDateFormat formatter= new SimpleDateFormat("yyyy_mm_dd");
    public static final String ACCOUNT_TABLE= "Account";
    public static final String KEY_DATE= "date";
    public static final String KEY_TYPE= "type";
    public static final String KEY_REMARKS= "remarks";
    public static final String KEY_IMAGE= "image";
    public static final String KEY_COLOR= "color";
    public static final String KEY_NUMBER= "number";
    public static final String KEY_ID= "ID";

    public AccountDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        String CREATE_TABLE_A= "CREATE TABLE if not exists "
                + ACCOUNT_TABLE
                +" (_id INTEGER PRIMARY KEY, date TEXT, type TEXT, remarks TEXT, image TEXT, color INTEGER, number REAL, ID INTEGER)";
        db.execSQL(CREATE_TABLE_A);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void insertData(Date date, String type, String remarks, String image, int color, float number, int ID) {
        SQLiteDatabase db= getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(KEY_DATE, formatter.format(date));
        cv.put(KEY_TYPE, type);
        cv.put(KEY_REMARKS, remarks);
        cv.put(KEY_IMAGE, image);
        cv.put(KEY_COLOR, color);
        cv.put(KEY_NUMBER, number);
        cv.put(KEY_ID, ID);
        db.insert(ACCOUNT_TABLE, null, cv);
        db.close();
    }


    public void deleteData(Date date, int ID) {
        SQLiteDatabase db= getWritableDatabase();
        String[] whereArgs= {formatter.format(date), ID+ ""};
        db.delete(ACCOUNT_TABLE, KEY_DATE + "= ? and "+ KEY_ID + "= ?", whereArgs);
        db.close();
    }

    public Cursor queryData(Date date) {
        SQLiteDatabase db= getWritableDatabase();
        Cursor cursor;
        if (date== null) {
            cursor= db.query(ACCOUNT_TABLE, new String[] {KEY_DATE, KEY_TYPE, KEY_REMARKS, KEY_IMAGE, KEY_COLOR, KEY_NUMBER, KEY_ID}, null, null, null, null, null);
        } else {
            String[] whereArgs= {formatter.format(date)};
            cursor= db.query(ACCOUNT_TABLE, new String[] {KEY_DATE, KEY_TYPE, KEY_REMARKS, KEY_IMAGE, KEY_COLOR, KEY_NUMBER, KEY_ID}, KEY_DATE +"=?" , whereArgs, null, null, null);
        }
        return cursor;
    }

}