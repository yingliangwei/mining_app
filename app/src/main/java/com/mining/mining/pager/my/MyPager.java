package com.mining.mining.pager.my;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.mining.mining.R;
import com.mining.mining.activity.TransferActivity;
import com.mining.mining.activity.c2s.C2CActivity;
import com.mining.mining.activity.invite.InviteActivity;
import com.mining.mining.activity.invite.InviteCodeActivity;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.activity.set.SetUserActivity;
import com.mining.mining.activity.wallet.UsdtBillActivity;
import com.mining.mining.activity.wallet.WalletActivity;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerMyBinding;
import com.mining.mining.entity.TextDrawableEntity;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.pager.my.adpater.ItemAdapter;
import com.mining.mining.util.SharedUtil;
import com.mining.util.Handler;
import com.mining.util.MessageEvent;
import com.mining.util.OnHandler;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.recycler.RecyclerItemClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyPager extends RecyclerAdapter implements OnData, OnHandler, View.OnClickListener, MenuItem.OnMenuItemClickListener {
    private final Activity context;
    private PagerMyBinding binding;
    private SharedPreferences sharedPreferences;
    private final Handler handler = new Handler(Looper.myLooper(), this);
    private final RecyclerItemClickListener.OnItemClickListener.Normal normal = new RecyclerItemClickListener.OnItemClickListener.Normal() {
        @Override
        public void onItemClick(View view, int position) {
            if (position == 0) {
                context.startActivity(new Intent(context, C2CActivity.class));
            }
        }
    };

    private final RecyclerItemClickListener.OnItemClickListener.Normal normal1 = new RecyclerItemClickListener.OnItemClickListener.Normal() {
        @Override
        public void onItemClick(View view, int position) {
            if (position == 0) {
                context.startActivity(new Intent(context, WalletActivity.class));
            } else if (position == 1) {
                context.startActivity(new Intent(context, TransferActivity.class));
            }
        }
    };

    private final OnData onData = new OnData() {
        @Override
        public void handle(String ds) {
            try {
                JSONObject jsonObject = new JSONObject(ds);
                int code = jsonObject.getInt("code");
                if (code == 200) {
                    String usdt = jsonObject.getString("usdt");
                    binding.usdt.setText(usdt);
                }
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }

        @Override
        public void connect(SocketManage socketManage) {
            try {
                SharedUtil sharedUtil = new SharedUtil(context);
                JSONObject jsonObject = sharedUtil.getLogin(3, 2);
                socketManage.print(jsonObject.toString());
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    };

    public MyPager(Activity context) {
        super(context);
        this.context = context;
        EventBus.getDefault().register(this);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        binding = PagerMyBinding.inflate(LayoutInflater.from(context), parent, false);
        String id = sharedPreferences.getString("id", null);
        if (id != null) {
            binding.id.setText(id);
        }
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        initToolbar();
        initRecycler();
        initView();
        SocketManage.init(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    private void initToolbar() {
        binding.toolbar.setOnMenuItemClickListener(this::onMenuItemClick);
    }

    private void initView() {
        binding.usdtL.setOnClickListener(this);
        binding.detail.setOnClickListener(this);
        binding.buy.setOnClickListener(this);
    }

    private void initRecycler() {
        List<TextDrawableEntity> entities = new ArrayList<>();
        entities.add(new TextDrawableEntity("宝石市场", context.getDrawable(R.mipmap.ic_ape_new_gemstone)));
        ItemAdapter itemAdapter = new ItemAdapter(context, entities);
        binding.recycler.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        binding.recycler.setAdapter(itemAdapter);
        binding.recycler.addOnItemTouchListener(new RecyclerItemClickListener(context, normal));
        List<TextDrawableEntity> entities1 = new ArrayList<>();
        entities1.add(new TextDrawableEntity("我的钱包", context.getDrawable(R.mipmap.ic_wallet_black)));
        entities1.add(new TextDrawableEntity("转账", context.getDrawable(R.mipmap.transfer)));
        entities1.add(new TextDrawableEntity("邀请好友", context.getDrawable(R.mipmap.invite)));

        ItemAdapter itemAdapter1 = new ItemAdapter(context, entities1);
        binding.common.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        binding.common.setAdapter(itemAdapter1);
        binding.common.addOnItemTouchListener(new RecyclerItemClickListener(context, normal1));
    }

    @Override
    public void connect(SocketManage socketManage) {
        try {
            String id = sharedPreferences.getString("id", null);
            String _key = sharedPreferences.getString("_key", null);
            if (id == null || _key == null) {
                LoginActivity.login(context);
                return;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 3);
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
                handler.sendMessage(1, jsonObject.toString());
                return;
            } else if (code == 202) {
                LoginActivity.login(context);
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
            try {
                JSONObject jsonObject = new JSONObject(str);
                JSONObject data = jsonObject.getJSONObject("data");
                initViewData(data);
                String invite_sum = jsonObject.getString("invite_sum");
                binding.inviteSum.setText(invite_sum);
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    }

    private void initViewData(JSONObject jsonObject) throws Exception {
        String name = jsonObject.getString("name");
        String usdt = jsonObject.getString("usdt");
        binding.usdt.setText(StringUtil.toRe(usdt));
        binding.name.setText(name);
        binding.nameX.setText(StringUtil.getStringStart(name));
    }


    @Override
    public void StatusBar(Activity activity) {
        StatusBarUtil.setImmersiveStatusBar(activity, true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.usdt_l) {
            Intent intent = new Intent(context, UsdtBillActivity.class);
            intent.putExtra("code", 3);
            context.startActivity(intent);
        } else if (v.getId() == R.id.detail) {
            context.startActivity(new Intent(context, InviteActivity.class));
        } else if (v.getId() == R.id.buy) {
            context.startActivity(new Intent(context, InviteCodeActivity.class));
        }
    }

    @Override
    public boolean onMenuItemClick(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.set) {
            context.startActivity(new Intent(context, SetUserActivity.class));
        }
        return false;
    }

    @Subscribe
    public void onEvent(MessageEvent messageEvent) {
        if (messageEvent.getW() == 3) {
            SocketManage.init(onData);
        }
    }
}
