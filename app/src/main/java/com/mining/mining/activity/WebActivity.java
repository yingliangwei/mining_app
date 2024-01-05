package com.mining.mining.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mining.mining.R;
import com.mining.mining.databinding.ActivityWebBinding;
import com.mining.util.StatusBarUtil;

public class WebActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    private String url;
    private ActivityWebBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersive(this, true);
        binding = ActivityWebBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initWeb();
    }

    private void initWeb() {
        binding.web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                binding.toolbar.setTitle(title);
            }
        });
        binding.web.setWebViewClient(new WebViewClient());
        OnBackPressedDispatcher dispatcher = getOnBackPressedDispatcher();
        dispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.web.canGoBack()) {
                    binding.web.goBack();
                } else {
                    if (isEnabled()) {
                        setEnabled(false);
                        onBackPressed();
                    }
                }
            }
        });
        initSettings(binding.web.getSettings());
        url = getIntent().getStringExtra("url");
        if (url == null) {
            finish();
            return;
        }
        binding.web.loadUrl(url);
    }

    private void initSettings(WebSettings settings) {
        settings.setJavaScriptEnabled(true);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.toolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.copy) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", url);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.web) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
        return false;
    }
}
