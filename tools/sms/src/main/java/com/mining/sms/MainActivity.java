package com.mining.sms;

import static android.os.Build.VERSION.SDK_INT;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.mining.sms.LongLink.OnData;
import com.mining.sms.LongLink.SocketManage;
import com.mining.sms.databinding.ActivityMainBinding;
import com.mining.sms.service.FirstService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends Activity implements OnData {
    private ActivityMainBinding binding;
    private SocketManage socketManage;
    private boolean is = true;

    private final Runnable countDownTimer = new Runnable() {
        @Override
        public void run() {
            while (is) {
                try {
                    socketManage.print("ping");
                    Thread.sleep(15_000);
                } catch (InterruptedException e) {
                    e.fillInStackTrace();
                }
            }
        }
    };
    private SMSVerification smsVerification;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        EventBus.getDefault().register(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initPermission(this);
        initSocket();
        initSMSVerification();

    }

    private void initSMSVerification() {
        smsVerification = new SMSVerification();
        IntentFilter mFilter01 = new IntentFilter("SMS_SEND_ACTIOIN");
        registerReceiver(smsVerification, mFilter01);
    }


    public class SMSVerification extends BroadcastReceiver {
        /*
               public static final int RESULT_ERROR_GENERIC_FAILURE   表示普通错误，值为1(0x00000001)
               public static final int RESULT_ERROR_NO_SERVICE    表示服务当前不可用，值为4 (0x00000004)
               public static final int RESULT_ERROR_NULL_PDU   表示没有提供pdu，值为3 (0x00000003)
               public static final int RESULT_ERROR_RADIO_OFF   表示无线广播被明确地关闭，值为2 (0x00000002)
               public static final int STATUS_ON_ICC_FREE    表示自由空间，值为0 (0x00000000)
               public static final int STATUS_ON_ICC_READ  表示接收且已读，值为1 (0x00000001)
               public static final int STATUS_ON_ICC_SENT   表示存储且已发送，值为5 (0x00000005)
               public static final int STATUS_ON_ICC_UNREAD  表示接收但未读，值为3 (0x00000003)
               public static final int STATUS_ON_ICC_UNSENT  表示存储但为发送，值为7 (0x00000007)*/
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    EventBus.getDefault().post("发送短信成功");
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    EventBus.getDefault().post("发送短信失败");
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    EventBus.getDefault().post("服务当前不可用");
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    EventBus.getDefault().post("没有提供pdu");
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    EventBus.getDefault().post("无线广播被明确地关闭");
                    break;
                case SmsManager.STATUS_ON_ICC_FREE:
                    EventBus.getDefault().post("自由空间");
                    break;
                case SmsManager.STATUS_ON_ICC_SENT:
                    EventBus.getDefault().post("存储且已发送");
                    break;
                case SmsManager.STATUS_ON_ICC_UNSENT:
                    EventBus.getDefault().post("存储但为发送");
                    break;
            }
            EventBus.getDefault().post("发送状态：" + getResultCode());
            System.out.println(getResultCode());
        }
    }

    @Override
    public void connect(SocketManage socketManage) {
        is = true;
        EventBus.getDefault().post("连接成功");
        new Thread(countDownTimer).start();
    }

    @Override
    public void handle(String ds) {
        if (ds.equals("pong")) {
            EventBus.getDefault().post("pong");
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(ds);
            int type = jsonObject.getInt("type");
            if (type == 0) {
                //心跳
                return;
            }
            if (type == 1) {
                EventBus.getDefault().post(jsonObject.toString());
                //发送短信命令
                String phone = jsonObject.getString("phone");
                String message = jsonObject.getString("message");
                sendSMSS(this, phone, message);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }


    /**
     * 发送短信
     *
     * @param content 上下文
     * @param phone   目的号码
     * @param context 内容
     */
    private void sendSMSS(Context content, String phone, String context) {
        if (context.isEmpty() || phone.isEmpty() || socketManage == null) {
            return;
        }
        SmsManager manager = SmsManager.getDefault();
        Intent itSend = new Intent("SMS_SEND_ACTIOIN");
        PendingIntent mSendPI = PendingIntent.getBroadcast(content, (int) System.currentTimeMillis(), itSend, PendingIntent.FLAG_UPDATE_CURRENT);
        if (context.length() > 70) {
            List<String> msgs = manager.divideMessage(context);
            for (String msg : msgs) {
                manager.sendTextMessage(phone, null, msg, mSendPI, null);
            }
            return;
        }
        manager.sendTextMessage(phone, null, context, mSendPI, null);
    }

    @Override
    public void error(String error) {
        is = false;
        EventBus.getDefault().post("断开连接，5秒后重连");
        try {
            Thread.sleep(5_000);
            initSocket();
        } catch (InterruptedException e) {
            e.fillInStackTrace();
        }
    }


    private void initSocket() {
        socketManage = new SocketManage();
        socketManage.setData(this);
        socketManage.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        is = false;
        EventBus.getDefault().unregister(this);
        unregisterReceiver(smsVerification);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(String message) {
        binding.text.append(message + "\n");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == 0) {
            //解决默认短信
            initPermission(this);
        } else if (requestCode == 10 && resultCode == -1) {
            ignoreBatteryOptimization(this);
        } else if (requestCode == 11 && resultCode == 0) {
            //再次申请电池优化
            ignoreBatteryOptimization(this);
        }
    }

    /**
     * 忽略电池优化
     */
    void ignoreBatteryOptimization(Activity activity) {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean hasIgnored;
        hasIgnored = powerManager.isIgnoringBatteryOptimizations(activity.getPackageName());
        //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
        if (!hasIgnored) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            startActivityForResult(intent, 11);
        }
    }

    //默认短信
    void initPermission(Activity activity) {
        String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);
        //获取手机当前设置的默认短信应用的包名
        String packageName = activity.getPackageName();
        if (defaultSmsApp == null) {
            System.out.println("defaultSmsApp null");
            return;
        }
        if (!defaultSmsApp.equals(packageName)) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName);
            startActivityForResult(intent, 10);
        }
    }
}
