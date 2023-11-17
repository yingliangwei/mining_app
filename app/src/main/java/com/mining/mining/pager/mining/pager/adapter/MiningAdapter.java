package com.mining.mining.pager.mining.pager.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.mining.mining.R;
import com.mining.mining.databinding.ItemMiningBinding;
import com.mining.mining.entity.MessageEvent;
import com.mining.mining.entity.MiningDataEntity;
import com.mining.mining.entity.MiningEntity;
import com.mining.mining.pager.home.HomePager;
import com.mining.mining.pager.mining.MiningPager;
import com.mining.mining.pager.mining.rule.RuleActivity;
import com.mining.mining.pager.my.MyPager;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.PayPass;

import org.greenrobot.eventbus.EventBus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MiningAdapter extends RecyclerView.Adapter<MiningAdapter.ViewHolder> implements OnData, View.OnClickListener {
    private final Context context;
    private final List<MiningEntity> list;
    private View mEmptyTextView;

    public MiningAdapter(Context context, List<MiningEntity> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemMiningBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MiningEntity mining = list.get(position);
        holder.binding.title.setText(mining.getName());
        holder.binding.miningUpgradation.setTag(position);
        holder.binding.miningUpgradation.setOnClickListener(this);
        holder.binding.rule.setTag(position);
        holder.binding.rule.setOnClickListener(this);
        if (mining.getIs_permanent().equals("1")) {
            holder.binding.miningSize.setVisibility(View.VISIBLE);
            holder.binding.miningSize.setTag(position);
            holder.binding.miningSize.setOnClickListener(this);
        } else {
            holder.binding.miningSize.setVisibility(View.GONE);
        }
        if (mining.getIs_usdt().equals("0")) {
            holder.binding.bg.setBackground(ContextCompat.getDrawable(context, R.mipmap.bg_ape_firend_first));
            holder.binding.miningUpgradation.setText(String.format("(%s)宝石购买(%s/天)", StringUtil.toRe(mining.getMining_gem()), mining.getDay()));
        } else {
            if (mining.getIs_permanent().equals("0")) {
                holder.binding.bg.setBackground(ContextCompat.getDrawable(context, R.mipmap.bg_ape_primary_not_authentication1));
            } else {
                holder.binding.bg.setBackground(ContextCompat.getDrawable(context, R.mipmap.bg_ape_firend_first));
            }
            holder.binding.miningUpgradation.setText(String.format("(%s)USDT购买(%s/天)", StringUtil.toRe(mining.getMining_gem()), mining.getDay()));
        }
        if (mining.getIsMining() == 1) {
            initView(holder.binding, mining);
        } else {
            if (mining.getIs_permanent().equals("1")) {
                holder.binding.text.setVisibility(View.VISIBLE);
                holder.binding.text.setText("未拥有该矿池");
            }
        }
        if (mining.getIsCard() == 0) {
            holder.binding.text.setVisibility(View.VISIBLE);
            holder.binding.text.setText("未实名");
        }
    }

    private void initView(ItemMiningBinding binding, MiningEntity mining) {
        JSONObject jsonObject = JSONObject.parseObject(mining.get_mining());
        if (jsonObject == null) {
            return;
        }
        MiningDataEntity entity = new Gson().fromJson(jsonObject.toString(), MiningDataEntity.class);
        if (mining.getIs_permanent().equals("1")) {
            binding.miningSize.setText(String.format("挖矿(%s)", entity.getMining_remaining()));
            binding.gemTop.setVisibility(View.GONE);
        } else {
            binding.gemTop.setVisibility(View.VISIBLE);
        }
        binding.dayGem.setText(String.format("%s/天", StringUtil.toRe(entity.getDay_gem())));
        binding.miningLv.setText(String.format("矿洞等级Lv.%s", entity.getMining_size()));
        binding.miningGem1.setText(String.format("累计 %s", StringUtil.toRe(entity.getMining_gem())));
        if (entity.getIs_usdt().equals("0") && entity.getIs_permanent().equals("0")) {
            binding.miningUpgradation.setText(String.format("(%s)宝石升级叠加", StringUtil.toRe(mining.getMining_gem())));
        } else {
            binding.miningUpgradation.setText(String.format("(%s)USDT升级叠加", StringUtil.toRe(mining.getMining_gem())));
        }
        long day = getRemainingDays(entity.getTime());
        binding.minerDay.setText(String.format("距离过期:%s/天", day));
        binding.miningTop.setVisibility(View.VISIBLE);
        binding.lvTop.setVisibility(View.VISIBLE);

        if (Integer.parseInt(mining.getMining_size()) <= Integer.parseInt(entity.getSuperposition())) {
            binding.miningUpgradation.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mining_size) {
            int position = (int) v.getTag();
            SocketManage.init(this, position);
        } else if (v.getId() == R.id.mining_upgradation) {
            int position = (int) v.getTag();
            MiningEntity entity = list.get(position);
            PayPass payPass = new PayPass(context);
            if (entity.getIs_usdt().equals("0")) {
                payPass.setMoney("支付:" + StringUtil.toRe(entity.getMining_gem()) + "宝石");
            } else {
                payPass.setMoney("支付:" + StringUtil.toRe(entity.getMining_gem()) + "USDT");
            }
            if (entity.get_mining() != null) {
                MiningDataEntity miningData = new Gson().fromJson(entity.get_mining(), MiningDataEntity.class);
                if (miningData.getIs_permanent().equals("0")) {
                    payPass.setMoney("支付:" + StringUtil.toRe(entity.getMining_gem()) + "宝石");
                } else {
                    payPass.setMoney("支付:" + StringUtil.toRe(entity.getMining_gem()) + "USDT");
                }
            }
            payPass.setPay(new Pay(position));
            payPass.show();
        } else if (v.getId() == R.id.rule) {
            int position = (int) v.getTag();
            MiningEntity entity = list.get(position);
            Intent intent = new Intent(context, RuleActivity.class);
            intent.putExtra("id", entity.getTab_id());
            context.startActivity(intent);
        }
    }

    @Override
    public void connect(SocketManage socketManage) {
        MiningEntity entity = list.get(socketManage.getPosition());
        SharedUtil sharedUtil = new SharedUtil(context);
        JSONObject jsonObject = sharedUtil.getLogin(8, 4);
        jsonObject.put("pit_id", entity.getId());
        jsonObject.put("position", socketManage.getPosition());
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            int position = jsonObject.getInteger("position");
            JSONObject data = jsonObject.getJSONObject("data");
            MiningEntity entity = new Gson().fromJson(data.toString(), MiningEntity.class);
            JSONObject mining = data.getJSONObject("mining");
            if (mining != null) {
                entity.set_mining(mining.toString());
            }
            list.set(position, entity);
            notifyItemChanged(position);
        }
        String msg = jsonObject.getString("msg");
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
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

    public void setEmptyTextView(LinearLayout blank) {
        this.mEmptyTextView = blank;
    }

    private long getRemainingDays(String expirationDat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime expirationDate = LocalDateTime.parse(expirationDat, formatter);
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expirationDate)) {
            return 0;
        } else {
            return Duration.between(now, expirationDate).toDays();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemMiningBinding binding;

        public ViewHolder(@NonNull ItemMiningBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }

    private class Pay implements PayPass.OnPay {
        private final int position;

        public Pay(int position) {
            this.position = position;
        }

        @Override
        public void onText(String pass) {
            if (pass.length() == 0) {
                Toast.makeText(context, "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            SocketManage.init(new UpgradationData(pass), position);
        }
    }

    private class UpgradationData implements OnData {
        private final String pass;

        public UpgradationData(String pass) {
            this.pass = pass;
        }

        @Override
        public void connect(SocketManage socketManage) {
            MiningEntity entity = list.get(socketManage.getPosition());
            SharedUtil sharedUtil = new SharedUtil(context);
            JSONObject jsonObject = sharedUtil.getLogin(8, 5);
            jsonObject.put("pit_id", entity.getId());
            jsonObject.put("pass", pass);
            jsonObject.put("position", socketManage.getPosition());
            socketManage.print(jsonObject.toString());
        }

        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            if (code == 200) {
                int position = jsonObject.getInteger("position");
                JSONObject data = jsonObject.getJSONObject("data");
                MiningEntity entity = new Gson().fromJson(data.toString(), MiningEntity.class);
                JSONObject mining = data.getJSONObject("mining");
                if (mining != null) {
                    entity.set_mining(mining.toString());
                }
                list.set(position, entity);
                notifyItemChanged(position);
                //更新宝石和USDT数量
                EventBus.getDefault().post(new MessageEvent(1, MiningPager.class));
                EventBus.getDefault().post(new MessageEvent(MyPager.class));
                EventBus.getDefault().post(new MessageEvent(HomePager.class));
            }
            String msg = jsonObject.getString("msg");
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

}
