package com.example.bianguojian.project;

import android.content.Context;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;


public class MyAdapter3 extends BaseAdapter {
    private List<AccountItem> list;
    private Context context;
    private boolean showDate= false;
    DecimalFormat format= new DecimalFormat(".00");

    public void setShowDate(boolean b) {
        showDate= b;
    }

    public MyAdapter3(List<AccountItem> l, Context c) {
        list= l;
        context= c;
    }

    @Override
    public int getCount() {
        if (list== null)
            return  0;
        else
            return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list== null)
            return null;
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView== null) {
            convertView= LayoutInflater.from(context).inflate(R.layout.account_item, null);
            viewHolder= new ViewHolder();
            viewHolder.layout= (RelativeLayout) convertView.findViewById(R.id.account_layout);
            viewHolder.image= (ImageView) convertView.findViewById(R.id.account_image);
            viewHolder.type= (TextView) convertView.findViewById(R.id.account_type);
            viewHolder.remarks= (TextView) convertView.findViewById(R.id.account_remarks);
            viewHolder.number= (TextView) convertView.findViewById(R.id.account_number);
            viewHolder.date= (TextView) convertView.findViewById(R.id.account_item_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        AccountItem ai= list.get(position);
        int resId= context.getResources().getIdentifier(ai.image, "drawable", context.getPackageName());
        viewHolder.image.setImageResource(resId);
        viewHolder.type.setText(ai.type);
        viewHolder.remarks.setText(ai.remarks);
        viewHolder.number.setText(format.format(ai.number));
        GradientDrawable myGrad= (GradientDrawable) (viewHolder.layout.getBackground());
        myGrad.setColor(ai.color);
        if (showDate) {
            viewHolder.date.setText(ai.date.getYear()+ "."+ (ai.date.getMonth()+ 1)+ "."+ ai.date.getDay());
        }
        return convertView;
    }

    private class ViewHolder {
        public RelativeLayout layout;
        public ImageView image;
        public TextView type;
        public TextView remarks;
        public TextView number;
        public TextView date;
    }

}