package com.mining.mining.activity.set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.activity.LogOffActivity;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.databinding.ActivitySetUserBinding;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.entity.RecyclerEntity;
import com.xframe.widget.recycler.OnRecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class SetUserActivity extends AppCompatActivity implements OnRecyclerItemClickListener, OnData {
    private ActivitySetUserBinding binding;
    private final List<List<RecyclerEntity>> entity = new ArrayList<>();
    private final List<RecyclerEntity> entities = new ArrayList<>();
    private final List<RecyclerEntity> entities3 = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivitySetUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initRecycler();
        SocketManage.init(this);
    }

    private void initRecycler() {
        entities.add(new RecyclerEntity(R.mipmap.nick, "昵称", 0, "未设置", "", "name"));
        entities.add(new RecyclerEntity(R.mipmap.phone, "绑定手机", 0, "未绑定", "", "phone", true));

        entities3.add(new RecyclerEntity(R.mipmap.pass, "修改支付密码", 0, "", "", "pay"));
        entities3.add(new RecyclerEntity(R.mipmap.card, "实名制", 0, "未实名", "", "card"));
        entities3.add(new RecyclerEntity(R.mipmap.code, "收款设置", 0, "", "", "code"));

        List<RecyclerEntity> entities1 = new ArrayList<>();
        entities1.add(new RecyclerEntity(R.mipmap.log_off, "注销账号", 0, "", "", "log_off"));
        entities1.add(new RecyclerEntity(R.mipmap.complaint, "关于", 0, "", "", "g"));

        List<RecyclerEntity> entities2 = new ArrayList<>();
        entities2.add(new RecyclerEntity(R.mipmap.back, "退出登录", 0, "", "", "back"));

        entity.add(entities);
        entity.add(entities3);
        entity.add(entities1);
        entity.add(entities2);
        binding.recycle.setOnRecyclerItemClickListener(this);
        binding.recycle.add(entity);
        binding.recycle.notifyDataSetChanged();
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void onItemClick(RecyclerEntity entity, int position) {
        if (entity.getKey() == null) {
            return;
        }
        switch (entity.getKey()) {
            case "name" -> {
                Intent intent = new Intent(this, ModifyNameActivity.class);
                intent.putExtra("name", entity.text);
                startActivity(intent);
            }
            case "pay" -> startActivity(new Intent(this, SetPayPassActivity.class));
            case "back" -> {
                SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            case "g" -> startActivity(new Intent(this, AboutActivity.class));
            case "card" -> {
                if (entity.isArray) {
                    return;
                }
                startActivity(new Intent(this, CardActivity.class));
            }
            case "log_off" -> startActivity(new Intent(this, LogOffActivity.class));
            case "code" -> startActivity(new Intent(this, SetPaymentActivity.class));
        }
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(7, 1);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            JSONObject data = jsonObject.getJSONObject("data");
            initData(data);
        }
    }


    private void initData(JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        String phone = jsonObject.getString("phone");
        int card = jsonObject.getInteger("card");
        entities.clear();
        entities3.clear();
        entities.add(new RecyclerEntity(R.mipmap.nick, "昵称", 0, name, "", "name"));
        entities.add(new RecyclerEntity(R.mipmap.phone, "绑定手机", 0, phone, "", "phone", true));
        entities3.add(new RecyclerEntity(R.mipmap.pass, "修改支付密码", 0, "", "", "pay"));
        if (card == 1) {
            entities3.add(new RecyclerEntity(R.mipmap.card, "实名制", 0, "以实名", "", "card", true));
        } else {
            entities3.add(new RecyclerEntity(R.mipmap.card, "实名制", 0, "未实名", "", "card"));
        }
        entities3.add(new RecyclerEntity(R.mipmap.code, "收款设置", 0, "", "", "code"));
        binding.recycle.notifyDataSetChanged();
    }
}
