package com.example.bianguojian.project;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 2016/12/25.
 */

public class RemindAlert extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        myDB myDatabase = new myDB(context, "MyDB", null, 1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Cursor cursor = myDatabase.getAllItem(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        Log.i("God_Bian", "test： " + calendar.get(Calendar.YEAR) + "." + calendar.get(Calendar.MONTH) + "." + calendar.get(Calendar.DAY_OF_MONTH));
        Log.i("God_Bian", "test2: " + format.format(calendar.getTimeInMillis()));
        if (cursor.moveToFirst()) {
            Notification.Builder builder = new Notification.Builder(context);
            builder.setContentTitle("点滴生活")
                    .setTicker("你有一条新消息")
                    .setContentText("你今天有日程安排，请留意哦")
                    .setSmallIcon(R.drawable.income)
                    .setAutoCancel(true);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent newIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, 0);
            builder.setContentIntent(pendingIntent);
            Notification notify = builder.build();
            manager.notify(0, notify);
        } else {
            Notification.Builder builder = new Notification.Builder(context);
            builder.setContentTitle("点滴生活")
                    .setTicker("你有一条新消息")
                    .setContentText("恭喜，今天没有行程安排")
                    .setSmallIcon(R.drawable.income)
                    .setAutoCancel(true);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent newIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, 0);
            builder.setContentIntent(pendingIntent);
            Notification notify = builder.build();
            manager.notify(0, notify);
        }
    }
}
