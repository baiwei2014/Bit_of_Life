package com.example.bianguojian.project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.text.Format;
import java.util.Calendar;
import java.util.List;

import static android.R.attr.format;

/**
 * Created by Administrator on 2016/12/7.
 */

public class MonthDateView extends View {
    private static final int NUM_COLUMNS = 7;
    private static final int NUM_ROWS = 6;
    private Paint mPaint;
    private int mDayColor = Color.parseColor("#000000");
    private int mSelectDayColor = Color.parseColor("#ffffff");
    private int mSelectBGColor = Color.parseColor("#1FC2F3");
    private int mOtherBGColor = Color.parseColor("#EEEEEE");
    private int mCurrentColor = Color.parseColor("#ff0000");
    private int mCurrentYear, mCurrentMonth, mCurrentDay;
    private int mSelectYear, mSelectMonth, mSelectDay;
    private int mColumnSize, mRowSize;
    private DisplayMetrics mDisplayMetrics;
    private int mDaySize = 24;
    private TextView tv_data, tv_week;
    private int weekRow;
    private int [][] daysString;
    private int mCircleRadius = 6;
    private DateClick dateClick;
    private int mCircleColor = Color.parseColor("#ff0000");
    private List<Integer> dayHasThingList;
    private Calendar calender = Calendar.getInstance();

