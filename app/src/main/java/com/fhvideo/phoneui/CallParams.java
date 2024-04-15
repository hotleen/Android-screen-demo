package com.fhvideo.phoneui;

import java.io.Serializable;

/**
 * 呼叫附加参数
 */

public class CallParams implements Serializable{

    private String accoutType;//机构号
    private String linktype;//路由方式
    private String tellerid;//柜员id
    private String skill;//技能号
    private String touristType;//呼叫者业务渠道
    private int queueRank;//优先级
    private String aiAccountType;//ai座席机构号
    private String callType;//呼叫类型，FH_LIVE_CALL_TYPE_AI：ai呼叫，CALL_TYPE_ARTIFICIAL，人工呼叫

    public String getAccoutType() {
        return accoutType;
    }

    public void setAccoutType(String accoutType) {
        this.accoutType = accoutType;
    }

    public String getLinktype() {
        return linktype;
    }

    public void setLinktype(String linktype) {
        this.linktype = linktype;
    }

    public String getTellerid() {
        return tellerid;
    }

    public void setTellerid(String tellerid) {
        this.tellerid = tellerid;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getTouristType() {
        return touristType;
    }

    public int getQueueRank() {
        return queueRank;
    }

    public void setQueueRank(int queueRank) {
        this.queueRank = queueRank;
    }

    public void setTouristType(String touristType) {
        this.touristType = touristType;
    }

    public String getAiAccountType() {
        return aiAccountType;
    }

    public void setAiAccountType(String aiAccountType) {
        this.aiAccountType = aiAccountType;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }
}
