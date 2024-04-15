package com.fhvideo.phoneui;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.fhvideo.FHLiveClient;
import com.fhvideo.FHLiveClientParams;
import com.fhvideo.FHLiveMacro;
import com.fhvideo.FHParams;
import com.fhvideo.FHResource;
import com.fhvideo.adviser.tool.FHAPlayer;
import com.fhvideo.bank.FHBankParams;
import com.fhvideo.bank.utils.FHStatusBarUtil;
import com.fhvideo.bean.FHLaborParams;
import com.fhvideo.bean.FHReCallParams;
import com.fhvideo.bean.GsonUtil;
import com.fhvideo.bean.UiEvent;
import com.fhvideo.fhcommon.FHBusiCallBack;
import com.fhvideo.fhcommon.utils.StringUtil;
import com.fhvideo.phoneui.busi.FHDialogListener;
import com.fhvideo.phoneui.busi.FHEventListener;
import com.fhvideo.phoneui.busi.FHLiveListener;
import com.fhvideo.phoneui.floatt.FloatManager;
import com.fhvideo.phoneui.utils.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;

public class FHLiveActivity extends BaseActivity implements FHLiveListener {


    //private FHCallFragment fhCallFragment;
    private com.fhvideo.phoneui.FHLiveFragment fhLiveFragment;

    private RelativeLayout fh_rl_hint;
    private TextView hintmsg, hint_right, tv_toast_net, hint_sub, hint_left;
    public static boolean autoLogout = true;//挂断后是否自动登出

    private String liveData;
    private LiveData mData, tmpData;

    /**
     * 判断是否需要开启视频会话页
     */
    public static boolean returnStartActivity = false;
    private boolean isOverlayBack = false;
    /**
     * 记录界面走onStop的时间戳
     */
    private long stopTime = 0;
    private String TAG = "addlogFHLiveActivity-->";
    /**
     * 是否投屏
     */
    private boolean isScreen = false;
    /**
     * 是否最小化
     */
    private boolean isMin = false;

    /**
     * 呼叫文件名
     */
    private String wavpath = "video.wav";

    public static Intent newInstance(Activity activity) {
        Intent intent = new Intent(activity, FHLiveActivity.class);
        return intent;
    }

    public static void backLiveActivity(Activity activity) {
        if (returnStartActivity)
            activity.startActivity(newInstance(activity));
    }

    private static FHEventListener fhEventLisenter;

    public static void setEventLisenter(FHEventListener eventListener) {
        fhEventLisenter = eventListener;
    }

