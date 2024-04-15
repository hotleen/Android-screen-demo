package com.fhvideo.phoneui.busi;

import android.widget.TextView;

public interface FHDialogListener {

    void controlView(TextView leftView, TextView rightView, TextView subView);

    void leftOnClick();

    void rightOnClick();

    void subOnClick();

}
