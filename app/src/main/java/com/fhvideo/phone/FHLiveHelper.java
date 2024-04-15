package com.fhvideo.phone;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.fhvideo.FHLiveClient;
import com.fhvideo.FHLiveMacro;
import com.fhvideo.FHLiveSessionParams;
import com.fhvideo.FHLiveTeller;
import com.fhvideo.bank.utils.SharedPreUtil;
import com.fhvideo.bean.FHLog;
import com.fhvideo.bean.GsonUtil;
import com.fhvideo.bean.UiEvent;
import com.fhvideo.fhcommon.FHBusiCallBack;
import com.fhvideo.fhcommon.params.FHVideoParams;
import com.fhvideo.fhcommon.utils.StringUtil;
import com.fhvideo.phone.test.TestView;
import com.fhvideo.phoneui.FHLiveActivity;
import com.fhvideo.phoneui.FHUIConstants;
import com.fhvideo.phoneui.FHVideoManager;
import com.fhvideo.phoneui.LiveData;
import com.fhvideo.phoneui.busi.FHEventListener;
import com.fhvideo.phoneui.utils.ToastUtil;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;

public class FHLiveHelper {
    public static final String SHOW_HOME = "showHome"//显示 首页
            , SHOW_MY = "showMy"//显示 我的
            , HIDE_LOGIN = "hideLogin"//隐藏登录页
            , SHOW_PROGRESS_BAR = "showProgressBar", SHOW_HINT = "showHint";
    public static boolean isLogin = false;//是否登录

    private static FHLiveHelper instance;

    public static FHLiveHelper getHelper() {
        if (instance == null)
            synchronized (FHLiveHelper.class) {
                if (instance == null)
                    instance = new FHLiveHelper();
            }
        return instance;
    }

    private Context context;

    /**
     * 初始化
     */
    public void initSDK(Application application) {
        context = application.getApplicationContext();
        //配置包名
        FHVideoManager.getInstance().setPackageName("com.fhvideo.phone");
        //设置支持arm64-v8a架构
        FHVideoManager.getInstance().setArm64(true);
        //开启debug日志
        FHVideoManager.getInstance().openDebug(true);

        String url = SharedPreUtil.getInstance(context).getKeyStringValue("demo_url", BuildConfig.BASE_URL);
        //初始化SDK
        FHVideoManager.getInstance().initSDK(application, url);
        CrashReport.initCrashReport(context, "d9cbd7f599", false);

        //FHVideoParams.setIsPrivateIM(true);
        //FHClient.getInstance().setServerVersion(FHTellerParams.V_2_8_3);

    }

    /*
     *  设置im通道使用内网
     *
     *  @param imIntranet    是否使用内网，默认外网
     *                       true :内网；false：外网
     **/
    public void setImIntranet(boolean imIntranet) {
        // FHVideoManager.getInstance().setImIntranet(imIntranet);
    }

    /**
     * 登录
     *
     * @param uId
     * @param uType
     * @param accessToken
     * @param callBack
     */
    public void login(String uId, int uType, String accessToken, FHBusiCallBack callBack) {
        FHVideoManager.getInstance().login(uId, uType, accessToken, callBack);
    }

    /**
     * 登出
     */
    public void logout() {
        FHLiveHelper.isLogin = false;
        FHVideoManager.getInstance().logout(null);
    }


    private String callType = CALL_TYPE_ARTIFICIAL;
    public static final String CALL_TYPE_AI = FHLiveMacro.FH_LIVE_CALL_TYPE_AI, CALL_TYPE_ARTIFICIAL = "CALL_TYPE_ARTIFICIAL";

    public void setCallType(String type) {
        callType = type;
    }

