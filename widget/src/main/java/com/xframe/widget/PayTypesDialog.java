package com.xframe.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.xframe.widget.databinding.DialogPayBinding;

/**
 * @author lyudony
 * @date 2020/8/4.
 * description：支付方式dialog
 */
public class PayTypesDialog extends Dialog {
    private DialogPayBinding binding;
    private OnPayType payType;

    //dialog构造方法 实现时需要传上下文和一个dialog主题
    public PayTypesDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogPayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //设置基本属性
        Window dialogWindow = getWindow();
        //设置在底部显示
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        //设置宽度和手机持平
        lp.width = AbsListView.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.y = 0;//设置Dialog距离底部的距离
        dialogWindow.setAttributes(lp);
        binding.wx.setChecked(true);
        initCheckBox();
        initClick();
    }

    private void initCheckBox() {
        binding.zfb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.wx.setChecked(false);
                }
            }
        });
        binding.wx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.zfb.setChecked(false);
                }
            }
        });
    }

    private void initClick() {
        binding.zfbL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.zfb.setChecked(true);
            }
        });
        binding.wxL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wxL.setClickable(true);
            }
        });
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.wx.isChecked() && payType != null) {
                    payType.OnPayType(0);
                    dismiss();
                } else if (payType != null) {
                    payType.OnPayType(1);
                    dismiss();
                }
            }
        });
    }


    public void setPayType(OnPayType payType) {
        this.payType = payType;
    }

    public interface OnPayType {
        void OnPayType(int type);
    }

}

