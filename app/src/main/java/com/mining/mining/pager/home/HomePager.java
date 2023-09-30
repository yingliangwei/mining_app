package com.mining.mining.pager.home;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.R;
import com.mining.mining.activity.PluginSearchActivity;
import com.mining.mining.adapter.PagerAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerHomeBinding;
import com.mining.mining.entity.ClassfiyEntity;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.pager.home.adapter.VerticalAdapter;
import com.mining.mining.util.SharedUtil;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import q.rorbin.verticaltablayout.VerticalTabLayout;
import q.rorbin.verticaltablayout.widget.TabView;

public class HomePager extends RecyclerAdapter implements VerticalTabLayout.OnTabSelectedListener, FileSelectionDialog.OnFileSelection, OnData, OnHandler, Toolbar.OnMenuItemClickListener {
    private final Activity context;
    private PagerHomeBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper(), this);

    private final OnData onData = new OnData() {
        @Override
        public void handle(String ds) {
            try {
                JSONObject jsonObject = new JSONObject(ds);
                String gem = jsonObject.getString("gem");
                String day_gem = jsonObject.getString("day_gem");
                binding.gem.setText(StringUtil.toRe(gem));
                binding.dayGem.setText(StringUtil.toRe(day_gem));
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }

        @Override
        public void connect(SocketManage socketManage) {
            try {
                SharedUtil sharedUtil = new SharedUtil(context);
                JSONObject jsonObject = sharedUtil.getLogin(12, 4);
                socketManage.print(jsonObject.toString());
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    };

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
        SocketManage.init(this);
        SocketManage.init(onData);
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
        try {
            SharedUtil sharedUtil = new SharedUtil(context);
            JSONObject jsonObject = sharedUtil.getLogin(12, 1);
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void handle(String ds) {
        handler.sendMessage(1, ds);
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 1) {
            try {
                JSONObject jsonObject = new JSONObject(str);
                int code = jsonObject.getInt("code");
                if (code == 200) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    initTabData(data);
                }
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    }

    private void initTabData(JSONArray data) throws JSONException {
        List<ClassfiyEntity> classfiyEntities = new ArrayList<>();
        List<RecyclerAdapter> recyclerAdapters = new ArrayList<>();

        for (int i = 0; i < data.length(); i++) {
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
        if (item.getItemId() == R.id.selected) {
            FileSelectionDialog dialog = new FileSelectionDialog(context);
            dialog.setFileSelection(this);
            dialog.show();
        } else if (item.getItemId() == R.id.search) {
            Intent intent = new Intent(context, PluginSearchActivity.class);
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
        }
    }
}
