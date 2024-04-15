package com.fhvideo.phoneui.view;

import android.app.Activity;
import android.view.View;

import com.fhvideo.bean.UiEvent;
import com.fhvideo.fhcommon.utils.StringUtil;
import com.fhvideo.phoneui.web.FHWebCallBack;
import com.fhvideo.phoneui.web.FHWebFinishCallBack;
import com.fhvideo.phoneui.web.WebViewUtil;
import com.tencent.smtt.sdk.WebView;

import org.greenrobot.eventbus.EventBus;

/**
 * web页
 */

public class FHWebViewNoSingle extends FHBaseView {


    public FHWebViewNoSingle() {
    }

    public void loadUrl(String url) {

        if (StringUtil.isEmpty(url) || !isInit)
            return;
        fh_web_view.setVisibility(View.VISIBLE);
        webViewUtil.loadUrl(url);
    }

    private WebView fh_web_view;

    private WebViewUtil webViewUtil;

    public void init(Activity context, View view, String name, final FHWebFinishCallBack fhWebFinishCallBack) {
        super.init(context, view);
        webViewUtil = new WebViewUtil();
        fh_web_view = getView(name);
        fh_web_view.setZ(110);
        fh_web_view.setBackgroundColor(0); // 设置背景色
        webViewUtil.initWebView(fh_web_view, new FHWebCallBack() {

            @Override
            public void jsToApp(String method, String msg) {
                EventBus.getDefault().post(new UiEvent(method + "", msg, "jatoapp"));
            }
        }, new FHWebFinishCallBack() {
            @Override
            public void onPageFinished(WebView webView, String s) {
                if (fhWebFinishCallBack!=null){
                    fhWebFinishCallBack.onPageFinished(webView, s);
                }
            }
        });
    }

    public WebView getFh_web_view() {
        return fh_web_view;
    }

    public WebViewUtil getWebViewUtil() {
        return webViewUtil;
    }


    @Override
    public void hidden() {
        super.hidden();
        webViewUtil.loadUrl("about:blank");
        fh_web_view.setVisibility(View.GONE);
    }

    public void hiddenParent(View parentView) {
        webViewUtil.loadUrl("about:blank");
        if (parentView != null) {
            parentView.setVisibility(View.GONE);
        }

    }

    public void showParentAndWebview(View parentView) {
        fh_web_view.setVisibility(View.VISIBLE);
        if (parentView != null) {
            parentView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void release() {
        super.release();
        mContext = null;
    }
}