    private void addVideoEvent(String type, String msg) {
        if (fhEventLisenter == null || StringUtil.isEmpty(type)) {
            return;
        }
        fhEventLisenter.onVideoEvent(type, msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(FHResource.getInstance().getId(this, "layout", "layout_fh_live"));
        returnStartActivity = true;
        // todo: 2. 注册eventbus add
        EventBus.getDefault().register(this);
        liveData = getIntent().getStringExtra("liveData");
        tmpData = GsonUtil.fromJson(liveData, LiveData.class);
        mData = GsonUtil.fromJson(liveData, LiveData.class);
        if (mData != null && !StringUtil.isEmpty(mData.getParams())) {
            try {
                JsonObject jsonObject =  new JsonParser().parse(mData.getParams()).getAsJsonObject();;

                if(jsonObject != null && !StringUtil.isEmpty(mData.getCallType())
                        && mData.getCallType().equals(FHLiveMacro.FH_LIVE_CALL_TYPE_AI)
                        && !StringUtil.isEmpty(jsonObject.get("aiAccountType").toString())
                ){
                    jsonObject.addProperty("accoutType",jsonObject.get("aiAccountType").toString());
                }
                FHVideoManager.getInstance().setCallType(mData.getCallType());
                mData.setParams(GsonUtil.toJson(jsonObject));
            }catch (Exception e){
                FHVideoManager.getInstance().setCallType(FHVideoManager.CALL_TYPE_ARTIFICIAL);

            }
            liveData = GsonUtil.toJson(mData);
        }
        FHVideoManager.getInstance().setActivity(this);
        initView();
        FHVideoManager.getInstance().setEventListener(this);

        toCall();
        initStatusBar();

    }

    private void initStatusBar() {
        //沉浸式代码配置
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        FHStatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏透明
        FHStatusBarUtil.setTranslucentStatus(this);
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        /*if (!FHStatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            FHStatusBarUtil.setStatusBarColor(this, 0x55000000);
        }*/
        FHStatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!isOverlayBack) {
            returnStartActivity = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stopTime != 0 && new Date().getTime() - stopTime > 100 && fhLiveFragment != null) {
            stopTime = 0;
            backMetting();
        }
        if (stopTime != 0 && new Date().getTime() - stopTime > 100 && aiLiveFragment != null) {
            stopTime = 0;
            backMetting();
        }
        stopTime = 0;
        if (isOverlayBack) {
            isOverlayBack = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTime = new Date().getTime();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FHVideoManager.getInstance().setActivity(null);
        ToastUtil.release();
        FHVideoManager.getInstance().setCallType(FHVideoManager.CALL_TYPE_ARTIFICIAL);

        addVideoEvent(FHUIConstants.ON_DESTROY_LIVE, istellerClose + "");
        fhEventLisenter = null;
        FHVideoManager.getInstance().releaseVideo();
        FHAPlayer.getInstance().stopPlayer();
        EventBus.getDefault().unregister(this);
        FHVideoManager.getInstance().setEventListener(null);
    }

    private void initView() {
        fh_rl_hint = getView("fh_rl_hint");
        hintmsg = getView("tv_hint_msg");
        tv_toast_net = getView("tv_toast_net");
        hint_left = getView("tv_hint_cancel");
        hint_right = getView("tv_hint_confirm");
        hint_sub = getView("tv_hint_sub");
    }

    private void toCall() {
        if (liveData == null) {
            ToastUtil.getInatance(this).show("呼叫参数不能为空");
            return;
        }
        FHAPlayer.getInstance().startPlayer(wavpath, activity);
        /*if (fhCallFragment == null) {
            fhCallFragment = FHCallFragment.newInstance(liveData);
            fhCallFragment.setEventListener(this);
            addFragment(fhCallFragment);
        } else {
            switchFragment(fhCallFragment);
        }*/
        toLive();
    }

    private FHAiLiveFragment aiLiveFragment;

    private void toAiLive() {
        if (aiLiveFragment == null) {
            aiLiveFragment = FHAiLiveFragment.newInstance(liveData);
            aiLiveFragment.setEventListener(this);
            addFragment(aiLiveFragment);
        } else {
            switchFragment(aiLiveFragment);
        }
    }

    private void toLive() {
        if (FHVideoManager.getInstance().getCallType().equals(FHVideoManager.CALL_TYPE_AI)) {
            toAiLive();
            return;
        }
        if (!FHVideoManager.getInstance().getCallType().equals(FHVideoManager.CALL_TYPE_LOCALPREVIEW))
            FHVideoManager.getInstance().setCallType(FHVideoManager.CALL_TYPE_ARTIFICIAL);
        if (fhLiveFragment == null) {
            fhLiveFragment = FHLiveFragment.newInstance(liveData);
            fhLiveFragment.setEventListener(this);
            addFragment(fhLiveFragment);

        } else {
            switchFragment(fhLiveFragment);
        }
    }

    private com.fhvideo.phoneui.FHBusiResultFragment busiResultFragment;

    private void toBusi() {
        if (busiResultFragment == null) {
            busiResultFragment = FHBusiResultFragment.newInstance();
            addFragment(busiResultFragment);
            removeFragment(aiLiveFragment);
            aiLiveFragment = null;
        } else {
            switchFragment(busiResultFragment);
        }
    }

    private void backMetting() {
        isMin = false;
        isScreen = false;
        toLive();
        if (FHVideoManager.getInstance().getCallType().equals(FHVideoManager.CALL_TYPE_AI)) {
            aiLiveFragment.backMetting();
            return;
        }
        fhLiveFragment.backMetting();
    }


    private void callEvent(String event, String msg) {
        /*if (fhCallFragment != null && fhCallFragment.isAdded()) {
            fhCallFragment.receiveEvent(event, msg);
        }*/
        liveEvent(event, msg);
    }

    private void liveEvent(String event, String msg) {
        if (FHVideoManager.getInstance().getCallType().equals(FHVideoManager.CALL_TYPE_AI)) {
            if (aiLiveFragment != null && aiLiveFragment.isAdded()) {
                aiLiveFragment.receiveEvent(event, msg);
            }
            return;
        }
        if (fhLiveFragment != null && fhLiveFragment.isAdded()) {
            if (msg.equals("transferclose")) {
                activity.startActivity(new Intent(activity.getApplicationContext(), FHLiveActivity.class));
            }
            fhLiveFragment.receiveEvent(event, msg);
        }
        if (aiLiveFragment != null && aiLiveFragment.isAdded()) {
            if (msg.equals("transferclose")) {
                activity.startActivity(new Intent(activity.getApplicationContext(), FHLiveActivity.class));
            }
            aiLiveFragment.receiveEvent(event, msg);
        }
    }

    private void handleTrans(String msg) {//以下交易示例仅供参考
        com.fhvideo.phoneui.MsgInfo info = new Gson().fromJson(msg, MsgInfo.class);
        if (info != null) {
            String url = "";
            url = com.fhvideo.bean.URLCons.getServer() + FHUIConstants.redirectUrl +
                    "?sessionid=" + info.getSessionid()
                    + "&tradeNo=" + info.getTradeNo()
                    + "&businessType=" + info.getBusinessType()
                    + "&appKey=tzb"
                    + "&uid=" + mData.getUid()
                    + "&locate=1"
                    + "&language=" + FHParams.getLanguage();
            liveEvent(FHBankParams.FH_HANDLE_TRANS, url);
        }
    }

    @Override
    public void showHint(final String hint) {
        hint_right.setVisibility(View.VISIBLE);
        hint_left.setVisibility(View.VISIBLE);
        hint_sub.setVisibility(View.GONE);
        hintmsg.setText(hint);
        hint_left.setText("取消");
        hint_right.setText("确定");
        fh_rl_hint.setVisibility(View.VISIBLE);
        hint_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fh_rl_hint.setVisibility(View.GONE);
            }
        });
        hint_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fh_rl_hint.setVisibility(View.GONE);
            }
        });
    }


    /**
     * @param error
     */
    @Override
    public void showError(String error, final boolean toClose) {
        hintmsg.setText(error);
        hint_right.setVisibility(View.GONE);
        hint_left.setVisibility(View.GONE);
        hint_sub.setVisibility(View.VISIBLE);
        fh_rl_hint.setVisibility(View.VISIBLE);
        hint_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fh_rl_hint.setVisibility(View.GONE);
                if (toClose) {
                    close(false);
                }
            }
        });
    }

    @Override
    public void showCustomDialog(String msg, final FHDialogListener listener) {
        hintmsg.setText(msg);
        fh_rl_hint.setVisibility(View.VISIBLE);
        listener.controlView(hint_left, hint_right, hint_sub);
        hint_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fh_rl_hint.setVisibility(View.GONE);
                listener.leftOnClick();
            }
        });
        hint_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fh_rl_hint.setVisibility(View.GONE);
                listener.rightOnClick();
            }
        });
        hint_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fh_rl_hint.setVisibility(View.GONE);
                listener.subOnClick();
            }
        });
    }

    @Override
    public void showLive(String sessionId) {
        if (!hintmsg.getText().toString().contains("40087")) {
            fh_rl_hint.setVisibility(View.GONE);
        }
        toLive();
    }

    // todo: 10. 构造悬浮窗
    @Override
    public void addFlot(String type) {
        moveAppToFront();
        FloatManager.getInstance().createFloatView(FHLiveActivity.this, type);
        moveTaskToBack(true);//最小化
        isMin = true;
    }

    private FHLaborParams laborParams;

    private FHReCallParams reCallParams;

    public void quit() {

        FHVideoManager.getInstance().setCallType(FHVideoManager.CALL_TYPE_ARTIFICIAL);
        FHAPlayer.getInstance().stopPlayer();
        //fhCallFragment = null;
        fhLiveFragment = null;
        aiLiveFragment = null;
        busiResultFragment = null;
        returnStartActivity = false;
        moveAppToFront();
        if (Build.VERSION.SDK_INT >= 21)
            finishAndRemoveTask();
        else
            finish();
    }

    private boolean istellerClose = false;

    @Override
    public void close(boolean isTeller) {
        istellerClose = isTeller;
        FHAPlayer.getInstance().stopPlayer();

        if (FHVideoManager.getInstance().getCallType().equals(FHVideoManager.CALL_TYPE_AI)) {

            if (FHLiveClient.getInstance().isTransferArtificial()
                    && FHLiveClient.getInstance().getLaborParams() != null
                    && !StringUtil.isEmpty(FHLiveClient.getInstance().getLaborParams().getCallType())) { //转人工\
                FHVideoManager.getInstance().closeVideo(new FHBusiCallBack() {
                    @Override
                    public void onSuccess(String s) {

                        if (mData != null && !StringUtil.isEmpty(mData.getParams())) {
                            try {
                                JsonObject callParams =  new JsonParser().parse(mData.getParams()).getAsJsonObject();;

                                laborParams = FHLiveClient.getInstance().getLaborParams();
                                callParams.addProperty("tellerid","");

                                if (laborParams.getCallType().equals("INST")) {//INST-按机构呼叫
                                    callParams.addProperty("linktype","0");

                                } else if (laborParams.getCallType().equals("SKILL")) {//SKILL-按技能呼叫、
                                    callParams.addProperty("linktype","2");
                                }

                                callParams.addProperty("accoutType",laborParams.getInstId() + "");
                                callParams.addProperty("aiAccountType","");
                                callParams.addProperty("skill",laborParams.getSkillId() + "");
                                callParams.addProperty("callType",FHVideoManager.CALL_TYPE_ARTIFICIAL);
                                mData.setParams(GsonUtil.toJson(callParams));

                                // 转人工 去除ext
                                mData.setExtData(null);
                                liveData = GsonUtil.toJson(mData);
                                FHVideoManager.getInstance().setCallType(FHVideoManager.CALL_TYPE_ARTIFICIAL);

                            }catch (Exception e){
                                FHVideoManager.getInstance().setCallType(FHVideoManager.CALL_TYPE_ARTIFICIAL);

                            }
                            liveData = GsonUtil.toJson(mData);
                        }
                        toCall();
                        //状态置空
                        FHLiveClient.getInstance().setTransferArtificial(false);
                        FHLiveClient.getInstance().setLaborParams(null);
                    }

                    @Override
                    public void onError(String s) {

                    }
                });


                return;
            } else if (FHLiveClient.getInstance().isAiRecall() && FHLiveClient.getInstance().getReCallParam() != null) {//重新呼叫
                FHVideoManager.getInstance().closeVideo(new FHBusiCallBack() {
                    @Override
                    public void onSuccess(String s) {
                        reCallParams = FHLiveClient.getInstance().getReCallParam();

                        mData.setExtData(GsonUtil.toJson(reCallParams));
                        liveData = GsonUtil.toJson(mData);
                        aiLiveFragment.onDestroyView();
                        removeFragment(aiLiveFragment);
                        aiLiveFragment = null;
                        toCall();
                        //状态置空
                        FHLiveClient.getInstance().setAiRecall(false);
                        FHLiveClient.getInstance().setReCallParams(null);
                    }

                    @Override
                    public void onError(String s) {

                    }
                });


                return;
            }
            closeLive();

            //toBusi();
        } else {
            closeLive();
        }


    }

    private void closeLive() {
        //fhCallFragment = null;
        fhLiveFragment = null;
        aiLiveFragment = null;
        busiResultFragment = null;
        returnStartActivity = false;
        moveAppToFront();
        if (Build.VERSION.SDK_INT >= 21)
            finishAndRemoveTask();
        else
            finish();
    }


    private void moveAppToFront() {
        returnStartActivity = false;
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        //获取栈列表，5.0以上只能获取当前应用栈列表和一些系统不重要的栈
        List<ActivityManager.RunningTaskInfo> recentList = am.getRunningTasks(30);
        recentList.remove(0);
        for (ActivityManager.RunningTaskInfo info : recentList) {
            if (info.topActivity.getPackageName().equals(getPackageName())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    am.moveTaskToFront(info.id, 0);
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode == 0) {
                showError(getResString("fh_cancel_screen"), false);
            } else {
                // todo: 8发出悬浮窗事件 add
                EventBus.getDefault().post(new UiEvent(FHUIConstants.FH_FLOAT_TYPE_SHARE, FHUIConstants.MIN_VIDEO));
                FHVideoManager.getInstance().startScreen(resultCode, data);
            }
        } else if (requestCode == 101) {
            isOverlayBack = true;
        }
    }

    @Override
    public void showLongToast(boolean isShow, String msg) {
        tv_toast_net.setText(msg);
        if (isShow) {
            tv_toast_net.setVisibility(View.VISIBLE);
        } else {
            tv_toast_net.setVisibility(View.GONE);
        }
    }

    /**
     * 主线程
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mainEvent(UiEvent ros) {
        if (ros == null || StringUtil.isEmpty(ros.getType()))
            return;
        switch (ros.getType()) {
            case FHUIConstants.START_SESSION:
                addVideoEvent(ros.getType(), ros.getMsg());
                break;
            case FHUIConstants.SHOW_ERROR:
                showError(ros.getMsg(), ros.getSuc());
                break;

            // todo: 1 点击悬浮窗 add
            case FHUIConstants.ONCLICK_FLOAT:
                addVideoEvent(ros.getType(), "");
                break;
            case FHUIConstants.SHOW_TRANS:
                addVideoEvent(ros.getType(), ros.getMsg());
                handleTrans(ros.getMsg());
                break;
            case FHUIConstants.NEW_NOTI:
                break;
            case "CALL_TYPE_ARTIFICIAL":
                if (tmpData != null) {
                    liveData = GsonUtil.toJson(tmpData);
                }
                mData = GsonUtil.fromJson(liveData, LiveData.class);
                toCall();
                break;
            case "CLOSE_FHLIVE":
                close(false);
                break;
            case FHLiveClientParams.CALL_EVENT_REFUSE://拒绝
            case FHLiveClientParams.CALL_EVENT_RING://振铃中
            case FHLiveClientParams.CALL_EVENT_QUEUE://排队中
            case FHLiveClientParams.CALL_EVENT_BUSY://忙碌中
                callEvent(ros.getType(), ros.getMsg());
                break;
            case FHLiveClientParams.CALL_EVENT_ANSWER://接听
                /*if(fhCallFragment!= null){
                    callEvent(ros.getType(), ros.getMsg());
                }else {
                    liveEvent(ros.getType(), ros.getMsg());
                }*/
                liveEvent(ros.getType(), ros.getMsg());
                break;
            case FHBankParams.FH_ON_CALL_ERROR:
                showHint(ros.getMsg());
                break;
            case FHBankParams.FH_UPT_TELLER_ID: //柜员接听后通知给出柜员id
                FHUIConstants.setTellerId(ros.getMsg());
                break;
            case FHLiveClientParams.INTERACT_EVENT_RESUME:
            case FHLiveClientParams.INTERACT_EVENT_PAUSE:
            case FHBankParams.FH_TELLER_CLOSE_VIDEO:
            case FHBankParams.FH_VIDEO_NEW_PUSH:
            case FHBankParams.FH_VIDEO_TELLER_TYPE:
            case FHBankParams.FH_START_SCREEN:
            case FHBankParams.FH_STOP_BLOOD:
            case FHBankParams.FH_START_PPT:
            case FHLiveClientParams.INTERACT_EVENT_IDCARD_FACE:
            case FHLiveClientParams.INTERACT_EVENT_IDCARD_NATIONAL_EMBLEM:
            case FHLiveClientParams.INTERACT_EVENT_BANK_CARD:
            case FHLiveClientParams.INTERACT_EVENT_FACE_RECOGNITION:
            case FHLiveClientParams.INTERACT_EVENT_CLOSE_CARD:
            case FHLiveClientParams.CALL_EVENT_TRANSFER_CLOSE:
            case FHBankParams.FH_ON_USER_JOIN:
            case FHBankParams.FH_ON_USER_AUDIO_AVAILABLE:
            case FHBankParams.FH_VIDEO_ON_FIRST_FRAME:
            case FHBankParams.FH_ON_USER_LEAVE:
            case FHBankParams.FH_VIDEO_ON_SUCCESS_JOIN: //自己加入房间
            case FHBankParams.FH_NET_WEAK: //弱网
                liveEvent(ros.getType(), ros.getMsg());
                break;
            case FHBankParams.FH_ON_ERROR:
                showHint(ros.getMsg());
                break;
            case FHBankParams.FH_UPT_NETWORK:
                if (ros.getMsg().equals("true")) {
                    tv_toast_net.setVisibility(View.GONE);
                } else {
                    tv_toast_net.setText(getResString("fh_net_weak_hint"));
                    tv_toast_net.setVisibility(View.VISIBLE);
                }
                liveEvent(ros.getType(), ros.getMsg());
                break;
            case FHBankParams.INTERACT_EVENT_NETWORK_QUALITY_ARRAY:
            case FHBankParams.FH_ONUSER_SUBSTREAM_AVAILABLE:
            case FHBankParams.FH_VIDEO_NEW_MSG:
                liveEvent(ros.getType(), ros.getMsg());
                break;
            case FHBankParams.FH_NEW_NOTI:
                fhEventLisenter.onVideoEvent(FHUIConstants.NEW_NOTI, "");
                break;
            case FHBankParams.INTERACT_EVENT_USER_VOICE_VOLUME:
                fhEventLisenter.onVideoEvent(FHUIConstants.USER_VOICE_VOLUME, ros.getMsg());
                liveEvent(ros.getType(), ros.getMsg());
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
