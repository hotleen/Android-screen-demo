package com.fhvideo.phoneui.busi;


public interface FHLiveListener {

    void showHint(String hint);

    void showError(String error, boolean toClose);

    void showCustomDialog(String msg, FHDialogListener listener);

    void showLive(String sessionId);

    void addFlot(String type);

    void close(boolean isTeller);
    void quit();


    void showLongToast(boolean isShow, String msg);


}
