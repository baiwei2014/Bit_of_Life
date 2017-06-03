package com.example.bianguojian.project;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.bianguojian.project.view.GestureLockView;

public class lockActivity extends Activity {
    private SharedPreferences sharedPreferences;
    private String Password = null;
    private String Type = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        sharedPreferences = this.getSharedPreferences("MY_PREFERENCE", this.MODE_PRIVATE);
        Password = sharedPreferences.getString("PASSWORD", null);
        if (Password == null) {
            Log.i("GodBian", "空");
            Intent intent = new Intent();
            intent.setClass(lockActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if(Password.equals("setPass")) {
            Toast.makeText(lockActivity.this, "输入密码", Toast.LENGTH_SHORT).show();
            GestureLockView gestureLockView = (GestureLockView) findViewById(R.id.gestureView);
            gestureLockView.setGestureListener(new GestureLockView.GestureListener() {
                @Override
                public boolean getGesture(String gestureCode) {
                    if (0 == gestureCode.length()) return true;
                    else {
                        Intent intent = new Intent();
                        intent.putExtra("Password", gestureCode);
                        lockActivity.this.setResult(101, intent);
                        lockActivity.this.finish();
                    }
                    return false;
                }
            });
        } else if(Password.equals("setPass2")) {
            Toast.makeText(lockActivity.this, "确认密码", Toast.LENGTH_SHORT).show();
            GestureLockView gestureLockView = (GestureLockView) findViewById(R.id.gestureView);
            gestureLockView.setGestureListener(new GestureLockView.GestureListener() {
                @Override
                public boolean getGesture(String gestureCode) {
                    if (0 == gestureCode.length()) return true;
                    else {
                        Intent intent = new Intent();
                        intent.putExtra("Password", gestureCode);
                        setResult(102, intent);
                        finish();
                        return true;
                    }
                }
            });
        } else if(Password.equals("setPass2")) {
            Toast.makeText(lockActivity.this, "确认密码", Toast.LENGTH_SHORT).show();
            GestureLockView gestureLockView = (GestureLockView) findViewById(R.id.gestureView);
            gestureLockView.setGestureListener(new GestureLockView.GestureListener() {
                @Override
                public boolean getGesture(String gestureCode) {
                    if (0 == gestureCode.length()) return true;
                    else {
                        Intent intent = new Intent();
                        intent.putExtra("Password", gestureCode);
                        setResult(102, intent);
                        finish();
                        return true;
                    }
                }
            });
        } else if(Password.equals("editPass")) {
            Toast.makeText(lockActivity.this, "输入原密码", Toast.LENGTH_SHORT).show();
            GestureLockView gestureLockView = (GestureLockView) findViewById(R.id.gestureView);
            gestureLockView.setGestureListener(new GestureLockView.GestureListener() {
                @Override
                public boolean getGesture(String gestureCode) {
                    if (0 == gestureCode.length()) return true;
                    else {
                        Intent intent = new Intent();
                        intent.putExtra("Password", gestureCode);
                        setResult(103, intent);
                        finish();
                        return true;
                    }
                }
            });
        } else {
            GestureLockView gestureLockView = (GestureLockView) findViewById(R.id.gestureView);
            gestureLockView.setGestureListener(new GestureLockView.GestureListener() {
                @Override
                public boolean getGesture(String gestureCode) {
                    if (0 == gestureCode.length()) return true;
                    if (gestureCode.equals(Password)) {
                        Intent intent = new Intent();
                        intent.setClass(lockActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                    Toast.makeText(lockActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
    }
}
