package com.example.bianguojian.project;

/**
 * Created by Administrator on 2016/12/25.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 84339 on 2016/11/22.
 */

public class DiaryDB extends SQLiteOpenHelper {
    private SimpleDateFormat formatter= new SimpleDateFormat("yyyy_mm_dd");
    private static final String DIARY_TABLE= "Diary";
    private static final String KEY_DATE= "date";
    private static final String KEY_TEXT= "text";
    private static final String KEY_IMGLOC= "img_loc";
    private static final String KEY_STYLE= "style";



    public DiaryDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        String CREATE_TABLE_A= "CREATE TABLE if not exists "
                + DIARY_TABLE
                +" (_id INTEGER PRIMARY KEY, date TEXT, text TEXT, img_loc TEXT, style INTEGER)";
        db.execSQL(CREATE_TABLE_A);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void insertData(Date date, String text, String img_loc, int style) {
        SQLiteDatabase db= getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(KEY_DATE, formatter.format(date));
        cv.put(KEY_TEXT, text);
        cv.put(KEY_IMGLOC, img_loc);
        cv.put(KEY_STYLE, style);
        db.insert(DIARY_TABLE, null, cv);
        db.close();
    }

    public void updateData(Date date, String text, String img_loc, int style) {
        SQLiteDatabase db= getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(KEY_TEXT, text);
        cv.put(KEY_IMGLOC, img_loc);
        cv.put(KEY_STYLE, style);
        String whereClause= KEY_DATE +"=?";
        String[] whereArgs= {formatter.format(date)};
        db.update(DIARY_TABLE, cv, whereClause, whereArgs);
        db.close();
    }

    public void deleteData(Date date) {
        SQLiteDatabase db= getWritableDatabase();
        String[] whereArgs= {formatter.format(date)};
        db.delete(DIARY_TABLE, KEY_DATE + "= ?", whereArgs);
        db.close();
    }

    public Cursor queryData(Date date) {
        SQLiteDatabase db= getWritableDatabase();
        Cursor cursor;
        if (date== null) {
            cursor= db.query(DIARY_TABLE, new String[] {KEY_DATE, KEY_TEXT, KEY_IMGLOC, KEY_STYLE}, null, null, null, null, null);
        } else {
            String[] whereArgs= {formatter.format(date)};
            cursor= db.query(DIARY_TABLE, new String[] {KEY_DATE, KEY_TEXT, KEY_IMGLOC, KEY_STYLE}, KEY_DATE +"=?" , whereArgs, null, null, null);
        }
        return cursor;
    }



}