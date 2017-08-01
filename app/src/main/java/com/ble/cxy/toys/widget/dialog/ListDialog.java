package com.ble.cxy.toys.widget.dialog;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ble.cxy.toys.R;
import com.ble.cxy.toys.adapter.DevAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by coin on 2016/7/27.
 */
public class ListDialog extends AlertDialog {

    public ListDialog(Context context) {
        super(context, R.style.CustomDialog);
    }

    public ListDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    private TextView tvTitle;
    private ListView mdialogListv;
    private DevAdapter mAdapter;
    List<BluetoothDevice> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        setContentView(R.layout.list_dialog);
        initView();
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.dialog_list_title);
        mdialogListv = (ListView) findViewById(R.id.dialog_list);
        mdialogListv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.listAlertListItemClick((BluetoothDevice) mAdapter.getItem(position));
            }
        });
        mList = new ArrayList<>();
        mAdapter = new DevAdapter(getContext());
        mdialogListv.setAdapter(mAdapter);
        mAdapter.setDates(mList);
    }

    public void setData(BluetoothDevice dev){
        if(dev == null){
            return;
        }
        boolean flag = false;
        for (BluetoothDevice devbean:mList) {
            if(TextUtils.equals(devbean.getAddress(),dev.getAddress())){
                flag = true;
            }
        }
        if(!flag) {
            mList.add(dev);
            mAdapter.setDates(mList);
        }
    }

    public void setData(List<BluetoothDevice> devs){
        if(devs == null){
            return;
        }

        List<BluetoothDevice> temp = new ArrayList<>();
        for (BluetoothDevice bldev : devs) {
            boolean flag = false;
            for (BluetoothDevice devbean:mList) {
                if(TextUtils.equals(devbean.getAddress(),bldev.getAddress())){
                    flag = true;
                }
            }
            if(!flag) {
                temp.add(bldev);
            }
        }
        if(temp.size() > 0) {
            mList.addAll(temp);
            mAdapter.setDates(mList);
        }
    }


    private OnClicklistAlertListener listener;

    public void setOnClickAlertListener(OnClicklistAlertListener listener){
        this.listener = listener;
    }

    public interface OnClicklistAlertListener{
        void listAlertListItemClick(BluetoothDevice device);
    }
}
