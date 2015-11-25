package com.example.webviewtest;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText mEtAddress;
    private Button mBtnGo, mBtnClose;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEtAddress = (EditText) findViewById(R.id.et_address);
        mBtnGo = (Button) findViewById(R.id.btn_go);
        mBtnClose = (Button) findViewById(R.id.btn_close);
        mWebView = (WebView) findViewById(R.id.webview);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mBtnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl(mEtAddress.getText().toString());
            }
        });
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mEtAddress.setText("http://www.baidu.com");
        // 设置支持JavaScript脚本
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // 设置可以访问文件
        webSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
        }
        webSettings.setAllowContentAccess(true);
        // 设置可以支持缩放
        webSettings.setSupportZoom(true);
        // 设置默认缩放方式尺寸是far
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        // 设置出现缩放工具
        webSettings.setBuiltInZoomControls(false);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        if (!TextUtils.isEmpty(mEtAddress.getText().toString())) {
            mWebView.loadUrl(mEtAddress.getText().toString());

            // 设置WebViewClient
            mWebView.setWebViewClient(new WebViewClient() {
                // url拦截
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Log.d(TAG, "---shouldOverrideUrlLoading");
                    // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
                    view.loadUrl(url);
                    // 相应完成返回true
                    return super.shouldOverrideUrlLoading(view, url);
                }

                // 页面开始加载
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    Log.d(TAG, "---onPageStarted");
                    mProgressBar.setVisibility(View.VISIBLE);
                    super.onPageStarted(view, url, favicon);
                }

                // 页面加载完成
                @Override
                public void onPageFinished(WebView view, String url) {
                    Log.d(TAG, "---onPageFinished");
                    Log.i(TAG, "page count is "+mWebView.getChildCount());
                    mProgressBar.setVisibility(View.GONE);
                    super.onPageFinished(view, url);
                }

                // WebView加载的所有资源url
                @Override
                public void onLoadResource(WebView view, String url) {
//                    Log.d(TAG, "---onLoadResource");
                    super.onLoadResource(view, url);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Log.d(TAG, "---onReceivedError");
                    mWebView.loadData("<a></a>", "text/html", "utf-8");
                    super.onReceivedError(view, errorCode, description, failingUrl);
                }

            });

            // 设置WebChromeClient
            mWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                // 处理javascript中的alert
                public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                    Log.e(TAG, "---onJsAlert");
                    return super.onJsAlert(view, url, message, result);
                }

                @Override
                // 处理javascript中的confirm
                public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                    Log.e(TAG, "---onJsConfirm");
                    return super.onJsConfirm(view, url, message, result);
                }

                @Override
                // 处理javascript中的prompt
                public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
                                          final JsPromptResult result) {
                    Log.e(TAG, "---onJsPrompt");
                    return super.onJsPrompt(view, url, message, defaultValue, result);
                }

                // 设置网页加载的进度条
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    Log.e(TAG, "---onProgressChanged");
                    mProgressBar.setProgress(newProgress);
                    super.onProgressChanged(view, newProgress);
                }

                // 设置程序的Title
                @Override
                public void onReceivedTitle(WebView view, String title) {
                    Log.e(TAG, "---onReceivedTitle");
                    super.onReceivedTitle(view, title);
                }
            });
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "page count is "+mWebView.getChildCount());
        if (mWebView.canGoBack()) {
            Log.i(TAG, "go back");
            mWebView.goBack();
        } else {
            finish();
        }

        return false;


    }

    String _twoWayHashSource = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    String _twoWayHashTarget = "zyxwvutsrqponmlkjihgfedcbaZYXWVUTSRQPONMLKJIHGFEDCBA9876543210=-_";

    private String encodeUrlString(String url)
    {
        String hash = "";
        int hashpos = url.indexOf("#");
        if (hashpos > 0)
        {
            hash = url.substring(hashpos);
            url = url.substring(0, hashpos);
        }

        String args = "";
        int argspos = url.indexOf("?");
        if (argspos > 0)
        {
            args = url.substring(argspos);
            url = url.substring(0, argspos);
        }
        String out = "s";
        url = Base64.encodeToString(url.getBytes(),Base64.NO_WRAP);
        for (int i=0; i<url.length(); i++)
        {
            char c = url.charAt(i);
            int j = _twoWayHashSource.indexOf(c);
            out += _twoWayHashTarget.charAt(j);
        }

        return out + args + hash;
    }

    private String decodeUrlString(String url)
    {
        if (String.valueOf(url.charAt(0)).equals("s"));
        {
            String hash = "";
            int hashpos = url.indexOf("#");
            if (hashpos > 0)
            {
                hash = url.substring(hashpos);
                url = url.substring(0, hashpos);
            }

            String args = "";
            int argspos = url.indexOf("?");
            if (argspos > 0)
            {
                args = url.substring(argspos);
                url = url.substring(0, argspos);
            }

            String out = "";
            for (int i=1; i<url.length(); i++)
            {
                char c = url.charAt(i);
                int j = _twoWayHashTarget.indexOf(c);
                out += _twoWayHashSource.charAt(j);
            }

            url = Base64.decode(out, Base64.DEFAULT) + args + hash;
        }

        return url;
    }

}
