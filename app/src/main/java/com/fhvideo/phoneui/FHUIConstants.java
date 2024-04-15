package com.fhvideo.phoneui;


import com.fhvideo.FHLiveClient;

public class FHUIConstants {
    public static final String TAG ="FHUIEvent_"
            ,SHOW_LIVE =TAG+ "showLive"//切换视频页
            ,SHOW_TRANS =TAG+ "showTrans"//切换交易页
            ,SHOW_FILE =TAG+ "showFile"//切换投文档页
            ,SHOW_IMG =TAG+ "showImg"//切换截图页
            ,BACK_FRAGMENT =TAG+ "backFragment"//返回上一页
            ,BACK_METTING = TAG+"backMetting"//返回会话
            ,CHANGE_SCREEN = TAG+"changeScreen"
            ,MIN_VIDEO = TAG+"minVideo"//最小化
            ,ONCLICK_FLOAT = TAG+"onClickFloat"//悬浮窗点击
            ,START_SESSION = TAG+"startSession"//开启会话

            ,SHOW_ERROR = TAG+"showError"//显示错误提示
            ,SHOW_VIDEO =TAG+ "showVideo"//切换视频页
            ,SHOW_CALL =TAG+ "showCall"//切换呼叫页
            ,ONQUIT =TAG+ "onQuit"//挂断
            ,NEW_NOTI =TAG+ "newNiti"//新通知
            ,RESET_TRANS =TAG+ "resetTrans"//重置交易页
            ,SHOW_QUIT =TAG+ "showQuit"//显示挂断
            ,JSTOAPP =TAG+ "jsToApp"//js与app交互
            ,UPT_VIDEO_TYPE =TAG+ "uptVideoType"//更换视频模式
            ,SHOW_TOAST = TAG + "showToast"
            ,ON_DESTROY_LIVE = TAG + "onDestroyLive"
            ,UPT_WEB_TIME = TAG + "uptWebTime"
            ,CLOSE_LIVE = TAG + "closeLive"
            ,USER_VOICE_VOLUME = TAG + "userVoiceVolume"
            ;

    public static String redirectUrl = "/vc/busiroom/teller-business/transRedirect.html";

    private static boolean cusIsAudio = false;

    public static boolean isCusIsAudio() {
        return cusIsAudio;
    }

    public static void setCusIsAudio(boolean cusIsAudio) {
        FHUIConstants.cusIsAudio = cusIsAudio;
    }

    private static String tellerId = "";

    public static String getTellerId() {
        return FHLiveClient.getInstance().getTellerId();
    }

    public static void setTellerId(String tellerId) {
        FHUIConstants.tellerId = tellerId;
    }

    public static final String FH_FLOAT_TYPE_MIN = "float_min";
    public static final String FH_FLOAT_TYPE_SHARE = "float_share";
    public static final String FH_FLOAT_TYPE_PUSH = "float_push";
    public static final String FH_FLOAT_TYPE_WEB = "float_web";

    /**
     * 自己投屏时是否显示座席画面
     */
    public static boolean iShowOwnShareScreen = true;

    public static boolean iShowOwnShareScreenFloatView() {
        return iShowOwnShareScreen;
    }

    public static void setShowOwnShareScreenFloatView(boolean iShowOwnShareScreen) {
        FHUIConstants.iShowOwnShareScreen = iShowOwnShareScreen;
    }

    /**
     * 座席投屏时是否显示小窗口
     */
    public static boolean iShowTellerShareScreen = false;

    public static boolean iShowTellerShareScreenFloatView() {
        return iShowTellerShareScreen;
    }

    public static void setShowTellerShareScreenFloatView(boolean iShowTellerShareScreen) {
        FHUIConstants.iShowTellerShareScreen = iShowTellerShareScreen;
    }

}
