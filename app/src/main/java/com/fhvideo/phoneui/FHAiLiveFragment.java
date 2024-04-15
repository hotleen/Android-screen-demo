package com.fhvideo.phoneui;

import static android.content.Context.TELEPHONY_SERVICE;
import static com.fhvideo.FHLiveClientParams.getSelectFunctionUrl;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fhvideo.FHLiveClient;
import com.fhvideo.FHLiveClientParams;
import com.fhvideo.FHResource;
import com.fhvideo.adviser.bean.CallData;
import com.fhvideo.adviser.bean.CusCallInfo;
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
import com.fhvideo.phoneui.bean.AiChekParam;
import com.fhvideo.phoneui.bean.FHAICheckParam;
import com.fhvideo.phoneui.bean.FHAISelectFunctionResult;
import com.fhvideo.phoneui.bean.FHFunctionSelectBean;
import com.fhvideo.phoneui.busi.FHLiveListener;
import com.fhvideo.phoneui.busi.FHVideoListener;
import com.fhvideo.phoneui.floatt.FloatManager;
import com.fhvideo.phoneui.utils.ToastUtil;
import com.fhvideo.phoneui.view.FHAICheckView;
import com.fhvideo.phoneui.view.FHCallView;
import com.fhvideo.phoneui.view.FHChatView;
import com.fhvideo.phoneui.view.FHHintView;
import com.fhvideo.phoneui.view.FHWebView;
import com.fhvideo.phoneui.view.FHWebViewNoSingle;
import com.fhvideo.phoneui.web.FHWebFinishCallBack;
import com.google.gson.JsonParser;
import com.tencent.smtt.sdk.WebView;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

public class FHAiLiveFragment extends FHBaseFragment implements FHVideoListener {

    private FHSurfaceView mainRender, localRender, thirdRender, fourthRender;
    private FHPhoneStateListener fhPhoneStateListener;
    private TelephonyManager telephonyManager;
    private FHLiveListener listener;
    private static FHAiLiveFragment liveFragment = null;
    /**
     * 是否是前置摄像头
     */
    private boolean isfront = true;
    /**
     * 是否正在打电话
     */
    private boolean FHPhoneState = false;

    /**
     * 是否开始会话了
     *
     * @param uid
     * @param chatMode
     * @return
     */
    private boolean isStart = false;

    private boolean isMin = false;

    private boolean isScreen = false;

    private boolean isPause = false;

    private long minVideoTime = 0;

    private int portTime = 0;
    private String showWebTime = "";

    private String uid, sessionid;
    private boolean isteller = false, isvideo = false, startType = true;
    private boolean istransfer = false; //会话中收到一定是转接
    private String wavpath = "video.wav";
    //结束会话
    private boolean isClose = false;

    private int leaveErrorNum = 0;

    private boolean hasautoback = false;

    private int showHintNum = 0; //统计弹框显示次数

    private RelativeLayout rl_ai_border, rl_video_local, rl_video_main, rl_main, rl_ai_live;
    private TextView tv_ai_hint_title;
    private ImageView iv_close_btn;
    private FHSurfaceView sv_local, sv_main;
    private int msgindex = 0;


    private int winWidth = 0//屏幕宽
            , winHeight = 0;//屏幕高

    private RelativeLayout.LayoutParams rlParams;
    private RelativeLayout rl_video;
    private int videoMargin = 0;//视频区域上下边距

    private boolean rotation = false;

    private View tmpSurface;//临时主画布

    private FHBaseCallView baseCallView;//呼叫view
    private CallData mData;//呼叫参数
    private boolean isCalling = false;

    private boolean isShow = false;//是否在前台
    private boolean isConfirm = false;//座席是否接通

    private FHWebViewNoSingle msgFhWebView;//消息字幕弹窗webview
    private FHWebViewNoSingle dialogFhWebView;// dialog 弹窗选择webview
    private FrameLayout dialogWebviewParent;// dialog webview 父控件

    private FHAICheckView fhaiCheckView;//ai检测view

    private ImageView red_border_img;
    private AlphaAnimation animation;

    private CountDownTimer aiCheckCountDownTimer;//人脸验证最多90s自动消失

