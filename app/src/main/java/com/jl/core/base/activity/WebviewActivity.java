package com.jl.core.base.activity;

import android.os.Build;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.jl.core.log.LogUtils;
import com.jl.core.utils.StringUtil;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.ActivityWebviewBinding;


// Webview公共页面
public class WebviewActivity extends BaseActivity {
    private ActivityWebviewBinding binding;
    private String mTitle;
    private RelativeLayout mLlMain;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    protected void initData() {
        binding = getBindView();
        mTitle = getIntent().getStringExtra("title");
        mLlMain = (RelativeLayout) findViewById(R.id.rl_main);
        if (StringUtil.isEmpty(mTitle)){
            mLlMain.setVisibility(View.GONE);
        }else {
            mLlMain.setVisibility(View.VISIBLE);
            setTitle(mTitle);
        }
        initWebView();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    private void initWebView() {
        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setDomStorageEnabled(true);//主要是这句
        webSettings.setJavaScriptEnabled(true);//启用js
//        webSettings.setBlockNetworkImage(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);

//        webSettings.setAppCacheEnabled(true);注意下是不是版本问题
        webSettings.setDomStorageEnabled(true);
        webSettings.supportMultipleWindows();
        webSettings.setAllowContentAccess(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);

//        解决图片不显示问题
//        android webview 从Lollipop(5.0)开始webview默认不允许混合模式，https当中不能加载http资源，而开发的
//        时候可能使用的是https的链接，但是链接中的图片可能是http的，所以需要设置开启。
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setBlockNetworkImage(false);

        binding.webView.setWebChromeClient(new WebChromeClient());//这行最好不要丢掉
        //该方法解决的问题是打开浏览器不调用系统浏览器，直接用webview打开
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

//        binding.webView.getSettings().setJavaScriptEnabled(true);
//        binding.webView.getSettings().setDomStorageEnabled(true);
//        binding.webView.getSettings().setSupportMultipleWindows(true);
//        binding.webView.getSettings().setLoadWithOverviewMode(true);
//        binding.webView.getSettings().setUseWideViewPort(true);
//        binding.webView.setWebChromeClient(new WebChromeClient());

        LogUtils.i(getIntent().getStringExtra("url"));
        binding.webView.loadUrl(getIntent().getStringExtra("url"));

    }
}
