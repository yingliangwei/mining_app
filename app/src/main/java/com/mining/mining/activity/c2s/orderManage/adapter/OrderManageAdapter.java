package com.mining.mining.activity.c2s.orderManage.adapter;

import android.content.Context;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.R;
import com.mining.mining.databinding.ItemOrderManageBinding;
import com.mining.mining.entity.C2cEntity;
import com.mining.mining.util.SharedUtil;
import com.mining.util.Handler;
import com.mining.util.OnHandler;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.json.JSONObject;

import java.util.List;

public class OrderManageAdapter extends RecyclerView.Adapter<OrderManageAdapter.ViewHolder> implements View.OnClickListener, OnData, OnHandler {
    public final List<C2cEntity> list;
    private final Context context;
    private String c2c_id = "0";
    private final Handler handler = new Handler(Looper.myLooper(), this);
    private final int code;
    private View mEmptyTextView;

    public OrderManageAdapter(Context context, List<C2cEntity> list, int code) {
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
        C2cEntity entity = list.get(position);
        holder.binding.gem.setText(entity.getArticle());
        holder.binding.usdt.setText(StringUtil.toRe(entity.getUsdt()));
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
            C2cEntity entity = list.get(position);
            c2c_id = entity.getId();
            SocketManage.init(this);
        }
    }

    @Override
    public void connect(SocketManage socketManage) {
        try {
            SharedUtil sharedUtil = new SharedUtil(context);
            JSONObject jsonObject = sharedUtil.getLogin(4, 13);
            jsonObject.put("c2c_id", c2c_id);
            jsonObject.put("is", code);
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handle(String ds) {
        try {
            JSONObject jsonObject = new JSONObject(ds);
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
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemOrderManageBinding binding;

        public ViewHolder(@NonNull ItemOrderManageBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
