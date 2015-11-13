package com.btasdoven.notifier;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Batuhan on 6.10.2015.
 */
public class NotificationAdapter extends BaseAdapter {

    public static class ListData {
        public String id;
        public String name;
        public int[] timestamp;
        public int period;

        public ListData(String _id, String _name, int[] _ts, int _period) {
            id = _id;
            timestamp = new int[_ts.length];
            for(int i = 0; i < _ts.length; ++i)
                timestamp[i] = _ts[i];
            period = _period;
            name = _name;
        }
    }

    private ArrayList<ListData> mData = new ArrayList<ListData>();
    private LayoutInflater mInflater;
    private DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public NotificationAdapter(Context ctx) {
        mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void clear() {
        mData = new ArrayList<ListData>();
    }

    public void addItem(final ListData item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView textView;
        public TextView textViewDate;
        public TextView textViewPeriod;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_notifications, null);
            holder.textView = (TextView) convertView.findViewById(R.id.textView);
            holder.textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
            holder.textViewPeriod = (TextView) convertView.findViewById(R.id.textViewPeriod);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        ListData data = mData.get(position);
        holder.textView.setText(data.name);
        String str = "";
        for (int i = 0; i < data.timestamp.length; ++i) {
            str += df.format(new Timestamp((long)data.timestamp[i] * 1000));
            str += "\n";
        }
        str = str.substring(0, str.length()-1);
        holder.textViewDate.setText(str);

        int s = data.period;
        int m = s/60; s = s % 60;
        int h = m/60; m = m % 60;
        int d = h/24; h = h % 24;

        str = "";
        if (d > 0)
            str += d + " day ";
        if (h > 0)
            str += h + " hour ";
        if (m > 0)
            str += m + " min. ";
        if (s > 0)
            str += s + " sec. ";
        str = str.substring(0, str.length()-1);
        holder.textViewPeriod.setText(str);

        return convertView;
    }
}
