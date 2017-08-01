package com.ble.cxy.toys.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ble.cxy.toys.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxy19892 on 2016/8/16.
 */
public class DevAdapter extends BaseAdapter {
    List<BluetoothDevice> mList = new ArrayList<>();
    private LayoutInflater inflater;

    public DevAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    public void setDates(List<BluetoothDevice> List){
        mList = List;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(mList == null)
        return 0;
        else
            return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        if(mList==null)
        return 0;
        else{
            return position;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_text, null);
            holder.tv = (TextView) convertView.findViewById(R.id.item_tv);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        if(mList != null && mList.get(position) != null) {
            if(TextUtils.isEmpty(mList.get(position).getName())){
                holder.tv.setText(mList.get(position).getAddress());
            }else
                holder.tv.setText(mList.get(position).getName());
        }

        return convertView;
    }

    class ViewHolder{
        TextView tv;
    }
}
