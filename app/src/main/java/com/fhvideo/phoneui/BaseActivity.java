package com.fhvideo.phoneui;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.fhvideo.FHResource;

public class BaseActivity extends AppCompatActivity {
    protected Activity activity;

    public FragmentTransaction transaction;
    public Fragment showFragment//当前显示Fragment
            , previousFragment//上一个Fragment
            ;

    public void removeFragment(Fragment fragment) {
        if (fragment == null)
            return;
        transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.remove(fragment)
                .commit();
    }
    public void addFragment(Fragment fragment) {
        if (fragment == null)
            return;
        transaction = getSupportFragmentManager()
                .beginTransaction();

        transaction.add(FHResource.getInstance().getId(activity,"id","fl"), fragment);
        if (showFragment != null && !fragment.equals(showFragment)) {
            previousFragment = showFragment;
            transaction.hide(showFragment);
        }
        transaction.show(fragment)
                .commit();
        showFragment = fragment;

    }

    public void switchFragment(Fragment fragment) {
        if (fragment == null || showFragment == null || fragment.equals(showFragment))
            return;
        previousFragment = showFragment;
        transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.hide(showFragment)
                .show(fragment)
                .commit()
        ;
        showFragment = fragment;
    }
    /**
     * 获取String.xml中字符串
     * @param name 字符串id名
     * @return
     */
    protected String getResString(String name){
        return FHResource.getInstance().getString(activity,name);
    }

    /**
     * 设置单个view
     * @param name 控件id名
     */
    protected <T extends View> T getView(String name){
        T view = findViewById(FHResource.getInstance().getId(activity,"id",name));
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
        T view = findViewById(FHResource.getInstance().getId(activity,"id",name));
        if(view == null ) {
            Log.e("FH_ERROR","findViewById 出错："+name);
            return view;
        }
        if(listener != null)
            view.setOnClickListener(listener);
        return view;
    }
}
