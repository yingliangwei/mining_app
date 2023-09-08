package com.mining.mining.activity.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mining.mining.databinding.ActivitySetUserBinding;
import com.mining.mining.util.StatusBarUtil;
import com.xframe.widget.entity.RecyclerEntity;
import com.xframe.widget.recycler.OnRecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class SetUserActivity extends AppCompatActivity implements OnRecyclerItemClickListener {
    private ActivitySetUserBinding binding;
    private final List<List<RecyclerEntity>> entity = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivitySetUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initRecycler();
    }

    private void initRecycler() {
        List<RecyclerEntity> entities = new ArrayList<>();
        entities.add(new RecyclerEntity("昵称", 0, "", ""));
        entities.add(new RecyclerEntity("收款方式", 0, "未设置", ""));
        entities.add(new RecyclerEntity("绑定手机", 0, "10000***000", ""));
        entities.add(new RecyclerEntity("关于", 0, "", ""));
        entity.add(entities);
        binding.recycle.add(entity);
        binding.recycle.notifyDataSetChanged();
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void onItemClick(RecyclerEntity entity, int position) {
        if (position == 0) {
            startActivity(new Intent(this, ModifyNameActivity.class));
        }
    }
}
