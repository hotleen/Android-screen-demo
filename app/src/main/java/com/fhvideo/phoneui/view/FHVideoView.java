package com.fhvideo.phoneui.view;

import android.app.Activity;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fhvideo.FHLiveClient;
import com.fhvideo.FHLiveClientParams;
import com.fhvideo.FHLiveMacro;
import com.fhvideo.FHLiveSessionParams;
import com.fhvideo.FHResource;
import com.fhvideo.bank.FHBankParams;
import com.fhvideo.bank.FHPermission;
import com.fhvideo.bank.FHSurfaceView;
import com.fhvideo.bank.bean.PushFile;
import com.fhvideo.bean.FHLog;
import com.fhvideo.fhcommon.bean.FHFileDirUtil;
import com.fhvideo.phoneui.FHUIConstants;
import com.fhvideo.phoneui.FHVideoManager;
import com.fhvideo.phoneui.floatt.FloatManager;
import com.fhvideo.phoneui.utils.GestureTouchHandler;
import com.fhvideo.phoneui.utils.LogUtils;
import com.fhvideo.fhcommon.utils.StringUtil;
import com.fhvideo.fhcommon.utils.SystemUtil;
import com.fhvideo.bean.AnsEvent;
import com.fhvideo.bean.GsonUtil;
import com.fhvideo.bean.UiEvent;
import com.fhvideo.phoneui.busi.FHVideoListener;
import com.fhvideo.phoneui.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

/**
 * 视频会话操作界面
 */

public class FHVideoView implements View.OnClickListener {

    private static FHVideoView instance;
    private FHVideoListener videoListener;

    public static FHVideoView getInstance() {
        if (instance == null)
            instance = new FHVideoView();
        return instance;
    }

    private RelativeLayout rl;
    private Activity mContext;

    public View getView(Activity activity, FHVideoListener listener) {
        this.videoListener = listener;
        mContext = activity;
        if (rl == null) {
            rl = (RelativeLayout) LayoutInflater.from(activity).inflate(FHResource.getInstance().getId(activity,"layout","layout_fh_video"), null, false);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rl.setLayoutParams(lp);
            initView();
        } else {//还原

        }
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        return rl;
    }

    private boolean isFirst = true;

    private void initView() {
        isFirst = true;
        getSize();
        initFl();

        initSurface();
        initTool();
        FHWebView.getInstance().init(mContext, rl);
        FHHintView.getInstance().init(mContext, rl);
        FHChatView.getInstance().init(mContext, rl);
        FHChatView.getInstance().setVideoListener(videoListener);
        FHPushView.getInstance().init(mContext, rl);
        sv_local.callOnClick();
        hideTool();

    }

    public static final String
            PUSH_TIME = "pushtime"//推送时间
            , SHOW_PUSH = "show_push"//显示推送
            , SHOW_TOOL = "showTool"//显示功能界面
            ;