    public void callTeller(Activity activity) {
        if (!FHLiveHelper.isLogin) {//是否登录
            ToastUtil.getInatance(activity).show("请先登录客户端");
            return;
        }

        //设置远程主流画布渲染模式
        FHLiveSessionParams.setFhVideoRemoteRenderMode(FHLiveMacro.VIDEO_RENDER_MODE_FILL);
        //设置远程辅流画布渲染模式
        FHLiveSessionParams.setFhVideoRemoteSubRenderMode(FHLiveMacro.VIDEO_RENDER_MODE_FILL);

        FHVideoParams.FHRenderParams params = new FHVideoParams.FHRenderParams();
        params.fillMode = FHLiveMacro.VIDEO_RENDER_MODE_FILL;
        params.mirrorType = FHLiveMacro.VIDEO_MIRROR_TYPE_AUTO;
        //设置本地预览渲染信息
        FHLiveClient.getInstance().setLocalRenderParams(params);

        AppCallParams callParams = getCallParams();
        if (callParams == null) {//是否配置呼叫路由
            ToastUtil.getInatance(activity).show("路由方式不能为空");
        }
        LiveData data = getLiveData();
        data.setParams(GsonUtil.toJson(callParams));
        FHLiveActivity.setEventLisenter(eventListener);
        FHLiveActivity.autoLogout = false;
        Intent intent = new Intent(activity, FHLiveActivity.class);
        intent.putExtra("liveData", GsonUtil.toJson(data));
        activity.startActivity(intent);
        isMetting = true;
    }

    private boolean isMetting = false;

    /**
     * 是否进入会话页
     *
     * @return
     */
    public boolean isMetting() {
        return isMetting && Build.VERSION.SDK_INT >= 30;
    }

    private TestView.FHAudioVolumeCallBack fhAudioVolumeCallBack;

    public void setFhAudioVolumeCallBack(TestView.FHAudioVolumeCallBack fhAudioVolumeCallBack) {
        this.fhAudioVolumeCallBack = fhAudioVolumeCallBack;
    }

    private FHEventListener eventListener = new FHEventListener() {
        @Override
        public void onVideoEvent(String type, String msg) {
            switch (type) {
                case FHUIConstants.ONCLICK_FLOAT://悬浮窗被点击
                    EventBus.getDefault().post(new UiEvent("是否返回视频会话", FHLiveHelper.SHOW_HINT));
                    break;
                case FHUIConstants.NEW_NOTI://通知栏有新通知

                    break;
                case FHUIConstants.ON_DESTROY_LIVE://会话结束
                    isMetting = false;
                    EventBus.getDefault().post(new UiEvent("", type));
                    break;
                case FHUIConstants.USER_VOICE_VOLUME:
                    /*
                     * 该回调需开启 FHLiveClient.getInstance().enableAudioVolumeEvaluation(interval,enable_vad);才可生效
                     * interval 设置 onUserVoiceVolume 回调的触发间隔，单位为 ms，最小间隔为 100ms，如果小于等于 0 则会关闭回调，建议设置为300ms。
                     * enable_vad  true：打开本地人声检测 ；false：关闭本地人声检测。
                     */
                    FHLog.addLog("房间内成员音量回调-->" + msg);
                    if (fhAudioVolumeCallBack != null) {
                        fhAudioVolumeCallBack.onVoiceVolume(msg);
                    }
                    break;
            }
        }
    };


    private LiveData liveData;
    private AppCallParams callParams;

    public LiveData getLiveData() {
        if (liveData == null) {
            String data = SharedPreUtil.getInstance(context).getKeyStringValue("demo_data_more", "");
            if (StringUtil.isEmpty(data))
                liveData = new LiveData();
            else
                liveData = GsonUtil.fromJson(data, LiveData.class);
        }
        return liveData;
    }

    public void setLiveData(LiveData liveData) {
        this.liveData = liveData;
        SharedPreUtil.getInstance(context).saveKeyStringValue("demo_data_more", GsonUtil.toJson(liveData));

    }

    public AppCallParams getCallParams() {
        if (callParams == null) {
            if (getLiveData() != null || !StringUtil.isEmpty(getLiveData().getParams())) {
                callParams = GsonUtil.fromJson(getLiveData().getParams(), AppCallParams.class);
            }
        }
        return callParams;
    }

    public void setCallParams(AppCallParams callParams) {
        this.callParams = callParams;
    }


}
