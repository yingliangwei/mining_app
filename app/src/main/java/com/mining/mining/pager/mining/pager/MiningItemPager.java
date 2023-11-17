package com.mining.mining.pager.mining.pager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerItemMiningBinding;
import com.mining.mining.entity.MiningEntity;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.pager.mining.pager.adapter.MiningAdapter;
import com.mining.mining.util.SharedUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import java.util.ArrayList;
import java.util.List;

public class MiningItemPager extends RecyclerAdapter implements OnData {
    private PagerItemMiningBinding binding;
    private final Context context;
    private final String mining_id;
    private MiningAdapter miningAdapter;
    private final List<MiningEntity> list = new ArrayList<>();

    public MiningItemPager(Context context, String mining_id) {
        super(context);
        this.context = context;
        this.mining_id = mining_id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerItemMiningBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        initRecycler();
        SocketManage.init(this);
    }


    private void initRecycler() {
        miningAdapter = new MiningAdapter(context, list);
        if (binding != null) {
            miningAdapter.setEmptyTextView(binding.blank);
            binding.recycle.setLayoutManager(new LinearLayoutManager(context));
            binding.recycle.setAdapter(miningAdapter);
        }
    }


    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(context);
        JSONObject jsonObject = sharedUtil.getLogin(8, 3);
        jsonObject.put("tab_id", mining_id);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            JSONArray data = jsonObject.getJSONArray("data");
            if (data == null) {
                return;
            }
            for (int i = 0; i < data.size(); i++) {
                JSONObject jsonObject1 = data.getJSONObject(i);
                MiningEntity entity = new Gson().fromJson(jsonObject1.toString(), MiningEntity.class);
                JSONObject mining = jsonObject1.getJSONObject("mining");
                if (mining != null) {
                    entity.set_mining(mining.toString());
                }
                list.add(entity);
                miningAdapter.notifyItemChanged(list.size() - 1);
            }
        }
    }

}
