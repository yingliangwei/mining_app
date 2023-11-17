package com.mining.mining.pager.my;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.activity.HelpActivity;
import com.mining.mining.activity.TransferActivity;
import com.mining.mining.activity.c2s.C2CActivity;
import com.mining.mining.activity.invite.InviteActivity;
import com.mining.mining.activity.invite.InviteCodeActivity;
import com.mining.mining.activity.set.SetUserActivity;
import com.mining.mining.activity.wallet.UsdtBillActivity;
import com.mining.mining.activity.wallet.WalletActivity;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerMyBinding;
import com.mining.mining.entity.MessageEvent;
import com.mining.mining.entity.TextDrawableEntity;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.pager.my.activity.ExamineActivity;
import com.mining.mining.pager.my.adpater.ItemAdapter;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.recycler.RecyclerItemClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MyPager extends RecyclerAdapter implements OnData, View.OnClickListener, MenuItem.OnMenuItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private final Activity context;
    private PagerMyBinding binding;
    private final List<TextDrawableEntity> entities1 = new ArrayList<>();

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
            } else if (position == 2) {
                context.startActivity(new Intent(context, HelpActivity.class));
            } else if (position == 3) {
                context.startActivity(new Intent(context, ExamineActivity.class));
            }
        }
    };
    private ItemAdapter itemAdapter1;

    public MyPager(Activity context) {
        super(context);
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        binding = PagerMyBinding.inflate(LayoutInflater.from(context), parent, false);
        String id = sharedPreferences.getString("id", null);
        if (id != null) {
            binding.id.setText(id);
        }
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EventBus.getDefault().register(this);
        initToolbar();
        initRecycler();
        initView();
        SocketManage.init(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMassage(MessageEvent event) {
        if (event.isClass(getClass())) {
            SocketManage.init(this);
        }
    }

    private void initToolbar() {
        binding.toolbar.setOnMenuItemClickListener(this::onMenuItemClick);
    }

    private void initView() {
        binding.Swipe.setOnRefreshListener(this);
        binding.usdtL.setOnClickListener(this);
        binding.detail.setOnClickListener(this);
        binding.buy.setOnClickListener(this);
        binding.login.setOnClickListener(this);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initRecycler() {
        List<TextDrawableEntity> entities = new ArrayList<>();
        entities.add(new TextDrawableEntity("宝石市场", context.getDrawable(R.mipmap.ic_ape_new_gemstone)));
        ItemAdapter itemAdapter = new ItemAdapter(context, entities);
        binding.recycler.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        binding.recycler.setAdapter(itemAdapter);
        binding.recycler.addOnItemTouchListener(new RecyclerItemClickListener(context, normal));
        entities1.add(new TextDrawableEntity("我的钱包", context.getDrawable(R.mipmap.ic_wallet_black)));
        entities1.add(new TextDrawableEntity("USDT转账", context.getDrawable(R.mipmap.transfer)));
        entities1.add(new TextDrawableEntity("帮助", context.getDrawable(R.mipmap.help)));

        itemAdapter1 = new ItemAdapter(context, entities1);
        binding.common.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        binding.common.setAdapter(itemAdapter1);
        binding.common.addOnItemTouchListener(new RecyclerItemClickListener(context, normal1));
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(context);
        JSONObject jsonObject = sharedUtil.getLogin(3, 1);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        binding.Swipe.setRefreshing(false);
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            JSONObject data = jsonObject.getJSONObject("data");
            initViewData(data);
            String gem = jsonObject.getString("sum");
            binding.gem.setText(StringUtil.toRe(gem));
            String invite_sum = jsonObject.getString("invite_sum");
            binding.inviteSum.setText(invite_sum);
            String root = data.getString("root");
            if (root.equals("1")) {
                if (entities1.size() != 4) {
                    entities1.add(new TextDrawableEntity("提现审核", ContextCompat.getDrawable(getContext(), R.mipmap.examine)));
                }
                itemAdapter1.notifyItemChanged(entities1.size() - 1);
            }
            return;
        }
        String msg = jsonObject.getString("msg");
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void error(String error) {
        binding.Swipe.setRefreshing(false);
    }

    private void initViewData(JSONObject jsonObject) {
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
        } else if (v.getId() == R.id.login) {
            context.startActivity(new Intent(context, SetUserActivity.class));
        }
    }

    @Override
    public boolean onMenuItemClick(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.set) {
            context.startActivity(new Intent(context, SetUserActivity.class));
        }
        return false;
    }

    @Override
    public void onRefresh() {
        SocketManage.init(this);
    }
}
