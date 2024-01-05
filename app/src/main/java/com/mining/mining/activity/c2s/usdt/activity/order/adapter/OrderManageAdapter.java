package com.mining.mining.activity.c2s.usdt.activity.order.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.databinding.ItemOrderManageBinding;
import com.mining.mining.entity.C2cEntity;
import com.mining.mining.entity.C2cUsdtEntity;
import com.mining.mining.entity.MessageEvent;
import com.mining.mining.pager.home.HomePager;
import com.mining.mining.pager.mining.MiningPager;
import com.mining.mining.pager.my.MyPager;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class OrderManageAdapter extends RecyclerView.Adapter<OrderManageAdapter.ViewHolder> implements View.OnClickListener, OnData {
    public final List<C2cUsdtEntity> list;
    private final Context context;
    private String c2c_id = "0";
    private final int code;
    private View mEmptyTextView;
    private int position;

    public OrderManageAdapter(Context context, List<C2cUsdtEntity> list, int code) {
        this.list = list;
        this.code = code;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemOrderManageBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        C2cUsdtEntity entity = list.get(position);
        holder.binding.gem1.setText(R.string.usdt);
        holder.binding.usdt1.setText(R.string.rmb);
        holder.binding.gem.setText(StringUtil.toRe(entity.getUsdt()));
        holder.binding.usdt.setText(StringUtil.toRe(entity.getPrice()));
        holder.binding.name.setText(entity.getName());
        holder.binding.nameX.setText(StringUtil.getStringStart(entity.getName()));
        holder.binding.exit.setTag(position);
        holder.binding.exit.setOnClickListener(this);
    }

    public void setEmptyTextView(View emptyTextView) {
        mEmptyTextView = emptyTextView;
    }

    @Override
    public int getItemCount() {
        if (list.size() != 0 && mEmptyTextView != null) {
            mEmptyTextView.setVisibility(View.GONE);
        } else if (mEmptyTextView != null) {
            mEmptyTextView.setVisibility(View.VISIBLE);
        }
        return list.size();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.exit) {
            int position = (int) v.getTag();
            this.position = position;
            C2cUsdtEntity entity = list.get(position);
            c2c_id = entity.getId();
            SocketManage.init(this);
        }
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(context);
        JSONObject jsonObject = sharedUtil.getLogin(4, 13);
        jsonObject.put("c2c_id", c2c_id);
        jsonObject.put("is", code);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        String msg = jsonObject.getString("msg");
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            list.remove(position);
            notifyItemRemoved(position);
            EventBus.getDefault().post(new MessageEvent(HomePager.class));
            EventBus.getDefault().post(new MessageEvent(1, MiningPager.class));
            EventBus.getDefault().post(new MessageEvent(MyPager.class));
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemOrderManageBinding binding;

        public ViewHolder(@NonNull ItemOrderManageBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
