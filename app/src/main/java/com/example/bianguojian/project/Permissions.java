package com.example.bianguojian.project;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;

public class Permissions extends Activity {
    private Handler myHandle= new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxPermissions rxPermissions= new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            Intent intent= new Intent(Permissions.this, lockActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Permissions.this, "(●'◡'●)不给权限，那就去死吧(ノ｀Д)ノ", Toast.LENGTH_SHORT).show();
                            myHandle.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 3000);

                        }
                    }
                });

    }
}
