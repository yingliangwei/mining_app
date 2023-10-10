package com.mining.battleroyale.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.mining.battleroyale.databinding.LayoutGameBinding;

public class GameView extends LinearLayout {
    private LayoutGameBinding binding;

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = LayoutGameBinding.inflate(LayoutInflater.from(context), this, false);
        addView(binding.getRoot());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
