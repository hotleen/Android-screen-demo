package com.fhvideo.phoneui.view;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fhvideo.FHLiveClientParams;
import com.fhvideo.FHResource;
import com.fhvideo.adviser.tool.FHAPlayer;
import com.fhvideo.bank.FHBankParams;
import com.fhvideo.fhcommon.FHBusiCallBack;
import com.fhvideo.phoneui.FHBaseCallView;
import com.fhvideo.phoneui.FHVideoManager;
import com.fhvideo.phoneui.busi.FHDialogListener;
import com.fhvideo.phoneui.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 呼叫页
 */

public class FHCallView implements FHBaseCallView {

    private static FHCallView instance;
    public static FHCallView getInstance(Activity activity){
        if(instance==null){
            synchronized (FHCallView.class){
                if(instance == null)
                    instance = new FHCallView(activity);
            }
        }
        return instance;
    }

    public FHCallView(Activity activity){
        mContext = activity;
        if(mView == null){
            mView = (RelativeLayout) LayoutInflater.from(activity).inflate(FHResource.getInstance().getId(activity,"layout","layout_fh_call"), null, false);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mView.setLayoutParams(lp);
            initView();
            initTimer();
        }
    }

    /**
     * 获取UI控件
     *
     */
    @Override
    public View getView() {
        return mView;
    }

