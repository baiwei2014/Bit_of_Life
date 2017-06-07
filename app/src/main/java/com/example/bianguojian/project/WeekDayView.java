package com.example.bianguojian.project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by Administrator on 2016/12/7.
 */

public class WeekDayView extends View {
    //上横线颜色
    private int mTopLineColor = Color.parseColor("#CCE4F2");
    //下横线颜色
    private int mBottomLineColor = Color.parseColor("#CCE4F2");
    //周一到周五颜色
    private int mWeekdayColor = Color.parseColor("#000000");
    //周末的颜色
    private int mWeekendColor = Color.parseColor("#fa4451");
    //线的宽度
    private int mStrokeWidth = 4;
    private int mWeekSize = 18;
    private Paint paint;
    private DisplayMetrics mDisplayMetrics;
    private String[] weekString = new String[]{"日", "一", "二", "三", "四", "五", "六"};

    public WeekDayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDisplayMetrics = getResources().getDisplayMetrics();
        paint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = mDisplayMetrics.densityDpi * 30;
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = mDisplayMetrics.densityDpi * 300;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        //进行画上下线
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mTopLineColor);
        paint.setStrokeWidth(mStrokeWidth);
        canvas.drawLine(0, height, width, height, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(mWeekSize * mDisplayMetrics.scaledDensity);
        int columnWidth = width / 7;
        for (int i = 0; i < weekString.length; i++) {
            String text = weekString[i];
            int fontWidth = (int)paint.measureText(text);
            int startX = columnWidth*i + (columnWidth - fontWidth)/2;
            int startY = (int)(height/2 - (paint.ascent() + paint.descent())/2);
            if (text.indexOf("日") > -1 || text.indexOf("六") > -1) {
                paint.setColor(mWeekendColor);
            } else {
                paint.setColor(mWeekdayColor);
            }
            paint.setStrokeWidth(10);
            canvas.drawText(text, startX, startY, paint);
        }
    }

    public void setTopLineColor(int mTopLineColor) {
        this.mTopLineColor = mTopLineColor;
    }

    public void setBottomLineColor(int mBottomLineColor) {
        this.mBottomLineColor = mBottomLineColor;
    }

    public void setWeekendColor(int mWeekendColor) {
        this.mWeekendColor = mWeekendColor;
    }

    public void setWeekdayColor(int mWeekdayColor) {
        this.mWeekdayColor = mWeekdayColor;
    }

    public void setStrokeWidth(int mStrokeWidth) {
        this.mStrokeWidth = mStrokeWidth;
    }

    public void setWeekString(String[] weekString) {
        this.weekString = weekString;
    }
}



























