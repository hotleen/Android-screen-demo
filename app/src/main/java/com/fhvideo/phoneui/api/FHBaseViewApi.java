package com.fhvideo.phoneui.api;

import android.view.View;
/**
 * 组件库UI基类
 */
public interface FHBaseViewApi {
    /**
     * 获取UI控件，供业务层使用
     * @return 布局文件
     */
    View getView();

    /**
     * 出错提示
     * @param msg 提示信息
     */
    void onError(String msg);

    /**
     * 警告提示
     * @param msg 提示信息
     */
    void warn(String msg);

    /**
     * 释放UI资源
     */
    void release();
}
