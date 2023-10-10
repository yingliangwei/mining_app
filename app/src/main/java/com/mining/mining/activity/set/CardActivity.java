package com.mining.mining.activity.set;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivityCardBinding;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;


public class CardActivity extends AppCompatActivity implements OnData, View.OnClickListener {
    private ActivityCardBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersive(this, true);
        binding = ActivityCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initView() {
        binding.name.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        binding.post.setOnClickListener(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        try {
            SharedUtil sharedUtil = new SharedUtil(this);
            JSONObject jsonObject = sharedUtil.getLogin(18, 1);
            jsonObject.put("name", binding.name.getText().toString());
            jsonObject.put("card", binding.card.getText().toString());
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handle(String ds) {
        try {
            JSONObject jsonObject =  JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            String msg = jsonObject.getString("msg");
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.post) {
            if (binding.name.getText().length() < 1) {
                Toast.makeText(this, "请输入正确姓名", Toast.LENGTH_SHORT).show();
                return;
            }
            if (binding.card.getText().length() != 18) {
                Toast.makeText(this, "请输入正确身份证", Toast.LENGTH_SHORT).show();
                return;
            }
            SocketManage.init(this);
        }
    }
}
