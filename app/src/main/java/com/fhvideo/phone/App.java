package com.fhvideo.phone;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.fhvideo.FHLiveClient;
import com.fhvideo.FHLiveSessionParams;
import com.fhvideo.adviser.FHTellerParams;
import com.fhvideo.phoneui.FHVideoManager;
import com.tencent.smtt.sdk.QbSdk;

import java.io.File;

public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = getApplicationContext();


//        FHTellerParams.setServerVersion(FHTellerParams.V_2_8_4_03);

        //一、初始化
        FHLiveHelper.getHelper().initSDK(this);
        FHLiveSessionParams.setSubStreamAvailable(true);
        registerActivity();
        initX5WebView(this);
    }
    private void initX5WebView(Context context){
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
            }
            @Override
            public void onCoreInitFinished() {
            }
        };
        QbSdk.setDownloadWithoutWifi(true);
        //x5内核初始化接口
        QbSdk.initX5Environment(context.getApplicationContext(),  cb);
    }

    private static Context instance;
    public static Context getInstance(){
        return instance;
    }

    private static int actiNum = 0,preNum = 0;
    private void registerActivity() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                preNum = actiNum;
                actiNum++;
                if(preNum<=0 && actiNum >0 &uptVideoType){
                    muteVideo(false);
                }

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                preNum = actiNum;
                actiNum--;
                if(preNum>0 && actiNum <=0)
                    muteVideo(true);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }
    private boolean uptVideoType = false;
    private void muteVideo(boolean mute){
        if(!FHLiveClient.getInstance().isInSession())
            return;
        if(mute && FHTellerParams.getVideoType().equals(FHTellerParams.FH_VIDEO_TYPE_VIDEO)){
            uptVideoType = true;
            FHVideoManager.getInstance().stopCamera();
        }
        if(!mute && uptVideoType){
            uptVideoType = false;
            FHVideoManager.getInstance().startCamera();

        }

    }

    public static boolean isBackGround(){
        if(actiNum==0)
            return true;
        else
            return false;
    }
}
