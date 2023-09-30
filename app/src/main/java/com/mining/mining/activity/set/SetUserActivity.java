package com.mining.mining.activity.set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mining.mining.R;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.databinding.ActivitySetUserBinding;
import com.mining.util.Handler;
import com.mining.util.OnHandler;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.entity.RecyclerEntity;
import com.xframe.widget.recycler.OnRecyclerItemClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SetUserActivity extends AppCompatActivity implements OnRecyclerItemClickListener, OnHandler, OnData, View.OnClickListener {
    private ActivitySetUserBinding binding;
    private final List<List<RecyclerEntity>> entity = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private SharedPreferences sharedPreferences;
    private final List<RecyclerEntity> entities = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivitySetUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initRecycler();
        initView();
        SocketManage.init(this);
    }

    private void initView() {
        binding.back.setOnClickListener(this);
    }


    private void initRecycler() {
        entities.add(new RecyclerEntity("昵称", 0, "未设置", "", "name"));
        entities.add(new RecyclerEntity("绑定手机", 0, "未绑定", "", "phone"));
        entities.add(new RecyclerEntity("绑定欧易UID", 0, "未绑定", "", "uid"));
        entities.add(new RecyclerEntity("修改支付密码",  0, "", "", "pay"));
        List<RecyclerEntity> entities1 = new ArrayList<>();
        entities1.add(new RecyclerEntity("建议和反馈", 0, "", "", "j"));
        entities1.add(new RecyclerEntity("关于", 0, "", "", "g"));
        entity.add(entities);
        entity.add(entities1);
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
            case "name":
            case "uid": {
                Intent intent = new Intent(this, ModifyNameActivity.class);
                intent.putExtra("name", entity.text);
                startActivity(intent);
                break;
            }
            case "pay":
                startActivity(new Intent(this, SetPayPassActivity.class));
                break;
        }
    }

    @Override
    public void connect(SocketManage socketManage) {
        String id = sharedPreferences.getString("id", null);
        String _key = sharedPreferences.getString("_key", null);
        if (id == null || _key == null) {
            LoginActivity.login(SetUserActivity.this);
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 7);
            jsonObject.put("code", 1);
            jsonObject.put("id", id);
            jsonObject.put("_key", _key);
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handle(String ds) {
        try {
            JSONObject jsonObject = new JSONObject(ds);
            int code = jsonObject.getInt("code");
            if (code == 200) {
                JSONObject data = jsonObject.getJSONObject("data");
                handler.sendMessage(1, data.toString());
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 0) {
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        } else if (w == 1) {
            try {
                JSONObject jsonObject = new JSONObject(str);
                initData(jsonObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void initData(JSONObject jsonObject) throws JSONException {
        String name = jsonObject.getString("name");
        String phone = jsonObject.getString("phone");
        String uid = jsonObject.getString("uid");
        entities.clear();
        entities.add(new RecyclerEntity("昵称", 0, name, "", "name"));
        entities.add(new RecyclerEntity("绑定手机", 0, phone, "", "phone"));
        if (!uid.equals("")) {
            entities.add(new RecyclerEntity("绑定欧易UID", 0, uid, "", "uid"));
        } else {
            entities.add(new RecyclerEntity("绑定欧易UID", 0, "未绑定", "", "uid"));
        }
        binding.recycle.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            sharedPreferences.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
