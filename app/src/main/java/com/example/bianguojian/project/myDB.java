package com.example.bianguojian.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/11/22.
 */

public class myDB extends SQLiteOpenHelper {

    private static final String DB_NAME = "MyDB";
    private static final String TABLE_NAME = "dayManagerTable";
    private static final int DB_VERSION = 1;
    private SQLiteDatabase db;
    private Context context;

    public myDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        this.context = context;
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        String CREATE_TABLE="CREATE TABLE if not exists "
                +TABLE_NAME
                +" (_id INTEGER PRIMARY KEY, year INTEGER, month INTEGER, day INTEGER, time Text, eventName Text)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertToDB(int year, int month, int day, String time, String eventName) {
        ContentValues cv = new ContentValues();
        cv.put("year", year);
        cv.put("month", month);
        cv.put("day", day);
        cv.put("time", time);
        cv.put("eventName", eventName);
        db.insert(TABLE_NAME, null, cv);
    }

    /*
    public void update(String name, String birth, String gift) {
        ContentValues cv = new ContentValues();
        if(!birth.equals("")) cv.put("birth", birth);
        if(!gift.equals("")) cv.put("gift", gift);
        String whereClause = "name=?";
        String[] whereArgs = {name};
        db.update(TABLE_NAME, cv, whereClause, whereArgs);
    }
    */

    public void delete(int year, int month, int day, String time, String eventName) {
        String whereClause = "year=? and month=? and day=? and time=? and eventName=?";
        String[] whereArgs = {year+"", month+"", day+"", time, eventName};
        db.delete(TABLE_NAME, whereClause, whereArgs);
    }

    public boolean isNameHas(String name) {
        SQLiteDatabase rdb = getReadableDatabase();
        Cursor cursor = rdb.rawQuery("select * from MyTB where name=?", new String[] {name});
        return (cursor.getCount() != 0);
    }

    public Cursor getAllItem(int year, int month, int day) {
        SQLiteDatabase rdb = getReadableDatabase();
        Cursor cursor = rdb.rawQuery("select * from dayManagerTable where year=" + year + " and month=" + month + " and day=" + day, null);
        return cursor;
    }
}