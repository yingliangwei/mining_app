package com.xframe.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.xframe.widget.databinding.DialogPayPassBinding;

public class PayPass extends Dialog implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private final DialogPayPassBinding binding;
    private OnPay pay;
    private Context context;

    public PayPass(@NonNull Context context) {
        super(context);
        binding = DialogPayPassBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());
        setDialogLocation();
        initView();
    }

    private void initView() {
        binding.togglePwd.setOnCheckedChangeListener(this);
        binding.tvCancel.setOnClickListener(this);
        binding.tvConfirm.setOnClickListener(this);
    }

    public void setDialogLocation() {
        Window win = this.getWindow();
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

    public void setMoney(String money) {
        binding.money.setVisibility(View.VISIBLE);
        binding.money.setText(money);
    }

    public void setPay(OnPay pay) {
        this.pay = pay;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            //如果选中，显示密码
            binding.etPayPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            binding.etPayPwd.setSelection(0, binding.etPayPwd.getText().length());
        } else {
            //否则隐藏密码
            binding.etPayPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.etPayPwd.setSelection(0, binding.etPayPwd.getText().length());
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvCancel) {
            dismiss();
        } else if (v.getId() == R.id.tvConfirm) {
            String payPwd = binding.etPayPwd.getText().toString();
            dismiss();
            if (pay != null) {
                pay.onText(payPwd);
            }
        }
    }

    public interface OnPay {
        void onText(String pass);
    }
}
