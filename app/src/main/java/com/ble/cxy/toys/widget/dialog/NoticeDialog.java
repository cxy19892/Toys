package com.ble.cxy.toys.widget.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ble.cxy.toys.R;


/**
 * Created by coin on 2016/7/27.
 */
public class NoticeDialog extends AlertDialog implements View.OnClickListener{

    public NoticeDialog(Context context) {
        super(context);
    }

    public NoticeDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    private TextView tvTitle, tvMessage, btnCancle, btnEnter;
    private String title, msg, cancle, comfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_layout);
        initView();
    }

    public NoticeDialog setTitle(String title){
        this.title = title;
        return this;
    }

    public NoticeDialog setMessage(String msg){
        this.msg = msg;
        return this;
    }

    public NoticeDialog setCancleMessage(String msg){
        this.cancle = msg;
        return this;
    }
    public NoticeDialog setComfirmMessage(String msg){
        this.comfirm = msg;
        return this;
    }

    public void setCancleVisiable(int visiable){
        btnCancle.setVisibility(visiable);
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.dialog_title);
        tvMessage = (TextView) findViewById(R.id.dialog_message);
        btnCancle = (TextView) findViewById(R.id.dialog_cancle);
        btnCancle.setOnClickListener(this);
        btnEnter = (TextView) findViewById(R.id.dialog_enter);
        btnEnter.setOnClickListener(this);
        tvTitle.setText(title);
        tvMessage.setText(msg);
        btnCancle.setText(TextUtils.isEmpty(cancle)?"取消":cancle);
        btnEnter.setText(TextUtils.isEmpty(comfirm)?"确认":comfirm);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dialog_cancle:
                listener.cancle(this);
                break;
            case R.id.dialog_enter:
                listener.enter(this);
                break;
        }
    }

    private OnClickAlertListener listener;

    public void setOnClickAlertListener(OnClickAlertListener listener){
        this.listener = listener;
    }

    public void setClickCanclevisiable(int visiable){
        btnCancle.setVisibility(visiable);
    }
    public void setTitlevisiable(int visiable){
        tvTitle.setVisibility(visiable);
    }
    public void setMessageColor(int color){
        tvMessage.setTextColor(color);
    }
    public interface OnClickAlertListener{
        void cancle(AlertDialog dialog);
        void enter(AlertDialog dialog);
    }
}
