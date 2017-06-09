package com.example.bianguojian.project;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.getContext;

public class DrawActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private ImageView img;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;
    private int width;
    private int height;

    private ImageView back;
    private ImageView pen;
    private ImageView eraser;
    private ImageView color;

    private View choose_view;

    private LinearLayout color_layout;
    private LinearLayout pen_layout;
    private Button black, dark_grey, light_grey, blue, red, green, yellow;

    private ImageView clear;

    private SeekBar width_bar;
    private View circleview;

    private FloatingActionButton ok_button;


    private Map<Integer, Integer> colors= new HashMap();

    private int pen_color= 0xff000000;
    private int pen_width= 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        InitialView();


        ok_button.setOnClickListener(this);

        img.setOnTouchListener(this);
        img.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                width= img.getWidth();
                height= img.getHeight();
                img.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                drawImage();
            }
        });
    }

    private void InitialView() {
        img=  (ImageView) this.findViewById(R.id.img);
        ok_button= (FloatingActionButton) this.findViewById(R.id.paint_ok);
        back= (ImageView) findViewById(R.id.back);
        pen= (ImageView) findViewById(R.id.pen);
        color= (ImageView) findViewById(R.id.color);
        eraser= (ImageView) findViewById(R.id.eraser);
        choose_view= (View) findViewById(R.id.choose_view);
        color_layout= (LinearLayout)findViewById(R.id.color_layout);
        pen_layout= (LinearLayout)findViewById(R.id.pen_layout);
        black= (Button)findViewById(R.id.black);
        dark_grey= (Button) findViewById(R.id.dark_grey);
        light_grey= (Button) findViewById(R.id.light_grey);
        blue= (Button) findViewById(R.id.blue);
        red= (Button) findViewById(R.id.red);
        green= (Button) findViewById(R.id.green);
        yellow= (Button) findViewById(R.id.yellow);
        width_bar= (SeekBar) findViewById(R.id.width_seekbar);
        circleview= findViewById(R.id.circle);
        clear= (ImageView) findViewById(R.id.clear);
        MapColor();
        Button[] buttons= {black, dark_grey, light_grey, blue, red, green, yellow};
        for (int i = 0; i< 7; i++) {
            GradientDrawable myGrad=(GradientDrawable) (buttons[i].getBackground());
            myGrad.setColor(colors.get(buttons[i].getId()));
            buttons[i].setOnClickListener(this);
        }
        pen_layout.setVisibility(View.GONE);
        clear.setVisibility(View.GONE);
        width_bar.setMax(15);
        width_bar.setProgress(0);
        circleview.setLayoutParams(new LinearLayout.LayoutParams(pen_width+ 3, pen_width+ 3));
        back.setOnClickListener(this);
        pen.setOnClickListener(this);
        eraser.setOnClickListener(this);
        color.setOnClickListener(this);
        choose_view.setOnClickListener(this);
        width_bar.setOnSeekBarChangeListener(this);
        clear.setOnClickListener(this);

    }

    private void drawImage() {
        bitmap= Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas= new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        paint= new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setDither(true);
        canvas.drawBitmap(bitmap, new Matrix(), paint);
        img.setImageBitmap(bitmap);
    }


    private int startX;
    private int startY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX= (int) event.getX();
                startY= (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int endX= (int) event.getX();
                int endY= (int) event.getY();
                canvas.drawLine(startX, startY, endX, endY, paint);
                startX= endX;
                startY= endY;
                img.setImageBitmap(bitmap);
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.paint_ok:
                java.text.SimpleDateFormat formater= new java.text.SimpleDateFormat("yyyy_mm_dd_hh_mm_ss");
                Date currentDate= new Date(System.currentTimeMillis());
                String filename= "MyDiary/" +formater.format(currentDate) + ".jpg";
                try {
                    File folder= new File(Environment.getExternalStorageDirectory()+ "/MyDiary");
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    File file= new File(Environment.getExternalStorageDirectory(), filename);
                    OutputStream out= new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra("filename", filename);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.back:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.color:
                color_layout.setVisibility(View.VISIBLE);
                pen_layout.setVisibility(View.GONE);
                clear.setVisibility(View.GONE);
                setChooseView(color);
                paint.setStrokeWidth(pen_width);
                paint.setColor(pen_color);
                break;
            case R.id.pen:
                clear.setVisibility(View.GONE);
                color_layout.setVisibility(View.GONE);
                pen_layout.setVisibility(View.VISIBLE);
                setChooseView(pen);
                GradientDrawable myGrad=(GradientDrawable) (circleview.getBackground());
                myGrad.setColor(pen_color);
                paint.setStrokeWidth(pen_width);
                paint.setColor(pen_color);
                break;
            case R.id.eraser:
                clear.setVisibility(View.VISIBLE);
                color_layout.setVisibility(View.GONE);
                pen_layout.setVisibility(View.GONE);
                setChooseView(eraser);
                paint.setStrokeWidth(25);
                paint.setColor(0xffffffff);
                break;
            case R.id.clear:
                img.setImageBitmap(null);
                drawImage();
                break;
            default:
                if (colors.containsKey(v.getId())) {
                    setChooseColor(v);
                    pen_color = colors.get(v.getId());
                    if (paint.getColor()!= 0xffffffff)
                        paint.setColor(pen_color);
                }
        }

    }

    private void setChooseView(ImageView view) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) choose_view.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_LEFT, view.getId());
        choose_view.setLayoutParams(params);
    }

    private void setChooseColor(View v) {
        for (Map.Entry<Integer, Integer> e : colors.entrySet()) {
            if (e.getValue()== pen_color) {
                ((Button)this.findViewById(e.getKey())).setText("");
                break;
            }
        }
        ((Button)v).setText("〇");
    }

    private void MapColor() {
        colors.put(black.getId(), 0xff000000);
        colors.put(dark_grey.getId(), 0xff808080);
        colors.put(light_grey.getId(), 0xffd0d0d0);
        colors.put(blue.getId(), 0xff1a8cff);
        colors.put(red.getId(), 0xffff1a40);
        colors.put(green.getId(), 0xff2bd965);
        colors.put(yellow.getId(), 0xffffdd33);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            pen_width= 5+ progress;
            circleview.setLayoutParams(new LinearLayout.LayoutParams(pen_width+ 3, pen_width+ 3));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (paint.getColor()!= 0xffffffff)
            paint.setStrokeWidth(pen_width);
    }
}