package com.fhvideo.phone;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fhvideo.bank.FHPermission;
import com.fhvideo.bank.utils.FHStatusBarUtil;
import com.fhvideo.bean.UiEvent;
import com.fhvideo.fhcommon.utils.StringUtil;
import com.fhvideo.phone.fragment.HomeFragment;
import com.fhvideo.phone.fragment.MyFragment;
import com.fhvideo.phoneui.FHLiveActivity;
import com.fhvideo.phoneui.FHUIConstants;
import com.fhvideo.phoneui.FHVideoManager;
import com.fhvideo.phoneui.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private AppCompatActivity mActivity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mActivity = this;
        EventBus.getDefault().register(mActivity);
        initView();
        initStatusBar();
        initFragment();
        //申请权限
        /*FHPermission.getInstance().checkPermission(this);*/
        //获取监听通知权限，需要跳页，用于监听通知栏新通知
        FHPermission.getInstance().checkNotiPermission(this);

        if (!FHPermission.getInstance().isLocServiceEnable(mActivity)) {
            FHPermission.getInstance().startLocService(mActivity, 10012);
        }
        setNoti();
    }

    private void setNoti(){
        List<String> whiteList = new ArrayList<String>();
        whiteList.add("com.fhvideo.phone");
        //设置通知栏白名单
        FHVideoManager.getInstance().setWhiteListScreen(whiteList);

        NotifyObject notifyObject = new NotifyObject();
        notifyObject.content ="投屏内容";
        notifyObject.title = "投屏标题";
        notifyObject.subText = "";
        notifyObject.param = "";
        Notification notification = NotificationUtil.createScreenNotification(getApplicationContext(), notifyObject);
        //设置投屏通知
        FHVideoManager.getInstance().setScreenNotification(notification);
        NotifyObject liveNoti = new NotifyObject();
        liveNoti.content ="会话内容";
        liveNoti.title = "会话标题";
        liveNoti.subText = "";
        liveNoti.param = "";
        Notification Livenotification = NotificationUtil.createScreenNotification(getApplicationContext(), liveNoti);
        //设置投屏通知
        FHVideoManager.getInstance().setLiveForegroundNotification(Livenotification);
    }
    /**主线程*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mainEvent(UiEvent event){
        if(event == null || StringUtil.isEmpty(event.getType()))
            return;
        switch (event.getType()){
            case FHLiveHelper.SHOW_HOME:
                showFragment(homeFragment,myFragment);
                break;
            case FHLiveHelper.SHOW_MY:
                showFragment(myFragment,homeFragment);
                break;
            case FHLiveHelper.SHOW_PROGRESS_BAR:
                showProgress((Boolean) event.getObj());
                break;
            case FHLiveHelper.SHOW_HINT:
                if(isFront)
                    showHint(event.getMsg());
                break;
            case FHUIConstants.ON_DESTROY_LIVE:
                fh_rl_hint.setVisibility(View.GONE);
                break;
        }
    }

    private HomeFragment homeFragment;
    private MyFragment myFragment;
    private void initFragment(){
        homeFragment = new HomeFragment();
        myFragment = new MyFragment();
        addFragment(homeFragment,fl_home.getId());
        addFragment(myFragment,fl_home.getId());
        showFragment(homeFragment,myFragment);
    }


    private void showFragment(Fragment showFragment,Fragment hideFragment){
        if( showFragment == null || hideFragment== null )
            return;
        FHFragmentHelper.getInstance().showFragment(mActivity,showFragment,hideFragment);
    }
    private void hideFragment(Fragment fragment){
        if( fragment == null )
            return;
        FHFragmentHelper.getInstance().hideFragment(mActivity,fragment);
    }
    private void switchFragment(Fragment hide,Fragment show){
        if(hide == null || show == null )
            return;
        FHFragmentHelper.getInstance().switchFragment(mActivity,hide,show);
    }

    private void addFragment(Fragment fragment,int id){
        if(fragment == null)
            return;
        FHFragmentHelper.getInstance().addFragment(mActivity,fragment,id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(mActivity);

    }

    private FrameLayout fl_home;
    private ProgressBar progressbar;
    private void initView() {
        fl_home = findViewById(R.id.fl_home);
        progressbar = findViewById(R.id.progressbar);
        initHint();
    }
    private void showProgress(boolean show){
        if(show){
            progressbar.setVisibility(View.VISIBLE);
        }else {
            progressbar.setVisibility(View.GONE);
        }
    }

    private RelativeLayout fh_rl_hint;
    private TextView hintmsg, hint_right, tv_hint_sub, hint_left;
    private void initHint() {
        fh_rl_hint = findViewById(R.id.fh_rl_hint);
        hintmsg = findViewById(R.id.tv_hint_msg);
        hint_left = findViewById(R.id.tv_hint_cancel);
        hint_right = findViewById(R.id.tv_hint_confirm);
        tv_hint_sub = findViewById(R.id.tv_hint_sub);
        tv_hint_sub.setVisibility(View.VISIBLE);
        hint_left.setVisibility(View.GONE);
        hint_right.setVisibility(View.GONE);
        tv_hint_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fh_rl_hint.setVisibility(View.GONE);
            }
        });
    }
    private void showHint(final String str) {
        hintmsg.setText(str);
        fh_rl_hint.setVisibility(View.VISIBLE);
        if(str.equals("是否返回视频会话")
        ){
            tv_hint_sub.setVisibility(View.GONE);
            hint_left.setVisibility(View.VISIBLE);
            hint_right.setVisibility(View.VISIBLE);
        } else {
            tv_hint_sub.setVisibility(View.VISIBLE);
            hint_left.setVisibility(View.GONE);
            hint_right.setVisibility(View.GONE);
        }
        hint_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fh_rl_hint.setVisibility(View.GONE);
            }
        });
        hint_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fh_rl_hint.setVisibility(View.GONE);
                if (str.equals("是否返回视频会话")) {
                    startActivity(new Intent(mActivity, FHLiveActivity.class));
                }

            }
        });
        tv_hint_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fh_rl_hint.setVisibility(View.GONE);
                if(str.equals("缺少必要权限，是否立即申请！")){
                    if (FHLiveHelper.getHelper().getLiveData().getChatMode() == 1){
                        FHPermission.getInstance().checkVoicePermission(mActivity);
                    }else {
                        //申请权限
                        FHPermission.getInstance().checkPermission(mActivity);
                    }
                }
            }
        });
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


    private boolean isFront = false;//是否在前台

    @Override
    protected void onResume() {
        super.onResume();
        isFront= true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFront= false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        FHLiveActivity.backLiveActivity(mActivity);
    }

    private long firsttime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(FHLiveHelper.getHelper().isMetting()){
                ToastUtil.getInatance(mActivity).show("请不要切后台！");
                return true;
            }
            long nowtime = new Date().getTime();
            if (nowtime - firsttime < 2000) {
                finish();
            } else {
                firsttime = nowtime;
                ToastUtil.getInatance(mActivity).show("再按一次退出程序！");
            }
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_HOME){
            if(FHLiveHelper.getHelper().isMetting()){
                ToastUtil.getInatance(mActivity).show("请不要切后台！");
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
