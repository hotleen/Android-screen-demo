package com.fhvideo.phone.fragment;

import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fhvideo.bank.utils.FHStatusBarUtil;
import com.fhvideo.bean.UiEvent;
import com.fhvideo.phone.FHLiveHelper;
import com.fhvideo.phone.R;

import org.greenrobot.eventbus.EventBus;

public class MyFragment extends FHBaseFragment {
    /**
     * 初始化前配置
     */
    @Override
    public void preInit() {
        mFragment = this;
        listener = this;
    }

    /**
     * 获取布局
     */
    @Override
    public int getLayout() {
        return R.layout.fragment_my;
    }

    /**
     * 初始化布局
     */
    @Override
    public void initView() {
        initTop();
        initBot();
        initFL();
    }



    /**
     * 初始化事件和数据
     */
    @Override
    public void initEventAndData() {

    }

    @Override
    public void mainEvent(UiEvent event) {
        super.mainEvent(event);
        switch (event.getType()){
            case FHLiveHelper.HIDE_LOGIN:
                FHStatusBarUtil.setStatusBarDarkTheme(getActivity(), false);

                hideFragment();
                if(FHLiveHelper.isLogin){
                    iv_my_head.setBackgroundResource(R.mipmap.ic_fh_my_head_login);
                    tv_my_name.setText("虚拟营业厅");
                }else {
                    iv_my_head.setBackgroundResource(R.mipmap.ic_fh_my_head_unlogin);
                    tv_my_name.setText("登录");

                }
                break;

        }
    }

    private void hideFragment(){
        fl_my.setVisibility(View.GONE);
        hideFragment(loginFragment);
    }

    private LoginFragment loginFragment;
    private void showLogin(){
        if(loginFragment == null){
            loginFragment = new LoginFragment();
            addFragment(loginFragment,fl_my.getId());
        }
        fl_my.setVisibility(View.VISIBLE);
        showFragment(loginFragment);
    }

    private FrameLayout fl_my;
    private void initFL() {
        fl_my = mView.findViewById(R.id.fl_my);
        fl_my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private TextView tv_my_name;
    private ImageView iv_my_head;
    private void initTop(){
        tv_my_name = mView.findViewById(R.id.tv_my_name);
        iv_my_head = mView.findViewById(R.id.iv_my_head);
        tv_my_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogin();
            }
        });
        iv_my_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogin();
            }
        });
    }

    private RelativeLayout rl_bot_home,rl_bot_my;
    private ImageView iv_bot_home,iv_bot_my;
    private TextView tv_bot_home,tv_bot_my;
    private void initBot() {
        rl_bot_home = mView.findViewById(R.id.rl_bot_home);
        rl_bot_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new UiEvent("", FHLiveHelper.SHOW_HOME));

            }
        });
        iv_bot_home = mView.findViewById(R.id.iv_bot_home);
        tv_bot_home = mView.findViewById(R.id.tv_bot_home);



        rl_bot_my = mView.findViewById(R.id.rl_bot_my);
        iv_bot_my = mView.findViewById(R.id.iv_bot_my);
        tv_bot_my = mView.findViewById(R.id.tv_bot_my);

        Glide.with(getActivity())
                .load(R.mipmap.ic_fh_bot_my_b)
                .into(iv_bot_my);
        tv_bot_my.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_333));
    }
}
