package com.mining.mining.activity.c2s.pager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.R;
import com.mining.mining.activity.c2s.OrderManageActivity;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerAddC2sBinding;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.util.ArithHelper;
import com.mining.util.Handler;
import com.mining.util.MessageEvent;
import com.mining.util.OnHandler;
import com.mining.util.StringUtil;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.PayPass;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

public class AddC2cPager extends RecyclerAdapter implements OnData, OnHandler, View.OnClickListener, Toolbar.OnMenuItemClickListener {
    private final SharedPreferences sharedPreferences;
    private PagerAddC2sBinding binding;
    private final Activity activity;
    private String pass;
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private final OnData onData = new OnData() {
        @Override
        public void handle(String ds) {
            try {
                JSONObject jsonObject = new JSONObject(ds);
                String msg = jsonObject.getString("msg");
                handler.sendMessage(0, msg);
                SocketManage.init(AddC2cPager.this);
                EventBus.getDefault().post(new MessageEvent(4, ""));
                EventBus.getDefault().post(new MessageEvent(3, ""));
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }

        @Override
        public void connect(SocketManage socketManage) {
            try {
                String id = sharedPreferences.getString("id", null);
                String _key = sharedPreferences.getString("_key", null);
                if (id == null || _key == null) {
                    return;
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", 4);
                jsonObject.put("code", 9);
                jsonObject.put("pass", pass);
                jsonObject.put("usdt", binding.buyUsdt1.getText().toString());
                jsonObject.put("gem", binding.gemBuy.getText().toString());
                jsonObject.put("id", id);
                jsonObject.put("_key", _key);
                socketManage.print(jsonObject.toString());
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    };

    private final OnData onData1 = new OnData() {
        @Override
        public void handle(String ds) {
            try {
                JSONObject jsonObject = new JSONObject(ds);
                String msg = jsonObject.getString("msg");
                handler.sendMessage(0, msg);
                EventBus.getDefault().post(new MessageEvent(1, ""));
                EventBus.getDefault().post(new MessageEvent(4, ""));
                SocketManage.init(AddC2cPager.this);
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }

        @Override
        public void connect(SocketManage socketManage) {
            try {
                String id = sharedPreferences.getString("id", null);
                String _key = sharedPreferences.getString("_key", null);
                if (id == null || _key == null) {
                    return;
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", 4);
                jsonObject.put("code", 10);
                jsonObject.put("pass", pass);
                jsonObject.put("usdt", binding.sellUsdt1.getText().toString());
                jsonObject.put("gem", binding.sellGem.getText().toString());
                jsonObject.put("id", id);
                jsonObject.put("_key", _key);
                socketManage.print(jsonObject.toString());
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    };
    private double usdt;

    public AddC2cPager(Activity context) {
        super(context);
        this.activity = context;
        sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        EventBus.getDefault().register(this);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event) {
        if (event.getW() == 5) {
            SocketManage.init(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        try {
            String id = sharedPreferences.getString("id", null);
            String _key = sharedPreferences.getString("_key", null);
            if (id == null || _key == null) {
                return;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 4);
            jsonObject.put("code", 8);
            jsonObject.put("id", id);
            jsonObject.put("_key", _key);
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handle(String ds) {
        try {
            JSONObject jsonObject = new JSONObject(ds);
            int code = jsonObject.getInt("code");
            if (code == 200) {
                JSONObject data = jsonObject.getJSONObject("data");
                handler.sendMessage(1, data.toString());
            }
            handler.sendMessage(2, "");
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void error(String error) {
        handler.sendMessage(3, "");
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 0) {
            Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (view == null) {
                return;
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } else if (w == 1) {
            initUserData(str);
        } else if (w == 2) {
            binding.Smart.finishRefresh(1000, true, false);
        } else if (w == 3) {
            binding.Smart.finishRefresh(1000, false, false);
        }
    }

    private void initUserData(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            String buy_usdt = jsonObject.getString("buy_usdt");
            String sell_usdt = jsonObject.getString("sell_usdt");
            String usdt = jsonObject.getString("usdt");
            String gem = jsonObject.getString("gem");
            binding.buyUsdt.setText(StringUtil.toRe(buy_usdt));
            binding.buyUsdt1.setText(StringUtil.toRe(buy_usdt));
            binding.buyUsdt1.setSelection(0, binding.buyUsdt1.getText().length());
            binding.sellUsdt.setText(StringUtil.toRe(sell_usdt));
            binding.sellUsdt1.setText(StringUtil.toRe(sell_usdt));
            binding.sellUsdt1.setSelection(0, binding.sellUsdt1.getText().length());
            binding.usdt.setText(StringUtil.toRe(usdt));
            binding.gem.setText(StringUtil.toRe(gem));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sell_all) {
            String gem = binding.gem.getText().toString();
            binding.sellGem.setText(String.valueOf((int) Double.parseDouble(gem)));
            binding.sellGem.setSelection(0, binding.sellGem.getText().length());
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
                    try {
                        JSONObject jsonObject = new JSONObject(ds);
                        int code = jsonObject.getInt("code");
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
                    } catch (Exception e) {
                        e.fillInStackTrace();
                    }
                }

                @Override
                public void connect(SocketManage socketManage) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type", 5);
                        jsonObject.put("code", 7);
                        socketManage.print(jsonObject.toString());
                    } catch (Exception e) {
                        e.fillInStackTrace();
                    }
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
                    try {
                        JSONObject jsonObject = new JSONObject(ds);
                        int code = jsonObject.getInt("code");
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
                    } catch (Exception e) {
                        e.fillInStackTrace();
                    }
                }

                @Override
                public void connect(SocketManage socketManage) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type", 5);
                        jsonObject.put("code", 7);
                        socketManage.print(jsonObject.toString());
                    } catch (Exception e) {
                        e.fillInStackTrace();
                    }
                }
            };
            SocketManage.init(onData2);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.manage) {
            getContext().startActivity(new Intent(getContext(), OrderManageActivity.class));
        }
        return true;
    }
}
