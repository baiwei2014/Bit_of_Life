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
public class MyAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;

    public MyAdapter(Context context, List<String> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_string, null);
            viewHolder = new ViewHolder();
            viewHolder.functionName = (TextView)convertView.findViewById(R.id.function_name);
            convertView.setTag(viewHolder);
        } else {
            convertView = view;
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.functionName.setText(list.get(position));

        return convertView;
    }

    private class ViewHolder {
        public TextView functionName;
    }
}