    /**
     * 更新UI
     *
     * @param type 更新类型
     * @param data 更新数据
     */
    private boolean isSubStream = false;
    public void onVideoEvent(final String type, String data) {
        switch (type) {
            case FHBankParams.FH_ONCLICK_BACK://物理返回键
                hideTool();
                onClickClose();
                break;
            case FHBankParams.FH_ONUSER_SUBSTREAM_AVAILABLE://辅流
                rotation = false;
                if (data.equals("true")) {
                    iv_rotate.setVisibility(View.VISIBLE);
                    isSubStream = true;
                } else {
                    iv_rotate.setVisibility(View.GONE);
                    isSubStream = false;
                }
                break;
            case FHBankParams.FH_ON_ERROR://显示错误信息
                FHHintView.getInstance().warn(data);
                break;
            case FHBankParams.FH_VIDEO_ON_FIRST_FRAME://主画布第一帧画面绘制
                if(isFirst && !StringUtil.isEmpty(sv_main.getUid()) && data.equals(sv_main.getUid())){
                    isFirst= false;
                    iv_net_main.setVisibility(View.VISIBLE);
                    rl_main.setVisibility(View.GONE);
                    onClickSurface(rl_video_main);
                }

                break;
            case FHBankParams.FH_ON_USER_LEAVE://用户离开房间
                onUserLeave(data);
                break;
            case FHBankParams.FH_HANDLE_TRANS:
                onTransEvent(data);
                break;
            case FHLiveClientParams.INTERACT_EVENT_USER_JOIN:
                if (othersShow && (!StringUtil.isEmpty(sv_third.getUid()) && data.equals(sv_third.getUid()))) {
                    rl_video_third.setVisibility(View.VISIBLE);
                }
                if (!othersShow && (!StringUtil.isEmpty(sv_third.getUid()) && data.equals(sv_third.getUid()))) {
                    rl_video_third.setVisibility(View.GONE);
                }
                break;
            case FHBankParams.FH_VIDEO_NEW_MSG://新消息
                onNewMsg(data);
                break;
            case FHBankParams.FH_BACK_METTING:
                isPush = false;
                FHWebView.getInstance().hidden();
                break;
            case FHLiveClientParams.INTERACT_EVENT_IDCARD_FACE:
            case FHLiveClientParams.INTERACT_EVENT_IDCARD_NATIONAL_EMBLEM:
            case FHLiveClientParams.INTERACT_EVENT_BANK_CARD:
            case FHLiveClientParams.INTERACT_EVENT_FACE_RECOGNITION:
            case FHLiveClientParams.INTERACT_EVENT_CLOSE_CARD:
                if(isVideo){
                    showCardHint(type);
                } else {
                    rl_open_webcam.setVisibility(View.VISIBLE);
                    sure_open.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            uptVideoType();
                            rl_open_webcam.setVisibility(View.GONE);
                            showCardHint(type);
                        }
                    });
                }
                break;

        }
    }

    public void onTransEvent(String url) {
        FHWebView.getInstance().loadUrl(url);
    }


    private String tellerId = "";

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mainEvent(UiEvent ros) {
        if (ros != null)
            switch (ros.getType()) {
                case "onReceivedError"://页面加载出错 隐藏交易页
                    if (isPush)
                        return;
                    //ToastUtil.getInatance(mContext).show(getResString("fh_web_error"));
                    break;
                case "jatoapp"://
                    FHWebView.getInstance().hidden();
                    FHChatView.getInstance().hideSoftInputFromWindow();
                    break;
               /* case FHBankParams.FH_VIDEO_NEW_PUSH://新推送
                    onNewPush(ros.getMsg());
                    break;*/
                case PUSH_TIME://新推送显示5秒后隐藏
                    FHPushView.getInstance().hiddenShow();
                    break;
                case SHOW_PUSH://显示推送
                    showPush(ros);
                    break;
                case SHOW_TOOL:
                    hideTool();
                    break;
                case FHUIConstants.ONCLICK_FLOAT:
                    if (isPush) {
                        //videoListener.removeFloatView();
                        EventBus.getDefault().post(new UiEvent("", true, FHBankParams.FH_BACK_METTING));

                        isPush = false;
                        FHWebView.getInstance().hidden();
                    }
                    break;
            }
    }

    private void showPush(UiEvent ros) {
        iv_push_point.setVisibility(View.INVISIBLE);
        iv_more_point.setVisibility(View.GONE);
        FHPushView.getInstance().hidden();
        if (Build.VERSION.SDK_INT >= 23) {
            if (!FHPermission.getInstance().checkFloat(mContext)) {
                FHPermission.getInstance().applyFloat(mContext);
                return;
            }
        }
        FloatManager.getInstance().createFloatView(mContext, FHUIConstants.FH_FLOAT_TYPE_PUSH);
        isPush = true;
        FHWebView.getInstance().loadUrl(ros.getMsg());
    }

    private void showCardHint(String type){
        if(type.equals(FHLiveClientParams.INTERACT_EVENT_CLOSE_CARD)){
            LogUtils.e("showCardHint::close_card");
            showTool();
            showOthers(View.VISIBLE);
            onClickSurface(rl_video_main);
            mask_idcard_face.setVisibility(View.GONE);
            iv_close_mask.setVisibility(View.GONE);
            return;
        }
        if(type.equals(FHLiveClientParams.INTERACT_EVENT_IDCARD_FACE)){
            mask_idcard_face.setImageResource(getMipmap("idcard_face_hint"));
        } else if(type.equals(FHLiveClientParams.INTERACT_EVENT_IDCARD_NATIONAL_EMBLEM)){
            mask_idcard_face.setImageResource(getMipmap("idcard_national_emblem"));
        } else if(type.equals(FHLiveClientParams.INTERACT_EVENT_BANK_CARD)){
            mask_idcard_face.setImageResource(getMipmap("bank_card_hint"));
        } else if(type.equals(FHLiveClientParams.INTERACT_EVENT_FACE_RECOGNITION)){
            mask_idcard_face.setImageResource(getMipmap("face_fount_hint"));
            FHVideoManager.getInstance().changeCameraType(0);
        }
        if( !type.equals(FHLiveClientParams.INTERACT_EVENT_FACE_RECOGNITION)){
            FHVideoManager.getInstance().changeCameraType(1);
        }
        onClickSurface(rl_video_local);
        showOthers(View.GONE);
        mask_idcard_face.setVisibility(View.VISIBLE);
        iv_close_mask.setVisibility(View.VISIBLE);
        hideTool();
    }

    /**
     * 获取图片id
     * @param name
     * @return
     */
    public int getMipmap(String name){
        return FHResource.getInstance().getId(mContext,"mipmap",name);
    }

    private boolean isPush = false;

    private boolean isStart = false;
    public void onStart(boolean isVideo) { //会话开启
        rl_main.setVisibility(View.GONE);
        isStart = true;
        if(!FHVideoManager.getInstance().getCallType().equals(FHLiveMacro.FH_LIVE_CALL_TYPE_NORMAL)
            && !FHVideoManager.getInstance().getCallType().equals(FHLiveMacro.FH_LIVE_CALL_TYPE_LOCALPREVIEW)){
            iv_more.setVisibility(View.GONE);
            iv_more_point.setVisibility(View.GONE);

        }
        showTool();
        if (runnable == null) {
            runnable = new VideoRunnable();
            videoHandler.postDelayed(runnable, 0);
        }
    }

    private VideoRunnable runnable;
    Handler videoHandler = new Handler();

    private class VideoRunnable implements Runnable {
        @Override
        public void run() {
            gettime();
            videoHandler.postDelayed(this, 1000);
        }
    }

    private int videotime = 0;

    //顶部提示信息
    private void gettime() {
        videotime++;

        String timer = getResString("fh_uid") + " " + FHUIConstants.getTellerId() + " " + getResString("fh_time");
        int mm = videotime / 60;
        int ss = videotime % 60;
        if (mm < 10) {
            timer = timer + " " + "0" + mm + getResString("fh_minute");
        } else {
            timer = timer + " " + mm + getResString("fh_minute");
        }
        if (ss < 10) {
            timer = timer + " 0" + ss + " " + getResString("fh_second");
        } else {
            timer = timer + " " + ss + " " + getResString("fh_second");
        }
        tv_time.setText(timer);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void ansEvent(AnsEvent ros) {
        if (ros != null && (
                ros.getType().equals(PUSH_TIME)
                        || ros.getType().equals(SHOW_TOOL)
        )) {
            if (ros.getType().equals(PUSH_TIME)) {
                SystemClock.sleep(5 * 1000);
                if (!StringUtil.isEmpty(FHPushView.getInstance().pushtime) && ros.getMsg().equals(FHPushView.getInstance().pushtime)) {
                    EventBus.getDefault().post(new UiEvent(ros.getMsg(), true, ros.getType()));
                }
            } else if (ros.getType().equals(SHOW_TOOL)) {
                SystemClock.sleep(5 * 1000);
                if (!StringUtil.isEmpty(show_tool_time) && ros.getMsg().equals(show_tool_time)) {
                    EventBus.getDefault().post(new UiEvent(ros.getMsg(), true, ros.getType()));
                }
            }
        }
    }

    //释放资源
    public void release() {
        isStart = false;
        isPush = false;
        show_tool_time = "";
        videotime = 0;
        isVideo = true;
        videoHandler.removeCallbacks(runnable);
        runnable = null;
        rl = null;
        EventBus.getDefault().unregister(this);
        instance = null;
        FHChatView.getInstance().release();
        FHHintView.getInstance().release();
        FHPushView.getInstance().release();
        FHWebView.getInstance().release();
    }

    private PushFile push;

    public void onNewPush(String data) {//新推送
        if (StringUtil.isEmpty(data))
            return;
        if(!FHVideoManager.getInstance().getCallType().equals(FHLiveMacro.FH_LIVE_CALL_TYPE_NORMAL)
                && !FHVideoManager.getInstance().getCallType().equals(FHLiveMacro.FH_LIVE_CALL_TYPE_LOCALPREVIEW))
            return;
            push = GsonUtil.fromJson(data, PushFile.class);
        if (push != null) {
            iv_more_point.setVisibility(View.VISIBLE);
            iv_push_point.setVisibility(View.VISIBLE);
            FHPushView.getInstance().onNewPush(push);
        }
    }

    private void onNewMsg(String data) {//新消息
        if (StringUtil.isEmpty(data))
            return;
        if(!FHVideoManager.getInstance().getCallType().equals(FHLiveMacro.FH_LIVE_CALL_TYPE_NORMAL)
                && !FHVideoManager.getInstance().getCallType().equals(FHLiveMacro.FH_LIVE_CALL_TYPE_LOCALPREVIEW))
            return;
        if (FHChatView.getInstance().rl_chat.getVisibility() == View.GONE) {
            iv_more_point.setVisibility(View.VISIBLE);
            iv_chat_point.setVisibility(View.VISIBLE);
            showTool();
        }
        FHChatView.getInstance().onNewMsg(data);
    }

    private boolean tellerScreen = false;
    private String tellerType = "";

    private String showScreenUserType = "";;

    // todo: 4柜员投屏
    public void onUptTellerType(String type, String data) {//柜员视频类型
        if (StringUtil.isEmpty(type))
            return;
        /*if (type.equals(FHBankParams.FH_START_PPT)) {//柜员ppt
            rl_teller_type.setVisibility(View.VISIBLE);
            tv_teller_type.setText(getResString("fh_show_ppt);
            iv_teller_type.setImageResource(getMipmap("ic_fh_ppt_ing);
            refreshView(data);
        } else*/
        rotation = false;
        if (type.equals(FHBankParams.FH_START_SCREEN)
                    ||type.equals(FHBankParams.FH_START_PPT)
            ) {//柜员投屏
            if(data.equals("1"))
                return;
            showScreenUserType = data;
            rl_teller_type.setVisibility(View.VISIBLE);
            tv_teller_type.setText(getResString("fh_show_screen"));
            iv_teller_type.setImageResource(getMipmap("ic_fh_screen_ing"));
            refreshView(data);

            tmpSurfaceView.setOnClickListener(null);
            tmpSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mGestureTouchHandler.onTouchEvent(event);
                    return true;
                }
            });
            if(data.equals("3")){
                tellerType = "third";
            } else if(data.equals("2")){
                tellerType = "main";
            }
            iv_rotate.setVisibility(View.VISIBLE);
        } else if (type.equals(FHBankParams.FH_STOP_BLOOD)) {//视频
            if(!StringUtil.isEmpty(showScreenUserType) && !data.equals(showScreenUserType))
                return;
            showScreenUserType = "";
            rl_teller_type.setVisibility(View.GONE);
            tellerScreen = false;
            showOthers(View.VISIBLE);

            mGestureTouchHandler.reset();
            tmpSurfaceView.setOnTouchListener(null);
            tmpSurfaceView.setOnClickListener(this);
            iv_rotate.setVisibility(View.GONE);
            tellerType = "";
            isSubStream = false;
            if(data.equals("3")){
                FHVideoManager.getInstance().rotationSubStream(sv_third.getUid(), false);
            } else if(data.equals("2")){
                FHVideoManager.getInstance().rotationSubStream(sv_main.getUid(), false);
            }
        }

    }

    private FHSurfaceView tmpSurfaceView;
    private void refreshView(String flag) {
        if (FHUIConstants.iShowTellerShareScreenFloatView())
            return;
        if (flag.equals("3")) {
            tmpSurfaceView = sv_third;
            onClickSurface(rl_video_third);

        } else if (flag.equals("2")) {
            tmpSurfaceView = sv_main;
            onClickSurface(rl_video_main);
        }
        showOthers(View.GONE);
        tellerScreen = true;
    }

    boolean iss = false;

    // todo: 5点击事件
    @Override
    public void onClick(View v) {
        if (v.getId() == ll_min.getId()) {//最小化视频窗口
            onClickmin();
        } else if (v.getId() == iv_more.getId()) {//更多
            if(rl_more.getVisibility() == View.VISIBLE){
                rl_more.setVisibility(View.GONE);
            }else {
                iv_more_point.setVisibility(View.GONE);
                rl_more.setVisibility(View.VISIBLE);
                showTool();
            }

        } else if (v.getId() == ll_switch.getId()) {//前后摄像头切换
            if (isVideo) {
                hideTool();
                //切换摄像头
                FHVideoManager.getInstance().switchCamera();
            }
            //验证分支
        } else if (v.getId() == ll_push_btn.getId()) {//推送
            iv_push_point.setVisibility(View.INVISIBLE);
            iv_more_point.setVisibility(View.GONE);
            hideTool();
            FHPushView.getInstance().onClickPush();
        } else if (v.getId() == ll_chat_btn.getId()) {//聊天
            iv_chat_point.setVisibility(View.INVISIBLE);
            iv_more_point.setVisibility(View.GONE);
            hideTool();
            FHChatView.getInstance().onClickChat();
        } else if (v.getId() == ll_video_audio.getId()) {//音视频切换
            hideTool();
            uptVideoType();
        } else if (v.getId() == ll_screen_btn.getId()) {//投屏
            if (!FHBankParams.isConnected()) { //判断网络状态提示
                ToastUtil.getInatance(mContext).show(getResString("fh_net_weak_hint"));
                return;
            }
            hideTool();
            onClickScreen();
        } else if (v.getId() == ll_close_btn.getId()) {//结束会话
            hideTool();
            onClickClose();
        } else if (v.getId() == sv_local.getId()) {//
            onClickSurface(rl_video_local);

        } else if (v.getId() == sv_main.getId()) {//
            onClickSurface(rl_video_main);

        } else if (v.getId() == sv_third.getId()) {//
            onClickSurface(rl_video_third);
        } else if (v.getId() == iv_rotate.getId()) {//
            rotation = !rotation;
            if (tellerType.equals("subStream")) {
                videoListener.rotationRemote(rotation);
            } else if (tellerType.equals("main")) {
                if(isSubStream){
                    videoListener.rotationRemote(rotation);
                } else {
                    FHVideoManager.getInstance().rotationSubStream(sv_main.getUid(), rotation);
                }
            } else if(tellerType.equals("third")){
                if(isSubStream){
                    videoListener.rotationRemote(rotation);
                } else {
                    FHVideoManager.getInstance().rotationSubStream(sv_third.getUid(), rotation);
                }
                //FHVideoManager.getInstance().rotationSubStream(sv_third.getUid(), rotation);
            }

        } else if (v.getId() == iv_close_mask.getId()) {
            showCardHint("close_card");
        } else if(v.getId() == cancel_open.getId()){
            rl_open_webcam.setVisibility(View.GONE);
        }

    }

    //用户离开
    private void onUserLeave(String data) {

        if (data.equals(sv_third.getUid())) {
            rl_video_local.setLayoutParams(local_sv_params);
            rl_video_main.setLayoutParams(main_sv_params);
            rl_video_third.setLayoutParams(third_sv_params);
            sv_fourth.setLayoutParams(forth_sv_params);
            rl_video_main.setZ(10);
            rl_video_local.setZ(15);
            rl_video_third.setZ(15);
            sv_fourth.setZ(15);

            tmpSurface = rl_video_main;
            showOthers(View.VISIBLE);
            rl_video_third.setVisibility(View.GONE);
            rl_video_main.setVisibility(View.VISIBLE);
            if(FHVideoManager.getInstance().getCallType().equals(FHLiveMacro.FH_LIVE_CALL_TYPE_NORMAL)
                    && !FHVideoManager.getInstance().getCallType().equals(FHLiveMacro.FH_LIVE_CALL_TYPE_LOCALPREVIEW))
                ToastUtil.getInatance(mContext).show(getResString("fh_thrid_close"));
        }
    }
    /**
     * 获取String.xml中字符串
     * @param name 字符串id名
     * @return
     */
    protected String getResString(String name){
        return FHResource.getInstance().getString(mContext,name);
    }

    /**
     * 设置单个view
     * @param name 控件id名
     */
    protected <T extends View> T getView(String name){
        T view = rl.findViewById(FHResource.getInstance().getId(mContext,"id",name));
        if(view == null){
            Log.e("FH_ERROR","findViewById 出错："+name);
        }
        return view;
    }

    /**
     * 设置单个view
     * @param name 控件id名
     * @param listener 点击事件监听
     */
    protected <T extends View> T getView(String name, View.OnClickListener listener){
        T view = rl.findViewById(FHResource.getInstance().getId(mContext,"id",name));
        if(view == null ) {
            Log.e("FH_ERROR","findViewById 出错："+name);
            return view;
        }
        if(listener != null)
            view.setOnClickListener(listener);
        return view;
    }
    private boolean rotation = false;

    public void switchSurface(View surfaceA, View surfaceB) {//切换画布
        if (Build.VERSION.SDK_INT >= 21) {
            float tmpz = surfaceA.getZ();
            ViewGroup.LayoutParams tmp = (ViewGroup.LayoutParams) surfaceA.getLayoutParams();
            surfaceA.setLayoutParams(surfaceB.getLayoutParams());
            surfaceA.setZ(surfaceB.getZ());
            surfaceB.setLayoutParams(tmp);
            surfaceB.setZ(tmpz);
        }
    }

    private View tmpSurface;//临时主画布

    public void changeMainSurfaceLocation(View surface) {
        if (tmpSurface == null)
            tmpSurface = rl_video_main;
        if (tmpSurface.equals(surface))
            return;
        switchSurface(surface, tmpSurface);
        tmpSurface = surface;
    }

    private void onClickSurface(View view) {
        if (view.getLayoutParams().width == RelativeLayout.LayoutParams.MATCH_PARENT || view.getLayoutParams().width > 900) {
            if(!isStart)
                return;
            FHChatView.getInstance().hidden();
            FHPushView.getInstance().hidden();
            if (rl_tool.getVisibility() == View.VISIBLE)
                hideTool();
            else
                showTool();
        } else {
            if (FHVideoManager.getInstance().getCallType().equals(FHLiveMacro.FH_LIVE_CALL_TYPE_LOCALPREVIEW))
                return;
            changeMainSurfaceLocation(view);
        }
    }

    private void showBottom(boolean show) {
        if (show) {
            showTool();
        } else {
            hideTool();
        }

    }

    private boolean othersShow = true;//小窗口是否显示

    //右侧小窗口控制
    private void showOthers(int gone) {
        if (tellerScreen)
            return;
        if (gone == View.GONE)
            othersShow = false;
        else
            othersShow = true;
        if (rl_video_local.getLayoutParams().width > 0 && rl_video_local.getLayoutParams().width < 900 && !StringUtil.isEmpty(sv_local.getUid())) {
            rl_video_local.setVisibility(gone);
        }
        if (rl_video_main.getLayoutParams().width > 0 && rl_video_main.getLayoutParams().width < 900 && !StringUtil.isEmpty(sv_main.getUid())) {
            rl_video_main.setVisibility(gone);
        }
        if (rl_video_third.getLayoutParams().width > 0 && rl_video_third.getLayoutParams().width < 900 && !StringUtil.isEmpty(sv_third.getUid())) {
            rl_video_third.setVisibility(gone);
        }
    }

    private void onClickmin() {
        hideTool();
        FHHintView.getInstance().show(getResString("fh_min_video_sure"), getResString("fh_cancel"), getResString("fh_confirm"), new FHHintView.OnClickHintListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                //最小化视频窗
                videoListener.minVideo(FHUIConstants.FH_FLOAT_TYPE_MIN);
            }
        });
    }

    //todo: 5 投屏点击
    private void onClickScreen() {
        FHHintView.getInstance().show(getResString("fh_share_screen"),
                getResString("fh_cancel"),
                getResString("fh_confirm"),
                new FHHintView.OnClickHintListener() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onConfirm() {
                        //切换投屏
                        videoListener.uptVideoType(FHBankParams.FH_VIDEO_TYPE_SREEN);
                    }
                });
    }

    private void onClickClose() {
        if (!FHBankParams.isConnected()) {
            noNet();
            return;
        }
        FHHintView.getInstance().show(getResString("fh_close_video"),
                getResString("fh_cancel"),
                getResString("fh_confirm"),
                new FHHintView.OnClickHintListener() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onConfirm() {
                        //结束会话
                        videoListener.leave();
                    }
                });
    }

    private int showHintNum = 0; //统计弹框显示次数

    private void noNet() {
        if (showHintNum != 0) {
            showHintNum = 0;
            videoListener.closeVideo(false);
            return;
        }
        showHintNum++;
        FHHintView.getInstance().warn(getResString("leave_net_error"));
    }

    public void setVideoType(boolean video) {
        isVideo = !video;
        uptVideoType();
    }

    private boolean isVideo = true;

    private void uptVideoType() {
        isVideo = !isVideo;
        if (isVideo) {
            iv_video_audio.setImageResource(getMipmap("ic_fh_switch_audio"));
            tv_video_audio.setText(getResString("fh_change_audio"));
            iv_switch.setImageResource(getMipmap("ic_fh_switch_camera"));
            tv_switch.setTextColor(FHResource.getInstance().getColor(mContext,"white"));
            videoListener.uptVideoType(FHBankParams.FH_VIDEO_TYPE_VIDEO);

        } else {
            iv_video_audio.setImageResource(getMipmap("ic_fh_switch_camera"));
            tv_video_audio.setText(getResString("fh_change_video"));
            iv_switch.setImageResource(getMipmap("ic_fh_switch_nocamera"));
            tv_switch.setTextColor(FHResource.getInstance().getColor(mContext,"fh_color_9c"));
            videoListener.uptVideoType(FHBankParams.FH_VIDEO_TYPE_AUDIO);

        }
    }


    private String show_tool_time = "";
    private RelativeLayout.LayoutParams params;

    private void showTool() {
        if (rl_tool.getVisibility() != View.VISIBLE)
            rl_tool.setVisibility(View.VISIBLE);
        show_tool_time = new Date().getTime() + "";
    }

    private void hideTool() {
        rl_tool.setVisibility(View.GONE);
        rl_more.setVisibility(View.GONE);
        show_tool_time = new Date().getTime() + "";
    }

    private void uptParams(View view, int top) {
        params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.topMargin = SystemUtil.dp2px(mContext, top);
        view.setLayoutParams(params);
    }

    private ViewGroup.LayoutParams local_sv_params, main_sv_params, third_sv_params, forth_sv_params;
    private FHSurfaceView sv_main, sv_local, sv_third, sv_fourth;
    private RelativeLayout rl_main, rl_video_main, rl_video_local, rl_video_third;
    private ImageView  iv_main_screen;

    private void initSurface() {
        //主画面
        sv_main = getView("sv_main");
        rl_video_main = getView("rl_video_main");
        rl_video_main.setZ(10);
        sv_main.setOnClickListener(this);

        sv_local = getView("sv_local");
        rl_video_local = getView("rl_video_local");
        rl_video_local.setZ(15);
        sv_local.setOnClickListener(this);

        //第三方
        sv_third = getView("sv_third");
        rl_video_third = getView("rl_video_third");
        rl_video_third.setZ(15);
        sv_third.setOnClickListener(this);


        //第四方
        sv_fourth = getView("sv_fourth");
        sv_fourth.setZ(15);

        rl_main = getView("rl_main");
        rl_main.setZ(9);

        iv_main_screen = getView("iv_main_screen");
        iv_main_screen.setZ(12);
        //initScroll();        //本地画面

        //rl_fourth = getView("rl_fourth);
        tmpSurfaceView = sv_main;
        local_sv_params = rl_video_local.getLayoutParams();
        main_sv_params = rl_video_main.getLayoutParams();
        third_sv_params = rl_video_third.getLayoutParams();
        forth_sv_params = sv_fourth.getLayoutParams();

        if (FHVideoManager.getInstance().getCallType().equals(FHLiveMacro.FH_LIVE_CALL_TYPE_LOCALPREVIEW)){
            rl_video_local.setVisibility(View.GONE);
            rl_video_third.setVisibility(View.GONE);
            sv_fourth.setVisibility(View.GONE);
            videoListener.setSurface(null, sv_main, null, null);
        }else {
            //设置画布
            videoListener.setSurface(sv_main, sv_local, sv_third, sv_fourth);
        }

        initTouch();
    }

    private GestureTouchHandler mGestureTouchHandler;
    private void initTouch(){
        mGestureTouchHandler = new GestureTouchHandler(mContext.getApplicationContext());
        mGestureTouchHandler.setOnTouchResultListener(new GestureTouchHandler.OnTouchResultListener() {
            @Override
            public void onTransform(float x1, float y1, float x2, float y2) {

            }

            @Override
            public void onScalForm(Matrix matrix) {
                tmpSurfaceView.setAnimationMatrix(matrix);

            }

            @Override
            public void onClick() {
                FHChatView.getInstance().hidden();
                FHPushView.getInstance().hidden();
                if (rl_tool.getVisibility() == View.VISIBLE)
                    hideTool();
                else
                    showTool();            }
        });
        mGestureTouchHandler.setViewSize(winWidth,rl_video.getLayoutParams().height);
    }


    private int winWidth = 0, winHeight = 0;

    private void getSize() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        winWidth = outMetrics.widthPixels;
        winHeight = outMetrics.heightPixels;
    }

    private RelativeLayout.LayoutParams rlParams;

    private void initFl() {
        rl_video = getView("rl_video");
        rl_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        rlParams = (RelativeLayout.LayoutParams) rl_video.getLayoutParams();
        /*if (winHeight == 0 || winHeight == 0)
            return;
        if (winHeight * 9 - winWidth * 16 > 0) {
            rlParams.height = winWidth * 16 / 9;
            rl_video.setLayoutParams(rlParams);
        }*/
    }

    private RelativeLayout rl_tool, rl_more, rl_teller_type, rl_video, rl_open_webcam;
    private LinearLayout ll_close_btn, ll_min, ll_push_btn, ll_chat_btn, ll_video_audio, ll_screen_btn, ll_switch;
    private TextView tv_time, tv_video_audio, tv_switch, tv_teller_type, cancel_open, sure_open;
    private ImageView iv_more, iv_more_point, iv_push_point, iv_chat_point, iv_video_audio, iv_switch, iv_teller_type, iv_rotate
            //, iv_hint_slide
            ;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initTool() {

        rl_tool = getView("rl_tool");
        rl_tool.setZ(110);
        /*rl_tool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideTool();
            }
        });*/
        tv_time = getView("tv_time");
        //柜员状态
        rl_teller_type = getView("rl_teller_type");
        rl_teller_type.setZ(110);
        iv_teller_type = getView("iv_teller_type");
        tv_teller_type = getView("tv_teller_type");

        //更多
        iv_more = getView("iv_more");
        iv_more_point = getView("iv_more_point");
        iv_more.setOnClickListener(this);

        //旋转
        iv_rotate = getView("iv_rotate");
        iv_rotate.setOnClickListener(this);

        rl_more = getView("rl_more");
        //推送
        ll_push_btn = getView("ll_push_btn");
        iv_push_point = getView("iv_push_point");
        ll_push_btn.setOnClickListener(this);

        //聊天
        ll_chat_btn = getView("ll_chat_btn");
        iv_chat_point = getView("iv_chat_point");
        ll_chat_btn.setOnClickListener(this);

        //最小化
        ll_min = getView("ll_min");
        ll_min.setOnClickListener(this);
        //挂断
        ll_close_btn = getView("ll_close_btn");
        ll_close_btn.setOnClickListener(this);
        //投屏
        ll_screen_btn = getView("ll_screen_btn");
        ll_screen_btn.setOnClickListener(this);
        //切换摄像头
        ll_switch = getView("ll_switch");
        tv_switch = getView("tv_switch");
        iv_switch = getView("iv_switch");
        ll_switch.setOnClickListener(this);
        //音视频
        ll_video_audio = getView("ll_video_audio");
        tv_video_audio = getView("tv_video_audio");
        iv_video_audio = getView("iv_video_audio");
        ll_video_audio.setOnClickListener(this);

        /*iv_hint_slide = getView("iv_hint_slide);
        iv_hint_slide.setZ(110);*/

        iv_net_main = getView("iv_net_main");
        iv_net_local = getView("iv_net_local");
        iv_net_thrid = getView("iv_net_thrid");
        iv_net_main.setZ(16);
        iv_net_local.setZ(16);
        iv_net_thrid.setZ(16);

        mask_idcard_face = getView("mask_idcard_face");
        iv_close_mask = getView("iv_close_mask");
        mask_idcard_face.setOnClickListener(this);
        iv_close_mask.setOnClickListener(this);

        rl_open_webcam = getView("rl_open_webcam");
        cancel_open = getView("cancel_open");
        cancel_open.setOnClickListener(this);
        sure_open = getView("sure_open");

        Button btn_sendFrameImg = getView("btn_sendFrameImg");
        btn_sendFrameImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = FHFileDirUtil.getImg(mContext);
                System.out.println(path);
                FHLiveClient.getInstance().sendFrameImg( path + "/videostop.jpg");



//                FHLiveClient.getInstance().sendFrameImg( "/storage/sdcard0/DCIM/Camera/IMG_20231205_201651.jpg");

            }
        });

        Button btn_stopFrameImg = getView("btn_stopFrameImg");
        btn_stopFrameImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FHLiveClient.getInstance().stopFrameImg();
            }
        });


        final Button btn_audioRoute = getView("btn_audioRoute");
        btn_audioRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAudioRoute){
                    btn_audioRoute.setText("听筒");
                    FHVideoManager.getInstance().setAudioRoute(FHLiveMacro.FH_AUDIO_ROUTE_EARPIECE);
                }else {
                    btn_audioRoute.setText("扬声器");
                    FHVideoManager.getInstance().setAudioRoute(FHLiveMacro.FH_AUDIO_ROUTE_SPEAKER);
                }
                isAudioRoute = !isAudioRoute;
            }
        });
    }
    boolean isAudioRoute = true;

    private ImageView iv_net_main, iv_net_local, iv_net_thrid, mask_idcard_face, iv_close_mask;

    /**
     * @param user    main主画面 loacl自己画面 thrid第三方画面
     * @param quality
     */
    public void showNetStatus(String user, int quality) {
        ImageView imageView = null;
        if (user.equals("main")) {
            imageView = iv_net_main;
        } else if (user.equals("local")) {
            imageView = iv_net_local;
        } else if (user.equals("third")) {
            imageView = iv_net_thrid;
        }
        if (imageView == null)
            return;
        int imgName = 0;
        if (quality == FHBankParams.QUALITY_Poor) {
            imgName = getMipmap("ic_fh_video_network_poor");
        } else if (quality == FHBankParams.QUALITY_Bad) {
            imgName = getMipmap("ic_fh_video_network_bad");
        } else if (quality == FHBankParams.QUALITY_Vbad) {
            imgName = getMipmap("ic_fh_video_network_vbad");
        } else {
            imgName = getMipmap("ic_fh_video_network_good");
        }
        Glide.with(mContext)
                .load(imgName)
                .thumbnail(0.1f)
                .into(imageView);
    }

}