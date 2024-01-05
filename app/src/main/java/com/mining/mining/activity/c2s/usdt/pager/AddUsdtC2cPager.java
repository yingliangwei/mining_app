package com.mining.mining.activity.c2s.usdt.pager;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.activity.c2s.usdt.activity.OrderManagementActivity;
import com.mining.mining.activity.c2s.usdt.activity.RootManageActivity;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerC2cAddUsdtBinding;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

public class AddUsdtC2cPager extends RecyclerAdapter implements OnData, Toolbar.OnMenuItemClickListener {
    private PagerC2cAddUsdtBinding binding;
    private final Activity activity;

    public AddUsdtC2cPager(Activity context) {
        super(context);
        activity = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerC2cAddUsdtBinding.inflate(LayoutInflater.from(getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        initToolbar();
        SocketManage.init(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> activity.finish());
        binding.toolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(getContext());
        JSONObject jsonObject = sharedUtil.getLogin(24, 6);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code != 200) {
            String msg = jsonObject.getString("msg");
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject buy = jsonObject.getJSONObject("buy");
        JSONObject sell = jsonObject.getJSONObject("sell");
        if (buy != null) {
            initBuy(buy);
        }
        if (sell != null) {
            initSell(sell);
        }
    }

    private void initBuy(JSONObject buy) {
        String usdt = buy.getString("usdt");
        String min = buy.getString("min");
        binding.buyUsdt.setText(min);
        binding.usdt1.setText(StringUtil.toRe(usdt));
    }

    private void initSell(JSONObject sell) {
        String usdt = sell.getString("usdt");
        String min = sell.getString("min");
        binding.sellUsdt.setText(min);
        binding.usdt.setText(StringUtil.toRe(usdt));
    }

    @Override
    public boolean onMenuItemClick(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.manage) {
            Intent intent = new Intent(getContext(), OrderManagementActivity.class);
            intent.putExtra("data_type", "1");
            getContext().startActivity(intent);
        } else if (item.getItemId() == R.id.order) {
            getContext().startActivity(new Intent(getContext(), RootManageActivity.class));
        }
        return false;
    }
}
