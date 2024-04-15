package com.fhvideo.phoneui.web;

import android.annotation.SuppressLint;

import com.fhvideo.FHParams;
import com.fhvideo.bean.FHLog;
import com.fhvideo.fhcommon.utils.StringUtil;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;


@SuppressLint("SetJavaScriptEnabled")
public class WebViewUtil {

    public void loadImg(String params){
        if(webView == null)
            return;
        webView.evaluateJavascript("javascript:loadImg('"+params +"')",null);
    }
    public void subSuccess(String params){
        if(webView == null)
            return;
        webView.evaluateJavascript("javascript:subSuccess('"+params +"')",null);
    }

    public void appToJs(String method,String params){
        if(webView == null)
            return;
        if(StringUtil.isEmpty(method))
            return;
        webView.evaluateJavascript("javascript:appToJs('"+method+"','"+params +"')",null);
    }
    public void loadUrl(String url){
        if(webView == null)
            return;

        if(StringUtil.isEmpty(url))
            return;
        webView.loadUrl(url);

    }

    private WebView webView;
    public void initWebView(WebView view, com.fhvideo.phoneui.web.FHWebCallBack callBack){
        webView = view;
        if(webView == null)
            return;
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccess(false);
        webSettings.setAllowFileAccessFromFileURLs(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setBlockNetworkImage(false);
        //设置编码方式
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        webView.addJavascriptInterface(new com.fhvideo.phoneui.web.JsToApp(callBack), "android");
        webView.removeJavascriptInterface("searchBoxJavaBridge_");
        webView.removeJavascriptInterface("accessibility");
        webView.removeJavascriptInterface("accessibilityTraversal");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                FHLog.addLog("页面加载出错："+error);
                super.onReceivedError(view, request, error);
                //EventBus.getDefault().post(new UiEvent("","onReceivedError"));

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                webView.loadUrl(s);
                return true;
            }
            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);

                webView.evaluateJavascript("javascript:appToJs('setUid','"+ FHParams.getUid() +"')",null);
            }

            @Override
            public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
                super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
                FHLog.addLog("页面加载出错：onReceivedHttpError");
               // EventBus.getDefault().post(new UiEvent("","onReceivedError"));


            }
        });
    }

    /**
     * 回调-->webview加载成功
     * 
     * @param view
     * @param callBack
     * @param fhWebFinishCallBack
     */
    public void initWebView(WebView view, FHWebCallBack callBack, final FHWebFinishCallBack fhWebFinishCallBack){
        webView = view;
        if(webView == null)
            return;
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccess(false);
        webSettings.setAllowFileAccessFromFileURLs(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setBlockNetworkImage(false);
        //设置编码方式
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        webView.addJavascriptInterface(new JsToApp(callBack), "android");
        webView.removeJavascriptInterface("searchBoxJavaBridge_");
        webView.removeJavascriptInterface("accessibility");
        webView.removeJavascriptInterface("accessibilityTraversal");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                FHLog.addLog("页面加载出错："+error);
                super.onReceivedError(view, request, error);
                //EventBus.getDefault().post(new UiEvent("","onReceivedError"));

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                webView.loadUrl(s);
                return true;
            }
            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                if (fhWebFinishCallBack!=null){
                    fhWebFinishCallBack.onPageFinished(webView,s);
                }
                webView.evaluateJavascript("javascript:appToJs('setUid','"+ FHParams.getUid() +"')",null);
            }

            @Override
            public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
                super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
                FHLog.addLog("页面加载出错：onReceivedHttpError");
               // EventBus.getDefault().post(new UiEvent("","onReceivedError"));


            }
        });
    }

}
