package com.fhvideo.phone.fragment;

import com.fhvideo.phone.R;

public class RouteFragment extends FHBaseFragment {
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
        return R.layout.fragment_route;
    }

    /**
     * 初始化布局
     */
    @Override
    public void initView() {

    }

    /**
     * 初始化事件和数据
     */
    @Override
    public void initEventAndData() {

    }
}
