package com.mining.mining.activity.c2s.usdt.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.bumptech.glide.Glide;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivityBuyDetailBinding;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.ImageDialog;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class BuyDetailActivity extends AppCompatActivity implements OnData, View.OnClickListener {
    private ActivityBuyDetailBinding binding;
    private String id;
    private int type;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersive(this, true);
        binding = ActivityBuyDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initIntent();
        initView();
    }

    private void initView() {
        binding.PaymentSuccessful.setVisibility(View.VISIBLE);
        binding.copy1.setOnClickListener(this);
        binding.copy2.setOnClickListener(this);
        binding.copy3.setOnClickListener(this);
        binding.copy5.setOnClickListener(this);
        binding.QRCode.setOnClickListener(this);
        binding.PaymentSuccessful.setOnClickListener(this);
    }

    private void initIntent() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        type = intent.getIntExtra("type", 1);
        if (id == null) {
            return;
        }
        SocketManage.init(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(24, 4);
        if (type == 2) {
            jsonObject = sharedUtil.getLogin(24, 14);
        }
        jsonObject.put("data_type", type);
        jsonObject.put("data_id", id);
        socketManage.print(jsonObject.toString());
        System.out.println(jsonObject);
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code != 200) {
            String msg = jsonObject.getString("msg");
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject data = jsonObject.getJSONObject("data");
        initJson(data);
    }

    private void initJson(JSONObject jsonObject) {
        //{"code":200,"data":{"Payment":{"code":"2958474980@qq.com","id":"2","image":"https://tingdao.cc/FuYLDRZMFnRhRYcTVpDR5ym6o9ai","time":"2023-11-29 21:41:16","user_id":"13"},"buy_user_id":"12","c2c_usdt_id":"1","card_name":"\u5c39\u6447\u52a8","id":"10","pay_type":"0","price":"6.00","rmb":"12.00","time":"2023-12-02 11:23:03","type":"0","usdt":"2","user_id":"13","user_name":"012345"},"msg":""}
        JSONObject Payment = jsonObject.getJSONObject("Payment");
        String pay_type = jsonObject.getString("pay_type");
        if (Payment != null) {
            String code = Payment.getString("code");
            String image = Payment.getString("image");
            binding.QRCode.setTag(image);
            binding.payName.setText(code);
            binding.copy3.setTag(code);
            if (pay_type.equals("2")) {
                String text = Payment.getString("text");
                binding.bankName.setText("银行卡类型");
                binding.QRCode.setText(text);
                binding.QRCode.setTag(text);
                binding.QRCode.setTextColor(Color.BLACK);
            }
        }
        String id = jsonObject.getString("id");
        String type = jsonObject.getString("type");
        binding.PaymentSuccessful.setTag(id);
        String name = jsonObject.getString("card_name");
        String rmb = jsonObject.getString("rmb");
        String time = jsonObject.getString("time");
        initTime(time);
        String pay_text = switch (pay_type) {
            case "1" -> "微信支付";
            case "2" -> "银行卡支付";
            default -> "支付宝支付";
        };
        String pay_text_1 = switch (pay_type) {
            case "1" -> "微信账号";
            case "2" -> "银行卡账号";
            default -> "支付宝账号";
        };
        binding.toolbar.setTitle(String.format("打开 %s 转账", pay_text));
        if (this.type == 2) {
            binding.pay.setVisibility(View.GONE);
            binding.bank.setVisibility(View.GONE);
            binding.PaymentSuccessful.setText("确认收款");
            binding.clionPay.setText("点击“确认收款“放币");
            binding.toolbar.setTitle("确认是否已经收款");
            if (type.equals("1")) {
                binding.PaymentSuccessful.setVisibility(View.VISIBLE);
            }
        }
        binding.ids.setText(id);
        binding.copy5.setTag(id);
        binding.cardName.setText(name);
        binding.copy2.setTag(name);
        binding.rmb.setText(rmb);
        binding.copy1.setTag(rmb);
        binding.payUser.setText(pay_text_1);
        binding.payType.setText(pay_text);
    }

    private void initTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(now, dateTime);
            long diff = duration.toMillis();

            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }

            if (diff <= 0) {
                return;
            }

            countDownTimer = new CountDownTimer(diff, 1_000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    long minutes = seconds / 60;
                    long remainingSeconds = seconds % 60;
                    String formattedTime = String.format(Locale.US, "%s:%s", minutes, remainingSeconds);
                    binding.time.setText(formattedTime);
                }

                @Override
                public void onFinish() {
                    binding.time.setText("订单已取消");
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.copy_1) {
            newPlainText((String) v.getTag());
        } else if (v.getId() == R.id.copy_2) {
            newPlainText((String) v.getTag());
        } else if (v.getId() == R.id.copy_3) {
            newPlainText((String) v.getTag());
        } else if (v.getId() == R.id.copy_5) {
            newPlainText((String) v.getTag());
        } else if (v.getId() == R.id.QRCode) {
            ImageDialog dialog = new ImageDialog(this);
            ImageView imageView = dialog.findViewById(R.id.image);
            Glide.with(this).load(v.getTag()).into(imageView);
            dialog.show();
        } else if (v.getId() == R.id.PaymentSuccessful) {
            SocketManage.init(new PaymentSuccessful((String) v.getTag(), this));
        }
    }

    private void newPlainText(String text) {
        // 获取剪贴板管理器
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建一个ClipData对象，包含要复制的内容
        ClipData clip = ClipData.newPlainText("label", text);
        // 将ClipData对象复制到剪贴板中
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
    }

    private class PaymentSuccessful implements OnData {
        private final String id;
        private final Context context;

        public PaymentSuccessful(String id, Context context) {
            this.id = id;
            this.context = context;
        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(context);
            JSONObject jsonObject = sharedUtil.getLogin(24, 5);
            if (type == 2) {
                jsonObject = sharedUtil.getLogin(24, 15);
            }
            jsonObject.put("data_type", type);
            jsonObject.put("data_id", id);
            socketManage.print(jsonObject.toString());
        }

        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            String msg = jsonObject.getString("msg");
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            int code = jsonObject.getInteger("code");
            if (code == 200) {
                finish();
            }
        }
    }
}