    private boolean needCancleOkhttp = true;//释放资源时是否需要取消网络请求

    /**
     * webview未初始化完成缓存的最新的字幕IM消息
     */
    private String newImMsg = "";
    /**
     * 字幕webview是否初始化完成
     */
    private boolean isMsgWebFinish;


    public static FHAiLiveFragment newInstance(String liveData) {
        Bundle args = new Bundle();
        args.putString("liveData", liveData);
        if (liveFragment == null) {
            synchronized (FHAiLiveFragment.class) {
                if (liveFragment == null) {
                    liveFragment = new FHAiLiveFragment();
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
    private FHAiLiveFragment() {
    }


    protected void initView() {
        rl_ai_live = getView("rl_ai_live");
        //初始化 呼叫ui
        initCall();
        getSize();
        initFl();
        rl_ai_border = getView("rl_ai_border");
        red_border_img = getView("red_border_img");
        tv_ai_hint_title = getView("tv_ai_hint_title");
        dialogWebviewParent = getView("web_view_parent");
        iv_close_btn = getView("iv_close_btn", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickClose();
            }
        });
        //主画面
        sv_main = getView("sv_main");
        rl_video_main = getView("rl_video_main");
        rl_main = getView("rl_main");
        sv_local = getView("sv_local");
        rl_video_local = getView("rl_video_local");

        FHWebView.getInstance().init(getActivity(), mView);
        FHHintView.getInstance().init(getActivity(), mView);

        setSurface(sv_local, sv_main, null, null);
        getWebTopMargin();
        initMsgWebView();
        initDialogWebView();
        fhaiCheckView = FHAICheckView.getInstance(getActivity());
        fhaiCheckView.init(getActivity(), mView);


        if (FHWebView.getInstance().getIv_web_close() != null) {
            FHWebView.getInstance().getIv_web_close().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideWeb("2", true);
                }
            });
        }

    }

    private void initDialogWebView() {
        dialogFhWebView = new FHWebViewNoSingle();
        dialogFhWebView.init(getActivity(), mView, "fh_dialog_web_view", new FHWebFinishCallBack() {
            @Override
            public void onPageFinished(WebView webView, String s) {
                //TODO
            }
        });
    }

    /**
     * 消息h5 view 初始化
     */
    private void initMsgWebView() {
        msgFhWebView = new FHWebViewNoSingle();
        msgFhWebView.init(getActivity(), mView, "fh_msg_view", new FHWebFinishCallBack() {
            @Override
            public void onPageFinished(WebView webView, String s) {
                isMsgWebFinish = true;
                if (!TextUtils.isEmpty(newImMsg)) {
                    msgFhWebView.getWebViewUtil().appToJs("subtitleText", newImMsg);
                    newImMsg = "";
                }
            }
        });
        msgFhWebView.loadUrl(FHLiveClientParams.getAiMsgUrl());
        msgFhWebView.getFh_web_view().setVisibility(View.INVISIBLE);

    }

    @Override
    public void onResume() {
        isShow = true;
        getFocus();
        super.onResume();
        if (!isCalling && minVideoTime != 0 && new Date().getTime() - minVideoTime > 1000) {
            EventBus.getDefault().post(new UiEvent("", true, FHBankParams.FH_BACK_METTING));
            return;
        }
        if (isCalling && isConfirm) {
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

    /**
     * 获取布局
     */
    @Override
    protected int getLayout() {
        return FHResource.getInstance().getId(getActivity(), "layout", "fh_fragment_ai_live");
    }


    @Override
    public void asyncEvent(AnsEvent ros) {
        super.asyncEvent(ros);
        if (ros != null && ros.getType().equals(FHUIConstants.UPT_WEB_TIME) && portTime > 0) {
            SystemClock.sleep(portTime * 1000);
            if (!StringUtil.isEmpty(showWebTime)
                    && ros.getMsg().equals(showWebTime)
            ) {
                EventBus.getDefault().post(new UiEvent(ros.getMsg(), ros.getType()));
            }
        }

    }


    /**
     * 切换本地与远程流界面
     */
    private void changeLayoutParams() {
        ViewGroup.LayoutParams localParams = rl_video_local.getLayoutParams();
        ViewGroup.LayoutParams mainParams = rl_video_main.getLayoutParams();

        rl_video_local.setLayoutParams(mainParams);
        rl_video_main.setLayoutParams(localParams);

        rl_video_local.setSelected(!rl_video_local.isSelected());

        if (rl_video_local.isSelected()) {
            rl_video_main.bringToFront();
        } else {
            rl_video_local.bringToFront();
        }
    }

    /**
     * 主线程 事件
     */
    public void mainEvent(UiEvent ros) {
        super.mainEvent(ros);
        if (ros != null && ros.getType() != null) {
            switch (ros.getType()) {
                case FHUIConstants.UPT_WEB_TIME://
                    if (!StringUtil.isEmpty(showWebTime) && ros.getMsg().equals(showWebTime)) {
                        hideWeb("2", true);
                    }
                    break;
                case FHBankParams.INTERACT_EVENT_HIDE_TIP:// 人脸 检测提示 隐藏
                    rl_ai_border.setVisibility(View.GONE);
                    red_border_img.setVisibility(View.GONE);
                    red_border_img.clearAnimation();

                    fhaiCheckView.showBorderInCheck(false,"");
                    break;
                case FHBankParams.INTERACT_EVENT_SHOW_TIP:// 人脸 检测提示 显示
                    FHAiParams params = GsonUtil.fromJson(ros.getMsg(), FHAiParams.class);
                    if (params != null && !StringUtil.isEmpty(params.getMsg())) {
                        tv_ai_hint_title.setText(params.getMsg());
                        rl_ai_border.setVisibility(View.VISIBLE);

                        fhaiCheckView.showBorderInCheck(true,params.getMsg());
                    }
                    red_border_img.setVisibility(View.VISIBLE);
                    startRedBorderAnimation();
                    break;
                case FHBankParams.INTERACT_EVENT_HIDE_WEB://web 隐藏
                    hideWeb("2", false);
                    break;
                case FHBankParams.INTERACT_EVENT_SUBTITLES:// 字幕
                    final com.fhvideo.phoneui.FHAiMsg aiMsg = GsonUtil.fromJson(ros.getMsg(), FHAiMsg.class);
                    if (aiMsg != null && aiMsg.getMsg() != null) {
                        if (msgFhWebView.getFh_web_view() != null && msgFhWebView.getFh_web_view().getVisibility() == View.INVISIBLE) {
                            msgFhWebView.getFh_web_view().setVisibility(View.VISIBLE);
                        }
                        if (isMsgWebFinish) {
                            newImMsg = "";
                            msgFhWebView.getWebViewUtil().appToJs("subtitleText", aiMsg.getMsg().replaceAll("\r|\n|\t", "").trim());
                        } else {
                            newImMsg = aiMsg.getMsg().replaceAll("\r|\n|\t", "").trim();
                        }
                    }

                    break;
                case "jatoapp":// js 交互

                    if ("selectEnd_determine".equals(ros.getMsg())) {//选择 操作
                        dealSelectFunction(ros.getObj());
                    } else if ("selectEnd_cancel".equals(ros.getMsg())) {//选择 操作
                        dialogFhWebView.hiddenParent(dialogWebviewParent);
                    } else {
                        String type;
                        if (ros.getMsg().equals("sure")) {
                            type = "1";
                        } else {
                            type = "2";
                        }
                        hideWeb(type, true);
                    }

                    break;
                case FHUIConstants.ONCLICK_FLOAT:
                    hideWeb("2", true);
                    break;
                case FHBankParams.INTERACT_EVENT_DISPLAY_WEB:
                    FHAiParams aiparams = GsonUtil.fromJson(ros.getMsg(), FHAiParams.class);
                    if (aiparams == null || StringUtil.isEmpty(aiparams.getUrl())) {
                        return;
                    }
                    showWebTime = System.currentTimeMillis() + "";
                    portTime = aiparams.getExpireTime();
                    EventBus.getDefault().post(new AnsEvent(showWebTime, FHUIConstants.UPT_WEB_TIME));
                    FHWebView.getInstance().loadUrl(aiparams.getUrl());
                    if (!rl_video_local.isSelected()){
                        changeLayoutParams();
                    }
                    break;
                case FHBankParams.FH_PHONE_STATE:
                    int stat = (int) ros.getObj();
                    uptPhoneStat(stat);
                    break;
                case FHBankParams.FH_CLOSE_PHONE:
                    restartVideo();
                    break;
                case "after_join":

                    break;
                case FHBankParams.ERROR_MSG_SHOW_POP:
                    if (StringUtil.isEmpty(ros.getMsg())) {
                        warn(getResString("fh_error"));
                    } else {
                        warn(ros.getMsg());
                    }
                    break;
                case FHBankParams.FH_BACK_METTING:
                    backMetting();
                    break;
                case FHUIConstants.MIN_VIDEO:
                    minVideo(FHUIConstants.FH_FLOAT_TYPE_SHARE);
                    break;
                case FHLiveClientParams.INTERACT_EVENT_LIVING_CHECK://活体检测：IM通知动作信息
                    // TODO: 2021/9/15 展示人脸识别 并提交 卡框位置 现在为虚拟流程
                    final AiChekParam aiChekParam = GsonUtil.fromJson(ros.getMsg(), AiChekParam.class);
                    if (aiChekParam == null) {
                        return;
                    }
                    if (aiCheckCountDownTimer != null) {
                        aiCheckCountDownTimer.cancel();
                    }
                    aiCheckCountDownTimer = new CountDownTimer(90 * 1000, 1000) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            fhaiCheckView.hidden();
                            msgFhWebView.getFh_web_view().setVisibility(View.VISIBLE);
                        }
                    };
                    aiCheckCountDownTimer.start();
                    fhaiCheckView.show();
                    msgFhWebView.getFh_web_view().setVisibility(View.GONE);
                    FHAICheckParam aiCheckParam = fhaiCheckView.getCheckParam();
                    FHLiveClient.getInstance().livingCheckStart(aiCheckParam.getWidthRatio(), aiCheckParam.getHeightRatio(), aiCheckParam.getXratio(), aiCheckParam.getYratio(), aiChekParam.getActionSequence(), new FHBusiCallBack() {
                        @Override
                        public void onSuccess(String s) {
                            fhaiCheckView.startCheck(aiChekParam);
                        }

                        @Override
                        public void onError(String s) {

                        }
                    });

                    break;
                case FHLiveClientParams.INTERACT_EVENT_FUNCTION_SELECT://ai 选择重新呼叫||转人工||挂断IM
//                    FHFunctionSelectBean functionSelectBean = GsonUtil.fromJson(ros.getMsg(), FHFunctionSelectBean.class);
                    dialogFhWebView.showParentAndWebview(dialogWebviewParent);
                    dialogFhWebView.loadUrl(getSelectFunctionUrl() + ros.getMsg());
                    break;
                case FHLiveClientParams.INTERACT_EVENT_LIVING_CHECK_RESULT://ai活体检测：IM通知检测结果
                    aiCheckCountDownTimer.cancel();
                    fhaiCheckView.hidden();
                    msgFhWebView.getFh_web_view().setVisibility(View.VISIBLE);
                    break;
                case FHBankParams.FH_NET_WEAK:
                    if (FHLiveClient.getInstance().isConnected()){
                        ToastUtil.getInatance(getActivity()).show(getResString("fh_net_bad"));
                    }
                    break;
            }
        }
    }

    /**
     * 处理选择事件
     * @param obj
     */
    private void dealSelectFunction(Object obj) {
        String content = (String) obj;
        dialogFhWebView.hiddenParent(dialogWebviewParent);
        if (TextUtils.isEmpty(content)) {
            return;
        }
        //解析
        String jsonString = new JsonParser().parse(content).getAsString();
        FHAISelectFunctionResult result = GsonUtil.fromJson(jsonString, FHAISelectFunctionResult.class);
        //获取type 字段
        String type = result.getType();
        if (TextUtils.isEmpty(type)) {
            return;
        }
        switch (type) {
            case "trans"://转人工
                FHFunctionSelectBean selectBean = result.getData();
                if (selectBean == null) {
                    return;
                }

                FHLiveClient.getInstance().aiTrans(selectBean.getInstId(), selectBean.getSkillId(), selectBean.getTransBy(), new FHBusiCallBack() {
                    @Override
                    public void onSuccess(String s) {

                    }

                    @Override
                    public void onError(String s) {

                    }
                });

                break;
            case "recall"://重新呼叫
                FHLiveClient.getInstance().aiRecall(new FHBusiCallBack() {
                    @Override
                    public void onSuccess(String s) {

                    }

                    @Override
                    public void onError(String s) {

                    }
                });
                break;
            case "close"://关闭
                aileave();
                break;
        }
    }


    public void aileave() {
        FHVideoManager.getInstance().closeVideoByAiLeave(new FHBusiCallBack() {
            @Override
            public void onSuccess(String s) {
                ToastUtil.getInatance(getActivity()).show("已挂断");
                closeVideo(false);
                if (com.fhvideo.phoneui.FHLiveActivity.autoLogout)
                    FHVideoManager.getInstance().logout(null);
            }

            @Override
            public void onError(String s) {
                if (leaveErrorNum == 0) {
                    leaveErrorNum++;
                    listener.showError(s, false);
                    closeVideo(false);
                } else {
                    leaveErrorNum = 0;
                    ToastUtil.getInatance(getActivity()).show("已挂断");
                    closeVideo(false);
                }
            }
        });
    }

    /**
     * 人脸出框 红色提示框闪动动画
     */
    private void startRedBorderAnimation() {
        if (animation == null) {
            animation = new AlphaAnimation(1, 0);
            animation.setRepeatCount(-1);
            animation.setDuration(100);
            animation.setStartOffset(300);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatMode(Animation.REVERSE);
        }


        red_border_img.startAnimation(animation);

    }


    private void hideWeb(String type, boolean resourceConfirm) {
        if (rl_video_local.isSelected()){
            changeLayoutParams();
        }
        showWebTime = "";
        if (resourceConfirm) {
            FHLiveClient.getInstance().staticResourConfirm(type, new FHBusiCallBack() {
                @Override
                public void onSuccess(String s) {
                }

                @Override
                public void onError(String s) {
                }
            });
        }

        FHChatView.getInstance().hideSoftInputFromWindow();
        FHWebView.getInstance().hidden();
//        backMetting();
//        EventBus.getDefault().post(new UiEvent("", true, FHBankParams.FH_BACK_METTING));
    }

    private void warn(String msg) {
        FHHintView.getInstance().warn(msg);
    }

    public void ansEvent(AnsEvent ros) {
        super.asyncEvent(ros);
        if (ros != null && ros.getType().equals(FHBankParams.FH_CLOSE_PHONE)) {
            if (ros.getType().equals(FHBankParams.FH_CLOSE_PHONE)) {
                SystemClock.sleep(100);
                EventBus.getDefault().post(new UiEvent("", ros.getType()));
            }
        }
    }


    /**
     * 事件处理
     *
     * @param evetn
     * @param msg
     */
    public void receiveEvent(String evetn, String msg) {
        if (isCalling) {
            callEvent(evetn, msg);
        } else {
            videoEvent(evetn, msg);
        }

    }

    /**
     * 视频事件
     *
     * @param evetn
     * @param msg
     */
    private void videoEvent(String evetn, String msg) {
        if (evetn == null) {
            return;
        }
        switch (evetn) {
            case FHBankParams.FH_TELLER_CLOSE_VIDEO:
                if (FHLiveClient.getInstance().isTransferArtificial() || FHLiveClient.getInstance().isAiRecall()) {
                    needCancleOkhttp = false;
                }
                ToastUtil.getInatance(getActivity()).show(getResString("fh_teller_close"));
                closeVideo(true);
                needCancleOkhttp = true;
                break;
            case FHBankParams.FH_VIDEO_ON_FIRST_FRAME://主画布第一帧画面绘制
                rl_main.setVisibility(View.GONE);
                break;
            case FHBankParams.INTERACT_EVENT_HIDE_WEB:
                hideWeb("2", false);
                break;
            case FHBankParams.INTERACT_EVENT_HIDE_TIP:
                rl_ai_border.setVisibility(View.GONE);
                break;
            case FHBankParams.INTERACT_EVENT_SHOW_TIP:
                FHAiParams params = GsonUtil.fromJson(msg, FHAiParams.class);
                if (params != null && !StringUtil.isEmpty(params.getMsg())) {
                    tv_ai_hint_title.setText(params.getMsg());
                    rl_ai_border.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    /**
     * 加入会话
     */
    private void startVideo() {
        FHVideoManager.getInstance().initVideoFragment(getActivity()
                , new FHBusiCallBack() {
                    @Override
                    public void onSuccess(String str) {
                        isStart = true;
                    }

                    @Override
                    public void onError(String error) {
                        listener.showError(error, false);
                    }
                });

    }


    /**
     * 坐席挂断传true, 自己挂断false
     *
     * @param isteller
     */
    @Override
    public void closeVideo(boolean isteller) {
        isClose = true;
        listener.close(isteller);
        FloatManager.getInstance().removeFloatView(getActivity());
        if (needCancleOkhttp) {
            FHVideoManager.getInstance().releaseVideo();
        } else {
            FHVideoManager.getInstance().releaseVideoNoCancleOkhttp();
        }

        FHAPlayer.getInstance().stopPlayer();

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


    @Override
    public void uptVideoType(String msg) {
        if (msg.equals(FHBankParams.FH_VIDEO_TYPE_VIDEO)) {
            changeVideo();
        } else if (msg.equals(FHBankParams.FH_VIDEO_TYPE_AUDIO)) {
            changeAudio();
        } else if (msg.equals(FHBankParams.FH_VIDEO_TYPE_SREEN)) {
            changeScreen();
        }
    }

    @Override
    public void setSurface(FHSurfaceView mainSurface, FHSurfaceView local, FHSurfaceView
            third, FHSurfaceView fourth) {
        mainRender = mainSurface;
        localRender = local;
        thirdRender = third;
        fourthRender = fourth;
        FHVideoManager.getInstance().setSurface(getActivity(), mainSurface, local, third, fourth);
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
        FHVideoManager.getInstance().changeSurface(localRender, FHVideoParams.getIdentity());
        mainRender.setVisibility(View.VISIBLE);
        FloatManager.getInstance().removeFloatView(getActivity());
    }


    @Override
    public void leave() {
        FHVideoManager.getInstance().closeVideo(new FHBusiCallBack() {
            @Override
            public void onSuccess(String s) {
                ToastUtil.getInatance(getActivity()).show("已挂断");
                closeVideo(false);
                if (FHLiveActivity.autoLogout)
                    FHVideoManager.getInstance().logout(null);
            }

            @Override
            public void onError(String s) {
                if (leaveErrorNum == 0) {
                    leaveErrorNum++;
                    listener.showError(s, false);
                    closeVideo(false);
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
                isScreen = true;
                FHVideoManager.getInstance().changeScreen();
            } else {
                listener.showError(getResString("fh_phone_not_support"), false);
            }
        } else {
            FHPermission.getInstance().applyFloat(getActivity());
        }
    }


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
    }

    //主界面获取焦点
    private void getFocus() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    onClickClose();
                    FHLog.addLog("[用户操作] 点击返回键");
                    //点击返回按钮
                    return true;
                }
                return false;
            }
        });
    }


    private void noNet() {
        if (showHintNum != 0) {
            showHintNum = 0;
            closeVideo(false);
            return;
        }
        showHintNum++;
        FHHintView.getInstance().warn(getResString("leave_net_error"));
    }

    private void onClickClose() {
        if (!FHBankParams.isConnected()) {
            noNet();
            return;
        }
        FHHintView.getInstance().show("您确定要退出当前会话吗？",
                getResString("fh_cancel"),
                getResString("fh_confirm"),
                new FHHintView.OnClickHintListener() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onConfirm() {
                        //结束会话
                        leave();
                    }
                });
    }


    //获取web页顶边距=(屏幕高-视频区高)/2+本地窗口高+本地窗口顶边距
    private void getWebTopMargin() {
        RelativeLayout.LayoutParams localParams = (RelativeLayout.LayoutParams) rl_video_local.getLayoutParams();
        FHWebView.getInstance().uptParams(videoMargin / 2 + localParams.topMargin + localParams.height);
    }


    private void getSize() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        winWidth = outMetrics.widthPixels;
        winHeight = outMetrics.heightPixels;
    }

    private void initFl() {
        rl_video = getView("rl_video");
        rl_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        rlParams = (RelativeLayout.LayoutParams) rl_video.getLayoutParams();
        if (winHeight == 0 || winHeight == 0)
            return;
        if (winHeight * 9 - winWidth * 16 > 0) {
            rlParams.height = winWidth * 16 / 9;
            rl_video.setLayoutParams(rlParams);
            videoMargin = winHeight - rlParams.height;
        }
    }

    /**
     * 初始化事件和数据
     */
    @Override
    protected void initEventAndData() {
        try {
            fhPhoneStateListener = new FHPhoneStateListener();
            telephonyManager = (TelephonyManager) getActivity().getSystemService(TELEPHONY_SERVICE);
            telephonyManager.listen(fhPhoneStateListener,
                    PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Exception e) {
        }
        call();
    }

    /**
     * 初始化 视频会话ui
     */
    private void initVideo() {
        uid = mData.getUid();
        isvideo = mData.getChatMode() == 3 || mData.getChatMode() == 4 ? true : false;

        startType = isvideo;
        iv_close_btn.setVisibility(View.VISIBLE);
        startVideo();
    }


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


    public void changeMainSurfaceLocation(View surface) {
        if (tmpSurface == null)
            tmpSurface = rl_video_main;
        if (tmpSurface.equals(surface))
            return;
        switchSurface(surface, tmpSurface);
        tmpSurface = surface;
    }


    public void initCall() {
        String liveData = getArguments().getString("liveData");
        mData = GsonUtil.fromJson(liveData, CallData.class);
        if (FHVideoManager.getInstance().getCallView() != null && FHVideoManager.getInstance().getCallView().getView() != null) {
            baseCallView = FHVideoManager.getInstance().getCallView();
        } else {
            baseCallView = FHCallView.getInstance(getActivity());
        }
        baseCallView.getView().setBackgroundResource(getMipmap("ic_fh_teller_is_audio"));

        addCallView(baseCallView.getView());
    }


    private void addCallView(View callView) {
        if (callView != null) {
            try {
                if (callView.getParent() != null) {
                    ViewGroup viewParent = (ViewGroup) callView.getParent();
                    viewParent.removeView(callView);
                }
                rl_ai_live.removeView(callView);
                rl_ai_live.addView(callView);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 呼叫开始
     */
    private void call() {
//        FHAPlayer.getInstance().startPlayer(wavpath, getActivity());
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
                        CusCallInfo callInfo = GsonUtil.fromJson(str, CusCallInfo.class);
                        if (callInfo != null) {
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


    /**
     * 呼叫 事件
     *
     * @param evetn
     * @param msg
     */
    private void callEvent(String evetn, String msg) {
        if (baseCallView != null)
            baseCallView.onCallEvent(evetn, msg);

        switch (evetn) {
            case FHLiveClientParams.CALL_EVENT_ANSWER://接听
                isCalling = false;
                confirm(msg);
                break;

        }
    }


    /**
     * //柜员接通
     *
     * @param msg
     */
    private void confirm(String msg) {
        sessionid = msg;
        if (!StringUtil.isEmpty(msg)) {
            if (isShow) {
                FHAPlayer.getInstance().stopPlayer();
                initVideo();
                if (baseCallView != null)
                    baseCallView.getView().setVisibility(View.GONE);
            } else {
                isConfirm = true;
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        release();

    }

    public void release() {

        showWebTime = "";
        liveFragment = null;
        FHWebView.getInstance().release();
        FHHintView.getInstance().release();
        msgFhWebView.release();
        baseCallView.release();
        fhaiCheckView.release();
        isStart = false;
        isMin = false;
        isCalling = false;
        listener = null;
        isConfirm = false;
        isShow = false;
        EventBus.getDefault().unregister(this);
        if (aiCheckCountDownTimer != null) {
            aiCheckCountDownTimer.cancel();
        }
    }


}
