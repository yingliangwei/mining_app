package com.mining.mining.activity.set;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.databinding.ActivityModifyNameBinding;
import com.mining.mining.entity.MessageEvent;
import com.mining.mining.pager.my.MyPager;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.greenrobot.eventbus.EventBus;


public class ModifyNameActivity extends AppCompatActivity implements OnData, View.OnClickListener {
    private ActivityModifyNameBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityModifyNameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
        String name = getIntent().getStringExtra("name");
        if (name != null) {
            binding.name.setText(name);
        }
    }

    private void initView() {
        binding.sava.setOnClickListener(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(7, 2);
        jsonObject.put("name", binding.name.getText().toString());
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        String msg = jsonObject.getString("msg");
        if (code == 202) {
            LoginActivity.login(this);
        } else if (code == 200) {
            EventBus.getDefault().post(new MessageEvent(MyPager.class));
            finish();
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sava) {
            String name = binding.name.getText().toString();
            if (name.length() == 0) {
                Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            SocketManage.init(this);
        }
    }
}
