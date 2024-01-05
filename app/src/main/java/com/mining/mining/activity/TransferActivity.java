package com.mining.mining.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivityTransferBinding;
import com.mining.mining.entity.MessageEvent;
import com.mining.mining.pager.my.MyPager;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.PayPass;

import org.greenrobot.eventbus.EventBus;


public class TransferActivity extends AppCompatActivity implements OnData, PayPass.OnPay, View.OnClickListener {
    private ActivityTransferBinding binding;
    private String pass;
    private final OnData onData = new OnData() {
        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(TransferActivity.this);
            JSONObject jsonObject = sharedUtil.getLogin(14, 1);
            jsonObject.put("uid", binding.uid.getText().toString());
            socketManage.print(jsonObject.toString());
        }

        @Override
        public void handle(String ds) {
            initUser(ds);
        }
    };

    private final OnData getUsdt = new OnData() {
        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            if (code == 200) {
                String usdt = jsonObject.getString("usdt");
                binding.userMoney.setText(getString(R.string.app_available_usdt, usdt));
            } else {
                String msg = jsonObject.getString("msg");
                Toast.makeText(TransferActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(TransferActivity.this);
            JSONObject jsonObject = sharedUtil.getLogin(5, 2);
            socketManage.print(jsonObject.toString());
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersive(this, true);
        binding = ActivityTransferBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initData();
        initToolbar();
        initView();
        SocketManage.init(getUsdt);
    }

    private void initData() {
        String id = getIntent().getStringExtra("id");
        if (id == null) {
            return;
        }
        binding.uid.setText(id);
        SocketManage.init(onData);
        String money = getIntent().getStringExtra("money");
        if (money == null) {
            return;
        }
        binding.money.setText(money);
    }

    private void initView() {
        binding.userInformation.setOnClickListener(this);
        binding.transfer.setOnClickListener(this);
    }

    private void initUser(String str) {
        JSONObject object = JSONObject.parseObject(str);
        int code = object.getInteger("code");
        if (code == 200) {
            JSONObject jsonObject = object.getJSONObject("data");
            String name = jsonObject.getString("name");
            String id = jsonObject.getString("id");
            binding.name.setText(name);
            binding.id.setText(id);
            binding.nameX.setText(StringUtil.getStringStart(name));
            return;
        }
        String msg = object.getString("msg");
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(TransferActivity.this);
        JSONObject jsonObject = sharedUtil.getLogin(14, 2);
        jsonObject.put("uid", binding.uid.getText().toString());
        jsonObject.put("pass", pass);
        jsonObject.put("money", binding.money.getText().toString());
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            EventBus.getDefault().post(new MessageEvent(MyPager.class));
        }
        String msg = jsonObject.getString("msg");
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onText(String pass) {
        this.pass = pass;
        SocketManage.init(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.user_information) {
            if (binding.uid.getText().length() == 0) {
                Toast.makeText(this, "转账账号不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            SocketManage.init(onData);
        } else if (v.getId() == R.id.transfer) {
            PayPass payPass = new PayPass(this);
            payPass.setMoney("消耗USDT:" + binding.money.getText().toString());
            payPass.setPay(this);
            payPass.show();
        }
    }
}
