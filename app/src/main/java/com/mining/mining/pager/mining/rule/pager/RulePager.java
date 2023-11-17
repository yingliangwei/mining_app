package com.mining.mining.pager.mining.rule.pager;

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
import com.mining.mining.databinding.PagerRuleBinding;
import com.mining.mining.entity.RuleEntity;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.util.SharedUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import java.util.ArrayList;
import java.util.List;

public class RulePager extends RecyclerAdapter implements OnData {
    private final String mining_tab_id;
    private PagerRuleBinding binding;
    private final List<RuleEntity> list = new ArrayList<>();
    private RuleAdapter ruleAdapter;

    public RulePager(Context context, String mining_tab_id) {
        super(context);
        this.mining_tab_id = mining_tab_id;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerRuleBinding.inflate(LayoutInflater.from(getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        initRecycler();
        SocketManage.init(this);
    }

    private void initRecycler() {
        ruleAdapter = new RuleAdapter(getContext(), list);
        binding.recycle.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycle.setAdapter(ruleAdapter);
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(getContext());
        JSONObject jsonObject = sharedUtil.getLogin(8, 6);
        jsonObject.put("mining_tab_id", mining_tab_id);
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
                JSONObject rule = data.getJSONObject(i);
                RuleEntity entity = new Gson().fromJson(rule.toString(), RuleEntity.class);
                list.add(entity);
                ruleAdapter.notifyItemChanged(list.size()-1);
            }
        }
    }
}
