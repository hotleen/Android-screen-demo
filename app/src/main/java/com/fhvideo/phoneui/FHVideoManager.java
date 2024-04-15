package com.fhvideo.phoneui;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.fhvideo.FHLiveClient;
import com.fhvideo.FHLiveMacro;
import com.fhvideo.FHLiveSessionParams;
import com.fhvideo.FHLiveTeller;
import com.fhvideo.FHResource;
import com.fhvideo.adviser.FHTellerParams;
import com.fhvideo.adviser.bean.CallData;
import com.fhvideo.adviser.bean.FHScreenParams;
import com.fhvideo.adviser.impl.bean.FHTellerBack;
import com.fhvideo.adviser.tool.FHTellerVideoCallBack;
import com.fhvideo.api.FHApiParams;
import com.fhvideo.bank.FHBankParams;
import com.fhvideo.bank.FHPermission;
import com.fhvideo.bank.FHSurfaceView;
import com.fhvideo.bank.MResource;
import com.fhvideo.bean.FHLog;
import com.fhvideo.bean.FHPrivApiInfo;
import com.fhvideo.bean.FHPrivApiParams;
import com.fhvideo.bean.UiEvent;
import com.fhvideo.fhcommon.FHBusiCallBack;
import com.fhvideo.fhcommon.params.FHVideoParams;
import com.fhvideo.fhcommon.utils.StringUtil;
import com.fhvideo.phoneui.busi.FHLiveListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class FHVideoManager {
    private static FHVideoManager instance;
    public static FHVideoManager getInstance(){
        if(instance == null)
            synchronized (FHVideoManager.class){
                if(instance == null)
                    instance = new FHVideoManager();
            }
        return instance;
    }

    private Activity activity;//视频页activity
    public Activity getActivity() {
        return activity;
    }
    public void setActivity(Activity activity) {
        this.activity = activity;
    }
    private  Context mContext = null;

    private Context getContext() {
        return mContext;
    }

    /**
     * 设置application 上下文
     * @param context
     */
    private void setContext(Context context) {
        mContext = context;
    }

    /**
     * 设置包名
     * @param name app包名
     */
    public void setPackageName(String name){
        FHResource.getInstance().setPackageName(name);
        MResource.setPackageName(name);
        FHResource.getInstance().setPackageName(name);
    }

    /**
     * 设置是否支持 arm64-v8a架构
     * @param arm64 true支持 false 不支持
     */
    public void setArm64(boolean arm64){
        FHTellerParams.setArm64(arm64);
    }

    /**
     * 设置投屏通知
     * @param notification
     */
    public void setScreenNotification(Notification notification){
        FHLiveSessionParams.setScreenNotification(notification);

    }
    public void setWhiteListScreen(List<String> names){
        FHLiveSessionParams.setWhiteListScreen(names);
    }
    /**
     * 设置投屏通知
     * @param notification
     */
    public void setLiveForegroundNotification(Notification notification){
        FHLiveSessionParams.setFhLiveForegroundNotification(notification);

    }
    /**
     * 初始SDK
     * @param application
     * @param url
     */
    public void initSDK(Application application,String url){
        initSDK(application,url,"");
    }

    /**
     * 开启debug 日志
     * @param open 默认关闭，true 开启；false 关闭
     */
    public void openDebug(boolean open){
        FHLiveClient.getInstance().openDebugLog(open);
    }
    /**
     * 初始SDK
     * @param application
     * @param url
     */
    public void initSDK(Application application,String url, String data){
        if(application == null){
            Log.e("FH_bankui_error","application 不能为空！");
            return;
        }
        if(StringUtil.isEmpty(url)){
            Log.e("FH_bankui_error","url 不能为空！");
            return;
        }
        setContext(application.getApplicationContext());
        FHLiveClient.getInstance().init(application,url,data);
        FHLiveClient.getInstance().setLiveCallBack(new FHPhoneLiveCallBack());

    }


    /**
     * 登录
     * @param uId
     * @param uType
     * @param accessToken
     * @param callBack
     */
    public void login(String uId, int uType, String accessToken, FHBusiCallBack callBack){
        FHLiveClient.getInstance().login(uId,uType,accessToken,callBack);
    }

    private com.fhvideo.phoneui.FHBaseCallView callView;

    /**
     * 设置呼叫页
     * @param view
     */
    public void setCallView(com.fhvideo.phoneui.FHBaseCallView view){
        callView = view;
    }

    public FHBaseCallView getCallView() {
        return callView;
    }

    /**
     * 判断定位服务是否开启
     * @param activity 当前activity
     * @return 开启返回true,结束返回false
     */
    public boolean isLocServiceEnable(Activity activity){
       return FHPermission.getInstance().isLocServiceEnable(activity);
    }
    private String CallType = CALL_TYPE_ARTIFICIAL;
    public static final String CALL_TYPE_AI = FHLiveMacro.FH_LIVE_CALL_TYPE_AI
            ,CALL_TYPE_ARTIFICIAL = FHLiveMacro.FH_LIVE_CALL_TYPE_NORMAL
            ,CALL_TYPE_LOCALPREVIEW = FHLiveMacro.FH_LIVE_CALL_TYPE_LOCALPREVIEW
            ;

    public String getCallType() {
        return CallType;
    }

    public void setCallType(String callType) {
        CallType = callType;
    }

    /**
     * 开启定位服务设置页
     * @param activity 当前activity
     * @param requestCode 请求码
     */
    public void startLocService(Activity activity,int requestCode){
        FHPermission.getInstance().startLocService(activity,requestCode);
    }



    /**
     * 登出座席
     * @param callBack
     */
    public void logout(final FHBusiCallBack callBack){
        FHLiveClient.getInstance().logout(callBack);
    }

    /**
     * 申请动态权限
     * @param activity
     */
    public void checkPermission(Activity activity){
        FHPermission.getInstance().checkPermission(activity);
    }

    public void call(Context context, CallData callData, FHBusiCallBack callBack){
        FHLiveClient.getInstance().call(context
                ,callData
                ,callBack
        );
    }
    public void addCallView(View callView){}


    /**
     * 释放会话资源
     */
    public void releaseVideo(){
        FHLiveClient.getInstance().releaseResources();
    }


    /**
     * 释放会话资源 不会 取消 okhttp请求
     */
    public void releaseVideoNoCancleOkhttp() {
        FHLiveClient.getInstance().releaseResourcesNoCancleOkhttp();
    }


    public void setVideoCallBack(FHTellerVideoCallBack callBack) {
        FHTellerBack.getInstance().setVideoCallBack(callBack);

    }


    public void setSurface(Activity activity, FHSurfaceView sv_main, FHSurfaceView sv_local, FHSurfaceView sv_third, FHSurfaceView sv_fourth) {
        FHLiveClient.getInstance().setSurface(activity,sv_main,sv_local,sv_third,sv_fourth);
    }

        /**
     * 初始化视频会话
     * @param activity
     */
    public void initVideoFragment(Activity activity,FHBusiCallBack callBack){
        FHLiveClient.getInstance().startSession(activity,callBack);

    }
    /**
     * 初始化视频会话
     * @param activity
     */
    public void startInviteSession(Activity activity, String inviteCode, FHBusiCallBack callBack){
        FHLiveClient.getInstance().startInviteSession(activity,inviteCode,callBack);

    }

    public void restartVideo(FHBusiCallBack callBack){
        FHLiveClient.getInstance().callExperimentalAPI(new FHPrivApiInfo(FHApiParams
                .RESTART_VIDEO,null,callBack
        ));

    }


    public void closeVideo(final FHBusiCallBack callBack){
        FHLiveClient.getInstance().closeSession(callBack);
    }

    public void closeVideoByAiLeave(final FHBusiCallBack callBack) {
        FHLiveClient.getInstance().closeSessionByAiLeave(callBack);
    }


    /**
     * 发送文字聊天
     * @param msg
     * @param callBack
     */
    public void sendText(String msg,FHBusiCallBack callBack){
        FHLiveClient.getInstance().sendTextMsg(msg,callBack);

    }

    public void rotationSubStream(boolean rotation){
        FHLiveClient.getInstance().rotateSubStream(rotation);

    }

    public void rotationSubStream(String uid,boolean rotation){
        FHLiveClient.getInstance().rotationSubStream(uid,rotation);

    }
    public void setReceiver(String receiver){
        FHLiveTeller.getInstance().setReceiver(receiver);

    }



    public void getTellers(String tellerid,FHBusiCallBack callBack){
        FHLiveClient.getInstance().getTellers(tellerid,callBack);
    }

    /**
     * 开启分段录制
     *
     * @param tranid   双录流水号
     * @param callBack
     */
    public void splitStart(String tranid, FHBusiCallBack callBack) {
        FHPrivApiParams params = new FHPrivApiParams();
        params.setTranid(tranid);
        FHLiveClient.getInstance().callExperimentalAPI(new FHPrivApiInfo(FHApiParams
                .SPLITSTART,params,callBack
        ));
    }

    /**
     * 关闭分段录制
     *
     * @param tranid   双录流水号
     * @param callBack
     */
    public void splitStop(String tranid, FHBusiCallBack callBack) {
        FHPrivApiParams params = new FHPrivApiParams();
        params.setTranid(tranid);
        FHLiveClient.getInstance().callExperimentalAPI(new FHPrivApiInfo(FHApiParams
                .SPLITSTOP,params,callBack
        ));
    }
    /**
     * 切换摄像头模式
     */
    public void switchCamera(){
        FHLiveClient.getInstance().changeCameraType(FHLiveClient.getInstance().getCameraType()==1?0:1);
    }

    public void changeCameraType(int type){
        FHLiveClient.getInstance().changeCameraType(type);

    }

    public void leaveRoom(FHBusiCallBack callBack){
        FHLiveClient.getInstance().callExperimentalAPI(new FHPrivApiInfo(FHApiParams
                .LEAVE_ROOM,null,callBack
        ));
    }

    public void startCamera(){
        FHLiveClient.getInstance().startCamera();

    }
    /**
     * 切换音频模式
     */
    public void stopCamera(){

        FHLiveClient.getInstance().stopCamera();
    }

    /**
     * 切换投屏模式 全屏
     */
    public void changeScreen(){
        FHLiveClient.getInstance().prepareScreen();
    }


    public void uptScreen(){
        FHLiveClient.getInstance().setScreenParams(new FHScreenParams(1920,1080,0,576,1024,1220,56));
    }

    /**
     * 开启投屏
     * @param resCode
     * @param data
     */
    public void startScreen(int resCode, Intent data){
        FHLiveClient.getInstance().startScreen(resCode,data);
    }

    /**
     * 更换主画布
     * @param surfaceView
     */
    public void changeSurface(FHSurfaceView surfaceView){
        FHLiveClient.getInstance().changeSurface(surfaceView, FHVideoParams.TELLER);
    }
    public void sendCustomDataMsg(String data, String type){
        FHLiveClient.getInstance().sendCustomDataMsg(data,type,null);

    }

    /**
     * 启用音量大小提示
     * 开启此功能后，SDK 会在 TRTCCloudListener 中的 onUserVoiceVolume 回调中反馈本地推流用户和远端用户音频的音量大小以及远端用户的最大音量。
     *
     * @param interval   设置 onUserVoiceVolume 回调的触发间隔，单位为 ms，最小间隔为 100ms，如果小于等于 0 则会关闭回调，建议设置为300ms。
     * @param enable_vad true：打开本地人声检测 ；false：关闭本地人声检测。
     */
    public void enableAudioVolumeEvaluation(int interval, boolean enable_vad) {
        FHLog.addLog("[启用音量大小提示]_interval=" + interval + "_enable_vad=" + enable_vad);
        FHLiveClient.getInstance().enableAudioVolumeEvaluation(interval, enable_vad);
    }

    /**
     * 设置音频路由
     * 设置“音频路由”，即设置声音是从手机的扬声器还是从听筒中播放出来，因此该接口仅适用于手机等移动端设备。
     * <p>
     * <p>
     * 手机有两个扬声器：一个是位于手机顶部的听筒，一个是位于手机底部的立体声扬声器。
     * 设置音频路由为听筒时，声音比较小，只有将耳朵凑近才能听清楚，隐私性较好，适合用于接听电话。
     * 设置音频路由为扬声器时，声音比较大，不用将手机贴脸也能听清，因此可以实现“免提”的功能。
     *
     * @param route 音频路由，即声音由哪里输出（扬声器、听筒），默认扬声器
     *              扬声器：TRTCAudioRoute.TRTC_AUDIO_ROUTE_SPEAKER
     *              听筒：  TRTCAudioRoute.TRTC_AUDIO_ROUTE_EARPIECE
     */
    public void setAudioRoute(int route){
        FHLog.addLog("[切换音频路由]_route=" + route);
        FHLiveClient.getInstance().setAudioRoute(route);
    }


    /**
     * 切换远程画布
     * @param surfaceView 画布
     * @param uType 用户身份
     */
    public void changeSurface(FHSurfaceView surfaceView, String uType){
        FHLiveClient.getInstance().changeSurface(surfaceView, uType);
    }

    public void muteLocalVideo(boolean mute){
        FHLiveClient.getInstance().pauseLocalVideo(mute);
    }

    public void backMetting(){
        FHLiveClient.getInstance().resetCamera();
    }
    public void setEventListener(FHLiveListener listener) {
        this.listener = listener;
    }
    private FHLiveListener listener;

    private int cancelIndex = 0;

    /**
     * 挂断呼叫
     * @param callBack
     */
    public void cancelCall(final FHBusiCallBack callBack){
        FHLiveClient.getInstance().cancelCall(new FHBusiCallBack() {
            @Override
            public void onSuccess(String s) {
                cancelIndex = 0;
                if(callBack!= null)
                    callBack.onSuccess(s);

                if(FHLiveActivity.autoLogout)
                    FHVideoManager.getInstance().logout(null);
                if(listener != null)
                    listener.quit();
            }

            @Override
            public void onError(String s) {
                if(callBack!= null)
                    callBack.onError(s);
                if(!FHLiveClient.getInstance().isConnected()){
                    if(cancelIndex<1)
                        cancelIndex = 1;
                    else
                        onSuccess(s);
                }else {
                    cancelIndex = 0;
                }

            }
        });
    }

    public void uptCameraParentVidew(View view){
        FHLiveClient.getInstance().uptCameraParentVidew(view);

    }

    private boolean localMute = false;//本地是否静音 true 静音
    public boolean isLocalMute(){
        return localMute;
    }
    public void muteLocalAudio(boolean mute){
        localMute = mute;
        FHLiveClient.getInstance().muteLocalAudio(mute);
    }
    public void updateLocalAudio(){
        FHLiveClient.getInstance().muteLocalAudio(localMute);
    }


    public void muteRemoteAudio(boolean isMute) {
        FHPrivApiParams params = new FHPrivApiParams();
        params.setMute(isMute);
        FHLiveClient.getInstance().callExperimentalAPI(new FHPrivApiInfo(FHApiParams
                .MUTE_REMOTE_AUDIO,params,null
        ));
    }

    public void cusOnThePhone(String isPhone){
        FHPrivApiParams params = new FHPrivApiParams();
        params.setIsPhone(isPhone);
        FHLiveClient.getInstance().callExperimentalAPI(new FHPrivApiInfo(FHApiParams
                .CUS_ON_THE_PHONE,params,null
        ));
    }
    private boolean FHPhoneState = false;

    public void uptPhoneStat(int stat){
        if (stat == FHBankParams.FH_PHONE_STATE_CALLING) {//振铃
            if (!FHPhoneState) {//当前没接电话，暂停音视频发送，暂停远程音频解码
                FHPhoneState = true;
                muteLocalVideo(false); //关闭本地视频流推送
                muteLocalAudio(false); // 关闭声音
                muteRemoteAudio(false); //关闭远程的

                cusOnThePhone("1");
            }
        } else if (stat == FHBankParams.FH_PHONE_STATE_ON) {//接通
            cusOnThePhone("1");
            FHPhoneState = true;
            leaveRoom(null);
        } else {//挂断
            if (FHPhoneState) {//当前正在接电话，恢复音视频发送，恢复远程音频解码
                cusOnThePhone("0");
                FHPhoneState = false;

                muteLocalVideo(false); //开启本地视频流推送
                muteLocalAudio(false); // 开启声音
                muteRemoteAudio(false); //开启远程的
            }
        }
    }

    /**
     * 最小化
     */
    public void minVideo(){
        if(activity==null)
            return;
        EventBus.getDefault().post(new UiEvent(FHUIConstants.FH_FLOAT_TYPE_MIN, FHUIConstants.MIN_VIDEO));
    }

    /**
     * 获取座席id
     * @return
     */
    public String getTellerId(){
        return FHUIConstants.getTellerId();
    }

    /**
     * 获取会话id
     * @return
     */
    public String getSessionId(){
        return FHLiveClient.getInstance().getSessionId();
    }

    /**
     * 结束会话
     */
    public void closeLive(){
        EventBus.getDefault().post(new UiEvent("", FHUIConstants.CLOSE_LIVE));
    }

}
