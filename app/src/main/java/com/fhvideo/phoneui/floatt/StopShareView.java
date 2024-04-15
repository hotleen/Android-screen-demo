package com.fhvideo.phoneui.floatt;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fhvideo.FHResource;
import com.fhvideo.adviser.FHTellerParams;
import com.fhvideo.bean.UiEvent;
import com.fhvideo.phoneui.FHUIConstants;
import com.fhvideo.phoneui.FHVideoManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by 武 on 2018/5/11.
 */

public class StopShareView extends RelativeLayout {

    private WindowManager windowManager;
    private View view = null;
    private WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

    private float x,y,startx,starty,sx,sy;

    private ImageView stop_img;
    private TextView  back_btn;

    public StopShareView(Activity context) {
        super(context);
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(FHResource.getInstance().getId(context,"layout","fh_stop_share"),this);
        view = findViewById(FHResource.getInstance().getId(context,"id","rl_float_video"));
        stop_img = view.findViewById(FHResource.getInstance().getId(context,"id","stop_img"));
        back_btn = view.findViewById(FHResource.getInstance().getId(context,"id","back_btn"));
        initLayoutParams();
        initEvent();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mainEvent(UiEvent ros){
        if(ros != null && ros.getType().endsWith(FHTellerParams.UPT_CUS_STATUS)){
        }
    }

    public void setStyle(String type){
        if(type.equals(FHUIConstants.FH_FLOAT_TYPE_SHARE) ){
            back_btn.setVisibility(GONE);
            stop_img.setVisibility(VISIBLE);
        }else {
            back_btn.setVisibility(VISIBLE);
            stop_img.setVisibility(GONE);

        }
    }

    /**
     * 初始化参数
     */
    private void initLayoutParams() {

        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (Build.VERSION.SDK_INT>=26) {//8.0新特性
            layoutParams.type= WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else{
            layoutParams.type= WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams.gravity = Gravity.TOP| Gravity.START;
        layoutParams.x = 0;
        layoutParams.y = view.getLayoutParams().width;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.format = PixelFormat.TRANSPARENT;
        windowManager.addView(this,layoutParams);


    }
    boolean  isinit = false;
    /**
     * 设置监听事件
     */
    private void initEvent() {
        view.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });

        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if (!isinit){
                            startx +=(motionEvent.getRawX()-layoutParams.x);
                            starty +=(motionEvent.getRawY()-layoutParams.y);
                            isinit = true;
                        }else {
                            startx += motionEvent.getRawX() - x;
                            starty += motionEvent.getRawY() - y;
                        }
                        sx = motionEvent.getRawX();
                        sy = motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        x = motionEvent.getRawX();
                        y = motionEvent.getRawY();
                        updateViewXY();
                        break;
                    case MotionEvent.ACTION_UP:
                        x = motionEvent.getRawX();
                        y = motionEvent.getRawY();
                        if((int)(x-sx)<30 && (int)(y-sy)<30 && (int)(sx-x)<30 && (int)(sy-y)<30){//点击事件
                            EventBus.getDefault().post(new UiEvent("",true,FHUIConstants.ONCLICK_FLOAT));
                        }

                        break;
                }
                return true;
            }
        });

        FHVideoManager.getInstance().uptCameraParentVidew(view);

    }

    public void release(){
        EventBus.getDefault().unregister(this);
    }

    private void updateViewXY(){
        layoutParams.x = (int)(x - startx);
        layoutParams.y = (int)(y-starty);
        windowManager.updateViewLayout(this,layoutParams);
    }
}
