package com.example.bianguojian.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends Activity {
    private int year;
    private int month;
    private int day;
    private int style;
    private Button header_left_button;
    private Button header_right_button;
    private ListView acList;
    private List<String> listString;
    private MyAdapter myAdapter;
    private PopupMenu popup = null;
    private LinearLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_list);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_layout);
        Bundle bundle = getIntent().getExtras();
        year = bundle.getInt("Year");
        month = bundle.getInt("Month");
        day = bundle.getInt("Day");
        style = bundle.getInt("Style");
        background = (LinearLayout)findViewById(R.id.listActivity_background);
        changeStyle(style);
        ((TextView)findViewById(R.id.header_text)).setText(year+"."+(month+1)+"."+day);
        header_left_button = (Button)findViewById(R.id.header_left_btn);
        header_right_button = (Button)findViewById(R.id.header_right_btn);
        acList = (ListView)findViewById(R.id.ac_list);
        listString = new ArrayList<String>();
        listString.add("日记");
        listString.add("日程安排");
        listString.add("生活记账");
        myAdapter = new MyAdapter(ListActivity.this, listString);
        setOnListener();
    }

    private void setOnListener() {
        header_left_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        acList.setAdapter(myAdapter);
        acList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent();
                    intent.setClass(ListActivity.this, MyerDiary.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("Year", year);
                    bundle.putInt("Month", month);
                    bundle.putInt("Day", day);
                    bundle.putInt("Style", style);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent();
                    intent.setClass(ListActivity.this, dayManager.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("Year", year);
                    bundle.putInt("Month", month);
                    bundle.putInt("Day", day);
                    bundle.putInt("Style", style);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent();
                    intent.setClass(ListActivity.this, AccountBook.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("Year", year);
                    bundle.putInt("Month", month);
                    bundle.putInt("Day", day);
                    bundle.putInt("Style", style);
                    bundle.putInt("Distance_Days", 0);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        /*
        header_right_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup = new PopupMenu(ListActivity.this, header_right_button);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.list, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.list_p1:
                                Toast.makeText(ListActivity.this, "增加被点击", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.list_p2:
                                Toast.makeText(ListActivity.this, "减少被点击", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                popup.show(); //showing popup menu
            }
        });
        */
    }

    public void changeStyle(int style) {
        if (style == 1) {
            background.setBackgroundResource(R.mipmap.style3);
        } else if (style == 2) {
            background.setBackgroundResource(R.mipmap.style4);
        }
    }
}
