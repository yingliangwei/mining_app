package com.mining.superbull.widget;

import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.superbull.R;
import com.mining.superbull.databinding.ActivityMainBinding;
import com.mining.util.Handler;
import com.mining.util.OnHandler;
import com.mining.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RunnerWidget extends RecyclerView implements Runnable, OnHandler {
    public final List<Integer> integerList;
    private final adapter adapter;
    private final Handler handler = new Handler(Looper.myLooper(), this);
    private final CenterLayoutManager centerLayoutManager;
    private int repeatCount = 50;
    private int duration = 50;
    private int super_1 = 0;
    private String gem;
    private String dayGem;
    private ActivityMainBinding binding;

    public RunnerWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        integerList = new ArrayList<>();
        integerList.add(1);
        //888 1
        integerList.add(R.drawable.symbol_1);
        integerList.add(0);
        //88 3
        integerList.add(R.drawable.symbol_2);
        integerList.add(0);
        //58 5
        integerList.add(R.drawable.symbol_3);
        integerList.add(0);
        //28 7
        integerList.add(R.drawable.symbol_4);
        integerList.add(1);
        adapter = new adapter(integerList);
        centerLayoutManager = new CenterLayoutManager(context);
        setLayoutManager(centerLayoutManager);
        setAdapter(adapter);
        smoothScrollToPosition(2);
        setNestedScrollingEnabled(false);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        if (centerLayoutManager == null) {
            super.smoothScrollToPosition(position);
            return;
        }
        centerLayoutManager.smoothScrollToPosition(this, new RecyclerView.State(), position);
    }

    public void setBinding(ActivityMainBinding binding, String dayGem, String gem) {
        this.binding = binding;
        this.gem = gem;
        this.dayGem = dayGem;
    }

    public void start(int super_1) {
        this.super_1 = super_1;
        new Thread(this).start();
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void run() {
        //初始化
        handler.sendMessage(0, "2");
        int[] ints = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < repeatCount; i++) {
            for (int anInt : ints) {
                integers.add(anInt);
            }
        }
        for (int i = 0; i < integerList.size(); i++) {
            handler.sendMessage(0, String.valueOf(integers.get(i)));
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.fillInStackTrace();
            }
        }
        handler.sendMessage(1, "");
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 0) {
            int integer = Integer.parseInt(str);
            super.smoothScrollToPosition(integer);
        } else if (w == 1) {
            stop();
        }
    }

    public void stop() {
        List<Integer> integers = new ArrayList<>();
        integers.add(0);
        integers.add(2);
        integers.add(4);
        integers.add(6);
        switch (super_1) {
            case 3:
                smoothScrollToPosition(7);
                break;
            case 4:
                smoothScrollToPosition(3);
                break;
            case 5:
                smoothScrollToPosition(5);
                break;
            case 6:
                smoothScrollToPosition(1);
                break;
            default:
                Random random = new Random();
                int randomNumber = random.nextInt(integers.size());
                smoothScrollToPosition(integers.get(randomNumber));
                break;
        }
        if (binding != null) {
            binding.superGem.setText(StringUtil.toRe(dayGem));
            binding.gem.setText(StringUtil.toRe(gem));
        }
    }

    @Nullable
    @Override
    public RunnerWidget.adapter getAdapter() {
        return adapter;
    }

    private class adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public final List<Integer> integerList;

        public adapter(List<Integer> integerList) {
            this.integerList = integerList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType != 0 && viewType != 1) {
                return new ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_runner, parent, false));
            }
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_runner_1, parent, false);
            if (viewType == 1) {
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));
            }
            return new ViewHolder_1(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ViewHolder) {
                ViewHolder holder1 = (ViewHolder) holder;
                holder1.imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), integerList.get(position)));
            }
        }

        @Override
        public int getItemCount() {
            return integerList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return integerList.get(position);
        }
    }

    private class ViewHolder_1 extends RecyclerView.ViewHolder {
        public ViewHolder_1(@NonNull View itemView) {
            super(itemView);
        }
    }

    /**
     * Created by iblade.Wang on 2019/5/22 17:08
     */
    public class CenterLayoutManager extends LinearLayoutManager {
        public CenterLayoutManager(Context context) {
            super(context);
        }

        public CenterLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public CenterLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
            RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(recyclerView.getContext());
            smoothScroller.setTargetPosition(position);
            startSmoothScroll(smoothScroller);
        }

        private class CenterSmoothScroller extends LinearSmoothScroller {
            public CenterSmoothScroller(Context context) {
                super(context);
            }

            @Override
            public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
                return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 100f / displayMetrics.densityDpi;
            }
        }
    }


    private class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}
