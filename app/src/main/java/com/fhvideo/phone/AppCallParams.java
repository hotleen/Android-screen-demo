package com.fhvideo.phone;

import com.fhvideo.phoneui.CallParams;

/**
 * APP 自定义呼叫参数
 */
public class AppCallParams extends CallParams {
    private String channelId;//渠道号
    private String scenarioId;//场景号

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }
}
