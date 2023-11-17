package com.mining.listen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.mining.listen.adapter.MainAdapter;
import com.mining.listen.databinding.ActivityMainBinding;
import com.mining.listen.entity.MainEntity;
import com.mining.listen.network.SocketOk;
import com.mining.listen.util.LogSharedUtil;
import com.mining.listen.util.SharedUtil;
import com.mining.util.Handler;
import com.mining.util.OnHandler;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.util.Base64;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity implements OnHandler, OnData, Toolbar.OnMenuItemClickListener {
    private ActivityMainBinding binding;
    private MainAdapter adapter;
    private final List<MainEntity> entities = new ArrayList<>();
    private final Handler handler = new Handler(Looper.myLooper(), this);
    private String key, pass, passphrase;

    private final OnData onData = new OnData() {
        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            if (code == 200) {
                JSONObject data = jsonObject.getJSONObject("data");
                key = data.getString("key");
                pass = data.getString("pass");
                passphrase = data.getString("passphrase");
                initWebSocketClion();
            } else if (code == 202) {
                SharedUtil sharedUtil = new SharedUtil(MainActivity.this);
                sharedUtil.clear();
            } else {
                String msg = jsonObject.getString("msg");
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(MainActivity.this);
            JSONObject jsonObject = sharedUtil.getLogin(6, 4);
            socketManage.print(jsonObject.toString());
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initRecycler();
        SocketManage.init(onData);
    }

    private void initToolbar() {
        binding.toolbar.setOnMenuItemClickListener(this);
    }

    private void initRecycler() {
        adapter = new MainAdapter(this, entities);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.recycler.setLayoutManager(manager);
        binding.recycler.setAdapter(adapter);
    }

    private void initWebSocketClion() {
        try {
            WebSocketClion webSocketClion = new WebSocketClion(new URI("wss://wsaws.okx.com:8443/ws/v5/business"));
            webSocketClion.connect();
        } catch (URISyntaxException e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 0) {
            JSONObject jsonObject = JSONObject.parseObject(str);
            MainEntity entity = new Gson().fromJson(jsonObject.toString(), MainEntity.class);
            entity.setJson(jsonObject.toString());
            entity.setText("未上传");
            entities.add(entity);
            adapter.notifyItemChanged(entities.size());

            LogSharedUtil logSharedUtil = new LogSharedUtil(MainActivity.this);
            logSharedUtil.put(entity.getPTime(), entity.getJson(), entity.getText());

            SharedUtil sharedUtil = new SharedUtil(MainActivity.this);
            JSONObject jsonObject1 = sharedUtil.getLogin(6, 3);
            jsonObject1.put("data", jsonObject);
            binding.recycler.scrollToPosition(adapter.getItemCount() - 1);
            new SocketOk(jsonObject1.toString(), this, entities.size());
        } else if (w == 1) {
            binding.text.setText(str);
        }
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        String msg = jsonObject.getString("msg");
        int position = jsonObject.getInteger("position");
        position = position - 1;

        MainEntity entity = entities.get(position);
        entity.setText(msg);
        entities.set(position, entity);
        adapter.notifyItemChanged(position);

        LogSharedUtil logSharedUtil = new LogSharedUtil(MainActivity.this);
        logSharedUtil.put(entity.getPTime(), entity.getJson(), entity.getText());
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        startActivity(new Intent(this, LogActivity.class));
        return false;
    }

    private class WebSocketClion extends WebSocketClient {

        public WebSocketClion(URI serverUri) {
            super(serverUri);
            MainActivity.TimerTask timerTask = new MainActivity.TimerTask(this);
            new Timer().schedule(timerTask, 0, 10_000);
        }

        @Override
        public void onOpen(ServerHandshake handshake) {
            handler.sendMessage(1, "连接成功");
            try {
                send(getUser().toString());
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMessage(String message) {
            System.out.println(message);
            if (message.equals("pong")) {
                return;
            }
            JSONObject jsonObject = JSONObject.parseObject(message);
            if (jsonObject == null) {
                return;
            }
            String event = jsonObject.getString("event");
            if (event == null) {
                JSONObject arg = jsonObject.getJSONObject("arg");
                if (arg == null) {
                    return;
                }
                JSONArray data = jsonObject.getJSONArray("data");
                JSONObject _data = data.getJSONObject(0);
                handler.sendMessage(0, _data.toString());
                return;
            }
            if (event.equals("login")) {
                send(getRecharge().toString());
                handler.sendMessage(1, "等待充值订单中...");
            }
        }


        @Override
        public void onClose(int code, String reason, boolean remote) {
            handler.sendMessage(1, "断开重连接中...");
            new Thread(this::reconnect).start();
        }

        @Override
        public void onError(Exception ex) {

        }

        public JSONObject getUser() throws NoSuchAlgorithmException, InvalidKeyException {
            String time = String.valueOf(System.currentTimeMillis() / 1000);
            JSONArray jsonArray = new JSONArray();
            JSONObject args = new JSONObject();
            args.put("apiKey", key);
            args.put("passphrase", passphrase);
            args.put("timestamp", time);
            args.put("sign", HmacSHA256(time, "/users/self/verify"));
            jsonArray.add(args);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("op", "login");
            jsonObject.put("args", jsonArray);
            return jsonObject;
        }

        public JSONObject getRecharge() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("op", "subscribe");
            JSONArray jsonArray = new JSONArray();
            JSONObject args = new JSONObject();
            args.put("channel", "deposit-info");
            jsonArray.add(args);
            jsonObject.put("args", jsonArray);
            return jsonObject;
        }

        public String HmacSHA256(String timestamp, String requestPath) throws NoSuchAlgorithmException, InvalidKeyException {
            String secret = pass;
            String message = timestamp + "GET" + requestPath;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secretKey);
            byte[] hash = sha256_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBytes(hash);
        }
    }

    private class TimerTask extends java.util.TimerTask {
        private final WebSocketClient webSocketClient;

        public TimerTask(WebSocketClion webSocketClion) {
            this.webSocketClient = webSocketClion;
        }

        @Override
        public void run() {
            if (webSocketClient.isOpen()) {
                webSocketClient.send("ping");
            }
        }
    }
}
