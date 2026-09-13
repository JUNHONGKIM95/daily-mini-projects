package com.junhong.onething;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewClientCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
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
    }

    @Override
    protected void onDestroy() {
        if (webView != null) webView.destroy();
        super.onDestroy();
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
            runOnUiThread(() -> OneThingWidget.updateAll(MainActivity.this));
        }
    }
}
