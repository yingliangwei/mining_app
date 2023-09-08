package com.mining.mining.pager.c2c;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerItemC2cBinding;
import com.mining.mining.entity.C2cEntity;
import com.mining.mining.pager.c2c.adapter.C2cAdapter;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.util.Handler;
import com.mining.mining.util.OnHandler;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class C2cGemPager extends RecyclerAdapter implements OnData, OnHandler {
    private PagerItemC2cBinding binding;
    private final Activity activity;
    private C2cAdapter c2cAdapter;
    private final List<C2cEntity> list = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private final int type;

    public C2cGemPager(Activity activity, int type) {
        this.activity = activity;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerItemC2cBinding.inflate(LayoutInflater.from(activity), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        initRecycler();
        SocketManage.init(this);
    }

    private void initRecycler() {
        c2cAdapter = new C2cAdapter(list, activity, type);
        binding.recycle.setLayoutManager(new LinearLayoutManager(activity));
        binding.recycle.setAdapter(c2cAdapter);
    }

    @Override
    public void connect(SocketManage socketManage) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 4);
            jsonObject.put("code", type);
            System.out.println(jsonObject);
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 1) {
            try {
                JSONArray array = new JSONArray(str);
                initRecyclerData(array);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initRecyclerData(JSONArray data) throws Exception {
        System.out.println(data);
        for (int i = 0; i < data.length(); i++) {
            String text = data.getString(i);
            C2cEntity entity = new Gson().fromJson(text, C2cEntity.class);
            list.add(entity);
        }
        binding.recycle.post(() -> c2cAdapter.notifyDataSetChanged());
    }

    @Override
    public void handle(String ds) {
        System.out.println(ds);
        try {
            JSONObject jsonObject = new JSONObject(ds);
            int code = jsonObject.getInt("code");
            if (code == 200) {
                JSONArray data = jsonObject.getJSONArray("data");
                handler.handleMessage(1, data.toString());
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }
}
