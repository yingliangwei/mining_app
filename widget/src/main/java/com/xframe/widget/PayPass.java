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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;


import org.w3c.dom.Text;

public class PayPass extends Dialog implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private final View binding;
    private OnPay pay;
    private Context context;

    public PayPass(@NonNull Context context) {
        super(context);
        binding = LayoutInflater.from(context).inflate(R.layout.dialog_pay_pass, new FrameLayout(context), false);
        setContentView(binding);
        setDialogLocation();
        initView();
    }

    private void initView() {
        ToggleButton toggleButton = binding.findViewById(R.id.togglePwd);
        TextView tvCancel = binding.findViewById(R.id.tvCancel);
        TextView tvConfirm = binding.findViewById(R.id.tvConfirm);
        toggleButton.setOnCheckedChangeListener(this);
        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
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
        TextView textView = binding.findViewById(R.id.money);
        textView.setVisibility(View.VISIBLE);
        textView.setText(money);
    }

    public void setPay(OnPay pay) {
        this.pay = pay;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ToggleButton toggleButton = binding.findViewById(R.id.togglePwd);
        EditText editText = binding.findViewById(R.id.etPayPwd);
        if (isChecked) {
            //如果选中，显示密码
            toggleButton.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            editText.setSelection(0, editText.getText().length());
        } else {
            //否则隐藏密码
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editText.setSelection(0, editText.getText().length());
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvCancel) {
            dismiss();
        } else if (v.getId() == R.id.tvConfirm) {
            EditText editText = binding.findViewById(R.id.etPayPwd);
            String payPwd = editText.getText().toString();
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
