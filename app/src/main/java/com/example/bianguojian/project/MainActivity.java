package com.example.bianguojian.project;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends Activity {
    private static final int UPDATE_CONTENT = 0;
    private ImageView iv_left;
    private ImageView iv_right;
    private TextView tv_date;
    private TextView tv_week;
    private TextView tv_today;
    private ListView acList;
    private Button header_left_button;
    private Button header_right_button;
    private MonthDateView monthDateView;
    private PopupMenu popup = null;
    private myDB myDatabase;
    private AccountDB db;
    Date lastShakeTime;
    private SensorManager mSensorManager = null;
    private Sensor mAccelerometerSensor = null;
    private SharedPreferences sharedPreferences;
    private LinearLayout background;
    private String Password_test;
    private final static String url= "http://wthrcdn.etouch.cn/weather_mini";
    private boolean isSetAlarm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<Integer> list = new ArrayList<Integer>();
        list.add(10);
        list.add(12);
        list.add(15);
        list.add(16);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_layout);
        sharedPreferences = this.getSharedPreferences("MY_PREFERENCE", this.MODE_PRIVATE);
        background = (LinearLayout)findViewById(R.id.main_Background);
        final int style = sharedPreferences.getInt("STYLE", 1);
        isSetAlarm = sharedPreferences.getBoolean("isSet", false);
        changeStyle(style);
        myDatabase = new myDB(this, "MyDB", null, 1);
        db= new AccountDB(this, "MyDiaryDB", null, 1);
        lastShakeTime = new Date(System.currentTimeMillis());
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        iv_left = (ImageView)findViewById(R.id.iv_left);
        iv_right = (ImageView)findViewById(R.id.iv_right);
        monthDateView = (MonthDateView)findViewById(R.id.monthDateView);
        tv_date = (TextView)findViewById(R.id.date_text);
        tv_week = (TextView)findViewById(R.id.week_text);
        tv_today = (TextView)findViewById(R.id.tv_today);
        header_left_button = (Button)findViewById(R.id.header_left_btn);
        header_right_button = (Button)findViewById(R.id.header_right_btn);
        monthDateView.setTextView(tv_date, tv_week);
        monthDateView.setDateClick(new MonthDateView.DateClick() {
            @Override
            public void onClickOnDate() {
                if (monthDateView.getSelectDay() != 0) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, ListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("Year", monthDateView.getSelectYear());
                    bundle.putInt("Month", monthDateView.getSelectMonth());
                    bundle.putInt("Day", monthDateView.getSelectDay());
                    bundle.putInt("Style", sharedPreferences.getInt("STYLE", 1));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        setOnListener();
        if(!isSetAlarm) registRemind();
    }

    private void setOnListener() {
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthDateView.onLeftClick();
            }
        });

        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthDateView.onRightClick();
            }
        });

        tv_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthDateView.setTodayToView();
            }
        });

        header_left_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        header_right_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup = new PopupMenu(MainActivity.this, header_right_button);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.main, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup1:
                                int style = sharedPreferences.getInt("STYLE", 1);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                if (style == 1) {
                                    editor.putInt("STYLE", 2);
                                    editor.commit();

                                    changeStyle(2);
                                } else if (style == 2) {
                                    editor.putInt("STYLE", 1);
                                    editor.commit();
                                    changeStyle(1);
                                }
                                Toast.makeText(MainActivity.this, "风格被切换,再次点击可还原", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.popup2:
                                String Password = sharedPreferences.getString("PASSWORD", null);
                                SharedPreferences.Editor editor2 = sharedPreferences.edit();
                                if (Password == null) {
                                    editor2.putString("PASSWORD", "setPass");
                                    editor2.commit();
                                    Intent intent = new Intent();
                                    intent.setClass(MainActivity.this, lockActivity.class);
                                    startActivityForResult(intent, 101);
                                } else {
                                    Password_test = Password;
                                    editor2.putString("PASSWORD", "editPass");
                                    editor2.commit();
                                    Intent intent = new Intent();
                                    intent.setClass(MainActivity.this, lockActivity.class);
                                    startActivityForResult(intent, 101);
                                }
                                break;
                            case R.id.popup3:
                                Calendar c= Calendar.getInstance();
                                int day= monthDateView.getCurrentDay();
                                int month= monthDateView.getCurrentMonth();
                                int year= monthDateView.getCurrentYear();
                                DatePickDialogUtil datePicKDialog = new DatePickDialogUtil(
                                        MainActivity.this, year, month, day);
                                datePicKDialog.dateTimePicKDialog(MainActivity.this, db);
                                break;
                            case R.id.popup4:
                                showAlertDialog();
                                break;
                            case R.id.popup5:
                                showAlertDialog2();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorEventListener, mAccelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    float[] accValues = new float[3];
                    accValues = event.values;
                    Date endShakeTime = new Date(System.currentTimeMillis());
                    long diff = endShakeTime.getTime() - lastShakeTime.getTime();
                    if (diff > 500 && (accValues[0] > 19 || accValues[1] > 19 || accValues[2] > 19)) {
                        lastShakeTime = endShakeTime;
                        List<String[]> listString = new ArrayList<String[]>();
                        Cursor cursor = myDatabase.getAllItem(monthDateView.getCurrentYear(), monthDateView.getCurrentMonth(), monthDateView.getCurrentDay()+1);
                        if(cursor.moveToFirst()) {
                            do {
                                listString.add(new String[]{cursor.getString(4), cursor.getString(5)});
                            } while (cursor.moveToNext());
                        }
                        if (listString.size() > 0) {
                            MyAdapter2 myAdapter2 = new MyAdapter2(MainActivity.this, listString);
                            LinearLayout dateTimeLayout = (LinearLayout) MainActivity.this.getLayoutInflater().inflate(R.layout.activity_list, null);
                            acList = (ListView)dateTimeLayout.findViewById(R.id.ac_list);
                            acList.setAdapter(myAdapter2);
                            AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("您明天的事务如下:")
                                    .setView(dateTimeLayout)
                                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                        }
                                    }).show();
                        }
                        else {
                            AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("您明天没有事:")
                                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                        }
                                    }).show();
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    public void changeStyle(int style) {
        if (style == 1) {
            background.setBackgroundResource(R.mipmap.style3);
        } else if (style == 2) {
            background.setBackgroundResource(R.mipmap.style4);
        }
    }

    private void showAlertDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(LayoutInflater.from(this).inflate(R.layout.alert_dialog, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.alert_dialog);
        Button btnPositive = (Button) dialog.findViewById(R.id.btn_add);
        Button btnNegative = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText etContent = (EditText) dialog.findViewById(R.id.et_content);
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String city = (etContent.getText()).toString();
                ConnectivityManager connMgr= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo= connMgr.getActiveNetworkInfo();
                if (networkInfo== null || !networkInfo.isConnected()) {
                    Toast.makeText(MainActivity.this, "当前没有可用网络！", Toast.LENGTH_SHORT).show();
                } else {
                    WeatherWebService weatherWebService = new WeatherWebService();
                    weatherWebService.execute(city);
                }
                dialog.dismiss();
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }
    private void showAlertDialog2() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(LayoutInflater.from(this).inflate(R.layout.alert_dialog2, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.alert_dialog2);
        Button btnPositive1 = (Button) dialog.findViewById(R.id.btn_add1);
        Button btnNegative1 = (Button) dialog.findViewById(R.id.btn_cancel1);

        btnPositive1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final EditText etContent1 = (EditText) dialog.findViewById(R.id.et_content1);
                final String s=etContent1.getText().toString();
                ConnectivityManager connMgr= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo= connMgr.getActiveNetworkInfo();
                if (networkInfo== null || !networkInfo.isConnected()) {
                    Toast.makeText(MainActivity.this, "当前没有可用网络！", Toast.LENGTH_SHORT).show();
                } else {
                        final String webServiceUrl = "http://fy.webxml.com.cn/webservices/EnglishChinese.asmx/TranslatorString";
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpURLConnection connection = null;
                                try {
                                    Log.i("key", "Begin the connetcion.");
                                    URL url = new URL(webServiceUrl);
                                    connection = (HttpURLConnection) url.openConnection();
                                    connection.setDoOutput(true);
                                    connection.setReadTimeout(8000);
                                    connection.setConnectTimeout(8000);
                                    connection.setRequestMethod("POST");
                                    connection.connect();

                                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                                    String request =s.toString();
                                    Log.i("test", request.toString());
                                    request = URLEncoder.encode(request, "utf-8");
                                    out.writeBytes("wordKey=" + request);
                                    out.flush();
                                    out.close();

                                    InputStream in = connection.getInputStream();
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                                    StringBuilder response = new StringBuilder();
                                    String line;

                                    while ((line = reader.readLine()) != null) {
                                        response.append(line);
                                    }

                                    Message message = new Message();
                                    message.what =UPDATE_CONTENT;
                                    message.obj = parseXMLWithPull(response.toString());
                                    handler.sendMessage(message);

                                    Log.i("response", response.toString());
                                }
                                catch (Exception ex) {
                                    Log.i("error", "Fail to connect:" + ex.toString());
                                    ex.printStackTrace();
                                }
                                finally {
                                    if (connection != null) {
                                        connection.disconnect();
                                    }
                                }
                            }
                        }).start();
                }
                dialog.dismiss();
            }
        });
        btnNegative1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }

    private void registRemind() {
        Intent intent = new Intent("E_CLOCK");
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);

        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 24*60*60*1000, pi);
        Log.i("God_Bian", "真的发广播了啊");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isSet", true);
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 101) {
            Password_test  = data.getStringExtra("Password");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("PASSWORD", "setPass2");
            editor.commit();
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, lockActivity.class);
            startActivityForResult(intent, 101);
        } else if (resultCode == 102) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(data.getStringExtra("Password").equals(Password_test)) {
                editor.putString("PASSWORD", Password_test);
                editor.commit();
            } else {
                Toast.makeText(MainActivity.this, "设置失败，2次输入密码不一致", Toast.LENGTH_SHORT).show();
                editor.putString("PASSWORD", null);
                editor.commit();
            }
        } else if (resultCode == 103) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(data.getStringExtra("Password").equals(Password_test)) {
                editor.putString("PASSWORD", "setPass");
                editor.commit();
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, lockActivity.class);
                startActivityForResult(intent, 101);
            } else {
                Toast.makeText(MainActivity.this, "输入密码错误,修改密码失败", Toast.LENGTH_SHORT).show();
                editor.putString("PASSWORD", Password_test);
                editor.commit();
            }
        }
    }
    //萌改
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE_CONTENT:
                    List<String> list = (List<String>) message.obj;

                    for (int i = 0; i < list.size(); i++)
                        Log.i("" + i, list.get(i));

                    if (list.size() == 1) {
                        Toast.makeText(MainActivity.this, "单词不存在，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // TextView TV1 = (TextView)findViewById(R.id.tv1);
                        //  TV1.setText(list.get(0)+list.get(1)+list.get(3));
                        AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("单词："+list.get(0)+"   "+"音标："+list.get(1)+"   "+"释义："+list.get(3))
                                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                }).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };


    private List<String> parseXMLWithPull(String xml) throws XmlPullParserException, IOException {
        try {
            List<String> list = new ArrayList<>();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("string".equals(parser.getName())) {
                            String str = parser.nextText();
                            list.add(str);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
            return  list;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private class WeatherWebService extends AsyncTask<String, Integer, ArrayList<String>> {
        public String city;
        public String high;
        public String low;
        public String type;

        @Override
        protected  ArrayList<String> doInBackground(String... params) {
            BufferedReader reader = null;
            ArrayList<String> result = new ArrayList<String>();
            StringBuffer sbf = new StringBuffer();
            city = params[0];
            String httpUrl = "http://wthrcdn.etouch.cn/weather_mini?city=" + params[0];
            try {
                URL url = new URL(httpUrl);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String strRead = null;
                while ((strRead = reader.readLine()) != null) {
                    sbf.append(strRead);
                    sbf.append("\r\n");
                }
                reader.close();
                result.add(sbf.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute( ArrayList<String>  result) {
            String result0 = result.get(0);
            Log.i("God_Bian", result0);
            if (result0.indexOf("high\":\"") != -1) {
                high = result0.substring(result0.indexOf("high\":\"")+10, result0.indexOf("\",\"type"));
                low = result0.substring(result0.indexOf("low\":\"")+9, result0.indexOf("\",\"date"));
                type = result0.substring(result0.indexOf("type\":\"")+7, result0.indexOf("\",\"low"));
                AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(city + ": " +  type + "  温度: " + low + " ~ " + high)
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }).show();
            } else {
                AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("查询失败，输入城市"+city+"可能有误，请检查")
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }).show();
            }
        }
    }




}

