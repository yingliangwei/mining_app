package com.xframe.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.xframe.widget.databinding.DialogUpdateBinding;

import java.util.HashMap;
import java.util.Map;

public class updateDialog extends Dialog implements View.OnClickListener {
    private final DialogUpdateBinding binding;
    private final Map<Integer, DialogInterface.OnClickListener> onClickListenerMap = new HashMap<>();

    public updateDialog(@NonNull Context context) {
        super(context);
        binding = DialogUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        setDialogLocation();
    }

    public void setProgress(int Progress) {
        binding.Progress.setVisibility(View.VISIBLE);
        binding.Progress.setProgress(Progress);
    }

    public NumberProgressBar getProgress() {
        return binding.Progress;
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
        binding.title.setText(titleId);
    }

    public void setMessage(String message) {
        binding.message.setText(message);
    }

    public void setOnNo(String text, DialogInterface.OnClickListener clickListener) {
        onClickListenerMap.put(binding.no.getId(), clickListener);
        binding.no.setVisibility(View.VISIBLE);
        binding.no.setText(text);
        binding.no.setOnClickListener(this);
    }

    public void setOnOk(String text, DialogInterface.OnClickListener clickListener) {
        onClickListenerMap.put(binding.ok.getId(), clickListener);
        binding.ok.setVisibility(View.VISIBLE);
        binding.ok.setText(text);
        binding.ok.setOnClickListener(this);
    }

    public void setOnClose(String text, DialogInterface.OnClickListener clickListener) {
        onClickListenerMap.put(binding.close.getId(), clickListener);
        binding.close.setVisibility(View.VISIBLE);
        binding.close.setText(text);
        binding.close.setOnClickListener(this);
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
