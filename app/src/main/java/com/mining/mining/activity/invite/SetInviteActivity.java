package com.mining.mining.activity.invite;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivityInviteSetBinding;
import com.mining.mining.entity.MessageEvent;
import com.mining.mining.pager.home.HomePager;
import com.mining.mining.pager.mining.MiningPager;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.greenrobot.eventbus.EventBus;

public class SetInviteActivity extends AppCompatActivity implements OnData, View.OnClickListener {
    private ActivityInviteSetBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityInviteSetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        initToolbar();
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initView() {
        binding.sava.setOnClickListener(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(11, 2);
        jsonObject.put("uid", binding.uid.getText().toString());
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        String msg = jsonObject.getString("msg");
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        if (code == 200) {
            //刷新矿池全部数据和宝石
            EventBus.getDefault().post(new MessageEvent(MiningPager.class));
            EventBus.getDefault().post(new MessageEvent(HomePager.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sava) {
            SocketManage.init(this);
        }
    }

}
