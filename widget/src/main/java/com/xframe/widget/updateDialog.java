package com.xframe.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;


import java.util.HashMap;
import java.util.Map;

public class updateDialog extends Dialog implements View.OnClickListener {
    private final View binding;
    private final Map<Integer, DialogInterface.OnClickListener> onClickListenerMap = new HashMap<>();

    public updateDialog(@NonNull Context context) {
        super(context);
        binding = LayoutInflater.from(context).inflate(R.layout.dialog_update, new FrameLayout(context), false);
        setContentView(binding);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        setDialogLocation();
    }

    public void setProgress(int Progress) {
        NumberProgressBar numberProgressBar = binding.findViewById(R.id.Progress);
        numberProgressBar.setVisibility(View.VISIBLE);
        numberProgressBar.setProgress(Progress);
    }

    public NumberProgressBar getProgress() {
        return binding.findViewById(R.id.Progress);
    }

    private void setDialogLocation() {
        Window win = getWindow();
        if (win == null) {
            return;
        }
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setWindowAnimations(R.style.dialog_sen5_full);
        win.setBackgroundDrawableResource(android.R.color.transparent);
        win.setAttributes(lp);
    }

    public void setTitle(String titleId) {
        TextView title = binding.findViewById(R.id.title);
        title.setText(titleId);
    }

    public void setMessage(String message) {
        TextView textView = binding.findViewById(R.id.message);
        textView.setText(message);
    }

    public void setOnNo(String text, DialogInterface.OnClickListener clickListener) {
        Button button = binding.findViewById(R.id.no);
        onClickListenerMap.put(button.getId(), clickListener);
        button.setVisibility(View.VISIBLE);
        button.setText(text);
        button.setOnClickListener(this);
    }

    public void setOnOk(String text, DialogInterface.OnClickListener clickListener) {
        Button button = binding.findViewById(R.id.ok);
        onClickListenerMap.put(button.getId(), clickListener);
        button.setVisibility(View.VISIBLE);
        button.setText(text);
        button.setOnClickListener(this);
    }

    public void setOnClose(String text, DialogInterface.OnClickListener clickListener) {
        Button button = binding.findViewById(R.id.close);
        onClickListenerMap.put(button.getId(), clickListener);
        button.setVisibility(View.VISIBLE);
        button.setText(text);
        button.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        DialogInterface.OnClickListener clickListener = onClickListenerMap.get(v.getId());
        if (clickListener == null) {
            return;
        }
        clickListener.onClick(this, v.getId());
    }
}
