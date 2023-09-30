package com.mining.mining.pager.mining.pager.adapter;

import android.content.Context;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mining.mining.databinding.ItemMiningBinding;
import com.mining.mining.entity.MiningDataEntity;
import com.mining.mining.entity.MiningEntity;
import com.mining.mining.pager.mining.pager.MiningItemPager;
import com.mining.util.Handler;
import com.mining.util.MessageEvent;
import com.mining.util.OnHandler;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.PayPass;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MiningAdapter extends RecyclerView.Adapter<MiningAdapter.ViewHolder> implements View.OnClickListener, OnData, OnHandler, PayPass.OnPay {
    private final Context context;
    private final List<MiningEntity> list;
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private final String tab_id;
    private String pass;
    private int position;
    private final MiningItemPager miningItemPager;
    private View mEmptyTextView;

    public MiningAdapter(Context context, List<MiningEntity> list, String tab_id, MiningItemPager miningItemPager) {
        this.context = context;
        this.list = list;
        this.tab_id = tab_id;
        this.miningItemPager = miningItemPager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemMiningBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MiningEntity mining = list.get(position);
        holder.binding.miningGem.setText(String.format(holder.binding.miningGem.getText().toString(), StringUtil.toRe(mining.getMining_gem())));
        holder.binding.minerUsdt.setText(String.format(holder.binding.minerUsdt.getText().toString(), StringUtil.toRe(mining.getMining_gem()), mining.getDay(), StringUtil.toRe(mining.getMoon_gem())));
        holder.binding.miningGem.setId(position);
        holder.binding.miningGem.setOnClickListener(this);
        if (mining.getMining() != null) {
            holder.binding.time.setVisibility(View.VISIBLE);
            MiningDataEntity entity = new Gson().fromJson(mining.getMining(), MiningDataEntity.class);

            if (entity.getIsSuperposition().equals("1")) {
                holder.binding.miningGem.setVisibility(View.GONE);
                holder.binding.text.setVisibility(View.VISIBLE);
            }

            holder.binding.miningLv.setText(String.format(holder.binding.miningLv.getText().toString(), entity.getMining_size()));
            holder.binding.miningGem1.setText(String.format(holder.binding.miningGem1.getText().toString(), StringUtil.toRe(entity.getMining_gem())));
            holder.binding.dayGem.setText(String.format(holder.binding.dayGem.getText().toString(), StringUtil.toRe(entity.getDay_gem())));
            holder.binding.time.setText(String.format(holder.binding.time.getText().toString(), entity.getTime()));
        } else {
            holder.binding.miningLv.setText(String.format(holder.binding.miningLv.getText().toString(), 0));
            holder.binding.dayGem.setText(String.format(holder.binding.dayGem.getText().toString(), StringUtil.toRe(mining.getDay_gem())));
            holder.binding.time.setVisibility(View.GONE);
            holder.binding.miningGem1.setText(String.format(holder.binding.miningGem1.getText().toString(), 0));
        }
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
    public void connect(SocketManage socketManage) {
        try {
            MiningEntity entity = list.get(position);
            String id = miningItemPager.getSharedPreferences().getString("id", null);
            String _key = miningItemPager.getSharedPreferences().getString("_key", null);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 8);
            jsonObject.put("code", 3);
            jsonObject.put("tab_id", tab_id);
            jsonObject.put("pass", pass);
            jsonObject.put("pit_id", entity.getId());
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
                JSONObject pit_data = jsonObject.getJSONObject("pit_data");
                handler.sendMessage(1, pit_data.toString());
                EventBus.getDefault().post(new MessageEvent(1, ""));
            }
            String msg = jsonObject.getString("msg");
            handler.sendMessage(0, msg);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 0) {
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        } else if (w == 1) {
            renew(str);
        }
    }

    private void renew(String text) {
        try {
            JSONObject pit_data = new JSONObject(text);
            MiningEntity mining = new Gson().fromJson(pit_data.toString(), MiningEntity.class);
            String _mining = pit_data.getString("mining");
            if (!_mining.equals("{}")) {
                if (_mining.length() != 0) {
                    mining.setMining(_mining);
                }
            }
            list.set(position, mining);
            notifyItemChanged(position);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClick(View v) {
        this.position = v.getId();
        MiningEntity mining = list.get(position);
        PayPass payPass = new PayPass(context);
        payPass.setMoney(StringUtil.toRe(mining.getMining_gem()) + "宝石");
        payPass.setPay(this);
        payPass.show();
    }

    @Override
    public void onText(String pass) {
        this.pass = pass;
        SocketManage.init(this);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemMiningBinding binding;

        public ViewHolder(@NonNull ItemMiningBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
