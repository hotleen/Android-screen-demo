package com.fhvideo.phoneui.view;

import android.app.Activity;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fhvideo.FHResource;
import com.fhvideo.bank.utils.SystemUtil;
import com.fhvideo.phoneui.bean.AICheckData;
import com.fhvideo.phoneui.bean.AiChekParam;
import com.fhvideo.phoneui.bean.FHAICheckParam;
import com.fhvideo.phoneui.view.ai.CircularSeekBar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FHAICheckView extends FHBaseView {
    private static FHAICheckView instance;
    private RelativeLayout content_rl;
    private LinearLayout aiCheckLin;
    private int screenWidth;//屏幕宽度
    private ImageView circleImg;
    private CircularSeekBar circularSeekBar;
    private TextView showCheckTv;
    private RelativeLayout rlAiBorderInChek;
    private TextView tvAiHintTitleInCheck;

    List<AICheckData> checkData;

    private CountDownTimer countDownTimer;
    private float currentCountDownTimes;
    private int index = 0;
    private RotateAnimation animation;
    private float checkTime = 5;

    public static FHAICheckView getInstance(Activity activity) {
        if (instance == null) {
            synchronized (FHCallView.class) {
                if (instance == null)
                    instance = new FHAICheckView(activity);
            }
        }
        return instance;
    }

    public FHAICheckView(Activity activity) {
        mContext = activity;
        if (mView == null) {
            mView = (LinearLayout) LayoutInflater.from(activity).inflate(FHResource.getInstance().getId(activity, "layout", "layout_fh_ai_check"), null, false);

        }
    }


    @Override
    public void init(Activity context, View view) {
        super.init(context, view);
        content_rl = getView("content_rl");
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) content_rl.getLayoutParams();
        params.height = screenWidth / 9 * 16;
        content_rl.setLayoutParams(params);
        aiCheckLin = getView("ai_check_lin");
        circleImg = getView("ai_check_circle_img");
        showCheckTv = getView("show_check_tv");
        circularSeekBar = getView("check_seek_bar");
        rlAiBorderInChek = getView("rl_ai_border_in_chek");
        tvAiHintTitleInCheck = getView("tv_ai_hint_title_in_check");
    }


    @Override
    public void hidden() {
        super.hidden();
        aiCheckLin.setVisibility(View.GONE);

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (animation != null) {
            animation.cancel();
        }
        circleImg.clearAnimation();
    }

    public void startCheck(AiChekParam aiChekParam) {
        index = 0;
        if (animation != null) {
            animation.cancel();
        }
        try {
            checkTime = Float.valueOf(aiChekParam.getActionTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        int resource = FHResource.getInstance().getId(mContext, "mipmap", "ic_fh_ai_normal_circle");
        circleImg.setImageResource(resource);
        circleImg.clearAnimation();
        circleImg.setVisibility(View.GONE);
        circularSeekBar.setVisibility(View.VISIBLE);
        String[] actions = aiChekParam.getActionSequence().split(",");
        checkData = new ArrayList<>();

        for (String action : actions) {
            if ("1".equals(action)) {
                checkData.add(new AICheckData(checkTime, "张开嘴巴"));
            } else if ("2".equals(action)) {
                checkData.add(new AICheckData(checkTime, "眨眨眼睛"));
            }
        }

        startCountdown();


    }

    private void startCountdown() {
        getCountDownInfo();
        circularSeekBar.setMax((int) (currentCountDownTimes * 1000));
        countDownTimer = new CountDownTimer((long) (currentCountDownTimes * 1000), 1) {
            @Override
            public void onTick(long l) {
                circularSeekBar.setProgress((int) (currentCountDownTimes * 1000) - (int) l);
            }

            @Override
            public void onFinish() {
                if (index < checkData.size() - 1) {
                    ++index;
                    countDownTimer.cancel();
                    startCountdown();
                } else {
                    countDownTimer.cancel();
                    waitResult();

                }
            }
        };
        countDownTimer.start();
    }

    private void waitResult() {
        showCheckTv.setText("正在检测，请稍后...");
        circularSeekBar.setVisibility(View.GONE);
        circleImg.setVisibility(View.VISIBLE);
        int resource = FHResource.getInstance().getId(mContext, "mipmap", "ic_fh_ai_wait_result");
        circleImg.setImageResource(resource);
        animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        animation.setDuration(1800);
        animation.setRepeatCount(-1);//动画的反复次数
        animation.setFillAfter(true);//设置为true，动画转化结束后被应用
        circleImg.startAnimation(animation);//開始动画

    }

    private void getCountDownInfo() {
        currentCountDownTimes = checkData.get(index).getTime();
        showCheckTv.setText(checkData.get(index).getMsg());
    }


    public void show() {
        aiCheckLin.setVisibility(View.VISIBLE);
    }

    public FHAICheckParam getCheckParam() {
        FHAICheckParam checkParam = new FHAICheckParam();
        int width = screenWidth;//视频流宽
        int height = screenWidth / 9 * 16;//视频流高

        int circleDiameter = SystemUtil.dp2px(mContext, 324);//显示人脸圆的直径
        float widthRatio = new BigDecimal(circleDiameter).divide(new BigDecimal(width), 2, BigDecimal.ROUND_HALF_UP).floatValue();//宽度占视频流宽比例  圆圈直径/流的宽度
        float heightRatio = new BigDecimal(circleDiameter).divide(new BigDecimal(height), 2, BigDecimal.ROUND_HALF_UP).floatValue();//高度占视频流宽比例  圆圈直径/流的高度
        float xratio = new BigDecimal((width - circleDiameter) / 2).divide(new BigDecimal(width), 2, BigDecimal.ROUND_HALF_UP).floatValue();//起始坐标X,从左上角起算，占视频流宽比例
        float yratio = new BigDecimal(SystemUtil.dp2px(mContext, 138)).divide(new BigDecimal(height), 2, BigDecimal.ROUND_HALF_UP).floatValue();//起始坐标y,从左上角起算，占视频流高比例

        checkParam.setWidthRatio(widthRatio);
        checkParam.setHeightRatio(heightRatio);
        checkParam.setXratio(xratio);
        checkParam.setYratio(yratio);
        return checkParam;
    }

    /**
     * 展示人脸出框提醒（人脸检测内部） 与文字
     */
    public void showBorderInCheck(boolean isShow,String borderMsg){
        rlAiBorderInChek.setVisibility(isShow?View.VISIBLE:View.GONE);
        tvAiHintTitleInCheck.setText(borderMsg);
    }

    @Override
    public void release() {
        super.release();
        instance = null;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (animation != null) {
            animation.cancel();
        }
        circleImg.clearAnimation();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
