package com.ble.cxy.toys;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ble.cxy.toys.widget.dialog.ListDialog;
import com.ble.cxy.toys.widget.dialog.NoticeDialog;
import com.ble.cxy.toys.widget.dialog.ProDialog;
import com.buildwin.bluetooth.impl.BuildwinBluetoothManager;
import com.buildwin.power.callback.BlueToothCallback;
import com.buildwin.power.cwpower.BuildwinPowerControl;
import com.buildwin.power.cwpower.CwBluetoothPower;
import com.buildwin.power.cwpower.DevicePowerControl;
import com.buildwin.power.modelmark.AuxModel;
import com.buildwin.power.modelmark.BatteryModel;
import com.buildwin.power.modelmark.MainModel;
import com.buildwin.power.modelmark.MusicModel;
import com.buildwin.power.modelmark.OTAModel;
import com.buildwin.power.modelmark.VersionModel;
import com.buildwin.power.modelmark.VolumeModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.find_dev)
    Button findDev;
    @BindView(R.id.connect_dev)
    Button connectDev;
    @BindView(R.id.dis_connect_dev)
    Button disConnectDev;
    @BindView(R.id.header1)
    TextView header1;
    @BindView(R.id.header2)
    TextView header2;
    @BindView(R.id.header3)
    TextView header3;
    @BindView(R.id.header4)
    TextView header4;
    @BindView(R.id.bottom)
    TextView bottom;

    private BluetoothDevice device = null;
    private ListDialog listDialog = null;
    private ProDialog proDialog;
    // 蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    // 打开蓝牙回调
    private static final int REQUEST_ENABLE_BT = 1;
    private CwBluetoothPower cwpower;
    private DevicePowerControl devicePowerControl;
    private BuildwinPowerControl builwinPowerControl;

    public enum STATE {
        DEV_NOTFUND,
        DEV_FUNDED,
        DEV_DISCONNECT,
        DEV_CONNECTING,
        DEV_CONNECTED,
    }

    private STATE currentState = STATE.DEV_NOTFUND;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        registerReceiver(mMainReceiver, makeMainIntentFilter());
