package com.mining.mining.pager.my.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.databinding.ItemExamineBinding;
import com.mining.mining.entity.WithdrawalEntity;
import com.mining.mining.pager.my.activity.USDTBillActivity;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import java.util.List;

public class ExamineAdapter extends RecyclerView.Adapter<ExamineAdapter.ViewHolder> implements View.OnClickListener {
    private final Context context;
    private final List<WithdrawalEntity> entities;
    private String text;

    public ExamineAdapter(Context context, List<WithdrawalEntity> entities) {
        this.context = context;
        this.entities = entities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemExamineBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WithdrawalEntity entity = entities.get(position);
        JSONObject user = entity.getUser();
        if (user != null) {
            String name = user.getString("name");
            String id = user.getString("id");
            holder.binding.name.setText(name);
            holder.binding.nameX.setText(StringUtil.getStringStart(name));
            holder.binding.id.setText(id);
        }
        holder.binding.usdt.setText(StringUtil.toRe(entity.getUsdt()));
        holder.binding.address.setText(entity.getAddress());
        holder.binding.no.setTag(position);
        holder.binding.no.setOnClickListener(v -> {
            ExamineAdapter.this.text = holder.binding.text.getText().toString();
            ExamineAdapter.this.onClick(v);
        });
        holder.binding.yes.setTag(position);
        holder.binding.yes.setOnClickListener(v -> {
            ExamineAdapter.this.text = holder.binding.text.getText().toString();
            ExamineAdapter.this.onClick(v);
        });
        holder.binding.ok.setTag(position);
        holder.binding.ok.setOnClickListener(v -> {
            ExamineAdapter.this.text = holder.binding.text.getText().toString();
            ExamineAdapter.this.onClick(v);
        });
        holder.binding.billId.setTag(position);
        holder.binding.billId.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (v.getId() == R.id.yes) {
            SocketManage.init(new Withdrawal(position, "1"));
        } else if (v.getId() == R.id.no) {
            SocketManage.init(new Withdrawal(position, "2"));
        } else if (v.getId() == R.id.ok) {
            SocketManage.init(new Withdrawal(position, "3"));
        } else if (v.getId() == R.id.bill_id) {
            WithdrawalEntity entity = entities.get(position);
            Intent intent = new Intent(context, USDTBillActivity.class);
            JSONObject jsonObject = entity.getUser();
            String id = jsonObject.getString("id");
            intent.putExtra("bill_id", id);
            context.startActivity(intent);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemExamineBinding binding;

        public ViewHolder(@NonNull ItemExamineBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }

    private class Withdrawal implements OnData {
        private final WithdrawalEntity entity;
        private final String withdrawal_type;
        private final int position;

        public Withdrawal(int position, String withdrawal_type) {
            this.position = position;
            this.withdrawal_type = withdrawal_type;
            entity = entities.get(position);
        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(context);
            JSONObject jsonObject = sharedUtil.getLogin(21, 4);
            jsonObject.put("text", text);
            jsonObject.put("withdrawal_id", entity.getId());
            jsonObject.put("withdrawal_type", withdrawal_type);
            jsonObject.put("position", position);
            socketManage.print(jsonObject.toString());
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            if (code == 200) {
                int position = jsonObject.getInteger("position");
                entities.remove(position);
                notifyDataSetChanged();
            }
            String msg = jsonObject.getString("msg");
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
