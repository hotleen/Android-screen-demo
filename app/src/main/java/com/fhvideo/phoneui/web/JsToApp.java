package com.fhvideo.phoneui.web;

import android.webkit.JavascriptInterface;

import com.fhvideo.bean.FHLog;


/**
 * H5与app交互
 */
public class JsToApp {

    private FHWebCallBack callBack;
    public JsToApp(FHWebCallBack webCallBack){
        callBack = webCallBack;
    }
    @JavascriptInterface
    public void jsToApp(String method,String data){
        FHLog.addLog("新jsToApp("+method+"____"+data);
        if(callBack == null)
            return;
        callBack.jsToApp(method,data);
    }
    @JavascriptInterface
    public void jsToApp(String data){
        FHLog.addLog("新jsToApp("+data);
        if(callBack == null)
            return;
        callBack.jsToApp("data",data);

    }
    class Messages{
        private String methodName;

        private String methodParams;

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getMethodParams() {
            return methodParams;
        }

        public void setMethodParams(String methodParams) {
            this.methodParams = methodParams;
        }
    }
}
