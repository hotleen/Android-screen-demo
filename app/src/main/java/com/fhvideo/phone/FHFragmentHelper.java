package com.fhvideo.phone;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

/**
 * fragment 帮助类
 */
public class FHFragmentHelper {
    private static FHFragmentHelper instance;
    public static FHFragmentHelper getInstance(){
        if(instance == null)
            synchronized (FHFragmentHelper.class){
                if(instance == null)
                    instance = new FHFragmentHelper();
            }
        return instance;
    }


    /**
     * activity中添加fragment
     * @param parent activity
     * @param fragment 要添加的fragment
     * @param id 布局id
     */
    public void addFragment(AppCompatActivity parent,Fragment fragment, int id){
        if(fragment == null || parent == null)
            return;
        parent.getSupportFragmentManager()
                .beginTransaction()
                .add(id,fragment)
                .show(fragment)
                .commitAllowingStateLoss();

    }
    /**
     * activity中添加fragment
     * @param parent activity
     * @param fragment 要添加的fragment
     */
    public void removeFragment(AppCompatActivity parent,Fragment fragment){
        if(fragment == null || parent == null)
            return;
        parent.getSupportFragmentManager()
                .beginTransaction()
                .remove(fragment)
                .show(fragment)
                .commitAllowingStateLoss();

    }
    /**
     * fragment中添加fragment
     * @param parent activity
     * @param fragment 要添加的fragment
     * @param id 布局id
     */
    public void addFragment(Fragment parent,Fragment fragment, int id){
        if(fragment == null || parent == null)
            return;
        parent.getChildFragmentManager()
                .beginTransaction()
                .add(id,fragment)
                .show(fragment)
                .commitAllowingStateLoss();
    }
    /**
     * fragment中显示已添加fragment
     * @param parent activity
     * @param fragment 要显示的fragment
     */
    public void showFragment(Fragment parent,Fragment fragment){
        if(fragment == null || parent == null)
            return;
        parent.getChildFragmentManager()
                .beginTransaction()
                .show(fragment)
                .commitAllowingStateLoss();
    }
    /**
     * activity 中显示已添加fragment
     * @param parent activity
     * @param fragment 要显示的fragment
     */
    public void showFragment(AppCompatActivity parent,Fragment fragment){
        if(fragment == null  || parent == null)
            return;
        parent.getSupportFragmentManager()
                .beginTransaction()
                .show(fragment)
                .commitAllowingStateLoss();

    }
    /**
     * activity 中显示已添加fragment
     * @param parent activity
     * @param showFragment 要显示的fragment
     * @param hideFragment 要隐藏的fragment
     */
    public void showFragment(AppCompatActivity parent,Fragment showFragment,Fragment hideFragment){
        if(showFragment == null  || parent == null|| hideFragment == null)
            return;
        parent.getSupportFragmentManager()
                .beginTransaction()
                .show(showFragment)
                .hide(hideFragment)
                .commitAllowingStateLoss();

    }
    /**
     * getChildFragmentManager 中显示已添加fragment
     * @param parent Fragment
     * @param showFragment 要显示的fragment
     * @param hideFragment 要隐藏的fragment
     */
    public void showFragment(Fragment parent,Fragment showFragment,Fragment hideFragment){
        if(showFragment == null  || parent == null|| hideFragment == null)
            return;
        parent.getChildFragmentManager()
                .beginTransaction()
                .show(showFragment)
                .hide(hideFragment)
                .commitAllowingStateLoss();

    }
    /**
     * fragment中隐藏已添加fragment
     * @param parent activity
     * @param fragment 要显示的fragment
     */
    public void hideFragment(Fragment parent,Fragment fragment){
        if(fragment == null || parent == null)
            return;
        parent.getChildFragmentManager()
                .beginTransaction()
                .hide(fragment)
                .commitAllowingStateLoss();
    }
    /**
     * activity 中隐藏已添加fragment
     * @param parent activity
     * @param fragment 要显示的fragment
     */
    public void hideFragment(AppCompatActivity parent,Fragment fragment){
        if(fragment == null  || parent == null)
            return;
        parent.getSupportFragmentManager()
                .beginTransaction()
                .hide(fragment)
                .commitAllowingStateLoss();

    }
    /**
     * 切换fragment
     * @param parent activity
     * @param hide 要隐藏的fragment
     * @param show 要展示的fragment
     */
    public void switchFragment(AppCompatActivity parent,Fragment hide,Fragment show){
        if(hide == null ||show == null || parent == null)
            return;
        parent.getSupportFragmentManager()
                .beginTransaction()
                .hide(hide)
                .show(show)
                .commitAllowingStateLoss();

    }
    /**
     * 切换fragment
     * @param parent fragment
     * @param hide 要隐藏的fragment
     * @param show 要展示的fragment
     */
    public void switchFragment(Fragment parent,Fragment hide,Fragment show){
        if(hide == null ||show == null || parent == null)
            return;
        parent.getChildFragmentManager()
                .beginTransaction()
                .hide(hide)
                .show(show)
                .commitAllowingStateLoss();

    }
}
