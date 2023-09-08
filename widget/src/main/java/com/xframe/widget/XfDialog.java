package com.xframe.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.xframe.widget.databinding.DialogBinding;

public class XfDialog extends Dialog implements View.OnClickListener {
    private final DialogBinding binding;
    private View.OnClickListener onClick;
    private View.OnClickListener yesClick;

    public XfDialog(@NonNull Context context) {
        super(context);
        binding = DialogBinding.inflate(getLayoutInflater(), new FrameLayout(context), false);
        setContentView(binding.getRoot());
        init(context);
    }

    public DialogBinding getBinding() {
        return binding;
    }

    public void setNoClick(View.OnClickListener click) {
        this.onClick = click;
    }

    public void setYesClick(View.OnClickListener click) {
        this.yesClick = click;
    }

    public void setContext(String context) {
        binding.title.setText(context);
    }

    public void setSubText(String context) {
        binding.context.setVisibility(View.VISIBLE);
        binding.context.setText(context);
    }

    private void init(Context context) {
        setDialogLocation();
        initClick(context);
    }

    private void initClick(Context context) {
        binding.no.setOnClickListener(this);
        binding.yes.setOnClickListener(this);
    }

    public void setDialogLocation() {
        Window win = this.getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setWindowAnimations(R.style.dialog_sen5_full);
        win.setBackgroundDrawableResource(android.R.color.transparent);
        win.setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.no && onClick != null) {
            onClick.onClick(v);
            dismiss();
        } else if (v.getId() == R.id.yes && yesClick != null) {
            yesClick.onClick(v);
            dismiss();
        }
    }
}
