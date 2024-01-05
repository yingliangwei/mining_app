package com.mining.mining.pager.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.mining.mining.R;
import com.mining.mining.activity.CollectionActivity;
import com.mining.mining.activity.MainActivity;
import com.mining.mining.activity.NewsActivity;
import com.mining.mining.activity.PluginLogActivity;
import com.mining.mining.activity.PluginSearchActivity;
import com.mining.mining.activity.scan.ScanActivity;
import com.mining.mining.adapter.PagerAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerHomeBinding;
import com.mining.mining.entity.BannerEntity;
import com.mining.mining.entity.ClassfiyEntity;
import com.mining.mining.entity.MessageEvent;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.pager.home.adapter.BannerAdapter;
import com.mining.mining.pager.home.adapter.VerticalAdapter;
import com.mining.mining.util.SharedUtil;
import com.mining.mining.widget.BadgeActionProvider;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import q.rorbin.verticaltablayout.VerticalTabLayout;
import q.rorbin.verticaltablayout.widget.TabView;

public class HomePager extends RecyclerAdapter implements VerticalTabLayout.OnTabSelectedListener, OnData, Toolbar.OnMenuItemClickListener, View.OnClickListener {
    private final Activity context;
    private PagerHomeBinding binding;
    private final List<BannerEntity> bannerEntities = new ArrayList<>();
    private BannerAdapter bannerAdapter;
    private BadgeActionProvider badgeActionProvider;

    public HomePager(Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EventBus.getDefault().register(this);
        initToolbar();
        initView();
        initBanner();
        initNews();

        SocketManage.init(this);
        SocketManage.init(new getGam());
        SocketManage.init(new getBannerData());
        SocketManage.init(new getNewsSize());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event) {
        if (event.isClass(HomePager.class)) {
            if (event.w == 0) {
                SocketManage.init(new getGam());
            } else if (event.w == 1) {
                SocketManage.init(new getNewsSize());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(String message) {
        if (message.equals("gem")) {
            SocketManage.init(new getGam());
        }
    }

    private void initNews() {
        Menu menu = binding.toolbar.getMenu();
        MenuItem d = menu.findItem(R.id.news);
        badgeActionProvider = (BadgeActionProvider) MenuItemCompat.getActionProvider(d);
        if (badgeActionProvider == null) {
            return;
        }
        badgeActionProvider.setOnClickListener(0, what -> {
            Intent intent = new Intent(context, NewsActivity.class);
            context.startActivity(intent);
        });
        badgeActionProvider.setIcon(ContextCompat.getDrawable(context, R.mipmap.news));
    }

    private void initBanner() {
        bannerAdapter = new BannerAdapter(bannerEntities, context);
        binding.banner.setAdapter(bannerAdapter);
    }

    private void initView() {
        binding.user.setOnClickListener(this);
        binding.refresh.setOnClickListener(v -> SocketManage.init(new getGam()));
        binding.search.setOnClickListener(v -> {
            Intent intent = new Intent(context, PluginSearchActivity.class);
            context.startActivity(intent);
        });
    }

    private void initToolbar() {
        binding.toolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(context);
        JSONObject jsonObject = sharedUtil.getLogin(12, 1);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        binding.spinKit.setVisibility(View.GONE);
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            JSONArray data = jsonObject.getJSONArray("data");
            initTabData(data);
        }
    }

    @Override
    public void error(String error) {
        binding.spinKit.setVisibility(View.GONE);
    }

    private void initTabData(JSONArray data) {
        if (data == null) {
            return;
        }
        List<ClassfiyEntity> classfiyEntities = new ArrayList<>();
        List<RecyclerAdapter> recyclerAdapters = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            String name = jsonObject.getString("name");
            String id = jsonObject.getString("id");
            recyclerAdapters.add(new HomeVerticalItemPager(context, id));
            classfiyEntities.add(new ClassfiyEntity(name, id));
        }

        VerticalAdapter verticalAdapter = new VerticalAdapter(classfiyEntities);
        binding.vertical.setTabAdapter(verticalAdapter);
        binding.vertical.addOnTabSelectedListener(this);

        binding.pager.setUserInputEnabled(false);
        binding.pager.setAdapter(new PagerAdapter(recyclerAdapters));
        binding.pager.setOffscreenPageLimit(recyclerAdapters.size());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerHomeBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            Intent intent = new Intent(context, PluginSearchActivity.class);
            context.startActivity(intent);
        } else if (item.getItemId() == R.id.news) {
            Intent intent = new Intent(context, NewsActivity.class);
            context.startActivity(intent);
        } else if (item.getItemId() == R.id.plugin_log) {
            context.startActivity(new Intent(context, PluginLogActivity.class));
        } else if (item.getItemId() == R.id.Sweep) {
            context.startActivity(new Intent(context, ScanActivity.class));
        } else if (item.getItemId() == R.id.PaymentCode) {
            context.startActivity(new Intent(context, CollectionActivity.class));
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.user) {
            EventBus.getDefault().post(new MessageEvent(MainActivity.class));
        }
    }


    private class getGam implements OnData, RequestListener<Drawable> {
        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            String gem = jsonObject.getString("gem");
            String day_gem = jsonObject.getString("day_gem");
            String name = jsonObject.getString("name");
            binding.name.setText(name);
            binding.nameX.setText(StringUtil.getStringStart(name));
            binding.gem.setText(StringUtil.toRe(gem));
            if (day_gem.startsWith("-")) {
                binding.dayGem.setTextColor(context.getColor(android.R.color.holo_green_dark));
            } else {
                binding.dayGem.setTextColor(context.getColor(android.R.color.holo_red_dark));
            }
            binding.dayGem.setText(StringUtil.toRe(day_gem));
        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(context);
            JSONObject jsonObject = sharedUtil.getLogin(12, 4);
            socketManage.print(jsonObject.toString());
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            binding.toolbar.setNavigationIcon(resource);
            return true;
        }
    }

    private class getNewsSize implements OnData {
        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            if (code == 200) {
                int news_count = jsonObject.getInteger("news_count");
                badgeActionProvider.setNewsSize(news_count);
            }
        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(context);
            socketManage.print(sharedUtil.getLogin(12, 6).toString());
        }
    }

    private class getBannerData implements OnData {
        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            JSONArray data = jsonObject.getJSONArray("data");
            if (data == null) {
                return;
            }
            for (int i = 0; i < data.size(); i++) {
                JSONObject banner = data.getJSONObject(i);
                BannerEntity entity = new Gson().fromJson(banner.toString(), BannerEntity.class);
                entity.json = banner.toString();
                bannerEntities.add(entity);
            }
            bannerAdapter.notifyDataSetChanged();
        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(context);
            JSONObject jsonObject = sharedUtil.getLogin(12, 5);
            socketManage.print(jsonObject.toString());
        }
    }

    @Override
    public void StatusBar(Activity activity) {
        StatusBarUtil.setImmersiveStatusBar(context, true);
    }

    @Override
    public void onTabSelected(TabView tab, int position) {
        binding.pager.setCurrentItem(position);
    }

    @Override
    public void onTabReselected(TabView tab, int position) {

    }
}
