package com.example.bianguojian.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AccountBook extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private final static String DB_NAME= "MyDiaryDB";
    private static final int DB_VERSION= 1;
    private AccountDB db;

    private int distance_days;
    private static int ADD_ACCOUNT_REQUEST_CODE= 1;

    private double sum= 0;
    private double out= 0;
    private double in= 0;
    private int IDnum= 0;
    private DecimalFormat dformat= new DecimalFormat("0.00");


    private ListView account_list;
    private FloatingActionButton account_add;
    private TextView account_sum;
    private TextView account_outgo;
    private TextView account_income;
    private TextView account_date;

    private Date date;
    private List<AccountItem> list= new ArrayList<>();
    MyAdapter3 adapter= new MyAdapter3(list, this);
    private Handler handler= new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_book);
        Bundle bundle = getIntent().getExtras();
        int year = bundle.getInt("Year");
        int month = bundle.getInt("Month");
        int day = bundle.getInt("Day");
        distance_days= bundle.getInt("Distance_Days");
        date = new Date(year, month, day);
        Log.i("God_Bian", date+"");
        findview();
        db= new AccountDB(this, DB_NAME, null, DB_VERSION);
        account_list.setAdapter(adapter);
        account_list.setOnItemClickListener(this);
        if (distance_days== 0) {
            account_date.setText(year+ "年"+ (month+ 1)+ "月"+ day+ "日");
            account_list.setOnItemLongClickListener(this);
        } else {
            Calendar c= Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, 30);
            Date enddate= c.getTime();
            account_date.setText(date.getYear()+ "年"+ (date.getMonth()+ 1)+ "月"+ date.getDay()+ "日" + "—"+ enddate.getYear()+ "年"+ (enddate.getMonth()+ 1) + "月"+ enddate.getDay()+ "日");
            account_add.setVisibility(View.GONE);
            adapter.setShowDate(true);
        }
        getData();
    }

    private void findview() {
        account_list= (ListView) this.findViewById(R.id.account_list);
        account_add= (FloatingActionButton) this.findViewById(R.id.account_add);
        account_add.setOnClickListener(this);
        account_sum= (TextView) this.findViewById(R.id.account_sum);
        account_outgo= (TextView) this.findViewById(R.id.account_outgo);
        account_income= (TextView) this.findViewById(R.id.account_income);
        account_date= (TextView) this.findViewById(R.id.account_date);

    }

    private void getData() {
        Calendar c= Calendar.getInstance();
        c.setTime(date);
        for (int j= 0; j<= distance_days; j++) {
            Cursor cursor = db.queryData(c.getTime());
            int count = cursor.getCount();
            if (count != 0 && cursor.moveToFirst()) {
                for (int i = 0; i < count; i++) {
                    String type = cursor.getString(cursor.getColumnIndex(AccountDB.KEY_TYPE));
                    String remarks = cursor.getString(cursor.getColumnIndex(AccountDB.KEY_REMARKS));
                    String image = cursor.getString(cursor.getColumnIndex(AccountDB.KEY_IMAGE));
                    int color = cursor.getInt(cursor.getColumnIndex(AccountDB.KEY_COLOR));
                    float number = cursor.getFloat(cursor.getColumnIndex(AccountDB.KEY_NUMBER));
                    int ID = cursor.getInt(cursor.getColumnIndex(AccountDB.KEY_ID));
                    AccountItem item = new AccountItem(c.getTime(),image, type, remarks, number, color, ID);
                    if (ID > IDnum) {
                        IDnum = ID;
                    }
                    list.add(item);
                    cursor.moveToNext();
                    if (number > 0) {
                        in += number;
                    } else {
                        out += -number;
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
        sum = in - out;
        new runnable(0, 0, 0).run();
    }

    @Override
    public void onClick(View v) {
        Intent intent= new Intent(AccountBook.this, AccountAdd.class);
        startActivityForResult(intent, ADD_ACCOUNT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== RESULT_OK) {
            if (requestCode== ADD_ACCOUNT_REQUEST_CODE) {
                Bundle bundle= data.getExtras();
                String image= bundle.getString("image");
                String type= bundle.getString("type");
                String remarks= bundle.getString("remarks");
                int color= bundle.getInt("color");
                float number= Float.parseFloat(bundle.getString("number"));
                IDnum+= 1;
                AccountItem item= new AccountItem(date ,image, type, remarks, number, color, IDnum);
                list.add(item);
                adapter.notifyDataSetChanged();
                Log.i("God_Bian", date+"");
                db.insertData(date, type, remarks, image, color, number, IDnum);
                double tmpsum= sum, tmpin= in, tmpout= out;
                if (number> 0)
                    in+= number;
                else
                    out+= -number;
                sum+= number;
                new runnable(tmpsum, tmpin, tmpout).run();
            }
        }
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("删除分类")
                .setMessage("确定删除条目吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        double tmpsum= sum, tmpin= in, tmpout= out;
                        float number= list.get(position).number;
                        int ID= list.get(position).ID;
                        if (number> 0)
                            in-= number;
                        else
                            out-= -number;
                        sum-= number;
                        new runnable(tmpsum, tmpin, tmpout).run();
                        list.remove(position);
                        adapter.notifyDataSetChanged();
                        db.deleteData(date, ID);
                    }
                });
        builder.show();

        return true;
    }


    private class runnable implements  Runnable {
        double cursum;
        double curin;
        double curout;
        double onesum;
        double onein;
        double oneout;
        int times= 0;
        public runnable(double s, double i, double o) {
            cursum= s;
            curin= i;
            curout= o;
            onesum= (sum- cursum)/ 50;
            onein= (in- curin)/ 50;
            oneout= (out- curout)/ 50;
        }
        @Override
        public void run() {
            if (times< 50) {
                times++;
                cursum+= onesum;
                curin+= onein;
                curout+= oneout;
                account_income.setText("收入："+ dformat.format(curin));
                account_outgo.setText("支出："+ dformat.format(curout));
                account_sum.setText(dformat.format(cursum));
                handler.postDelayed(this, 10);
            }
        }
    }
}
