package com.fhvideo.phone.test;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fhvideo.FHLiveMacro;
import com.fhvideo.FHParams;
//import com.fhvideo.fhcommon.bean.FHVolumeInfo;
import com.fhvideo.fhcommon.bean.GsonUtil;
import com.fhvideo.phone.FHLiveHelper;
import com.fhvideo.phone.R;
import com.fhvideo.phoneui.FHVideoManager;

import java.util.ArrayList;


/**
 * 测试view
 */
public class TestView extends RelativeLayout {

    public TestView(Context context) {
        super(context);
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Button close, audio_route, mute_audio, enable_audio_volume;

    private TextView local_voice, remote_voice;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(R.layout.layout_test, this);
        initView();
        initListener();
    }

    private void initListener() {
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
            }
        });
        audio_route.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audio_route.isSelected()) {
                    FHVideoManager.getInstance().setAudioRoute(FHLiveMacro.FH_AUDIO_ROUTE_SPEAKER);
                    audio_route.setText("音频路由-当前-->扬声器");
                } else {
                    FHVideoManager.getInstance().setAudioRoute(FHLiveMacro.FH_AUDIO_ROUTE_EARPIECE);
                    audio_route.setText("音频路由-当前-->听筒");
                }
                audio_route.setSelected(!audio_route.isSelected());
            }
        });
        mute_audio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mute_audio.isSelected()) {
                    FHVideoManager.getInstance().muteLocalAudio(false);
                    mute_audio.setText("开关麦克风-当前-->开启");
                } else {
                    FHVideoManager.getInstance().muteLocalAudio(true);
                    mute_audio.setText("开关麦克风-当前-->关闭");
                }
                mute_audio.setSelected(!mute_audio.isSelected());
            }
        });
        enable_audio_volume.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enable_audio_volume.isSelected()) {
                    FHVideoManager.getInstance().enableAudioVolumeEvaluation(300, false);
                    enable_audio_volume.setText("预设音量大小回调开关-当前-->关闭");
                } else {
                    FHVideoManager.getInstance().enableAudioVolumeEvaluation(300, true);
                    enable_audio_volume.setText("预设音量大小回调开关-当前-->开启");
                }
                enable_audio_volume.setSelected(!enable_audio_volume.isSelected());
            }
        });

        FHLiveHelper.getHelper().setFhAudioVolumeCallBack(new FHAudioVolumeCallBack() {
            @Override
            public void onVoiceVolume(String json) {
//                ArrayList<FHVolumeInfo> fhVolumeInfos = GsonUtil.fromJson(json, FHVolumeListDto.class);
//                if (fhVolumeInfos == null || fhVolumeInfos.isEmpty()) {
//                    return;
//                }
//                for (FHVolumeInfo fhVolumeInfo : fhVolumeInfos) {
//                    if (TextUtils.isEmpty(fhVolumeInfo.getUserId()) || fhVolumeInfo.getUserId().equals(FHParams.getUid())) {
//                        local_voice.setText("本地音量->" + fhVolumeInfo.getVolume());
//                    } else {
//                        remote_voice.setText("远端音量->" + fhVolumeInfo.getVolume());
//                    }
//                }
            }
        });

    }

    private void initView() {
        close = findViewById(R.id.close);
        audio_route = findViewById(R.id.audio_route);
        mute_audio = findViewById(R.id.mute_audio);
        enable_audio_volume = findViewById(R.id.enable_audio_volume);
        local_voice = findViewById(R.id.local_voice);
        remote_voice = findViewById(R.id.remote_voice);
    }

    public interface FHAudioVolumeCallBack {
        void onVoiceVolume(String json);
    }
}
