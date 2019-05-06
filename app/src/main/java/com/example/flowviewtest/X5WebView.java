package com.example.flowviewtest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Debug;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.example.flowviewtest.entity.DetectedVideoInfo;
import com.example.flowviewtest.entity.VideoInfo;
import com.example.flowviewtest.utils.VideoFormatUtil;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yeqinfu on 2019/5/6
 */

public class X5WebView extends WebView {
    private String currentTitle = "";
    private String currentUrl = "";
    private LinkedBlockingQueue<DetectedVideoInfo> detectedTaskUrlQueue = new LinkedBlockingQueue<DetectedVideoInfo>();
    private SortedMap<String, VideoInfo> foundVideoInfoMap = Collections.synchronizedSortedMap(new TreeMap<String, VideoInfo>());
    private VideoSniffer videoSniffer;

    private WebChromeClient webChromeClient=new WebChromeClient(){
        @Override
        public void onReceivedTitle(WebView webView, String title) {
            super.onReceivedTitle(webView, title);
            currentTitle=title;
        }

    };

    private WebViewClient client = new WebViewClient() {
        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            super.onPageStarted(webView, s, bitmap);
            currentTitle=s;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {
            Log.d("yeqinfu","=shouldInterceptRequest=="+webResourceRequest.getUrl().toString());
            return super.shouldInterceptRequest(webView, webResourceRequest);

        }

        @Override
        public void onLoadResource(WebView webView, String s) {
            Log.d("yeqinfu","--onLoadResource->"+s);
            if (s.endsWith("m3u8")){
                String extension = MimeTypeMap.getFileExtensionFromUrl(s);
                if(VideoFormatUtil.containsVideoExtension(extension)){
                    detectedTaskUrlQueue.add(new DetectedVideoInfo(s,currentUrl,currentTitle));
                }
            }



            super.onLoadResource(webView, s);
        }

        /**
         * 防止加载网页时调起系统浏览器
         */
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!(url.startsWith("http") || url.startsWith("https"))) {
                //非http https协议 不动作
                return true;
            }

            //http https协议 在本webView中加载

            String extension = MimeTypeMap.getFileExtensionFromUrl(url);
            if(VideoFormatUtil.containsVideoExtension(extension)){
                detectedTaskUrlQueue.add(new DetectedVideoInfo(url,currentUrl,currentTitle));
                Log.d("yeqinfu", "shouldOverrideUrlLoading detectTaskUrlList.add(url):" + url);
                return true;
            }

            Log.d("yeqinfu", "shouldOverrideUrlLoading url="+url);
            view.loadUrl(url);
            return true;
        }

    };

    @SuppressLint("SetJavaScriptEnabled")
    public X5WebView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        this.setWebViewClient(client);
         this.setWebChromeClient(webChromeClient);
        // WebStorage webStorage = WebStorage.getInstance();
        initWebViewSettings();
        this.getView().setClickable(true);
    }

    private void initWebViewSettings() {







        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension
        // settings 的设计
    }

    public X5WebView(Context arg0) {
        super(arg0);
        setBackgroundColor(85621);
    }

}