    public MonthDateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDisplayMetrics = getResources().getDisplayMetrics();
        mPaint = new Paint();
        mCurrentYear = calender.get(Calendar.YEAR);
        mCurrentMonth = calender.get(Calendar.MONTH);
        mCurrentDay = calender.get(Calendar.DATE);
        setSelectYearMonth(mCurrentYear, mCurrentMonth, mCurrentDay);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if(heightMode == MeasureSpec.AT_MOST) {
            heightSize = mDisplayMetrics.densityDpi * 200;
        }
        if(widthMode == MeasureSpec.AT_MOST) {
            widthSize = mDisplayMetrics.densityDpi * 300;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected  void onDraw(Canvas canvas) {
        initSize();
        daysString = new int[6][7];
        mPaint.setTextSize(mDaySize * mDisplayMetrics.scaledDensity);
        String dayString;
        calender.set(Calendar.YEAR, mSelectYear);
        calender.set(Calendar.MONTH, mSelectMonth);
        calender.set(Calendar.DATE, 1);
        int mMonthDays = calender.getActualMaximum(calender.DATE);
        int weekNumber = calender.get(Calendar.DAY_OF_WEEK);
        for (int day = 0; day < mMonthDays; day++) {
            dayString = (day + 1) + "";
            int column = (day + weekNumber - 1) % 7;
            int row = (day + weekNumber - 1) / 7;
            daysString[row][column] = day + 1;
            int startX = (int)(mColumnSize * column + (mColumnSize - mPaint.measureText(dayString))/2);
            int startY = (int)(mRowSize * row + mRowSize / 2 - (mPaint.ascent() + mPaint.descent())/2);
            if (dayString.equals(mSelectDay+"")) {
                int startRecX = mColumnSize * column + 2;
                int startRecY = mRowSize * row + 3;
                int endRecX = startRecX + mColumnSize - 2;
                int endRecY = startRecY + mRowSize - 3;
                mPaint.setColor(mSelectBGColor);
                mPaint.setAlpha(255);
                canvas.drawRect(startRecX, startRecY, endRecX, endRecY, mPaint);
                weekRow = row + 1;
            } else {
                int startRecX = mColumnSize * column + 2;
                int startRecY = mRowSize * row + 3;
                int endRecX = startRecX + mColumnSize - 2;
                int endRecY = startRecY + mRowSize - 3;
                mPaint.setColor(mOtherBGColor);
                mPaint.setAlpha(0);
                canvas.drawRect(startRecX, startRecY, endRecX, endRecY, mPaint);
                weekRow = row + 1;
            }
            drawCircle(row, column, day + 1, canvas);
            if (dayString.equals(mSelectDay + "")) {
                mPaint.setColor(mSelectDayColor);
            } else if (dayString.equals(mCurrentDay+"") && mCurrentDay != mSelectDay && mCurrentMonth == mSelectMonth) {
                mPaint.setColor(mCurrentColor);
            } else {
                mPaint.setColor(mDayColor);
            }
            canvas.drawText(dayString, startX, startY, mPaint);
            if (tv_data != null) {
                tv_data.setText(mSelectYear + "年" + (mSelectMonth + 1) + "月");
            }
            if (tv_week != null) {
                tv_week.setText("第" + calender.get(Calendar.WEEK_OF_MONTH) + "周");
            }
        }
    }

    private void drawCircle(int row, int column, int day, Canvas canvas) {
        if (dayHasThingList != null && dayHasThingList.size() > 0) {
            if (!dayHasThingList.contains(day)) return;
            mPaint.setColor(mCircleColor);
            float circleX = (float)(mColumnSize * column + mColumnSize * 0.8);
            float circleY = (float)(mRowSize * row + mRowSize * 0.2);
            canvas.drawCircle(circleX, circleY, mCircleRadius, mPaint);
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private int downX = 0, downY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventCode = event.getAction();
        switch (eventCode) {
            case MotionEvent.ACTION_DOWN:
                downX = (int)event.getX();
                downY = (int)event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int upX = (int)event.getX();
                int upY = (int)event.getY();
                if(Math.abs(upX-downX) < 10 && Math.abs(upY - downY) < 10) {
                    performClick();
                    doClickAction((upX + downX)/2, (upY + downY)/2);
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void initSize() {
        mColumnSize = getWidth() / NUM_COLUMNS;
        mRowSize = getHeight() / NUM_ROWS;
    }

    private void setSelectYearMonth(int year, int month, int day) {
        mSelectYear = year;
        mSelectMonth = month;
        mSelectDay = day;
    }

    private void doClickAction(int x, int y) {
        int row = y / mRowSize;
        int column = x / mColumnSize;
        setSelectYearMonth(mSelectYear, mSelectMonth, daysString[row][column]);
        if (dateClick != null) {
            dateClick.onClickOnDate();
        }
        invalidate();
    }

    public void onLeftClick() {
        int year = mSelectYear;
        int month = mSelectMonth;
        int day = mSelectDay;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, mSelectYear);
        calendar.set(Calendar.MONTH, mSelectMonth);
        if(month == 0) {
            year = mSelectYear - 1;
            month = 11;
        } else if(calendar.getActualMaximum(calendar.DATE) == day) {
            month = month - 1;
            calendar.set(Calendar.MONTH, month);
            day = calendar.getActualMaximum(calendar.DATE);
        } else {
            month = month - 1;
        }
        setSelectYearMonth(year, month, day);
        invalidate();
    }

    public void onRightClick() {
        int year = mSelectYear;
        int month = mSelectMonth;
        int day = mSelectDay;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, mSelectYear);
        calendar.set(Calendar.MONTH, mSelectMonth);
        if(month == 11) {
            year = mSelectYear + 1;
            month = 0;
        } else if(calendar.getActualMaximum(calendar.DATE) == day) {
            month = month + 1;
            calendar.set(Calendar.MONTH, month);
        } else {
            month = month + 1;
        }
        setSelectYearMonth(year, month, 1);
        invalidate();
    }

    public int getSelectYear() {
        return mSelectYear;
    }

    public int getSelectMonth() {
        return mSelectMonth;
    }

    public int getSelectDay() {
        return mSelectDay;
    }

    public int getCurrentYear() {
        return mCurrentYear;
    }

    public int getCurrentMonth() {
        return mCurrentMonth;
    }

    public int getCurrentDay() {
        return mCurrentDay;
    }

    public void setDayColor(int mDayColor) {
        this.mDayColor = mDayColor;
    }

    public void setSelectDayColor(int mSelectDayColor) {
        this.mSelectDayColor = mSelectDayColor;
    }

    public void setSelectBGColor(int mSelectBGColor) {
        this.mSelectBGColor = mSelectBGColor;
    }

    public void setCurrentColor(int mCurrentColor) {
        this.mCurrentColor = mCurrentColor;
    }

    public void setDaySize(int mDaySize) {
        this.mDaySize = mDaySize;
    }

    public void  setTextView(TextView tv_date, TextView tv_week) {
        this.tv_data = tv_date;
        this.tv_week = tv_week;
        invalidate();
    }

    public void setDaysHasThingList(List<Integer> daysHasThingList) {
        this.dayHasThingList = daysHasThingList;
    }


    public void setCircleRadius(int mCircleRadius) {
        this.mCircleRadius = mCircleRadius;
    }

    public void setCircleColor(int mCircleColor) {
        this.mCircleColor = mCircleColor;
    }

    public interface DateClick{
        public void onClickOnDate();
    }

    public void setDateClick(DateClick dateClick) {
        this.dateClick = dateClick;
    }

    public void setTodayToView() {
        setSelectYearMonth(mCurrentYear, mCurrentMonth, mCurrentDay);
        invalidate();
    }
}
