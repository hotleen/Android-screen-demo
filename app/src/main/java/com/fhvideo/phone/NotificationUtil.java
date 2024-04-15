package com.fhvideo.phone;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationUtil {
    /**
     * 判断权限是否打开
     *
     * @param context
     * @return
     */
    public static boolean isNotificationEnabled(Context context) {
        //判断应用是否开启了通知权限 4.4以上可用，4.4以下默认返回true
        boolean isOpened = false;
        try {
            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            isOpened = manager.areNotificationsEnabled();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOpened;
    }

    /**
     * 跳转到设置权限的页面
     *
     * @param context
     */
    public static void gotoSet(Context context) {
        try {
            // 根据isOpened结果，判断是否需要提醒用户跳转AppInfo页面，去打开App通知权限
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);

            //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);

            // 小米6 -MIUI9.6-8.0.0系统，是个特例，通知设置界面只能控制"允许使用通知圆点"——然而这个玩意并没有卵用，我想对雷布斯说：I'm not ok!!!
            if ("MI 6".equals(Build.MODEL)) {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                intent.setAction("com.android.settings/.SubSettings");
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
            Intent intent = new Intent();
            //下面这种方案是直接跳转到当前应用的设置界面。
            //https://blog.csdn.net/ysy950803/article/details/71910806
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        }
    }

    /**
     * 消息通知
     * @param context
     * @param obj
     */
    public static void notifyMsg(Context context,  NotifyObject obj, int nid, long time, NotificationManager mNotifyMgr){
        if(context == null || obj == null)return;
        if(mNotifyMgr == null){
            mNotifyMgr =  (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);//NOTIFICATION_SERVICE
        }

        if(time <= 0)return;

        //准备intent
        Intent intent = new Intent(context,HomeActivity.class);
        if(obj.param != null && obj.param.trim().length() > 0){
            intent.putExtra("param",obj.param);
        }

        //notification
        Notification notification = null;
        String contentText = obj.content;
        // 构建 PendingIntent
        PendingIntent pi = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //版本兼容
        CharSequence name = context.getResources().getString(R.string.fh_call_inform);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){//兼容Android8.0
            String id =context.getPackageName();
            int importance =NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel =new NotificationChannel(id, name,importance);
            mChannel.enableLights(true);
            mChannel.setDescription(context.getResources().getString(R.string.fh_float_hint));
            mChannel.enableLights(true);
            mChannel.setSound(null,null);
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,400});
            mNotifyMgr.createNotificationChannel(mChannel);
            Notification.Builder builder = new Notification.Builder(context,id);
            builder.setAutoCancel(true)
                    .setContentIntent(pi)
                    //.setFullScreenIntent(pi,true)
                    .setContentTitle(obj.title)
                    .setContentText(obj.content)
                    .setOngoing(false)
                    .setTicker(name)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis());
            if(obj.subText != null && obj.subText.trim().length() > 0){
                builder.setSubText(obj.subText);
            }
            notification =  builder.build();
        }else if (Build.VERSION.SDK_INT >= 23) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle(obj.title)
                    .setContentText(contentText)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setTicker(name)
                    .setDefaults(2)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setWhen(System.currentTimeMillis());
            if(obj.subText != null && obj.subText.trim().length() > 0){
                builder.setSubText(obj.subText);
            }
            notification = builder.build();
        } else if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Notification.Builder builder = new Notification.Builder(context);
            builder.setAutoCancel(true)
                    .setContentIntent(pi)
                    .setContentTitle(obj.title)
                    .setContentText(obj.content)
                    .setOngoing(false)
                    .setTicker(name)
                    .setDefaults(2)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis());
            if(obj.subText != null && obj.subText.trim().length() > 0){
                builder.setSubText(obj.subText);
            }
            notification =  builder.build();
        }
        if(notification != null){
            mNotifyMgr.notify(nid, notification);
        }
    }


    public static Notification createScreenNotification(Context context,  NotifyObject obj){
        Notification notification = null;

        if(context == null || obj == null)
            return notification;

        NotificationManager    mNotifyMgr =  (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);//NOTIFICATION_SERVICE

        //准备intent
       // Intent intent = new Intent(context,MainActivity.class);
       /* if(obj.param != null && obj.param.trim().length() > 0){
            intent.putExtra("param",obj.param);
        }*/

        //notification
        String contentText = obj.content;
        // 构建 PendingIntent
        //PendingIntent pi = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //版本兼容
        CharSequence name = context.getResources().getString(R.string.fh_call_inform);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){//兼容Android8.0
            String id =context.getPackageName();
            int importance =NotificationManager.IMPORTANCE_MIN;
            NotificationChannel mChannel =new NotificationChannel(id, name,importance);
            mChannel.enableLights(true);
            mChannel.setDescription(context.getResources().getString(R.string.fh_float_hint));
            mChannel.enableLights(true);
            mChannel.setSound(null,null);
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,400});
            mNotifyMgr.createNotificationChannel(mChannel);
            Notification.Builder builder = new Notification.Builder(context,id);
            builder.setAutoCancel(true)
                    //.setContentIntent(pi)
                    //.setFullScreenIntent(pi,true)
                    .setContentTitle(obj.title)
                    .setContentText(obj.content)
                    .setOngoing(false)
                    .setTicker(name)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis());
            if(obj.subText != null && obj.subText.trim().length() > 0){
                builder.setSubText(obj.subText);
            }
            notification =  builder.build();
        }else if (Build.VERSION.SDK_INT >= 23) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle(obj.title)
                    .setContentText(contentText)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    //.setContentIntent(pi)
                    .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)

                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setTicker(name)
                    .setDefaults(2)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setWhen(System.currentTimeMillis());
            if(obj.subText != null && obj.subText.trim().length() > 0){
                builder.setSubText(obj.subText);
            }
            notification = builder.build();
        } else if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Notification.Builder builder = new Notification.Builder(context);
            builder.setAutoCancel(true)
                    //.setContentIntent(pi)
                    .setContentTitle(obj.title)
                    .setContentText(obj.content)
                    .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)

                    .setOngoing(false)
                    .setTicker(name)
                    .setDefaults(2)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis());
            if(obj.subText != null && obj.subText.trim().length() > 0){
                builder.setSubText(obj.subText);
            }
            notification =  builder.build();
        }
        return notification;
    }
}
