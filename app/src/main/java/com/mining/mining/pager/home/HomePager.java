package com.mining.mining.pager.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.mining.mining.R;
import com.mining.mining.activity.NewsActivity;
import com.mining.mining.activity.PluginSearchActivity;
import com.mining.mining.adapter.PagerAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerHomeBinding;
import com.mining.mining.entity.BannerEntity;
import com.mining.mining.entity.ClassfiyEntity;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.pager.home.adapter.BannerAdapter;
import com.mining.mining.pager.home.adapter.VerticalAdapter;
import com.mining.mining.util.SharedUtil;
import com.mining.mining.widget.BadgeActionProvider;
import com.mining.util.Handler;
import com.mining.util.MessageEvent;
import com.mining.util.OnHandler;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.plugin.lib.PluginManager;
import com.plugin.lib.Storage;
import com.plugin.lib.activity.BaseActivity;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.fileSelection.FileSelectionDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import q.rorbin.verticaltablayout.VerticalTabLayout;
import q.rorbin.verticaltablayout.widget.TabView;

public class HomePager extends RecyclerAdapter implements VerticalTabLayout.OnTabSelectedListener, FileSelectionDialog.OnFileSelection, OnData, OnHandler, Toolbar.OnMenuItemClickListener {
    private final Activity context;
    private PagerHomeBinding binding;
    private final List<BannerEntity> bannerEntities = new ArrayList<>();
    private BannerAdapter bannerAdapter;
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private final OnData getBannerData = new OnData() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handle(String ds) {
            System.out.println(ds);
            try {
                JSONObject jsonObject = JSONObject.parseObject(ds);
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.size(); i++) {
                    JSONObject banner = data.getJSONObject(i);
                    BannerEntity entity = new Gson().fromJson(banner.toString(), BannerEntity.class);
                    entity.json = banner.toString();
                    bannerEntities.add(entity);
                }
                bannerAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(context);
            JSONObject jsonObject = sharedUtil.getLogin(12, 5);
            socketManage.print(jsonObject.toString());
        }
    };

    private final OnData onData = new OnData() {
        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            String gem = jsonObject.getString("gem");
            String day_gem = jsonObject.getString("day_gem");
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
    };
    private final OnData getNewsSize = new OnData() {
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
    };
    private BadgeActionProvider badgeActionProvider;

    public HomePager(Activity context) {
        super(context);
        this.context = context;
        EventBus.getDefault().register(this);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerHomeBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        initToolbar();
        initView();
        initBanner();
        initNews();
        SocketManage.init(this);
        SocketManage.init(onData);
        SocketManage.init(getBannerData);
        SocketManage.init(getNewsSize);
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
        binding.search.setOnClickListener(v -> {
            Intent intent = new Intent(context, PluginSearchActivity.class);
            context.startActivity(intent);
        });
        binding.refresh.setOnClickListener(v -> SocketManage.init(onData));
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
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            JSONArray data = jsonObject.getJSONArray("data");
            initTabData(data);
        }
    }


    private void initTabData(JSONArray data) {
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            Intent intent = new Intent(context, PluginSearchActivity.class);
            context.startActivity(intent);
        } else if (item.getItemId() == R.id.news) {
            Intent intent = new Intent(context, NewsActivity.class);
            context.startActivity(intent);
        }
        return false;
    }

    @Override
    public void success(Dialog dialog, File file) {
        dialog.dismiss();
        loadPlugin(file);
    }

    private void loadPlugin(File file) {
        PluginManager pluginManager = new PluginManager(context, file);
        try {
            pluginManager.load();
            com.plugin.lib.entity.PluginEntity entity = pluginManager.getPluginEntity();
            int id = Storage.getInstance().getEntitiesSize();
            Storage.getInstance().add(id, entity);
            Storage.getInstance().id = id;

            String main = entity.getMain();
            if (main == null) {
                Toast.makeText(context, "加载异常", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(context, BaseActivity.class);
            intent.putExtra("className", main);
            intent.putExtra("id", id);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "加载异常", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(MessageEvent event) {
        if (event.getW() == 1) {
            SocketManage.init(onData);
        } else if (event.getW() == 6) {
            SocketManage.init(getNewsSize);
        }
    }
}
