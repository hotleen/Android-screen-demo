package com.fhvideo.phoneui.floatt;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;

import com.fhvideo.FHLiveMacro;
import com.fhvideo.bank.FHSurfaceView;
import com.fhvideo.bean.UiEvent;
import com.fhvideo.fhcommon.params.FHVideoParams;
import com.fhvideo.phoneui.FHUIConstants;
import com.fhvideo.phoneui.FHVideoManager;

import org.greenrobot.eventbus.EventBus;

/**
 * 悬浮窗管理
 */

public class FloatManager {
    private com.fhvideo.phoneui.floatt.FloatView floatView;
    private com.fhvideo.phoneui.floatt.StopShareView stopShareView;
    private static FloatManager instance;
    public FloatManager(){
    }
    public static FloatManager getInstance(){
        if (instance==null)
            instance = new FloatManager();
        return instance;
    }
    public FHSurfaceView getSurface(){
        if(floatView!=null)
            return floatView.getSurface();
        else
            return null;
    }
    /**
     * 创建悬浮窗
     */
    public void createFloatView(Activity context){
        createFloatView(context, FHUIConstants.FH_FLOAT_TYPE_SHARE);
    }
    /**
     * 创建悬浮窗
     */
    public void createFloatView(Activity context, String type){
        if (FHVideoManager.getInstance().getCallType().equals(FHLiveMacro.FH_LIVE_CALL_TYPE_LOCALPREVIEW)){
            floatView = new com.fhvideo.phoneui.floatt.FloatView(context);
            FHVideoManager.getInstance().changeSurface(floatView.getSurface(), FHVideoParams.getIdentity());
        }else {
            if(floatView==null && type.equals(FHUIConstants.FH_FLOAT_TYPE_WEB)){
                floatView = new com.fhvideo.phoneui.floatt.FloatView(context);
                FHVideoManager.getInstance().changeSurface(floatView.getSurface(), FHVideoParams.getIdentity());

            }else if(floatView==null && (FHUIConstants.iShowOwnShareScreenFloatView() || type.equals(FHUIConstants.FH_FLOAT_TYPE_MIN)
            )){
                floatView = new FloatView(context);
                if(FHUIConstants.isCusIsAudio())
                    return;
                FHVideoManager.getInstance().changeSurface(floatView.getSurface());
            }
        }

        if(stopShareView == null){
            stopShareView = new StopShareView(context);
            stopShareView.setStyle(type);
        }
    }

    /**
     * 删除悬浮窗
     * @param context 上下文
     */
    public void removeFloatView(Context context){
            try {
                WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                if(floatView != null){
                    windowManager.removeView(floatView);
                    floatView.release();
                    floatView = null;
                }
                if(stopShareView != null) {
                    windowManager.removeView(stopShareView);
                    stopShareView.release();
                    stopShareView = null;
                }
            }catch (Exception e){

            }
        EventBus.getDefault().post(new UiEvent("","removeFloatView"));

    }
}
