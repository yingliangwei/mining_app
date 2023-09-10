package com.mining.usdtrecharge;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mining.usdtrecharge.databinding.ActivityMainBinding;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity implements Handler.Callback {
    private ActivityMainBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper(), this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initWebSocketClion();
    }

    private void initWebSocketClion() {
        try {
            WebSocketClion webSocketClion = new WebSocketClion(new URI("wss://wsaws.okx.com:8443/ws/v5/business"), handler);
            webSocketClion.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        String meg = (String) msg.obj;
        String currentText = binding.text.getText().toString();
        binding.text.setText(String.format("%s\n%s", meg, currentText));
        return true;
    }
}
