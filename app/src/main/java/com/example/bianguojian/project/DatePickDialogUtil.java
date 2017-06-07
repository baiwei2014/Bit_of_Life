package com.example.bianguojian.project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Debug;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 2016/12/12.
 */

public class DatePickDialogUtil implements DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {
    private DatePicker timePickerBegin;
    private DatePicker timePickerEnd;
    private AlertDialog ad;
    private String dateTimeBegin;
    private String dateTimeEnd;
    private Date dateBegin;
    private Date dateEnd;
    int year;
    int month;
    int day;
    private Activity activity;

    public DatePickDialogUtil(Activity activity, int year, int month, int day) {
        this.activity = activity;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public void init(DatePicker timePickerBegin, DatePicker timePickerEnd) {
        timePickerBegin.updateDate(year, month, day);
        timePickerEnd.updateDate(year, month, day);
    }

    public void dateTimePicKDialog(final Context context, final AccountDB db) {
        LinearLayout dateTimeLayout = (LinearLayout) activity
                .getLayoutInflater().inflate(R.layout.account_datetime, null);
        timePickerBegin = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker_begin);
        timePickerEnd = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker_end);
        init(timePickerBegin, timePickerEnd);
        ad = new AlertDialog.Builder(activity)
                .setTitle("累计记账")
                .setView(dateTimeLayout)
                .setPositiveButton("查询", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        onDateChanged(null, 0, 0, 0);
                        dateBegin = new Date(timePickerBegin.getYear(), timePickerBegin.getMonth(), timePickerBegin.getDayOfMonth());
                        dateEnd = new Date(timePickerEnd.getYear(), timePickerEnd.getMonth(), timePickerEnd.getDayOfMonth());
                        if (dateBegin.getTime() > dateEnd.getTime()) {
                            Toast.makeText(context, "起始时间应该小于终止时间", Toast.LENGTH_SHORT).show();
                        } else {
                            double sum= 0;
                            double out= 0;
                            double in= 0;
                            while (dateBegin.getTime() <= dateEnd.getTime()) {
                                Log.i("God_Bian", dateBegin+"");
                                Cursor cursor = db.queryData(dateBegin);
                                int count = cursor.getCount();
                                if (count != 0 && cursor.moveToFirst()) {
                                    for (int i = 0; i < count; i++) {
                                        String type = cursor.getString(cursor.getColumnIndex(AccountDB.KEY_TYPE));
                                        String remarks = cursor.getString(cursor.getColumnIndex(AccountDB.KEY_REMARKS));
                                        String image = cursor.getString(cursor.getColumnIndex(AccountDB.KEY_IMAGE));
                                        int color = cursor.getInt(cursor.getColumnIndex(AccountDB.KEY_COLOR));
                                        float number = cursor.getFloat(cursor.getColumnIndex(AccountDB.KEY_NUMBER));
                                        cursor.moveToNext();
                                        Log.i("God_Bian", number+"");
                                        if (number > 0) {
                                            in += number;
                                        } else {
                                            out += -number;
                                        }
                                    }
                                }
                                dateBegin = new Date(dateBegin.getTime()+1*24*3600*1000);
                            }
                            sum = in - out;
                            Toast.makeText(context, "这些天，您累计收入" + sum + "元", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        onDateChanged(null, 0, 0, 0);
    }

    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
        dateTimeBegin = timePickerBegin.getYear() + "." + (timePickerBegin.getMonth()+1) + "." + timePickerBegin.getDayOfMonth();
        dateTimeEnd = timePickerEnd.getYear() + "." + (timePickerEnd.getMonth()+1) + "." + timePickerEnd.getDayOfMonth();
    }

}
