package com.fhvideo.phoneui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fhvideo.FHResource;
import com.fhvideo.bank.FHBankParams;
import com.fhvideo.bean.UiEvent;
import com.fhvideo.fhcommon.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;


public class FHBusiResultFragment extends FHBaseFragment {

    private static FHBusiResultFragment resultFragment = null;
    public static FHBusiResultFragment newInstance() {
        Bundle args = new Bundle();
        if (resultFragment == null) {
            synchronized (FHBusiResultFragment.class) {
                if (resultFragment == null) {
                    resultFragment = new FHBusiResultFragment();
                }
            }
        }
        resultFragment.setArguments(args);
        return resultFragment;
    }

    @SuppressLint("ValidFragment")
    private FHBusiResultFragment() {

    }
    /**
     * 获取布局
     */
    @Override
    protected int getLayout() {
        return FHResource.getInstance().getId(getActivity(),"layout","fh_fragment_busi_result");
    }

    private RelativeLayout rl_busi_result;
    private TextView tv_busi_type,tv_busi_quit,tv_busi_call_artificial;
    private ImageView iv_busi_type;
    /**
     * 初始化布局
     */
    @Override
    protected void initView() {
        rl_busi_result = getView("rl_busi_result", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tv_busi_type = getView("tv_busi_type");
        iv_busi_type = getView("iv_busi_type");

        tv_busi_quit = getView("tv_busi_quit", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FHVideoManager.getInstance().setCallType(FHVideoManager.CALL_TYPE_ARTIFICIAL);
                EventBus.getDefault().post(new UiEvent("","CLOSE_FHLIVE"));

            }
        });

        tv_busi_call_artificial = getView("tv_busi_call_artificial", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FHVideoManager.getInstance().setCallType(FHVideoManager.CALL_TYPE_ARTIFICIAL);
                EventBus.getDefault().post(new UiEvent("","CALL_TYPE_ARTIFICIAL"));

            }
        });
    }

    private String busiType;
    /**
     * 初始化事件和数据
     */
    @Override
    protected void initEventAndData() {
        busiType = FHBankParams.getEndRearAction();
        if(StringUtil.isEmpty(busiType) ||busiType.equals("failure")){//失败
            iv_busi_type.setBackgroundResource(getMipmap("ic_fh_busi_error"));
            tv_busi_type.setText(getResString("fh_busi_error"));
            tv_busi_call_artificial.setVisibility(View.VISIBLE);
        }else {//成功
            iv_busi_type.setBackgroundResource(getMipmap("ic_fh_busi_success"));
            tv_busi_type.setText(getResString("fh_busi_success"));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        resultFragment = null;
    }

    //主界面获取焦点
    private void getFocus() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
    }


}