//        regesiterBroadcastReceiver();
    }

    private void init() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            //获取蓝牙管理器
            cwpower = CwBluetoothPower.getInstance();
            //初始化管理器，设置回调函数，设置连接方式，
            //建议自动选择协议
            cwpower.init(this, blueToothCallbac, BuildwinBluetoothManager.SPP_CONNECT_WAY);
        }
    }

    @OnClick({R.id.find_dev, R.id.connect_dev, R.id.dis_connect_dev, R.id.cmd_f0, R.id.cmd_f1, R.id.cmd_f2, R.id.cmd_f3, R.id.cmd_f4, R.id.cmd_f5, R.id.cmd_f6, R.id.cmd_f7, R.id.cmd_f8, R.id.cmd_f9})
    public void onClick(View view) {
        if (device == null) {
            findDev();
            return;
        }
        switch (view.getId()) {
            case R.id.find_dev:
                if (currentState == STATE.DEV_CONNECTED || currentState == STATE.DEV_CONNECTING) {
                    devicePowerControl.disConnect();
                    findDev();
                }else{
                    findDev();
                }
                break;
            case R.id.connect_dev:
                if (currentState == STATE.DEV_FUNDED || currentState == STATE.DEV_DISCONNECT) {
                    startConnectDev();
                }
                break;
            case R.id.dis_connect_dev:
                if (currentState == STATE.DEV_CONNECTED) {
                    devicePowerControl.disConnect();
                }
                break;
            case R.id.cmd_f0:
                if (builwinPowerControl != null && currentState == STATE.DEV_CONNECTED) {
                    builwinPowerControl.customMethod("CUSTOMLED", new byte[]{2, (byte) 0xf0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00});
                }
                break;
            case R.id.cmd_f1:
                if (builwinPowerControl != null && currentState == STATE.DEV_CONNECTED) {
                    builwinPowerControl.customMethod("CUSTOMLED", new byte[]{2, (byte) 0xf1, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00});
                }
                break;
            case R.id.cmd_f2:
                if (builwinPowerControl != null && currentState == STATE.DEV_CONNECTED) {
                    builwinPowerControl.customMethod("CUSTOMLED", new byte[]{2, (byte) 0xf2, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00});
                }
                break;
            case R.id.cmd_f3:
                if (builwinPowerControl != null && currentState == STATE.DEV_CONNECTED) {
                    builwinPowerControl.customMethod("CUSTOMLED", new byte[]{2, (byte) 0xf3, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00});
                }
                break;
            case R.id.cmd_f4:
                if (builwinPowerControl != null && currentState == STATE.DEV_CONNECTED) {
                    builwinPowerControl.customMethod("CUSTOMLED", new byte[]{2, (byte) 0xf4, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00});
                }
                break;
            case R.id.cmd_f5:
                if (builwinPowerControl != null && currentState == STATE.DEV_CONNECTED) {
                    builwinPowerControl.customMethod("CUSTOMLED", new byte[]{2, (byte) 0xf5, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00});
                }
                break;
            case R.id.cmd_f6:
                if (builwinPowerControl != null && currentState == STATE.DEV_CONNECTED) {
                    builwinPowerControl.customMethod("CUSTOMLED", new byte[]{2, (byte) 0xf6, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00});
                }
                break;
            case R.id.cmd_f7:
                if (builwinPowerControl != null && currentState == STATE.DEV_CONNECTED) {
                    builwinPowerControl.customMethod("CUSTOMLED", new byte[]{2, (byte) 0xf7, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00});
                }
                break;
            case R.id.cmd_f8:
                if (builwinPowerControl != null && currentState == STATE.DEV_CONNECTED) {
                    builwinPowerControl.customMethod("CUSTOMLED", new byte[]{2, (byte) 0xf8, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00});
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //获取蓝牙管理器
            cwpower = CwBluetoothPower.getInstance();
            //初始化管理器，设置回调函数，设置连接方式，
            //建议自动选择协议
            cwpower.init(this, blueToothCallbac, BuildwinBluetoothManager.SPP_CONNECT_WAY);
            ScanDev();
        }
    }

    @Override
    protected void onDestroy() {
        devicePowerControl.disConnect();
        unregisterReceiver(mMainReceiver);
//        unregisterBroadcastReceiver();
        super.onDestroy();
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (proDialog != null && proDialog.isShowing())
                        proDialog.dismiss();
                    device = (BluetoothDevice) msg.obj;
                    currentState = STATE.DEV_FUNDED;
                    ParcelUuid[] uuid = device.getUuids();
                    if(uuid != null) {
                        StringBuilder SB = new StringBuilder();
                        for (ParcelUuid puid : uuid) {
                            SB.append(puid.getUuid());
                        }
                        bottom.setText(SB.toString());
                    }
                    startConnectDev();
                    break;
                case 1:
                    if (proDialog != null && proDialog.isShowing())
                        proDialog.dismiss();
                    break;

                case 2:
                    header1.setText(msg.obj.toString());
                    break;
                case 3:
                    header2.setText(msg.obj.toString());
                    break;
                case 4:
                    header3.setText(msg.obj.toString());
                    break;
                case 5:
                    header4.setText(msg.obj.toString());
                    break;
                case 6:
                    bottom.setText(msg.obj.toString());
                    break;
                case 7:
                    Log.d("chen", "handleMessage: 搜索结束");
                    if (proDialog != null && proDialog.isShowing())
                        proDialog.dismiss();
                    if(TextUtils.equals(header1.getText().toString(),"开始搜索"))
                        header1.setText("");
                    break;
            }
        }
    };

    private void startConnectDev() {
        //获取蓝牙管理器
        if (cwpower == null) {
            cwpower = CwBluetoothPower.getInstance();
        }
        //获取设备控制器
        if (devicePowerControl == null) {
            devicePowerControl = cwpower.getDevicePowerControl();
        }
        //连接设备
        currentState = STATE.DEV_CONNECTING;
        devicePowerControl.connect(device);
        Log.d("chen", "startConnectDev: 开始连接");
    }

    private IntentFilter makeMainIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.DEVICE_CONNECT);
        intentFilter.addAction(Constant.DEVICE_DISCONNECT);
        intentFilter.addAction(Constant.DEVICE_DISCOVERED);
        //接收设备信息广播
        intentFilter.addAction(VersionModel.DEVICE_INFO);
        //接收音量大小广播
        intentFilter.addAction(VolumeModel.VOLUME_LEVE);
        intentFilter.addAction(MainModel.CURRENT_STATE);
        //接收音乐状态广播，音乐模式是否有设备接入
        intentFilter.addAction(MusicModel.MUSIC_MODEL_STATE);
        //TF卡，U盘，手机音频连接断开广播。只有其中一种断开便接收到该广播
        intentFilter.addAction(MusicModel.MUSIC_SOURCE_CLOSE);
        //TF卡，U盘，手机音频连接或者插入广播。只有其中一种插入或者连接上便接收到该广播
        intentFilter.addAction(MusicModel.MUSIC_SOURCE_OPEN);
        //当前播放的是哪种设备，TF卡，U盘，手机
        intentFilter.addAction(MusicModel.MUSIC_SOURCE_STATE);
        //AUX是否有开启
        intentFilter.addAction(AuxModel.AUX_MODEL_STATE);
        //OTA升级握手成功
        intentFilter.addAction(OTAModel.OTA_START);
        //升级是否成功
        intentFilter.addAction(OTAModel.OTA_END);
        //歌曲列表
        intentFilter.addAction(MusicModel.MUSIC_LIST_NAME);
        intentFilter.addAction(BatteryModel.BATTERY_CONTENT);
        return intentFilter;
    }

    public final BroadcastReceiver mMainReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constant.DEVICE_CONNECT)) {
                //连接成功
                Log.d("chen", "DEVICE_CONNECT: 连接成功");
                currentState = STATE.DEV_CONNECTED;// = true;
                invalidateOptionsMenu();
                Message msg = Message.obtain();
                msg.what = 5;
                msg.obj = "已连接";
                mHandler.sendMessage(msg);
            } else if (action.equals(Constant.DEVICE_DISCONNECT)) {
                //连接断开或者失败
                Log.d("chen", "DEVICE_DISCONNECT: 连接失败");
                currentState = STATE.DEV_DISCONNECT;//mConnected = false;
                invalidateOptionsMenu();
                Message msg = Message.obtain();
                msg.what = 5;
                msg.obj = "已断开";
                mHandler.sendMessage(msg);
//                DeviceControlActivity.this.finish();
            } else if (action.equals(Constant.DEVICE_DISCOVERED)) {
                //发现服务完成后获取指令控制器
                Log.d("chen", "发现服务完成后获取指令控制器");
                builwinPowerControl = cwpower.getPowerControl();
                builwinPowerControl.getEquipmentInfo();
            } else if (action.equals(VolumeModel.VOLUME_LEVE)) {
                //接收当前设备的音量大小并设置UI
                Log.d("chen", "接收当前设备的音量大小并设置UI");
//                seekbar_volume.setProgress(intent.getIntExtra(VolumeModel.VOLUME_LEVE, 5));
            } else if (action.equals(VersionModel.DEVICE_INFO)) {
                Log.i("TAG", "发送设备信息");
//                initNoty();
                //接收设备信息后发送获取设备状态的指令
                builwinPowerControl.getState();
                HashMap<String, String> map = (HashMap<String, String>) intent.getSerializableExtra(VersionModel.DEVICE_INFO);
                StringBuilder builder = new StringBuilder();
                for (String key : map.keySet()) {
                    builder.append(key + ":" + map.get(key) + "\n");
                }
                Message msg = Message.obtain();
                msg.what = 6;
                msg.obj = "设备信息："+builder.toString();
                mHandler.sendMessage(msg);
            } else if (action.equals(MusicModel.MUSIC_MODEL_STATE)) {
                //接收音乐模式是否有开启，没有接入TF卡或者U盘，连接手机时返回0，否则返回1
                Message msg = Message.obtain();
                msg.what = 2;
                if (intent.getIntExtra(MusicModel.MUSIC_MODEL_STATE, 0) == 0){
                    msg.obj = "无TF卡";
                }else{
                    msg.obj = "有TF卡";
                }
                mHandler.sendMessage(msg);
            } else if (action.equals(AuxModel.AUX_MODEL_STATE)) {

            } else if (action.equals(OTAModel.OTA_START)) {


            } else if (action.equals(OTAModel.OTA_END)) {

            } else if (action.equals(MusicModel.MUSIC_LIST_NAME)) {

            } else if (action.equals(MainModel.CURRENT_STATE)) {
                //根据当前模式设置对应的UI，开发者可根据该广播打开对应模式的界面
                Message msg = Message.obtain();
                msg.what = 3;
                switch (intent.getIntExtra(MainModel.CURRENT_STATE, -1)) {
                    case 1:
                        msg.obj = "当前模式： FM";
                        break;
                    case 2:
                        msg.obj = "当前模式： aux";
                        break;
                    case 3:
                        msg.obj = "当前模式： music";
                        break;

                    default:
                        break;
                }
                mHandler.sendMessage(msg);
            } else if (action.equals(BatteryModel.BATTERY_CONTENT)) {
                //电池信息通知
                HashMap<String, String> map = (HashMap<String, String>) intent.getSerializableExtra(BatteryModel.BATTERY_CONTENT);
                Message msg = Message.obtain();
                msg.what = 4;
                msg.obj = "电池信息: \n"+ map.toString();
                mHandler.sendMessage(msg);
            }
        }

    };


    private void findDev() {
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            NoticeDialog noticeDialog = new NoticeDialog(MainActivity.this).setTitle("温馨提示").setMessage("该手机没有蓝牙功能");
            noticeDialog.setOnClickAlertListener(new NoticeDialog.OnClickAlertListener() {
                @Override
                public void cancle(AlertDialog dialog) {
                    dialog.dismiss();
                }

                @Override
                public void enter(AlertDialog dialog) {
                    dialog.dismiss();
                }
            });
            noticeDialog.show();
        } else {
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                NoticeDialog noticeDialog = new NoticeDialog(MainActivity.this).setTitle("温馨提示").setMessage("该手机未打开蓝牙，点击确定开启蓝牙!");
                noticeDialog.setOnClickAlertListener(new NoticeDialog.OnClickAlertListener() {
                    @Override
                    public void cancle(AlertDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void enter(AlertDialog dialog) {
                        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
                        dialog.dismiss();
                    }
                });
                noticeDialog.show();
            } else {
                ScanDev();
            }
        }
    }

    private BlueToothCallback blueToothCallbac = new BlueToothCallback() {

        @Override
        public void onCustomNotice(HashMap hashMap) {
            byte[] data  = (byte[]) hashMap.get("data");
            String str  = "";
            if(data!=null){
                for (int i = 0; i < data.length; i++) {
                    str+=data[i]+",";
                }
            }
            Log.i("chen","自定义通知名称："+hashMap.get("MethodName")+"数据:"+str);
        }

        @Override
        public void onBluetoothConnection(String s, String s1) {
            Message msg = Message.obtain();
            msg.what= 2;
            msg.obj = "Connection"+s+s1;
            mHandler.sendMessage(msg);
            //连接成功回调
            Intent intent = new Intent(Constant.DEVICE_CONNECT);
            sendBroadcast(intent);
        }

        @Override
        public void onBluetoothDisconnection(String s) {
            Message msg = Message.obtain();
            msg.what= 2;
            msg.obj = "Disconnection"+s;
            mHandler.sendMessage(msg);
            //连接失败或者连接断开回调
            Intent intent = new Intent(Constant.DEVICE_DISCONNECT);
            sendBroadcast(intent);

        }

        @Override
        public void onBluetoothScanDevice(final BluetoothDevice bluetoothDevice, int i, byte[] bytes) {

            if (listDialog == null || !listDialog.isShowing()) {
                listDialog = new ListDialog(MainActivity.this);
                listDialog.setOnClickAlertListener(new ListDialog.OnClicklistAlertListener() {
                    @Override
                    public void listAlertListItemClick(BluetoothDevice device) {
                        //停止扫描设备
//                        devicePowerControl.stopScan();

                        Message msg = Message.obtain();
                        msg.what = 0;
                        msg.obj = device;
                        mHandler.sendMessage(msg);
                        listDialog.dismiss();
                    }
                });
                listDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mHandler.sendEmptyMessage(1);
                    }
                });
                listDialog.show();
            }
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(proDialog != null && proDialog.isShowing()){
                        proDialog.dismiss();
                    }
                    //扫描到附近的设备
                    listDialog.setData(bluetoothDevice);
                }
            });
        }

        @Override
        public void onBluetoothSppBondedDevices(Set set) {
            final List<BluetoothDevice> list = new ArrayList<BluetoothDevice>(set);
            if(list != null && list.size() > 0) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //扫描到附近的设备
                        if (listDialog != null && !listDialog.isShowing())
                            listDialog.setData(list);
                    }
                });
            }
            Log.i("chen","已配对设备"+list.size());
        }

        @Override
        public void onServicesDiscovered() {
            //ble发现服务成功，spp连接稳定后回调
            Intent intent = new Intent(Constant.DEVICE_DISCOVERED);
            sendBroadcast(intent);
        }

        @Override
        public void onStartService() {
            Log.i("TAG", "Main蓝牙框架初始化");
            //蓝牙管理器初始化完成获取设备控制器
            devicePowerControl = cwpower.getDevicePowerControl();
            // Log.i("TAG",devicePowerControl.toString());
            if (!devicePowerControl.getIsBle()) {
                //获取已配对的设备列表
                devicePowerControl.getBluetoothSppBondedDevices();
            }
        }

        @Override
        public void onBluetoothConnect(BluetoothDevice bluetoothDevice) {

        }
    };

    private void ScanDev() {
        if (proDialog == null)
            proDialog = new ProDialog(this);
        proDialog.setMessage("搜索设备...");
        proDialog.show();
        Message msg = Message.obtain();
        msg.obj="开始搜索";
        msg.what=2;
        mHandler.sendMessage(msg);
        if (devicePowerControl != null) {
            devicePowerControl.stopScan();
            mBluetoothAdapter.startDiscovery();
            devicePowerControl.startScan(10000);
            if (!devicePowerControl.getIsBle()) {
                //获取已配对的设备列表
                devicePowerControl.getBluetoothSppBondedDevices();
            }
            mHandler.sendEmptyMessageDelayed(7, 10000);
        }
        /*new Thread() {
            @Override
            public void run() {
                if (mBluetoothAdapter.isEnabled()) {
                    Log.i("chen", "run: 开始搜索蓝牙");
                    mBluetoothAdapter.startDiscovery();
                }
            }
        }.start();*/
    }


    /**
     * 注册广播接收器
     *//*
    public void regesiterBroadcastReceiver() {
        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(_foundReceiver, foundFilter);
        IntentFilter finishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(_finishedReceiver, finishedFilter);
    }

    *//**
     * 注销广播接收器*//*

    public void unregisterBroadcastReceiver() {
        unregisterReceiver(_foundReceiver);
        unregisterReceiver(_finishedReceiver);
    }

     *//** 搜索蓝牙监听器，主要是监听 BluetoothDevice.ACTION_FOUND*//*

    private BroadcastReceiver _foundReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String mdeviceName = device.getName();
            if (mdeviceName != null){
                Log.i("huang", "搜索到的蓝牙：" + device.getName());
            }else{
                Log.i("huang", "搜索到的蓝牙：" + device.getAddress());
            }
                if (listDialog == null || !listDialog.isShowing()) {
                    listDialog = new ListDialog(MainActivity.this);
                    listDialog.setOnClickAlertListener(new ListDialog.OnClicklistAlertListener() {
                        @Override
                        public void listAlertListItemClick(BluetoothDevice device) {
                            mHandler.sendEmptyMessage(1);
                            devicePowerControl.stopScan();
                            stopSearch();
                            Message msg = Message.obtain();
                            msg.what = 0;
                            msg.obj = device;
                            mHandler.sendMessage(msg);
                            listDialog.dismiss();
                        }
                    });
                    listDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    });
                    listDialog.show();
                }

                listDialog.setData(device);
        }

    };

    private void stopSearch() {
        // 判断是否正在搜索蓝牙，如果是，则取消搜索
        if (mBluetoothAdapter.isDiscovering() == true)
            mBluetoothAdapter.cancelDiscovery();
    }

    *//**
     * 完成搜索监听器，监听 BluetoothAdapter.ACTION_DISCOVERY_FINISHED*//*

    private BroadcastReceiver _finishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSearch();
        }
    };*/
}
