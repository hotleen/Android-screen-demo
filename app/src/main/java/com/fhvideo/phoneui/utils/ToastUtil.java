package com.fhvideo.phoneui.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.lang.reflect.Field;

public class ToastUtil {
    private static ToastUtil instance;
    private static Context mContext;

    public static ToastUtil getInatance(Context context) {
        if(context != null)
            mContext = context.getApplicationContext();
        if (instance == null) {
            synchronized (ToastUtil.class) {
                if (instance == null) {
                    instance = new ToastUtil();
                }
            }
        }
        return instance;
    }

    private Toast toast;


    public void show(String msg) {
        try {
            if (mContext == null)
                return;
            if (toast == null) {
                toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
                // 在调用Toast.show()之前处理:
                hook(toast);
            }
            toast.setText(msg);
            toast.show();

        } catch (Exception e) {

        }

    }


    private static Field sFieldTN;
    private static Field sFieldTNHandler;

    static {
        try {
            sFieldTN = Toast.class.getDeclaredField("mTN");
            sFieldTN.setAccessible(true);
            sFieldTNHandler = sFieldTN.getType().getDeclaredField("mHandler");
            sFieldTNHandler.setAccessible(true);
        } catch (Exception e) {
        }
    }

    private static void hook(Toast toast) {
        try {
            Object tn = sFieldTN.get(toast);
            Handler preHandler = (Handler) sFieldTNHandler.get(tn);
            sFieldTNHandler.set(tn, new SafelyHandlerWrapper(preHandler));
        } catch (Exception e) {
        }
    }

    private static class SafelyHandlerWrapper extends Handler {
        private Handler impl;

        SafelyHandlerWrapper(Handler impl) {
            this.impl = impl;
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {
            }
        }

        @Override
        public void handleMessage(Message msg) {
            impl.handleMessage(msg);
        }
    }

    public static void release(){
        mContext = null;
        instance = null;
    }

}

