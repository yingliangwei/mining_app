package com.mining.mining.activity.set;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivitySetPayPassBinding;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;


public class SetPayPassActivity extends AppCompatActivity implements OnData, View.OnClickListener {
    private ActivitySetPayPassBinding binding;
    private final OnData onData = new OnData() {
        @Override
        public void handle(String ds) {
            try {
                JSONObject jsonObject = JSONObject.parseObject(ds);
                String msg = jsonObject.getString("msg");
                Toast.makeText(SetPayPassActivity.this, msg, Toast.LENGTH_SHORT).show();
                int code = jsonObject.getInteger("code");
                if (code == 200) {
                    finish();
                }
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }

        @Override
        public void connect(SocketManage socketManage) {
            try {
                SharedUtil sharedUtil = new SharedUtil(SetPayPassActivity.this);
                JSONObject jsonObject = sharedUtil.getLogin(7, 5);
                jsonObject.put("_pass", binding.pass1.getText().toString());
                jsonObject.put("pass", binding.pass.getText().toString());
                socketManage.print(jsonObject.toString());
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersive(this, true);
        binding = ActivitySetPayPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
        SocketManage.init(this);
    }

    private void initView() {
        binding.post.setOnClickListener(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }


    @Override
    public void handle(String ds) {
        try {
            JSONObject jsonObject =  JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            if (code == 200) {
                String is = jsonObject.getString("is");
                if (is.equals("0")) {
                    binding.passL.setVisibility(View.GONE);
                    binding.post.setText("设置密码");
                } else {
                    binding.passL.setVisibility(View.VISIBLE);
                    binding.post.setText("修改密码");
                }
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }


    @Override
    public void connect(SocketManage socketManage) {
        try {
            SharedUtil sharedUtil = new SharedUtil(this);
            JSONObject jsonObject = sharedUtil.getLogin(7, 4);
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.post) {
            SocketManage.init(onData);
        }
    }
}
