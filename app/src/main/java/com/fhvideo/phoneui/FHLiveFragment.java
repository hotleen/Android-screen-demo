package com.fhvideo.phoneui;

import static android.content.Context.TELEPHONY_SERVICE;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fhvideo.FHLiveClientParams;
import com.fhvideo.FHLiveMacro;
import com.fhvideo.FHResource;
import com.fhvideo.adviser.bean.CallData;
import com.fhvideo.adviser.bean.CusCallInfo;
import com.fhvideo.adviser.bean.FHNetworkQuality;
import com.fhvideo.adviser.tool.FHAPlayer;
import com.fhvideo.bank.FHBankParams;
import com.fhvideo.bank.FHPermission;
import com.fhvideo.bank.FHSurfaceView;
import com.fhvideo.bank.utils.FHPhoneStateListener;
import com.fhvideo.bean.AnsEvent;
import com.fhvideo.bean.FHLog;
import com.fhvideo.bean.GsonUtil;
import com.fhvideo.bean.UiEvent;
import com.fhvideo.fhcommon.FHBusiCallBack;
import com.fhvideo.fhcommon.params.FHVideoParams;
import com.fhvideo.fhcommon.utils.StringUtil;
import com.fhvideo.phoneui.busi.FHLiveListener;
import com.fhvideo.phoneui.busi.FHVideoListener;
import com.fhvideo.phoneui.floatt.FloatManager;
import com.fhvideo.phoneui.utils.ToastUtil;
import com.fhvideo.phoneui.view.FHCallView;
import com.fhvideo.phoneui.view.FHVideoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

public class FHLiveFragment extends Fragment implements FHVideoListener {

    private View liveView;
    private RelativeLayout rl_video_add;
    private FHSurfaceView mainRender, localRender, thirdRender, fourthRender;
    private FHPhoneStateListener fhPhoneStateListener;
    private TelephonyManager telephonyManager;
    private FHLiveListener listener;
    private static FHLiveFragment liveFragment = null;
    /**
     * 是否是前置摄像头
     */
    private boolean isfront = true;
    /**
     * 是否正在打电话
     */
    private boolean FHPhoneState = false;

    private boolean isMin = false;
    public static FHLiveFragment newInstance(String liveData) {
        Bundle args = new Bundle();
        args.putString("liveData", liveData);

        /*args.putString("uid", uid);
        args.putString("sessionid", FHLiveClient.getInstance().getSessionId());
        args.putBoolean("isvideo", chatMode == 3 ? true : false);*/
        if (liveFragment == null) {
            synchronized (FHLiveFragment.class) {
                if (liveFragment == null) {
                    liveFragment = new FHLiveFragment();
                }
            }
        }
        liveFragment.setArguments(args);
        return liveFragment;
    }

    public void setEventListener(FHLiveListener listener) {
        this.listener = listener;
    }

    @SuppressLint("ValidFragment")
    private FHLiveFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        liveView = inflater.inflate(FHResource.getInstance().getId(getActivity(),"layout","fh_fragment_live"), null);
        return liveView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rl_video_add = liveView.findViewById(FHResource.getInstance().getId(getActivity(),"id","rl_video_add"));
        initCall();
        try {
            fhPhoneStateListener = new FHPhoneStateListener();
            telephonyManager = (TelephonyManager) getActivity().getSystemService(TELEPHONY_SERVICE);
            telephonyManager.listen(fhPhoneStateListener,
                    PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Exception e) {
        }
    }
    private void initInviteVideo() {
        //todo: 3. 这个fragment是客服接通后进入这里 在进入到FHVideoView中去
        FHAPlayer.getInstance().stopPlayer();
        if(baseCallView != null)
            baseCallView.getView().setVisibility(View.GONE);
        uid = mData.getUid();
        isvideo = mData.getChatMode() == 3 || mData.getChatMode() == 4 ? true : false;
        startType = isvideo;
        if(!isvideo)
            FHVideoView.getInstance().setVideoType(isvideo);
        FHVideoView.getInstance().onStart(isvideo);
        FHVideoManager.getInstance().startInviteSession(getActivity()
                ,mData.getInviteCode()+""
                ,new FHBusiCallBack() {
                    @Override
                    public void onSuccess(String str) {
                    }

                    @Override
                    public void onError(String error) {
                        listener.showError(error, false);
                    }
                });

    }
    private void initVideo() {
        uid = mData.getUid();
        isvideo = mData.getChatMode() == 3 || mData.getChatMode() == 4 ? true : false;
        startType = isvideo;
        if(!isvideo)
            FHVideoView.getInstance().setVideoType(isvideo);
        FHVideoView.getInstance().onStart(isvideo);
        startVideo();
    }


