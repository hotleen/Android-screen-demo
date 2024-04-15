package com.fhvideo.phoneui.view;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fhvideo.bean.UiEvent;
import com.fhvideo.fhcommon.utils.StringUtil;
import com.fhvideo.phoneui.web.FHWebCallBack;
import com.fhvideo.phoneui.web.WebViewUtil;
import com.tencent.smtt.sdk.WebView;

import org.greenrobot.eventbus.EventBus;

/**
 * web页
 */

public class FHWebView extends FHBaseView {
    private static FHWebView instance;
    public static FHWebView getInstance(){
        if(instance == null)
            instance = new FHWebView();
        return instance;
    }





    public void loadUrl(String url){

        if(StringUtil.isEmpty(url) || !isInit)
            return;
        if(showClose)
            iv_web_close.setVisibility(View.VISIBLE);
        fh_web_view.setVisibility(View.VISIBLE);
        webViewUtil.loadUrl(url);
    }
    private WebView fh_web_view;

    private WebViewUtil webViewUtil;
    private ImageView iv_web_close;
    @Override
    public void init(Activity context, View view) {
        super.init(context, view);
        webViewUtil = new WebViewUtil();
        iv_web_close= getView("iv_web_close", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidden();
            }
        });
        iv_web_close.setZ(110);
        fh_web_view = getView("fh_web_view");
        fh_web_view.setZ(110);
        fh_web_view.setBackgroundColor(0); // 设置背景色
        webViewUtil.initWebView(fh_web_view, new FHWebCallBack() {

            @Override
            public void jsToApp(String method, String msg) {
                EventBus.getDefault().post(new UiEvent(method+"","jatoapp"));

                //
            }
        });


    }


    public ImageView getIv_web_close() {
        return iv_web_close;
    }

    private boolean showClose = false;
    public void uptParams(int top){
        showClose = true;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fh_web_view.getLayoutParams();
        params.topMargin = top;
        fh_web_view.setLayoutParams(params);
        RelativeLayout.LayoutParams closeParams = (RelativeLayout.LayoutParams) iv_web_close.getLayoutParams();
        closeParams.topMargin = top+closeParams.rightMargin;
        iv_web_close.setLayoutParams(closeParams);
    }


    @Override
    public void hidden() {
        super.hidden();
        if(showClose)
            iv_web_close.setVisibility(View.GONE);
        webViewUtil.loadUrl("about:blank");
        fh_web_view.setVisibility(View.GONE);
    }


    @Override
    public void release() {
        super.release();
        instance = null;
        mContext = null;
    }
}