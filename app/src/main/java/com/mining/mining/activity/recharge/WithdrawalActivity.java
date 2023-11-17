package com.mining.mining.activity.recharge;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivityWithdrawalBinding;
import com.mining.mining.entity.MessageEvent;
import com.mining.mining.pager.my.MyPager;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.greenrobot.eventbus.EventBus;


public class WithdrawalActivity extends AppCompatActivity implements OnData, View.OnClickListener, Toolbar.OnMenuItemClickListener {
    private ActivityWithdrawalBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityWithdrawalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
        SocketManage.init(this);
    }

    private void initView() {
        binding.recharge.setOnClickListener(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.toolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(21, 2);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            String usdt = jsonObject.getString("usdt");
            binding.userMoney.setText(StringUtil.toRe(usdt));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.recharge) {
            if (binding.usdt.getText().toString().equals("") || binding.name.getText().toString().equals("")) {
                Toast.makeText(this, "信息错误", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                double usdt = Double.parseDouble(binding.usdt.getText().toString());
                if (usdt < 1) {
                    Toast.makeText(this, "提现金额不能少于1USDT", Toast.LENGTH_SHORT).show();
                    return;
                }
                double _usdt = Double.parseDouble(binding.userMoney.getText().toString());
                if (_usdt < usdt) {
                    Toast.makeText(this, "提现金额不能大于可提现金额", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                e.fillInStackTrace();
                Toast.makeText(this, "金额错误", Toast.LENGTH_SHORT).show();
                return;
            }
            com.xframe.widget.PayPass payPass = new com.xframe.widget.PayPass(this);
            payPass.setMoney("支出USDT:" + binding.usdt.getText().toString());
            payPass.setPay(new PayPass());
            payPass.show();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.log) {
            startActivity(new Intent(this, WithdrawalLogActivity.class));
        }
        return false;
    }

    private class PayPass implements com.xframe.widget.PayPass.OnPay {
        @Override
        public void onText(String pass) {
            SocketManage.init(new Post(pass));
        }
    }

    private class Post implements OnData {
        private final String pass;

        public Post(String pass) {
            this.pass = pass;
        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(WithdrawalActivity.this);
            JSONObject jsonObject = sharedUtil.getLogin(21, 1);
            jsonObject.put("pass", pass);
            jsonObject.put("name", binding.name.getText().toString());
            jsonObject.put("usdt", binding.usdt.getText().toString());
            socketManage.print(jsonObject.toString());
        }

        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            if (code == 200) {
                EventBus.getDefault().post(new MessageEvent(MyPager.class));
                SocketManage.init(WithdrawalActivity.this);
            }
            String msg = jsonObject.getString("msg");
            Toast.makeText(WithdrawalActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
