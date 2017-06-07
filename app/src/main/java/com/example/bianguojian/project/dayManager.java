package com.example.bianguojian.project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class dayManager extends Activity {

    private int year;
    private int month;
    private int day;
    private int style;
    private Button header_left_button;
    private Button header_right_button;
    private ListView acList;
    private List<String[]> listString;
    private MyAdapter2 myAdapter2;
    private PopupMenu popup = null;
    private String initStartDateTime = "00:00-24:00"; // 初始化开始时间
    private String initEndDateTime = ""; // 初始化结束时间
    private myDB myDatabase;
    private LinearLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_day_manager);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_layout);
        Bundle bundle = getIntent().getExtras();
        year = bundle.getInt("Year");
        month = bundle.getInt("Month");
        day = bundle.getInt("Day");
        style = bundle.getInt("Style");
        background = (LinearLayout)findViewById(R.id.dayManager_background);
        changeStyle(style);
        myDatabase = new myDB(this, "MyDB", null, 1);
        ((TextView)findViewById(R.id.header_text)).setText(year+"."+(month+1)+"."+day);
        header_left_button = (Button)findViewById(R.id.header_left_btn);
        header_right_button = (Button)findViewById(R.id.header_right_btn);
        acList = (ListView)findViewById(R.id.daycost_list);
        listString = new ArrayList<String[]>();
        checklist();
        myAdapter2 = new MyAdapter2(dayManager.this, listString);
        setOnListener();
    }

    private void setOnListener() {
        header_left_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        acList.setAdapter(myAdapter2);
        acList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(dayManager.this)
                        .setTitle("")
                        .setMessage("是否删除?")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDatabase.delete(year, month, day, listString.get(position)[0], listString.get(position)[1]);
                                listString.remove(position);
                                myAdapter2.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public  void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.create().show();
                return true;
            }
        });

        header_right_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup = new PopupMenu(dayManager.this, header_right_button);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.list, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.list_p1: {
                                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                                        dayManager.this, initEndDateTime);
                                dateTimePicKDialog.dateTimePicKDialog(listString, myAdapter2, dayManager.this, year, month, day, myDatabase);
                                break;
                            }
                            case R.id.list_p2: {
                                Toast.makeText(dayManager.this, "长按即可删除哦", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            default:
                                break;

                        }
                        return true;
                    }
                });
                popup.show(); //showing popup menu
            }
        });
    }

    public void checklist() {
        Cursor cursor = myDatabase.getAllItem(year, month, day);
        if(cursor.moveToFirst()) {
            do {
                listString.add(new String[]{cursor.getString(4), cursor.getString(5)});
            } while (cursor.moveToNext());
        }
    }

    public void changeStyle(int style) {
        if (style == 1) {
            background.setBackgroundResource(R.mipmap.style3);
        } else if (style == 2) {
            background.setBackgroundResource(R.mipmap.style4);
        }
    }
}
