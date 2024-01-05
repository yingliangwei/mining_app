package com.mining.mining.pager.task;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bumptech.glide.Glide;
import com.mining.mining.R;
import com.mining.mining.activity.invite.SetInviteActivity;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.ItemTaskBinding;
import com.mining.mining.databinding.ItemTaskSignBinding;
import com.mining.mining.databinding.PagerTaskBinding;
import com.mining.mining.entity.MessageEvent;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.pager.home.HomePager;
import com.mining.mining.pager.mining.MiningPager;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class TaskPager extends RecyclerAdapter implements OnData, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private PagerTaskBinding binding;
    private final List<JSONObject> jsonObjects = new ArrayList<>();
    private final List<JSONObject> tasks = new ArrayList<>();
    private Signadapter signadapter;
    private TaskAdapter taskAdapter;

    public TaskPager(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerTaskBinding.inflate(LayoutInflater.from(getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EventBus.getDefault().register(this);
        initView();
        initSwipe();
        initSignRecycler();
        SocketManage.init(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event) {
        if (event.isClass(TaskPager.class)) {
            SocketManage.init(this);
        }
    }

    private void initView() {
        binding.isSign.setOnClickListener(this);
    }

    private void initSwipe() {
        binding.Swipe.setOnRefreshListener(this);
    }

    private void initSignRecycler() {
        signadapter = new Signadapter(jsonObjects);
        binding.SignRecycler.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        binding.SignRecycler.setAdapter(signadapter);

        taskAdapter = new TaskAdapter(tasks);
        binding.TaskRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.TaskRecycler.setAdapter(taskAdapter);
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(getContext());
        JSONObject jsonObject = sharedUtil.getLogin(23, 1);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void error(String error) {
        binding.Swipe.setRefreshing(false);
        binding.spinKit.setVisibility(View.GONE);
    }

    @Override
    public void handle(String ds) {
        binding.spinKit.setVisibility(View.GONE);
        binding.Swipe.setRefreshing(false);
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            initSignData(jsonObject);
            initTaskData(jsonObject);
            return;
        }
        String msg = jsonObject.getString("msg");
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void initTaskData(JSONObject jsonObject) {
        JSONArray taskSign = jsonObject.getJSONArray("taskList");
        if (taskSign == null) {
            return;
        }
        tasks.clear();
        for (int i = 0; i < taskSign.size(); i++) {
            tasks.add(taskSign.getJSONObject(i));
            taskAdapter.notifyItemChanged(i);
        }
    }

    private void initSignData(JSONObject jsonObject) {
        int isSign = jsonObject.getInteger("isSign");
        if (isSign == 1) {
            binding.isSignText.setText("以签到");
        } else {
            binding.isSignText.setText("签到领取宝石");
            binding.isSign.setOnClickListener(this);
        }
        JSONArray taskSign = jsonObject.getJSONArray("taskSign");
        if (taskSign == null) {
            return;
        }
        jsonObjects.clear();
        for (int i = 0; i < taskSign.size(); i++) {
            jsonObjects.add(taskSign.getJSONObject(i));
            signadapter.notifyItemChanged(i);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.isSign) {
            SocketManage.init(new sign());
        }
    }

    private class sign implements OnData {
        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(TaskPager.this.getContext());
            JSONObject jsonObject = sharedUtil.getLogin(23, 3);
            socketManage.print(jsonObject.toString());
        }

        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            if (code == 200) {
                jsonObjects.clear();
                tasks.clear();
                SocketManage.init(TaskPager.this);
                EventBus.getDefault().post(new MessageEvent(HomePager.class));
                EventBus.getDefault().post(new MessageEvent(1, MiningPager.class));
            }
            String msg = jsonObject.getString("msg");
            Toast.makeText(TaskPager.this.getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() {
        jsonObjects.clear();
        tasks.clear();
        SocketManage.init(this);
    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> implements View.OnClickListener {

        public final List<JSONObject> jsonObjects;

        public TaskAdapter(List<JSONObject> jsonObjects) {
            this.jsonObjects = jsonObjects;
        }

        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TaskViewHolder(ItemTaskBinding.inflate(LayoutInflater.from(TaskPager.this.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            JSONObject jsonObject = jsonObjects.get(position);
            String image = jsonObject.getString("image");
            String name = jsonObject.getString("name");
            String introduce = jsonObject.getString("introduce");
            int is = jsonObject.getInteger("is");
            holder.binding.name.setText(name);
            holder.binding.introduce.setText(introduce);
            if (is == 0) {
                holder.binding.ok.setText("去完成");
                holder.binding.ok.setTag(position);
                holder.binding.ok.setOnClickListener(this);
            } else if (is == 1) {
                holder.binding.ok.setText("领取");
                holder.binding.ok.setTag(position);
                holder.binding.ok.setOnClickListener(this);
            } else {
                holder.binding.ok.setText("以领取");
            }
            Glide.with(getContext()).load(image).into(holder.binding.image);
        }

        @Override
        public int getItemCount() {
            return jsonObjects.size();
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.ok) {
                int position = (int) v.getTag();
                JSONObject jsonObject = jsonObjects.get(position);
                int is = jsonObject.getInteger("is");
                if (is == 0) {
                    getContext().startActivity(new Intent(getContext(), SetInviteActivity.class));
                } else {
                    SocketManage.init(new receive(position, this));
                }
            }
        }
    }

    private class receive implements OnData {
        public final int position;
        public TaskAdapter adapter;

        public receive(int position, TaskAdapter adapter) {
            this.position = position;
            this.adapter = adapter;
        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(TaskPager.this.getContext());
            JSONObject jsonObject = sharedUtil.getLogin(23, 2);
            String task_id = adapter.jsonObjects.get(position).getString("id");
            jsonObject.put("task_id", task_id);
            socketManage.print(jsonObject.toString());
        }

        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            if (jsonObject == null) {
                return;
            }
            int code = jsonObject.getInteger("code");
            if (code == 200) {
                JSONObject jsonObject1 = tasks.get(position);
                jsonObject1.put("is", 2);
                tasks.set(position, jsonObject1);
                EventBus.getDefault().post(new MessageEvent(HomePager.class));
                EventBus.getDefault().post(new MessageEvent(MiningPager.class));
                adapter.notifyItemChanged(position);
            }
            String msg = jsonObject.getString("msg");
            Toast.makeText(TaskPager.this.getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private class TaskViewHolder extends ViewHolder {

        public ItemTaskBinding binding;

        public TaskViewHolder(@NonNull ItemTaskBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }

    private class Signadapter extends RecyclerView.Adapter<SignViewHolder> {
        private final List<JSONObject> jsonObjects;

        public Signadapter(List<JSONObject> jsonObjects) {
            this.jsonObjects = jsonObjects;
        }

        @NonNull
        @Override
        public SignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SignViewHolder(ItemTaskSignBinding.inflate(LayoutInflater.from(TaskPager.this.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SignViewHolder holder, int position) {
            JSONObject jsonObject = jsonObjects.get(position);
            String name = jsonObject.getString("name");
            String number = jsonObject.getString("number");
            String isSign = jsonObject.getString("isSign");
            holder.binding.name.setText(name);
            if (isSign.equals("0")) {
                holder.binding.number.setText(StringUtil.toRe(number));
            } else {
                holder.binding.number.setText("以签到");
            }
        }

        @Override
        public int getItemCount() {
            return jsonObjects.size();
        }
    }

    private class SignViewHolder extends RecyclerView.ViewHolder {
        public ItemTaskSignBinding binding;

        public SignViewHolder(@NonNull ItemTaskSignBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
