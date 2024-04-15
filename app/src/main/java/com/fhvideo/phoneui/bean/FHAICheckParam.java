package com.fhvideo.phoneui.bean;

public class FHAICheckParam {
    private float widthRatio;//宽度占视频流宽比例
    private float heightRatio;//高度占视频流宽比例
    private float xratio;//起始坐标X,从左上角起算，占视频流宽比例
    private float yratio;//起始坐标y,从左上角起算，占视频流宽比例

    public FHAICheckParam() {
    }

    public FHAICheckParam(float widthRatio, float heightRatio, float xratio, float yratio) {
        this.widthRatio = widthRatio;
        this.heightRatio = heightRatio;
        this.xratio = xratio;
        this.yratio = yratio;
    }

    public float getWidthRatio() {
        return widthRatio;
    }

    public void setWidthRatio(float widthRatio) {
        this.widthRatio = widthRatio;
    }

    public float getHeightRatio() {
        return heightRatio;
    }

    public void setHeightRatio(float heightRatio) {
        this.heightRatio = heightRatio;
    }

    public float getXratio() {
        return xratio;
    }

    public void setXratio(float xratio) {
        this.xratio = xratio;
    }

    public float getYratio() {
        return yratio;
    }

    public void setYratio(float yratio) {
        this.yratio = yratio;
    }
}
