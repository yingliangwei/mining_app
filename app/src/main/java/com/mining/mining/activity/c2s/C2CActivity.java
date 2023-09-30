package com.mining.mining.activity.c2s;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationBarView;
import com.mining.mining.activity.MainActivity;
import com.mining.mining.activity.c2s.pager.AddC2cPager;
import com.mining.mining.activity.c2s.pager.HomeC2sPager;
import com.mining.mining.adapter.PagerAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.ActivityC2sBinding;

import java.util.ArrayList;
import java.util.List;

public class C2CActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    private ActivityC2sBinding binding;
    private final List<RecyclerAdapter> recyclerAdapters = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置状态栏字体黑色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = ActivityC2sBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViewPager();
        initBottom();
    }

    private void initBottom() {
        binding.bottom.setOnItemSelectedListener(this);
    }

    private void initViewPager() {
        recyclerAdapters.add(new HomeC2sPager(this));
        recyclerAdapters.add(new AddC2cPager(this));
        binding.pager.setAdapter(new PagerAdapter(recyclerAdapters));
        binding.pager.setCurrentItem(recyclerAdapters.size());
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                recyclerAdapters.get(position).StatusBar(C2CActivity.this);
                binding.bottom.getMenu().getItem(position).setChecked(true);
            }
        });
        binding.pager.setCurrentItem(0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        recyclerAdapters.get(item.getOrder()).StatusBar(C2CActivity.this);
        binding.pager.setCurrentItem(item.getOrder());
        return true;
    }
}
