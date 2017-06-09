package com.example.bianguojian.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AccountAdd extends AppCompatActivity implements View.OnClickListener {
    private static int MODE= MODE_PRIVATE;
    private static final String PREFERENCE_NAME= "ACCOUNT_ADD";
    private SharedPreferences sharedPreferences;

    private int[] btidNum= {R.id.txt0, R.id.txt1, R.id.txt2,R.id.txt3, R.id.txt4, R.id.txt5, R.id.txt6, R.id.txt7, R.id.txt8, R.id.txt9};
    private Button[] numberbuttons= new Button[btidNum.length];
    private ImageButton del;
    private Button plus;
    private Button minus;
    private Button ok;
    private Button clear;
    private Button point;

    private ImageView image;
    private TextView type;
    private TextView number;
    private EditText remarks;

    private int layout_width;
    private ScrollView sv;
    private ScrollView sv2;
    private GridLayout layout;
    private GridLayout layout2;
    private Map<String, String> TypeToImage= new HashMap<>();
    private ArrayList<String> types;
    private ArrayList<Integer> colors;

    private ArrayList<String> types2;
    private ArrayList<Integer> colors2;

    private ArrayList<Integer> allColor= new ArrayList<>(Arrays.asList(0xfff75b44, 0xffe1184b, 0xff217cdc, 0xff00bebe, 0xff3fbe7d, 0xfff7c142, 0xff5f3a80, 0xff6fd1ff, 0xffff395f));

    private DecimalFormat dformat= new DecimalFormat("0.00");
    private float sum= 0;
    private String integer= "0";
    private String decimal= "00";
    private int loc= 1;
    private int state= 0;

    boolean outgo_or_income=  true;
    private Button income;
    private Button outgo;

    @Override
    protected void onCreate(Bundle savedInstanceloc) {
        super.onCreate(savedInstanceloc);
        setContentView(R.layout.activity_account_add);
        sharedPreferences= getSharedPreferences(PREFERENCE_NAME, MODE);
        initialTypeToImage();
        readData();
        findview();
    }

    private void findview() {

        income= (Button) this.findViewById(R.id.income);
        outgo= (Button) this.findViewById(R.id.outgo);
        sv2= (ScrollView) this.findViewById(R.id.account_add_sv2);
        sv= (ScrollView) this.findViewById(R.id.account_add_sv);
        layout2= (GridLayout) this.findViewById(R.id.account_add_layout2);
        layout= (GridLayout) this.findViewById(R.id.account_add_layout);
        image= (ImageView) this.findViewById(R.id.account_add_image);
        type= (TextView) this.findViewById(R.id.account_add_type);
        number= (TextView) this.findViewById(R.id.account_add_number);
        remarks= (EditText) this.findViewById(R.id.account_add_remarks);
        for (int i= 0; i< 10; i++) {
            numberbuttons[i]= (Button) findViewById(btidNum[i]);
            numberbuttons[i].setOnClickListener(this);
        }
        del= (ImageButton) this.findViewById(R.id.cal_del);
        plus= (Button) this.findViewById(R.id.cal_plus);
        minus= (Button) this.findViewById(R.id.cal_minus);
        ok= (Button) this.findViewById(R.id.cal_ok);
        clear= (Button) this.findViewById(R.id.cal_clear);
        point= (Button) this.findViewById(R.id.txt_point);
        del.setOnClickListener(this);
        plus.setOnClickListener(this);
        minus.setOnClickListener(this);
        ok.setOnClickListener(this);
        clear.setOnClickListener(this);
        point.setOnClickListener(this);
        sv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                sv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width= sv.getWidth();
                int num= width/ 125;
                int extra= width% 125;
                layout_width= 125+ extra/ num;
                layout.setColumnCount(num);
                layout2.setColumnCount(num);
                initialTypeViews(layout, types, colors);
                initialTypeViews(layout2, types2, colors2);
            }
        });
        new ClickListener().onClick(outgo);
        income.setOnClickListener(new ClickListener());
        outgo.setOnClickListener(new ClickListener());
    }

    private void initialTypeViews(final GridLayout gl, final ArrayList<String> t, final ArrayList<Integer> c) {
        for (int j= 0; j< t.size(); j++) {
            addView(t.get(j), c.get(j), gl);
        }
        LinearLayout.LayoutParams llp= new LinearLayout.LayoutParams(100, 100);
        final GridLayout.LayoutParams glp= new GridLayout.LayoutParams(llp);
        glp.setMargins((layout_width- 100)/ 2, 10, 0, 0);
        final FloatingActionButton add_view= new FloatingActionButton(this);
        add_view.setImageResource(R.drawable.add);
        add_view.setRippleColor(0x000000000);
        gl.addView(add_view, glp);
        add_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input= new EditText(AccountAdd.this);
                AlertDialog.Builder builder= new AlertDialog.Builder(AccountAdd.this);
                builder.setTitle("新建分类")
                        .setView(input)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String str= input.getText().toString();
                                if (!str.isEmpty()  &&!t.contains(str)) {
                                    gl.removeView(add_view);
                                    int color= allColor.get((new Random()).nextInt(allColor.size()));
                                    addView(str, color, gl);
                                    t.add(str);
                                    c.add(color);
                                    gl.addView(add_view, glp);
                                    saveData();
                                }
                            }
                        });
                builder.show();
            }
        });
    }


    public void addView(final String _type, final int _color, final GridLayout gl) {
        final LinearLayout ll= new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(0, 0, 0, 5);
        ll.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams llp= new LinearLayout.LayoutParams(layout_width, LinearLayout.LayoutParams.WRAP_CONTENT);
        GridLayout.LayoutParams glp= new GridLayout.LayoutParams(llp);
        glp.setMargins(0, 10, 0, 10);
        gl.addView(ll, glp);
        ImageView iv= new ImageView(this);
        final int resId= getResources().getIdentifier(TypeToImage.containsKey(_type)? TypeToImage.get(_type): "others", "drawable", getPackageName());
        iv.setImageResource(resId);
        GradientDrawable myGrad= new GradientDrawable();
        myGrad.setColor(_color);
        myGrad.setCornerRadius(50);
        iv.setBackground(myGrad);
        iv.setPadding(15, 15, 15, 15);
        ll.addView(iv, 100, 100);
        TextView tv= new TextView(this);
        tv.setText(_type);
        tv.setTextSize(15);
        tv.setGravity(Gravity.CENTER);
        ll.addView(tv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] topos= new int[2];
                image.getLocationOnScreen(topos);
                int[] position= new int[2];
                v.getLocationOnScreen(position);
                AnimationSet set= new AnimationSet(true);
                ScaleAnimation animation= new ScaleAnimation(1, 1.1f, 1, 1.1f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(100);
                v.startAnimation(animation);
                image.setImageResource(resId);
                GradientDrawable grad= (GradientDrawable) image.getBackground();
                grad.setColor(_color);
                type.setText(_type);
            }
        });
        iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                AlertDialog.Builder builder= new AlertDialog.Builder(AccountAdd.this);
                builder.setTitle("删除分类")
                        .setMessage("确定删除分类"+ _type +"吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                gl.removeView(ll);
                                if (gl.getId()== layout.getId()) {
                                    int index = types.indexOf(_type);
                                    types.remove(index);
                                    colors.remove(index);
                                } else {
                                    int index= types2.indexOf(_type);
                                    types2.remove(index);
                                    colors2.remove(index);
                                }
                                saveData();
                            }
                        });
                builder.show();
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId()== ok.getId()) {
            if (state== 0) {
                Float f= Float.parseFloat(number.getText().toString());
                if (f<= 0) {
                    Toast.makeText(this, "金额需要为正数", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent= new Intent();
                String typename = type.getText().toString();
                if (typename.equals("")) {
                    Toast.makeText(this, "请选择一种分类", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra("type", typename);
                intent.putExtra("remarks", remarks.getText().toString());
                intent.putExtra("image", TypeToImage.containsKey(typename)? TypeToImage.get(typename) : "others");
                if (outgo_or_income) {
                    intent.putExtra("color", colors.get(types.indexOf(typename)));
                    intent.putExtra("number", ""+ (-f));
                } else {
                    intent.putExtra("color", colors2.get(types2.indexOf(typename)));
                    intent.putExtra("number", ""+ f);
                }
                setResult(RESULT_OK, intent);
                finish();
            } else {
                if (loc!= 0) {
                    if (state == 1) {
                        sum += Float.parseFloat(number.getText().toString());
                    } else if (state == -1) {
                        sum -= Float.parseFloat(number.getText().toString());
                    }
                    loc= 0;
                    number.setText(dformat.format(sum));
                }
                ok.setText("确定");
                state = 0;
            }
        } else if (v.getId()== clear.getId()) {
            integer= "0";
            decimal= "00";
            number.setText("0.00");
            sum= 0;
            state= 0;
            loc= 1;
            ok.setText("确定");
        } else if (v.getId()== del.getId()) {
            if (loc== 0) {
                integer= "0";
                decimal= "00";
                number.setText("0.00");
                sum= 0;
                state= 0;
                loc= 1;
                ok.setText("确定");
            } else if (loc== 1) {
                if (integer.length()== 1)
                    integer= "0";
                else
                    integer= integer.substring(0, integer.length()- 1);
            } else if (loc== -1) {
                loc= 1;
            } else if (loc== -2) {
                decimal= "00";
                loc= -1;
            } else if (loc== -3) {
                decimal= decimal.substring(0, 1)+ "0";
                loc= -2;
            }
            number.setText(integer+ "."+ decimal);
        } else if (v.getId()== plus.getId()) {
            if (loc!= 0) {
                if (state == 1) {
                    sum += Float.parseFloat(number.getText().toString());
                } else if (state == -1) {
                    sum -= Float.parseFloat(number.getText().toString());
                }
            }
            if (state== 0) {
                ok.setText("=");
                sum = Float.parseFloat(number.getText().toString());
            }
            number.setText(dformat.format(sum));
            loc= 0;
            state= 1;
        } else if (v.getId()== minus.getId()) {
            if (loc!= 0) {
                if (state == 1) {
                    sum += Float.parseFloat(number.getText().toString());
                } else if (state == -1) {
                    sum -= Float.parseFloat(number.getText().toString());
                }
            }
            if (state== 0) {
                ok.setText("=");
                sum = Float.parseFloat(number.getText().toString());
            }
            number.setText(dformat.format(sum));
            state= -1;
            loc= 0;

        } else if (v.getId()== point.getId()) {
            if (loc== 1) {
                loc= -1;
            }
        } else {
            for (int i= 0; i< 10; i++) {
                if (v.getId()== numberbuttons[i].getId()) {
                    if (loc== 1) {
                        if (integer.equals("0")) {
                            integer= ""+ i;
                        } else {
                            integer+= i;
                        }
                    } else if (loc== -1) {
                        decimal= i+ decimal.substring(1);
                        loc= -2;
                    } else if (loc== -2) {
                        decimal= decimal.substring(0, 1)+ i;
                        loc= -3;
                    } else if (loc== 0) {
                        integer= ""+ i;
                        decimal= "00";
                        loc= 1;
                    }
                    number.setText(integer+ "."+ decimal);
                    break;
                }
            }
        }
    }

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId()== income.getId()) {
                outgo_or_income= false;
                sv2.setVisibility(View.VISIBLE);
                sv.setVisibility(View.GONE);
                income.setTextColor(0xff1a8cff);
                outgo.setTextColor(0xff333333);
                if (types2.size()== 0) {
                    type.setText("");
                } else {
                    String typename= types2.get(0);
                    type.setText(typename);
                    image.setImageResource(getResources().getIdentifier(TypeToImage.containsKey(typename)? TypeToImage.get(typename) : "others", "drawable", getPackageName()));
                    GradientDrawable grad = (GradientDrawable) image.getBackground();
                    grad.setColor(colors2.get(0));
                }
            } else if (v.getId()== outgo.getId()) {
                outgo_or_income= true;
                sv2.setVisibility(View.GONE);
                sv.setVisibility(View.VISIBLE);
                outgo.setTextColor(0xff1a8cff);
                income.setTextColor(0xff333333);
                if (types.size()== 0) {
                    type.setText("");
                } else {
                    String typename= types.get(0);
                    type.setText(typename);
                    image.setImageResource(getResources().getIdentifier(TypeToImage.containsKey(typename)? TypeToImage.get(typename) : "others", "drawable", getPackageName()));
                    GradientDrawable grad = (GradientDrawable) image.getBackground();
                    grad.setColor(colors.get(0));
                }
            }
        }
    }

    private void readData() {
        Boolean hasSaved= sharedPreferences.getBoolean("hasSaved", false);
        if (hasSaved) {
            types= new ArrayList<>();
            colors= new ArrayList<>();
            types2= new ArrayList<>();
            colors2= new ArrayList<>();
            getStringFromJson(types, sharedPreferences.getString("types", null));
            getStringFromJson(types2, sharedPreferences.getString("types2", null));
            getIntegerFromJson(colors, sharedPreferences.getString("colors", null));
            getIntegerFromJson(colors2, sharedPreferences.getString("colors2", null));

        } else {
            types= new ArrayList<>(Arrays.asList("一般", "餐饮", "生活用品", "交通", "服饰", "药品", "娱乐", "借出", "水电"));
            colors= new ArrayList<>(Arrays.asList(0xfff75b44, 0xffe1184b, 0xff217cdc, 0xff00bebe, 0xff3fbe7d, 0xfff7c142, 0xff5f3a80, 0xff6fd1ff, 0xffff395f));
            types2= new ArrayList<>(Arrays.asList("一般", "工资", "红包","借入"));
            colors2= new ArrayList<>(Arrays.asList(0xfff75b44, 0xff00bebe, 0xfff7c142, 0xff6fd1ff));
        }
    }

    private void saveData() {
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putBoolean("hasSaved", true);
        editor.putString("types", putDataToJson(types).toString());
        editor.putString("types2", putDataToJson(types2).toString());
        editor.putString("colors", putDataToJson(colors).toString());
        editor.putString("colors2", putDataToJson(colors2).toString());
        editor.commit();
    }

    private void getStringFromJson(ArrayList<String> to, String from) {
        try {
            if (from== null)
                return;
            JSONArray jsonArray= new JSONArray(from);
            for (int i = 0; i < jsonArray.length(); i++) {
                to.add(jsonArray.getString(i));
            }
        } catch (Exception e) {

        }
    }

    private void getIntegerFromJson(ArrayList<Integer> to, String from) {
        try {
            if (from== null)
                return;
            JSONArray jsonArray= new JSONArray(from);
            for (int i = 0; i < jsonArray.length(); i++) {
                to.add(jsonArray.getInt(i));
            }
        } catch (Exception e) {

        }
    }

    private <T> JSONArray putDataToJson(ArrayList<T> arr) {
        JSONArray jsonArray= new JSONArray();
        for (T entry: arr) {
            jsonArray.put(entry);
        }
        return jsonArray;
    }

    private void initialTypeToImage() {
        ArrayList<String> alltype= new ArrayList<>(Arrays.asList("一般", "餐饮", "生活用品", "交通", "服饰", "药品", "娱乐", "借出", "水电", "一般", "工资", "红包","借入"));
        ArrayList<String> allimage= new ArrayList<>(Arrays.asList("star" ,"catering", "living_goods", "transport", "clothing", "medicine", "entertainment",
                "lend", "water_eletricity", "star" , "salary", "redpacket", "borrow"));
        for (int i= 0; i< alltype.size(); i++) {
            TypeToImage.put(alltype.get(i), allimage.get(i));
        }
    }
}
