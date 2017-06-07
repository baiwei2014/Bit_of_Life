package com.example.bianguojian.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Administrator on 2016/10/13.
 */
public class MyAdapter2 extends BaseAdapter {

    private Context context;
    private List<String[]> list;

    public MyAdapter2(Context context, List<String[]> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        if (list == null) {
            return  0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list == null) {
            return null;
        }
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        View convertView;
        ViewHolder viewHolder;

        if (view == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.daylist_string, null);
            viewHolder = new ViewHolder();
            viewHolder.eventName = (TextView)convertView.findViewById(R.id.event_time);
            viewHolder.eventCost = (TextView)convertView.findViewById(R.id.event_name);
            convertView.setTag(viewHolder);
        } else {
            convertView = view;
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.eventName.setText(list.get(position)[0]);
        viewHolder.eventCost.setText(list.get(position)[1]);

        return convertView;
    }

    private class ViewHolder {
        public TextView eventName;
        public TextView eventCost;
    }
}