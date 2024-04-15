package com.fhvideo.phoneui.view;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.fhvideo.FHResource;


public class FHBaseView implements View.OnClickListener{
    protected boolean isInit = false;
    protected Context mContext;
    protected View mView;

    //初始化
    public void init(Activity context, View view){
        isInit = true;
        mContext = context;
        mView = view;
    }

    //隐藏
    public void hidden(){

    }
    //释放资源
    public void release(){

    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 获取String.xml中字符串
     * @param name 字符串id名
     * @return
     */
    protected String getResString(String name){
        return FHResource.getInstance().getString(mContext,name);
    }

    /**
     * 设置单个view
     * @param name 控件id名
     */
    protected <T extends View> T getView(String name){
        T view = mView.findViewById(FHResource.getInstance().getId(mContext,"id",name));
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
    protected <T extends View> T getView(String name, View.OnClickListener listener){
        T view = mView.findViewById(FHResource.getInstance().getId(mContext,"id",name));
        if(view == null ) {
            Log.e("FH_ERROR","findViewById 出错："+name);
            return view;
        }
        if(listener != null)
            view.setOnClickListener(listener);
        return view;
    }
}