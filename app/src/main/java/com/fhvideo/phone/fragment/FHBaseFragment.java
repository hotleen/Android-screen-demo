package com.fhvideo.phone.fragment;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fhvideo.bean.AnsEvent;
import com.fhvideo.bean.UiEvent;
import com.fhvideo.fhcommon.utils.StringUtil;
import com.fhvideo.phone.FHBaseUI;
import com.fhvideo.phone.FHFragmentHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Fragment 基类
 */
public abstract class FHBaseFragment extends Fragment implements FHBaseUI, View.OnClickListener {

    protected Activity mActivity;
    protected Fragment mFragment;
    protected View.OnClickListener listener;
    protected View mView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        preInit();
        mView = inflater.inflate(getLayout(),null);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mFragment != null && !EventBus.getDefault().isRegistered(mFragment))
            EventBus.getDefault().register(mFragment);
        initView();
        initEventAndData();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mFragment != null)
            release();
    }


    /**
     * 点击事件
     * @param view
     */
    @Override
    public void onClick(View view){};

    /**
     * 释放资源
     */
    public void release(){
        EventBus.getDefault().unregister(mFragment);
        mActivity = null;
        listener = null;
        mFragment = null;
    }

    /**主线程*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mainEvent(UiEvent event){
        if(event == null || StringUtil.isEmpty(event.getType()))
            return;
    }

    /**异步线程*/
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void asyncEvent(AnsEvent event){
        if(event == null || StringUtil.isEmpty(event.getType()))
            return;
    }

    protected void showFragment(Fragment showFragment,Fragment hideFragment){
        if( showFragment == null || hideFragment== null )
            return;
        FHFragmentHelper.getInstance().showFragment(mFragment,showFragment,hideFragment);
    }
    protected void showFragment(Fragment showFragment){
        if( showFragment == null  )
            return;
        FHFragmentHelper.getInstance().showFragment(mFragment,showFragment);
    }
    protected void hideFragment(Fragment fragment){
        if( fragment == null )
            return;
        FHFragmentHelper.getInstance().hideFragment(mFragment,fragment);
    }
    protected void switchFragment(Fragment hide,Fragment show){
        if(hide == null || show == null )
            return;
        FHFragmentHelper.getInstance().switchFragment(mFragment,hide,show);
    }

    protected void addFragment(Fragment fragment,int id){
        if(fragment == null)
            return;
        FHFragmentHelper.getInstance().addFragment(mFragment,fragment,id);
    }

}