    private long minVideoTime = 0;

    @Override
    public void onResume() {
        isShow = true;
        getFocus();
        super.onResume();
        if (!isCalling &&minVideoTime != 0 && new Date().getTime() - minVideoTime > 1000) {
            EventBus.getDefault().post(new UiEvent("", true, FHBankParams.FH_BACK_METTING));
            return;
        }
        if(isCalling &&isConfirm) {
            isConfirm = false;
            receiveEvent(FHLiveClientParams.CALL_EVENT_ANSWER, getResString("fh_metting"));
        }
    }

    @Override
    public void onPause() {
        minVideoTime = new Date().getTime();
        super.onPause();
        isShow = false;

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        liveFragment = null;
        rl_video_add.removeAllViews();
        FHVideoView.getInstance().release();
        isCalling = false;
        isMin = false;
        if(baseCallView != null)
            baseCallView.release();
        listener = null;
        isConfirm = false;
        isShow = false;

        EventBus.getDefault().unregister(this);
    }

    /**
     * 主线程
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mainEvent(UiEvent ros) {
        if (ros != null) {
            if (ros.getType().equals(FHBankParams.FH_PHONE_STATE)) {
                int stat = (int) ros.getObj();
                uptPhoneStat(stat);
            } else if (ros.getType().equals(FHBankParams.FH_CLOSE_PHONE)) { //挂断电话
                restartVideo();
            } else if (ros.getType().equals(FHLiveClientParams.INTERACT_EVENT_USER_JOIN)) { //进入会话
                FHVideoManager.getInstance().updateLocalAudio();
            } else if (ros.getType().equals("after_join")) {
//                FHBankBusi.getInstance().afterJoin(sessionid, ros.getMsg());
            } else if (ros.getType().equals(FHBankParams.ERROR_MSG_SHOW_POP)) {
                if (StringUtil.isEmpty(ros.getMsg())) {
                    FHVideoView.getInstance().onVideoEvent(FHBankParams.FH_ON_ERROR, getResString("fh_error"));
                } else {
                    FHVideoView.getInstance().onVideoEvent(FHBankParams.FH_ON_ERROR, ros.getMsg());
                }
            } else if (ros.getType().equals(FHBankParams.FH_UPT_TELLER_ID)) {
                FHVideoView.getInstance().onVideoEvent(ros.getType(), ros.getMsg());
            } else if (ros.getType().equals(FHBankParams.FH_BACK_METTING)) {
                backMetting();
            } else if (ros.getType().equals(FHUIConstants.MIN_VIDEO)) {
                minVideo(ros.getMsg());
            } else if (ros.getType().equals(FHUIConstants.CLOSE_LIVE)) {
               leave();
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void ansEvent(AnsEvent ros) {
        if (ros != null && ros.getType().equals(FHBankParams.FH_CLOSE_PHONE)) {
            if (ros.getType().equals(FHBankParams.FH_CLOSE_PHONE)) {
                SystemClock.sleep(100);
                EventBus.getDefault().post(new UiEvent("", ros.getType()));
            }
        }
    }

    private void addview(View videoView) {
        if (videoView != null) {
            rl_video_add.addView(videoView);
        }
    }

    private String uid, sessionid;
    private boolean isteller = false, isvideo = false,startType = true;
    private boolean istransfer = false; //会话中收到一定是转接
    private String wavpath = "video.wav";

    public void receiveEvent(String evetn, String msg) {
        if(isCalling){
            callEvent(evetn, msg);
        }else {
            videoEvent(evetn, msg);
        }

    }

    private void videoEvent(String evetn, String msg) {
        //视频类
        if (msg != null &&  evetn.equals(FHLiveClientParams.CALL_EVENT_TRANSFER_CLOSE)) {
            FHVideoView.getInstance().onUptTellerType(FHLiveClientParams.INTERACT_EVENT_SCREEN_PPT_OFF, msg);

            FHVideoManager.getInstance().leaveRoom(null);
        }
        /*if (msg != null && msg.startsWith("reset")) {
            restartVideo();
        }*/
        if (msg != null && evetn.equals(FHLiveClientParams.CALL_EVENT_ANSWER)) { // 会话中收到接听通知为转接
            istransfer = true;
            sessionid = msg;
            isvideo = startType;
            FHVideoView.getInstance().setVideoType(isvideo);

            ToastUtil.getInatance(getActivity()).show(getResString("fh_thrid_confirm"));
            initVideo();
        }
        if(evetn.equals(FHLiveClientParams.INTERACT_EVENT_PAUSE)){
            if (!StringUtil.isEmpty(wavpath)) {
                FHAPlayer.getInstance().startPlayer(wavpath, getActivity());
            }
        }else if(evetn.equals(FHLiveClientParams.INTERACT_EVENT_RESUME)){
            FHAPlayer.getInstance().stopPlayer();

        }
        if (evetn.equals(FHLiveClientParams.SESSION_EVENT_CLOSE)) {
            ToastUtil.getInatance(getActivity()).show(getResString("fh_teller_close"));
            closeVideo(true);
        }
        if (evetn.equals(FHLiveClientParams.INTERACT_EVENT_PUSH)) {
            FHVideoView.getInstance().onNewPush(msg);
        }
        if (evetn.equals(FHLiveClientParams.INTERACT_EVENT_UPT_CUSTOMER_VIDEO_TYPE)) {
            //FHVideoView.getInstance().onUptTellerType(evetn, msg);
        }
        if(evetn.equals(FHLiveClientParams.INTERACT_EVENT_SCREEN_ON) ||
                evetn.equals(FHLiveClientParams.INTERACT_EVENT_PPT_ON) ||
                evetn.equals(FHLiveClientParams.INTERACT_EVENT_SCREEN_PPT_OFF) ){
            FHVideoView.getInstance().onUptTellerType(evetn, msg);
        }
        if (evetn.equals(FHLiveClientParams.INTERACT_EVENT_USER_JOIN)) {
/*            if (StringUtil.isEmpty(mainRender.getUid()) && msg.equals(mainRender.getUid())) {

            }
            if (!StringUtil.isEmpty(thirdRender.getUid()) && msg.equals(thirdRender.getUid())) {
                thirdRender.setVisibility(View.GONE);
            }
            if (!StringUtil.isEmpty(fourthRender.getUid()) && msg.equals(fourthRender.getUid())) {
                fourthRender.setVisibility(View.GONE);
            }*/
            FHVideoView.getInstance().onVideoEvent(evetn, msg);
        }
        if (evetn.equals(FHBankParams.FH_VIDEO_ON_FIRST_FRAME)) {
            FHVideoView.getInstance().onVideoEvent(evetn, msg);
        }
        if (evetn.equals(FHBankParams.FH_ON_USER_LEAVE)) {
            FHAPlayer.getInstance().stopPlayer();
            FHVideoView.getInstance().onVideoEvent(evetn, msg);
        }
        if (evetn.equals("handleTrans")) {
            FHVideoView.getInstance().onVideoEvent(evetn, msg);
        }
        if(evetn.equals(FHBankParams.FH_UPT_NETWORK)){
            if(msg.equals("true")){
                FHVideoView.getInstance().showNetStatus("local", 0);
            } else {
                FHVideoView.getInstance().showNetStatus("local", 4);
                FHVideoView.getInstance().showNetStatus("main", 4);
            }
        }
        if (evetn.equals(FHBankParams.INTERACT_EVENT_NETWORK_QUALITY_ARRAY)) {
            if(msg!=null && FHBankParams.isConnected()){
                FHNetworkQuality info = GsonUtil.fromJson(msg, FHNetworkQuality.class);

                if(StringUtil.isEmpty(info.getUserId())){
                    FHVideoView.getInstance().showNetStatus("local",info.getQuality());
                } else if (mainRender != null && info.getUserId().equals(mainRender.getUid())) {
                    FHVideoView.getInstance().showNetStatus("main", info.getQuality());
                } else if (thirdRender != null && info.getUserId().equals(thirdRender.getUid())) {
                    FHVideoView.getInstance().showNetStatus("third", info.getQuality());
                } else {
                    listener.showLongToast(false, "");
                }
            }

        }
        if(evetn.equals(FHBankParams.FH_ONUSER_SUBSTREAM_AVAILABLE)){
            FHVideoView.getInstance().onVideoEvent(evetn, msg);
        }
        if(evetn.equals(FHBankParams.FH_VIDEO_NEW_MSG) ||
                evetn.equals(FHLiveClientParams.INTERACT_EVENT_IDCARD_FACE)
                || evetn.equals(FHLiveClientParams.INTERACT_EVENT_IDCARD_NATIONAL_EMBLEM)
                || evetn.equals(FHLiveClientParams.INTERACT_EVENT_BANK_CARD)
                || evetn.equals(FHLiveClientParams.INTERACT_EVENT_FACE_RECOGNITION)
                || evetn.equals(FHLiveClientParams.INTERACT_EVENT_CLOSE_CARD)
        ){

            FHVideoView.getInstance().onVideoEvent(evetn, msg);
        }
    }

    /**
     * 加入会话
     */
    private void startVideo() {
        FHVideoManager.getInstance().initVideoFragment(getActivity()
                ,new FHBusiCallBack() {
            @Override
            public void onSuccess(String str) {
                EventBus.getDefault().post(new UiEvent("ture",true,FHUIConstants.START_SESSION));
            }

            @Override
            public void onError(String error) {
                EventBus.getDefault().post(new UiEvent(error,false,FHUIConstants.SHOW_ERROR));
                EventBus.getDefault().post(new UiEvent("false",true,FHUIConstants.START_SESSION));
            }
        });

    }

    /**
     * 坐席挂断传true, 自己挂断false
     * @param isteller
     */
    public void closeVideo(boolean isteller) {
        FloatManager.getInstance().removeFloatView(getActivity());
        FHVideoManager.getInstance().releaseVideo();
        FHAPlayer.getInstance().stopPlayer();
        listener.close(isteller);
    }

    private void restartVideo() {
        FHVideoManager.getInstance().restartVideo(new FHBusiCallBack() {
            @Override
            public void onSuccess(String s) {
            }

            @Override
            public void onError(String s) {
                listener.showError(s, false);
            }
        });
    }


    // todo: 7 投屏
    @Override
    public void uptVideoType(String msg) {
        if(isCalling)
            return;
        if (msg.equals(FHBankParams.FH_VIDEO_TYPE_VIDEO)) {
            changeVideo();
        } else if (msg.equals(FHBankParams.FH_VIDEO_TYPE_AUDIO)) {
            changeAudio();
        } else if (msg.equals(FHBankParams.FH_VIDEO_TYPE_SREEN)) {
            changeScreen();
        }
    }

    @Override
    public void setSurface(FHSurfaceView mainSurface, FHSurfaceView local, FHSurfaceView third, FHSurfaceView fourth) {
        mainRender = mainSurface;
        localRender = local;
        thirdRender = third;
        fourthRender = fourth;
        FHVideoManager.getInstance().setSurface(getActivity(),mainSurface,local,third,fourth);
    }


    @Override
    public void switchCamera() {
        isfront = !isfront;
        FHVideoManager.getInstance().switchCamera();
    }

    @Override
    public void sendMsg(String msg) { //发送文字聊天
        FHVideoManager.getInstance().sendText(msg, new FHBusiCallBack() {
            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onError(String s) {

            }
        });
    }

    @Override
    public void rotationRemote(boolean rotation) {
        FHVideoManager.getInstance().rotationSubStream(rotation);

    }

    @Override
    public void minVideo(String type) {
        if (FHPermission.getInstance().checkFloat(getActivity())) {
            minVideoTime = new Date().getTime();
            listener.addFlot(type);
            isMin = true;
        } else {
            FHPermission.getInstance().applyFloat(getActivity());
        }
    }

    @Override
    public void removeFloatView() {
        if (FHVideoManager.getInstance().getCallType().equals(FHLiveMacro.FH_LIVE_CALL_TYPE_LOCALPREVIEW)){
            FHVideoManager.getInstance().changeSurface(localRender, FHVideoParams.getIdentity());
            localRender.setVisibility(View.VISIBLE);
        }else {
            FHVideoManager.getInstance().changeSurface(mainRender);
            mainRender.setVisibility(View.VISIBLE);
        }
        FloatManager.getInstance().removeFloatView(getActivity());
    }

    private int leaveErrorNum = 0;
    @Override
    public void leave() {
        FHVideoManager.getInstance().closeVideo(new FHBusiCallBack() {
            @Override
            public void onSuccess(String s) {
                ToastUtil.getInatance(getActivity()).show("已挂断");
                closeVideo(false);
                if(FHLiveActivity.autoLogout)
                    FHVideoManager.getInstance().logout(null);
            }

            @Override
            public void onError(String s) {
                if(leaveErrorNum == 0){
                    leaveErrorNum ++;
                    listener.showError(s, false);
                } else {
                    leaveErrorNum = 0;
                    ToastUtil.getInatance(getActivity()).show("已挂断");
                    closeVideo(false);
                }
            }
        });
    }

    //视频
    private void changeVideo() {
        isvideo = true;
        FHVideoManager.getInstance().startCamera();
    }

    //音频
    private void changeAudio() {
        isvideo = false;
        FHVideoManager.getInstance().stopCamera();
    }

    //投屏
    private void changeScreen() {
        if (FHPermission.getInstance().checkFloat(getActivity())) {
            if ((Build.VERSION.SDK_INT >= 21)) {
                FHVideoManager.getInstance().changeScreen();
            } else {
                listener.showError(getResString("fh_phone_not_support"), false);
            }
        } else {
            FHPermission.getInstance().applyFloat(getActivity());
        }
    }

    private boolean hasautoback = false;

    //来电状态更新
    private void uptPhoneStat(int stat) {
        if (stat == FHBankParams.FH_PHONE_STATE_CALLING) {//振铃
            if (!FHPhoneState) {//当前没接电话，暂停音视频发送，暂停远程音频解码
                FHPhoneState = true;

            }
        } else if (stat == FHBankParams.FH_PHONE_STATE_ON) {//接通
            hasautoback = true;
            FHPhoneState = true;
        } else {//挂断
            if (FHPhoneState) {//当前正在接电话，恢复音视频发送，恢复远程音频解码
                if (hasautoback) {
                    hasautoback = false;
                    EventBus.getDefault().post(new UiEvent("", true, FHBankParams.FH_AUTO_BACK));
                    EventBus.getDefault().post(new AnsEvent("", FHBankParams.FH_CLOSE_PHONE));
                }
                FHPhoneState = false;
            }
        }
        FHVideoManager.getInstance().uptPhoneStat(stat);
    }

    //返回会话
    public void backMetting() {
        FHLog.addLog("[用户操作] 返回会话");
        isMin = false;
        removeFloatView();
        minVideoTime = 0;
        FHVideoManager.getInstance().backMetting();
        FHVideoView.getInstance().onVideoEvent(FHBankParams.FH_BACK_METTING, "");

    }

    //主界面获取焦点
    private void getFocus() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    FHVideoView.getInstance().onVideoEvent(FHBankParams.FH_ONCLICK_BACK, "");
                    FHLog.addLog("[用户操作] 点击返回键");
                    //点击返回按钮
                    return true;
                }
                return false;
            }
        });
    }


    /**
     * 获取String.xml中字符串
     * @param name 字符串id名
     * @return
     */
    protected String getResString(String name){
        return FHResource.getInstance().getString(getActivity(),name);
    }



    private void initCall() {
        String liveData = getArguments().getString("liveData");
        mData = GsonUtil.fromJson(liveData, CallData.class);

        if(FHVideoManager.getInstance().getCallView() != null && FHVideoManager.getInstance().getCallView().getView() != null){
            baseCallView = FHVideoManager.getInstance().getCallView();
        } else {
            FHCallView.getInstance(getActivity()).release();
            baseCallView = FHCallView.getInstance(getActivity());
            baseCallView.getView().setVisibility(View.VISIBLE);

        }
        addCallView(baseCallView.getView());
        addview(FHVideoView.getInstance().getView(getActivity(), this));
        if(mData!= null && !StringUtil.isEmpty(mData.getCallType())
                &&mData.getCallType().equals(FHLiveMacro.FH_LIVE_CALL_TYPE_INVITE_CODE)
        ){
            FHVideoManager.getInstance().setCallType(FHLiveMacro.FH_LIVE_CALL_TYPE_INVITE_CODE);
            initInviteVideo();
        }else {
            call();
        }
    }

    private FHBaseCallView baseCallView;
    private CallData mData;
    private boolean isCalling = false;
    private void addCallView(View callView){
        if(callView != null){
            try {
                if(callView.getParent() != null){
                    ViewGroup viewParent = (ViewGroup) callView.getParent();
                    viewParent.removeAllViews();
                }
                rl_video_add.removeView(callView);
                callView.setZ(1);
                rl_video_add.addView(callView);
            }catch (Exception e){
            }
        }
    }
    private void call() {
        isCalling = true;
        if (!FHBankParams.isConnected()) { //判断网络状态提示
            listener.showError(getResString("fh_net_weak_hint"), true);
            return;
        }
        receiveEvent(FHBankParams.FH_CALL_TYPE_CALL,
                getResString("fh_waiting"));
        CallData data = new CallData();
        FHVideoManager.getInstance().call(getActivity(), mData,
                new FHBusiCallBack() {
                    @Override
                    public void onSuccess(String str) {
                        CusCallInfo callInfo = GsonUtil.fromJson(str,CusCallInfo.class);
                        if(callInfo != null){
                            receiveEvent(callInfo.getSucctype(), str);

                        }
                    }

                    @Override
                    public void onError(String error) {
                        if (error.equals(FHBankParams.GET_ADDRESS_LOCATION_ERROR))
                            baseCallView.onError("定位失败，请开启定位权限或检查网络");
                        else
                            baseCallView.onError(error);
                    }

                });
    }


    private void callEvent(String evetn, String msg) {
        if(baseCallView != null)
            baseCallView.onCallEvent(evetn,msg);

        switch (evetn){
            case FHLiveClientParams.CALL_EVENT_ANSWER://接听
                isCalling = false;
                confirm(msg);
                break;

        }
    }
    private boolean isShow = false;//是否在前台
    private boolean isConfirm = false;//座席是否接通

    private void confirm(String msg) {//柜员接通
        sessionid = msg;
        if (!StringUtil.isEmpty(msg)) {
            if (isShow) {
                FHAPlayer.getInstance().stopPlayer();
                initVideo();
                if(baseCallView != null)
                    baseCallView.getView().setVisibility(View.GONE);
            } else {
                isConfirm = true;
            }
        }
    }

}
