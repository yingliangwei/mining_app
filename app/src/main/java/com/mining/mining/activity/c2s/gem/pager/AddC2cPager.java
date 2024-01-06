package com.mining.mining.activity.c2s.gem.pager;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.activity.c2s.gem.activity.OrderActivity;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerAddC2sBinding;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.util.SharedUtil;
import com.mining.util.ArithHelper;
import com.mining.util.StringUtil;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.PayPass;

public class AddC2cPager extends RecyclerAdapter implements OnData, View.OnClickListener, Toolbar.OnMenuItemClickListener {
    private PagerAddC2sBinding binding;
    private final Activity activity;
    private String pass;
    private final OnData onData = new OnData() {
        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            String msg = jsonObject.getString("msg");
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            SocketManage.init(AddC2cPager.this);
        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(getContext());
            JSONObject jsonObject = sharedUtil.getLogin(4, 9);
            jsonObject.put("pass", pass);
            jsonObject.put("usdt", binding.buyUsdt1.getText().toString());
            jsonObject.put("gem", binding.gemBuy.getText().toString());
            socketManage.print(jsonObject.toString());
        }
    };

    private final OnData onData1 = new OnData() {
        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            String msg = jsonObject.getString("msg");
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            SocketManage.init(AddC2cPager.this);
        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(getContext());
            JSONObject jsonObject = sharedUtil.getLogin(4, 10);
            jsonObject.put("pass", pass);
            jsonObject.put("usdt", binding.sellUsdt1.getText().toString());
            jsonObject.put("gem", binding.sellGem.getText().toString());
            socketManage.print(jsonObject.toString());
        }
    };
    private double usdt;

    public AddC2cPager(Activity context) {
        super(context);
        this.activity = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerAddC2sBinding.inflate(LayoutInflater.from(getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        initToolbar();
        initView();
        initSmart();
        SocketManage.init(this);
    }

    private void initSmart() {
        binding.Smart.setRefreshHeader(new ClassicsHeader(getContext()));
        binding.Smart.setOnRefreshListener(refreshLayout -> SocketManage.init(AddC2cPager.this));
    }

    private void initToolbar() {
        binding.toolbar.setOnMenuItemClickListener(this);
        binding.toolbar.setNavigationOnClickListener(v -> activity.finish());
    }

    private void initView() {
        binding.registrationBuy.setOnClickListener(this);
        binding.sellAll.setOnClickListener(this);
        binding.registrationSell.setOnClickListener(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(getContext());
        JSONObject jsonObject = sharedUtil.getLogin(4, 8);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            JSONObject data = jsonObject.getJSONObject("data");
            initUserData(data.toString());
        }
        binding.Smart.finishRefresh(1000, true, false);

    }

    @Override
    public void error(String error) {
        
        binding.Smart.finishRefresh(1000, false, false);

    }

    private void initUserData(String str) {
        JSONObject jsonObject = JSONObject.parseObject(str);
        String buy_usdt = jsonObject.getString("buy_usdt");
        String sell_usdt = jsonObject.getString("sell_usdt");
        String usdt = jsonObject.getString("usdt");
        String gem = jsonObject.getString("gem");
        binding.buyUsdt.setText(StringUtil.toRe(buy_usdt));
        binding.buyUsdt1.setText(StringUtil.toRe(buy_usdt));
        binding.buyUsdt1.setSelection(binding.buyUsdt1.getText().length());
        binding.sellUsdt.setText(StringUtil.toRe(sell_usdt));
        binding.sellUsdt1.setText(StringUtil.toRe(sell_usdt));
        binding.sellUsdt1.setSelection(binding.sellUsdt1.getText().length());
        binding.usdt.setText(StringUtil.toRe(usdt));
        binding.gem.setText(StringUtil.toRe(gem));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sell_all) {
            String gem = binding.gem.getText().toString();
            binding.sellGem.setText(String.valueOf((int) Double.parseDouble(gem)));
            binding.sellGem.setSelection(binding.sellGem.getText().length());
        } else if (v.getId() == R.id.registration_buy) {
            if (binding.buyUsdt1.getText().length() == 0 || binding.gemBuy.getText().length() == 0) {
                Toast.makeText(getContext(), "信息为空", Toast.LENGTH_SHORT).show();
                return;
            }
            String a = binding.buyUsdt1.getText().toString();
            String b = binding.gemBuy.getText().toString();
            usdt = ArithHelper.mul(a, b);
            OnData onData2 = new OnData() {
                @Override
                public void handle(String ds) {
                    JSONObject jsonObject = JSONObject.parseObject(ds);
                    int code = jsonObject.getInteger("code");
                    if (code == 200) {
                        double commission = jsonObject.getDouble("commission");
                        double commissionUsdt = ArithHelper.mul(usdt, commission);
                        double result = ArithHelper.add(commissionUsdt, usdt);
                        PayPass payPass = new PayPass(getContext());
                        payPass.setMoney("消耗USDT:" + result + "\n手续费:" + commissionUsdt);
                        payPass.setPay(pass -> {
                            AddC2cPager.this.pass = pass;
                            SocketManage.init(onData);
                        });
                        payPass.show();
                    }
                }

                @Override
                public void connect(SocketManage socketManage) {
                    SharedUtil sharedUtil = new SharedUtil(getContext());
                    JSONObject jsonObject = sharedUtil.getLogin(5, 7);
                    socketManage.print(jsonObject.toString());
                }
            };
            SocketManage.init(onData2);
        } else if (v.getId() == R.id.registration_sell) {
            if (binding.sellUsdt1.getText().length() == 0 || binding.sellGem.getText().length() == 0) {
                Toast.makeText(getContext(), "信息为空", Toast.LENGTH_SHORT).show();
                return;
            }
            OnData onData2 = new OnData() {
                @Override
                public void handle(String ds) {
                    JSONObject jsonObject = JSONObject.parseObject(ds);
                    int code = jsonObject.getInteger("code");
                    if (code == 200) {
                        double commission = jsonObject.getDouble("commission");
                        double gem = Double.parseDouble(binding.sellGem.getText().toString());
                        double commissionUsdt = ArithHelper.mul(gem, commission);
                        double result = ArithHelper.add(gem, commissionUsdt);
                        PayPass payPass = new PayPass(getContext());
                        payPass.setMoney("消耗宝石:" + result + "\n手续费:" + commissionUsdt);
                        payPass.setPay(pass -> {
                            AddC2cPager.this.pass = pass;
                            SocketManage.init(onData1);
                        });
                        payPass.show();
                    }
                }

                @Override
                public void connect(SocketManage socketManage) {
                    SharedUtil sharedUtil = new SharedUtil(getContext());
                    JSONObject jsonObject = sharedUtil.getLogin(5, 7);
                    socketManage.print(jsonObject.toString());
                }
            };
            SocketManage.init(onData2);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.manage) {
            getContext().startActivity(new Intent(getContext(), OrderActivity.class));
        }
        return true;
    }
}
