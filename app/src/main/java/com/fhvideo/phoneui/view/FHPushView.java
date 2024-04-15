package com.fhvideo.phoneui.view;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.fhvideo.bank.bean.PushFile;
import com.fhvideo.bean.AnsEvent;
import com.fhvideo.bean.UiEvent;
import com.fhvideo.phoneui.adapter.FHPushAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 推送
 */

public class FHPushView extends FHBaseView {
    private static FHPushView instance;
    public static FHPushView getInstance(){
        if(instance == null)
            instance = new FHPushView();
        return instance;
    }

    private RelativeLayout rl_push,rl_push_show;
    private ImageView iv_push_close ,iv_push_icon,iv_push_show_close;
    private TextView tv_push_hint,tv_push_name;
    private GridView gv_push;

    private List<PushFile> pushs ;
    private FHPushAdapter pushAdapter;

    @Override
    public void init(Activity context, View view) {
        super.init(context, view);
        initRlPush(view);
        initRlPushShow(view);
        initList(context);
    }

    private void initList(Context context) {
        pushs = new ArrayList<PushFile>();
        pushAdapter = new FHPushAdapter(context,pushs);
        gv_push.setAdapter(pushAdapter);
    }

    private void initRlPushShow(View view) {
        rl_push_show = getView("rl_push_show");
        rl_push_show.setZ(110);
        iv_push_icon = getView("iv_push_icon");
        tv_push_name = getView("tv_push_name");
        iv_push_show_close= getView("iv_push_show_close");
        iv_push_show_close.setZ(110);
        rl_push_show.setOnClickListener(this);
        iv_push_show_close.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initRlPush(View view) {
        rl_push = getView("rl_push");
        rl_push.setZ(110);
        iv_push_close = getView("iv_push_close");
        tv_push_hint = getView("tv_push_hint");
        gv_push = getView("gv_push");
        iv_push_close.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == iv_push_close.getId()){//关闭推送窗
            onClickPush();
        }else if(v.getId() == rl_push_show.getId()){//新推送
            rl_push_show.setVisibility(View.GONE);
            iv_push_show_close.setVisibility(View.GONE);
            EventBus.getDefault().post(new UiEvent(pushFile.getUrl(), true, "show_push"));
        }else if(v.getId() == iv_push_show_close.getId()){
            rl_push_show.setVisibility(View.GONE);
            iv_push_show_close.setVisibility(View.GONE);
        }

    }

    public void onClickPush() {
        if(!isInit)
            return;
        if(rl_push.getVisibility() == View.VISIBLE){
            hidden();
        }else {
            rl_push_show.setVisibility(View.GONE);
            iv_push_show_close.setVisibility(View.GONE);
            rl_push.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hidden() {
        if(!isInit)
            return;
        rl_push_show.setVisibility(View.GONE);
        rl_push.setVisibility(View.GONE);
        iv_push_show_close.setVisibility(View.GONE);

    }

    public void hiddenShow(){
        if(!isInit)
            return;
        rl_push_show.setVisibility(View.GONE);
        iv_push_show_close.setVisibility(View.GONE);
    }

    public  String pushtime = "";//新推送显示时间

    private PushFile pushFile;
    public void onNewPush(PushFile push){//新推送
        if(!isInit)
            return;
        pushFile = push;
        rl_push_show.setVisibility(View.VISIBLE);
        iv_push_show_close.setVisibility(View.VISIBLE);
        tv_push_name.setText(push.getName());
        Glide.with(mContext.getApplicationContext())
                .load(push.getPic())
                .into(iv_push_icon);


        tv_push_hint.setVisibility(View.GONE);

        pushtime = new Date().getTime()+"";
        pushs.add(push);
        pushAdapter.refresh(pushs);
        EventBus.getDefault().post(new AnsEvent(pushtime+"", FHVideoView.PUSH_TIME));

    }

    @Override
    public void release() {
        super.release();
        mContext = null;
        instance = null;
    }
}