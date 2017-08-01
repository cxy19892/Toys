package com.ble.cxy.toys.widget.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.ble.cxy.toys.R;


/**
 * Created by coin on 2016/7/27.
 */
public class ProDialog extends AlertDialog {
    private TextView tvprosess;

    public ProDialog(Context context) {
        super(context, R.style.CustomDialog);
    }

    public ProDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    private String msg;

    public void setMessage(String msg){
        this.msg = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pro_dialog_layout);
        tvprosess = (TextView)findViewById(R.id.notice_prosess);
        if(msg != null)
            ((TextView)findViewById(R.id.notice_content)).setText(msg);
    }

    public void setProsess(String prosess){
        tvprosess.setVisibility(View.VISIBLE);
        tvprosess.setText(prosess);
    }

    public void setProsess(int prosess){
        tvprosess.setVisibility(View.VISIBLE);
        tvprosess.setText("已完成"+prosess + "%");
    }
}
