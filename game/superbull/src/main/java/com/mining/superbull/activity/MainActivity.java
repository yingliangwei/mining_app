package com.mining.superbull.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.mining.superbull.R;
import com.mining.superbull.databinding.ActivityMainBinding;
import com.mining.superbull.databinding.DialogMainBinding;
import com.mining.superbull.util.MusicServer;
import com.mining.superbull.util.SharedUtil;
import com.mining.util.StringUtil;
import com.plugin.activity.PluginActivity;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends PluginActivity implements View.OnClickListener, OnData {
    private ActivityMainBinding binding;
    private final Map<String, String> map = new HashMap<>();
    private String super_id = "1";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Window window = thisContex.getWindow();
        window.setNavigationBarColor(Color.TRANSPARENT);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
        SocketManage.init(this);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MusicServer.stop();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MusicServer.play(this, R.raw.bg);
    }

    private void initToolbar() {
        binding.exit.setOnClickListener(v -> finish());
    }

    private void initView() {
        binding.start.setOnClickListener(this);
        binding.bettingx.setOnClickListener(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(20, 1);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        if (jsonObject == null) {
            return;
        }
        int code = jsonObject.getInteger("code");
        if (code != 200) {
            String msg = jsonObject.getString("msg");
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return;
        }
        String gem = jsonObject.getString("gem");
        binding.gem.setText(StringUtil.toRe(gem));
        JSONArray data = jsonObject.getJSONArray("data");
        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject1 = data.getJSONObject(i);
            String _gem = jsonObject1.getString("gem");
            String id = jsonObject1.getString("id");
            if (i == 0) {
                binding.betting.setText(StringUtil.toRe(_gem));
                this.super_id = id;
            }
            map.put(id, _gem);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start) {
            binding.layoutBg.super1.stop();
            binding.layoutBg.super2.stop();
            binding.layoutBg.super3.stop();
            SocketManage.init(new betting(super_id));
        } else if (v.getId() == R.id.bettingx) {
            MyDialog dialog = new MyDialog(this);
            dialog.show();
        }
    }


    private class betting implements OnData {

        public final String super_id;

        public betting(String super_id) {
            this.super_id = super_id;
        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(MainActivity.this);
            JSONObject jsonObject = sharedUtil.getLogin(20, 2);
            jsonObject.put("super_id", super_id);
            socketManage.print(jsonObject.toString());
        }

        @Override
        public void error(String error) {
            Toast.makeText(thisContex, "网络异常", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            if (code != 200) {
                String msg = jsonObject.getString("msg");
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                return;
            }
            int super_1 = jsonObject.getInteger("super");
            int super_2 = jsonObject.getInteger("super_1");
            int super_3 = jsonObject.getInteger("super_2");
            String super_gem = jsonObject.getString("super_gem");
            String user_gem = jsonObject.getString("user_gem");
            notSelected(super_1, super_2, super_3, super_gem, user_gem);
        }
    }

    private void notSelected(int super_1, int super_2, int super_3, String super_gem, String user_gem) {
        binding.layoutBg.super1.start(super_1);
        binding.layoutBg.super2.start(super_2);
        binding.layoutBg.super3.start(super_3);
        binding.layoutBg.super1.setBinding(binding, super_gem, user_gem);
    }

    private class MyDialog extends Dialog implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
        private final DialogMainBinding binding;

        public MyDialog(@NonNull Context context) {
            super(context);
            binding = DialogMainBinding.inflate(LayoutInflater.from(context));
            setContentView(binding.getRoot());
            initView();
            setDialogLocation();
        }

        private void initView() {
            binding.wrong.setOnClickListener(this);
            binding.radio.setOnCheckedChangeListener(this);
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                String value = map.get(key);
                RadioButton button;
                if (key.equals(getKey(MainActivity.this.binding.betting.getText().toString()))) {
                    button = getRadioButton(key, value, true);
                } else {
                    button = getRadioButton(key, value, false);
                }
                binding.radio.addView(button, new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }

        private RadioButton getRadioButton(String id, String gem, boolean is) {
            RadioButton button = new RadioButton(getContext());
            button.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            int integer = Integer.parseInt(id);
            button.setId(integer);
            button.setText(StringUtil.toRe(gem));
            button.setChecked(is);
            return button;
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
            win.setBackgroundDrawableResource(android.R.color.transparent);
            win.setAttributes(lp);
        }

        @Override
        public void onClick(View v) {
            dismiss();
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            String key = String.valueOf(checkedId);
            String value = map.get(key);
            MainActivity.this.super_id = key;
            MainActivity.this.binding.betting.setText(StringUtil.toRe(value));
            dismiss();
        }

        public String getKey(String str) {
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                String value = StringUtil.toRe(map.get(key));
                if (str.equals(value)) {
                    return key;
                }
            }
            return "1";
        }
    }
}
