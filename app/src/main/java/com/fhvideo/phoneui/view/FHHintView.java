package com.fhvideo.phoneui.view;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fhvideo.bank.FHBankParams;
import com.fhvideo.bean.UiEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * 提示框
 */

public class FHHintView extends FHBaseView {


    private static FHHintView instance;
    public static FHHintView getInstance(){
        if(instance == null)
            instance = new FHHintView();
        return instance;
    }

    private RelativeLayout rl_video_hint;
    private TextView tv_hint_msg,tv_hint_cancel,tv_hint_confirm,tv_hint_sub;

    @Override
    public void init(Activity context, View view) {
        super.init(context, view);
        rl_video_hint = getView("fh_rl_hint");
        rl_video_hint.setZ(120);
        tv_hint_msg = getView("tv_hint_msg");
        tv_hint_cancel = getView("tv_hint_cancel");
        tv_hint_confirm = getView("tv_hint_confirm");
        tv_hint_cancel.setOnClickListener(this);
        tv_hint_confirm.setOnClickListener(this);

        tv_hint_sub = getView("tv_hint_sub");
        tv_hint_sub.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == tv_hint_cancel.getId()){//取消
            rl_video_hint.setVisibility(View.GONE);
            if(hintListener != null)
                hintListener.onCancel();
        }else if(v.getId() == tv_hint_confirm.getId()){//确认
            rl_video_hint.setVisibility(View.GONE);
            if(hintListener != null)
                hintListener.onConfirm();
        }else if(v.getId() == tv_hint_sub.getId()){//知道了
            rl_video_hint.setVisibility(View.GONE);
            if(tv_hint_msg.getText().toString().equals(FHBankParams.FH_CHECK_LOCATION_ERROR)){
                EventBus.getDefault().post(new UiEvent("","call_view_quit"));
            }
        }
    }
    @Override
    public void hidden(){
        if(rl_video_hint.getVisibility() == View.VISIBLE)
            rl_video_hint.setVisibility(View.GONE);
    }
    private OnClickHintListener hintListener;
    public void show(String msg,String cancel,String confirm,OnClickHintListener listener){
        hintListener = listener;
        tv_hint_msg.setText(msg);
        rl_video_hint.setVisibility(View.VISIBLE);
        tv_hint_sub.setVisibility(View.GONE);
        tv_hint_cancel.setVisibility(View.VISIBLE);
        tv_hint_confirm.setVisibility(View.VISIBLE);
        tv_hint_cancel.setText(cancel);
        tv_hint_confirm.setText(confirm);

    }

    public void warn(String msg){
        tv_hint_msg.setText(msg);
        rl_video_hint.setVisibility(View.VISIBLE);
        tv_hint_sub.setVisibility(View.VISIBLE);
        tv_hint_cancel.setVisibility(View.GONE);
        tv_hint_confirm.setVisibility(View.GONE);
    }


    @Override
    public void release() {
        super.release();
        instance = null;
    }

    public interface OnClickHintListener{
        public void onCancel();
        public void onConfirm();
    }
}