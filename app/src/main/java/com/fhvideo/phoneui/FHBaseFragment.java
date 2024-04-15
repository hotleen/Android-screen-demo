package com.fhvideo.phoneui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fhvideo.FHResource;
import com.fhvideo.bean.AnsEvent;
import com.fhvideo.bean.UiEvent;
import com.fhvideo.fhcommon.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public abstract class FHBaseFragment extends Fragment {

    protected View mView;

    protected boolean isHidden = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(getLayout(),null);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        isHidden = false;
        initView();
        initEventAndData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
    /**
     * 获取String.xml中字符串
     * @param name 字符串id名
     * @return
     */
    public String getResString(String name){
        return FHResource.getInstance().getString(getActivity(),name);
    }

    /**
     * 获取图片id
     * @param name
     * @return
     */
    public int getMipmap(String name){
        return FHResource.getInstance().getId(getActivity(),"mipmap",name);
    }

    /**
     * 获取颜色
     * @param name
     * @return
     */
    public int getColor(String name){
        return FHResource.getInstance().getColor(getActivity(),name);
    }
    /**
     * 设置单个view
     * @param name 控件id名
     */
    public <T extends View> T getView(String name){
        T view = mView.findViewById(FHResource.getInstance().getId(getActivity(),"id",name));
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
    public <T extends View> T getView(String name, View.OnClickListener listener){
        T view = mView.findViewById(FHResource.getInstance().getId(getActivity(),"id",name));
        if(view == null ) {
            Log.e("FH_ERROR","findViewById 出错："+name);
            return view;
        }
        if(listener != null)
            view.setOnClickListener(listener);
        return view;
    }


    /**获取布局*/
    protected abstract int getLayout();

    /**初始化布局*/
    protected abstract void initView();

    /**初始化事件和数据*/
    protected abstract void initEventAndData();



    /**主线程*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mainEvent(UiEvent ros){
        if(ros == null || StringUtil.isEmpty(ros.getType()))
            return;
    }

    /*异步线程*/
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void asyncEvent(AnsEvent ros){
        if(ros == null || StringUtil.isEmpty(ros.getType()))
            return;
    }

    /**原线程*/
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void postEvent(Object ros){

    }


}
