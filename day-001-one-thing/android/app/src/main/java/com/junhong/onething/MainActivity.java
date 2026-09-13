package com.junhong.onething;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewClientCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private static final int NOTIFICATION_PERMISSION_REQUEST = 1001;
    private static final String PERMISSION_PREFS = "one_thing_permissions";
    private static final String KEY_NOTIFICATION_ASKED = "notification_asked";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        webView = new WebView(this);
        webView.setBackgroundColor(getColor(R.color.paper));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        webView.addJavascriptInterface(new WidgetBridge(), "AndroidWidget");
        webView.setWebViewClient(new WebViewClientCompat() {
            @Override
            public android.webkit.WebResourceResponse shouldInterceptRequest(
                    WebView view,
                    android.webkit.WebResourceRequest request
            ) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }
        });

        setContentView(webView);
        webView.loadUrl("https://appassets.androidplatform.net/assets/index.html");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) webView.evaluateJavascript("window.syncFromAndroidWidget?.()", null);
        ensureNotificationPermissionAndSync();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) webView.destroy();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            TodayNotification.sync(this);
        }
    }

    private void ensureNotificationPermissionAndSync() {
        TodayStore.Task task = TodayStore.read(this);
        if (task.text.isEmpty() || task.completed) {
            TodayNotification.sync(this);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            boolean alreadyAsked = getSharedPreferences(PERMISSION_PREFS, MODE_PRIVATE)
                    .getBoolean(KEY_NOTIFICATION_ASKED, false);
            if (!alreadyAsked) {
                getSharedPreferences(PERMISSION_PREFS, MODE_PRIVATE)
                        .edit()
                        .putBoolean(KEY_NOTIFICATION_ASKED, true)
                        .apply();
                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST
                );
            }
            return;
        }
        TodayNotification.sync(this);
    }

    private final class WidgetBridge {
        @JavascriptInterface
        public String readToday() {
            TodayStore.Task task = TodayStore.read(MainActivity.this);
            JSONObject result = new JSONObject();
            try {
                result.put("text", task.text);
                result.put("completed", task.completed);
            } catch (JSONException ignored) {
                return "{}";
            }
            return result.toString();
        }

        @JavascriptInterface
        public void syncTask(String text, boolean completed) {
            TodayStore.save(MainActivity.this, text, completed);
            runOnUiThread(() -> {
                OneThingWidget.updateAll(MainActivity.this);
                ensureNotificationPermissionAndSync();
            });
        }
    }
}
