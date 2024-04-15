package com.fhvideo.phone;

/**
 * UI 基类
 */
public interface FHBaseUI {
    /**
     * 初始化前配置
     */
    void preInit();

    /**获取布局*/
    int getLayout();

    /**
     * 初始化布局
     */
    void initView();

    /**初始化事件和数据*/
    void initEventAndData();


}
