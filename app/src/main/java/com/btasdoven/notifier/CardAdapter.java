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
 * Created by Batuhan on 2.10.2015.
 */
public class CardAdapter extends BaseAdapter {

    public static class ListData {
        public String id;
        public String name;
        public String notifId;
        public int timestamp;
        public boolean completed;

        public ListData(String _id, String _name, String _notifId, int _ts, boolean _completed) {
            id = _id;
            notifId = _notifId;
            timestamp = _ts;
            completed = _completed;
            name = _name;
        }
    }

    private ArrayList<ListData> mData = new ArrayList<ListData>();
    private LayoutInflater mInflater;
    private DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public CardAdapter(Context ctx) {
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
        public CheckBox checkBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_cards, null);
            holder.textView = (TextView) convertView.findViewById(R.id.textView);
            holder.textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        ListData data = mData.get(position);
        holder.textView.setText(data.name);
        holder.textViewDate.setText(df.format(new Timestamp((long)data.timestamp * 1000)));
        holder.checkBox.setChecked(data.completed);

        return convertView;
    }
}
