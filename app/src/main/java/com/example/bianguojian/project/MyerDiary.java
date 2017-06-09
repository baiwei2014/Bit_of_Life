package com.example.bianguojian.project;



import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import java.io.File;
import java.util.Date;

public class MyerDiary extends AppCompatActivity implements View.OnClickListener {
    private int year;
    private int month;
    private int day;
    private Date date;
    private final static String DB_NAME= "MyDiaryDB";
    private static final int DB_VERSION= 1;
    private static final String KEY_DATE= "date";
    private static final String KEY_TEXT= "text";
    private static final String KEY_IMGLOC= "img_loc";
    private static final String KEY_STYLE= "style";
    private DiaryDB db;

    private final static int PICK_IMAGE_REQUEST_CODE= 1;
    private final static int DRAW_REQUEST_CODE= 2;
    private int style= 0;


    private EditText edit_diary;
    private int edit_width= 0;
    private FloatingActionButton fab;
    private FloatingActionButton image;
    private FloatingActionButton draw;
    private FloatingActionButton font;

    private boolean is_expand= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myer_diary);
        Bundle bundle = getIntent().getExtras();
        year = bundle.getInt("Year");
        month = bundle.getInt("Month");
        day = bundle.getInt("Day");
        date = new Date(year, month, day);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout= (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle((month+ 1)+"月"+day+"日");
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        image= (FloatingActionButton) findViewById(R.id.image);
        draw= (FloatingActionButton) findViewById(R.id.draw);
        font= (FloatingActionButton) findViewById(R.id.camera);
        image.setVisibility(View.GONE);
        draw.setVisibility(View.GONE);
        font.setVisibility(View.GONE);
        fab.setOnClickListener(this);
        image.setOnClickListener(this);
        draw.setOnClickListener(this);
        font.setOnClickListener(this);
        edit_diary= (EditText) this.findViewById(R.id.edit_diary);
        db= new DiaryDB(this, DB_NAME, null, DB_VERSION);
        ViewTreeObserver vto= edit_diary.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                edit_diary.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                edit_width=(int) (edit_diary.getWidth());
                getData();
            }
        });
    }

    private void getData() {
        Cursor cursor= db.queryData(date);
        int count= cursor.getCount();
        if (count!= 0 && cursor.moveToFirst()) {
            Log.i("Get", "getdata");
            String text= cursor.getString(cursor.getColumnIndex(KEY_TEXT));
            String ImgLoc= cursor.getString(cursor.getColumnIndex(KEY_IMGLOC));
            edit_diary.setText(text);
            edit_diary.setSelection(text.length());
            setEditImgSpan(ImgLoc, text);
            style= cursor.getInt(cursor.getColumnIndex(KEY_STYLE));
            if (style== 0) {
                edit_diary.getText().setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 0, edit_diary.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                edit_diary.getText().setSpan(new TypefaceSpan("serif"), 0, edit_diary.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                edit_diary.getText().setSpan(new ForegroundColorSpan(Color.BLACK), 0, edit_diary.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                font.setImageResource(R.drawable.normal);
            } else {
                edit_diary.getText().setSpan(new TypefaceSpan("monospace"), 0, edit_diary.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                edit_diary.getText().setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0, edit_diary.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                edit_diary.getText().setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, edit_diary.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                font.setImageResource(R.drawable.bold);
            }
        }
    }

    private void setEditImgSpan(String ImgLoc, String text) {
        String[] loc= ImgLoc.split(" ");
        if (loc.length< 2)
            return;
        int i= 0;
        while (i< loc.length) {
            int start = Integer.parseInt(loc[i]);
            int end = Integer.parseInt(loc[i + 1]);
            String path = text.substring(start, end);
            Log.i("ImgPath", path);
            File file= new File(path);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                if (width >= edit_width) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, edit_width, height * edit_width / width, true);
                }
                ImageSpan imgspan = new ImageSpan(this, bitmap);
                edit_diary.getText().setSpan(imgspan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            i+= 2;
        }
    }

    private void saveData() {
        Editable edit= edit_diary.getText();
        StringBuilder ImgLoc= new StringBuilder();
        Object[] spans= edit.getSpans(0, edit.length(), ImageSpan.class);
        for (int i= 0; i< spans.length; i++) {
            int start= edit.getSpanStart(spans[i]);
            int end= edit.getSpanEnd(spans[i]);
            ImgLoc.append(start+ " "+ end+ " ");
        }
        String text= edit_diary.getText().toString();
        Cursor cursor= db.queryData(date);
        if (cursor.getCount()!= 0) {
            Log.i("Save", "update");
            db.updateData(date, text, ImgLoc.toString(), style);
        } else {
            Log.i("Save", "insert");
            db.insertData(date, text, ImgLoc.toString(), style);
        }
        Log.i("ImgLoc", ImgLoc.toString());

    }


    @Override
    protected void onStop() {
        super.onStop();
        saveData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()== fab.getId()) {
            if (is_expand) {
                is_expand= false;
                RotateAnimation rotateAnimation= new RotateAnimation(0, -360, fab.getWidth()/ 2, fab.getHeight()/ 2);
                rotateAnimation.setDuration(300);
                fab.startAnimation(rotateAnimation);
                startButtonAnimation(image, 0, -5, 0, 75, false);
                startButtonAnimation(draw, 0, 55, 0, 55, false);
                startButtonAnimation(font, 0, 75, 0, -5, false);
                fab.setImageResource(R.drawable.add);
            } else {
                is_expand= true;
                RotateAnimation rotateAnimation= new RotateAnimation(0, 360, fab.getWidth()/ 2, fab.getHeight()/ 2);
                rotateAnimation.setDuration(300);
                fab.startAnimation(rotateAnimation);
                startButtonAnimation(image,-5, 0, 75, 0, true);
                startButtonAnimation(draw, 60, 0, 50, 0, true);
                startButtonAnimation(font, 75, 0, -5, 0, true);
                fab.setImageResource(R.drawable.sub);
            }
        } else if (v.getId()== image.getId()) {
            Intent intent= new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
        } else if (v.getId()== draw.getId()) {
            Intent intent=new Intent(MyerDiary.this, DrawActivity.class);
            startActivityForResult(intent, DRAW_REQUEST_CODE);
        } else if (v.getId()== font.getId()) {
            if (style== 0) {
                edit_diary.getText().setSpan(new TypefaceSpan("monospace"), 0, edit_diary.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                edit_diary.getText().setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0, edit_diary.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                edit_diary.getText().setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, edit_diary.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                font.setImageResource(R.drawable.bold);
                style= 1;
            } else {
                edit_diary.getText().setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 0, edit_diary.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                edit_diary.getText().setSpan(new TypefaceSpan("serif"), 0, edit_diary.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                edit_diary.getText().setSpan(new ForegroundColorSpan(Color.BLACK), 0, edit_diary.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                font.setImageResource(R.drawable.normal);
                style= 0;
            }
        }
    }

    private void startButtonAnimation(FloatingActionButton button ,float fromX, float toX, float fromY, float toY, boolean bigger) {
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation;
        if (bigger) {
            button.setVisibility(View.VISIBLE);
            scaleAnimation= new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
        } else {
            button.setVisibility(View.GONE);
            scaleAnimation= new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
        }
        scaleAnimation.setDuration(300);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(300);
        set.addAnimation(alphaAnimation);
        TranslateAnimation translateAnimation = new TranslateAnimation(fromX, toX, fromY, toY);
        translateAnimation.setDuration(300);
        set.addAnimation(translateAnimation);
        button.startAnimation(set);
    }

    private void AddImage(String path) {
        File file= new File(path);
        if (file.exists()) {
            Bitmap bitmap= BitmapFactory.decodeFile(path);
            int width= bitmap.getWidth();
            int height= bitmap.getHeight();
            if (width>= edit_width) {
                bitmap= Bitmap.createScaledBitmap(bitmap, edit_width, height* edit_width/ width, true);
            }
            SpannableString ss= new SpannableString(path);
            ImageSpan imgspan= new ImageSpan(this, bitmap);
            ss.setSpan(imgspan, 0, path.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            int end = edit_diary.getSelectionEnd();
            edit_diary.getText().insert(end, ss);
            edit_diary.getText().append("\n");
            edit_diary.setSelection(end + ss.length()+ 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== RESULT_OK) {
            Log.i("Add", "Image");
            if (requestCode== PICK_IMAGE_REQUEST_CODE) {
                if (data!= null) {
                    Uri uri= data.getData();
                    if (uri!= null) {
                        String path= uri.getPath();
                        AddImage(path);
                    }
                }
            } else if (requestCode== DRAW_REQUEST_CODE) {
                String filename= data.getStringExtra("filename");
                String path= Environment.getExternalStorageDirectory()+ "/"+ filename;
                AddImage(path);
            }
        }
    }
}
