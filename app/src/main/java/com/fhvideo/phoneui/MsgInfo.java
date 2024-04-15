package com.fhvideo.phoneui;

import java.io.Serializable;

/**
 * 交易信息
 */

public class MsgInfo implements Serializable {
    private String businessType;//交易类型
    private String tradeNo;//交易流水号
    private String sessionid;//会话id


    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }
}