    /**
     * 呼叫事件回调
     *
     * @param event 事件类型
     * @param msg   事件内容
     */
    @Override
    public void onCallEvent(String event, String msg) {
        switch (event){
            case FHLiveClientParams.CALL_EVENT_ANSWER://接听
                FHAPlayer.getInstance().stopPlayer();
                setCallType(event, getResString("fh_metting"));

                break;
            case FHLiveClientParams.CALL_EVENT_REFUSE://拒绝
                setCallType(event, getResString("fh_teller_busy"));
                break;
            case FHLiveClientParams.CALL_EVENT_RING://振铃中
            case FHLiveClientParams.CALL_EVENT_QUEUE://排队中
            case FHLiveClientParams.CALL_EVENT_BUSY://忙碌中
            case FHLiveClientParams.CALL_EVENT_REGAIN://忙碌中

                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    String succtype = jsonObject.optString("succtype");
                    String hintmsg = jsonObject.optString("hintmsg");
                    setCallType(event, hintmsg);

                } catch (JSONException e) {
                }
                break;
            case FHLiveClientParams.INTERACT_EVENT_NETWORK_STATUS://网络状态
                if(msg.equals("true")){

                }else {
                    ToastUtil.getInatance(mContext).show(getResString("fh_net_weak_hint"));
                }
                break;
            default:
                setCallType(event, msg);

                break;
        }
    }

    /**
     * 出错
     *
     * @param msg
     */
    @Override
    public void onError(String msg) {
        showWarn(msg
                , new FHDialogListener() {
                    @Override
                    public void controlView(TextView leftView, TextView rightView, TextView subView) {
                    }

                    @Override
                    public void leftOnClick() {
                    }

                    @Override
                    public void rightOnClick() {

                    }

                    @Override
                    public void subOnClick() {
                        if (!FHBankParams.isConnected()) { //断网
                            return;
                        }
                        hangUp();
                    }
                }
        );
    }

    /**
     * 警告
     *
     * @param msg
     */
    @Override
    public void warn(String msg) {
        showWarn(msg);
    }

    private RelativeLayout mView;
    private Activity mContext;


    private TextView tvCallType;
    private RelativeLayout rl_video_hint;
    private TextView tv_hint_msg,tv_hint_cancel,tv_hint_confirm,tv_hint_sub;

    private void initView() {
        tvCallType = getView("tv_call_type");
        getView("iv_call_quit", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHangUp(getResString("fh_call_quit"));
            }
        });
        rl_video_hint = getView("fh_rl_hint", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tv_hint_msg = getView("tv_hint_msg");
        tv_hint_cancel = getView("tv_hint_cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_video_hint.setVisibility(View.GONE);
                if(dialogListener != null)
                    dialogListener.leftOnClick();
            }
        });
        tv_hint_confirm = getView("tv_hint_confirm", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_video_hint.setVisibility(View.GONE);
                if(dialogListener != null)
                    dialogListener.rightOnClick();
            }
        });

        tv_hint_sub = getView("tv_hint_sub", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_video_hint.setVisibility(View.GONE);
                if(dialogListener != null)
                    dialogListener.subOnClick();
            }
        });

    }

    /**
     * 询问是否挂断
     * @param msg
     */
    private void showHangUp(String msg) {
        showHint(msg
                , new FHDialogListener() {
                    @Override
                    public void controlView(TextView leftView, TextView rightView, TextView subView) {
                        leftView.setText(getResString("fh_quit"));
                        rightView.setText(getResString("fh_wait"));
                    }

                    @Override
                    public void leftOnClick() {
                        hangUp();
                    }

                    @Override
                    public void rightOnClick() {

                    }

                    @Override
                    public void subOnClick() {

                    }
                }
        );
    }

    /**
     * 挂断
     */
    private void hangUp() {
        FHVideoManager.getInstance().cancelCall(new FHBusiCallBack() {
            @Override
            public void onSuccess(String s) {
                ToastUtil.getInatance(mContext).show("已挂断");
            }

            @Override
            public void onError(String s) {
                showWarn(s);
            }
        });
    }

    private FHDialogListener dialogListener;
    private void showHint(String msg, FHDialogListener listener){
        dialogListener = listener;
        if(dialogListener != null){
            dialogListener.controlView(tv_hint_cancel,tv_hint_confirm,tv_hint_sub);
        }
        tv_hint_msg.setText(msg);
        rl_video_hint.setVisibility(View.VISIBLE);
        tv_hint_sub.setVisibility(View.GONE);
        tv_hint_cancel.setVisibility(View.VISIBLE);
        tv_hint_confirm.setVisibility(View.VISIBLE);

    }
    private void showWarn(String msg, FHDialogListener listener){
        showWarn(msg);
        dialogListener = listener;
        if(dialogListener != null){
            dialogListener.controlView(tv_hint_cancel,tv_hint_confirm,tv_hint_sub);
        }
    }
    private void showWarn(String msg){
        dialogListener = null;
        tv_hint_msg.setText(msg);
        rl_video_hint.setVisibility(View.VISIBLE);
        tv_hint_sub.setVisibility(View.VISIBLE);
        tv_hint_cancel.setVisibility(View.GONE);
        tv_hint_confirm.setVisibility(View.GONE);
    }


    private void setCallType(String type, String msg) {
        switch (type) {
            case FHLiveClientParams.CALL_EVENT_BUSY://忙碌
                tvCallType.setText(msg);
                break;
            case FHLiveClientParams.CALL_EVENT_RING://接通中
            case FHLiveClientParams.CALL_EVENT_QUEUE://排队中
                tvCallType.setText(msg);
                break;
            case FHLiveClientParams.CALL_EVENT_ANSWER://接听
                tvCallType.setText(msg);
                FHAPlayer.getInstance().stopPlayer();
                cancelTimer();
                break;
            case FHLiveClientParams.CALL_EVENT_REFUSE://拒接
                ToastUtil.getInatance(mContext).show(msg);
                tvCallType.setText(getResString("fh_teller_busy"));
                break;
            default:
                tvCallType.setText(msg);
                startTimer();
                break;

        }
    }

    private void initTimer(){
        if(timer!= null)
            timer.cancel();
        timer = new Timer();
        if(task != null)
            task.cancel();
        task = new CallHintTask();
    }

    private long hintTime = 40*1000;//提示间隔事件
    private boolean isStartTimer = false;
    private void startTimer(){//开启
        if(isStartTimer || timer==null ||task == null)
            return;
        isStartTimer = true;
        timer.schedule(task,hintTime,hintTime);

    }
    private void cancelTimer(){//关闭
        isStartTimer = false;
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        if(task != null){
            task.cancel();
            task = null;
        }
    }
    private CallHintTask task;
    private Timer timer;
    //
    private class CallHintTask extends TimerTask {

        @Override
        public void run() {
            if(!isStartTimer)
                return;
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showHangUp(getResString("fh_call_timeout"));
                }
            });
        }
    }

    public void release(){
        cancelTimer();
        mView = null;
        mContext = null;
        instance = null;
    }



    /**
     * 获取String.xml中字符串
     * @param name 字符串id名
     * @return
     */
    private String getResString(String name){
        return FHResource.getInstance().getString(mContext,name);
    }

    /**
     * 设置单个view
     * @param name 控件id名
     */
    private <T extends View> T getView(String name){
        T view = mView.findViewById(FHResource.getInstance().getId(mContext,"id",name));
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
    private <T extends View> T getView(String name, View.OnClickListener listener){
        T view = mView.findViewById(FHResource.getInstance().getId(mContext,"id",name));
        if(view == null ) {
            Log.e("FH_ERROR","findViewById 出错："+name);
            return view;
        }
        if(listener != null)
            view.setOnClickListener(listener);
        return view;
    }
}
