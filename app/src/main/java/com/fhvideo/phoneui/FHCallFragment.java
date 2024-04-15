package com.fhvideo.phoneui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fhvideo.FHLiveClientParams;
import com.fhvideo.FHResource;
import com.fhvideo.adviser.bean.CallData;
import com.fhvideo.adviser.bean.CusCallInfo;
import com.fhvideo.adviser.tool.FHAPlayer;
import com.fhvideo.bank.FHBankParams;
import com.fhvideo.bean.GsonUtil;
import com.fhvideo.fhcommon.FHBusiCallBack;
import com.fhvideo.fhcommon.utils.StringUtil;
import com.fhvideo.phoneui.busi.FHLiveListener;
import com.fhvideo.phoneui.view.FHCallView;

import org.json.JSONException;
import org.json.JSONObject;


public class FHCallFragment extends Fragment {

    private FHLiveListener listener;
    private final String UPT_HINT = "upt_hint";
    private View callView;
    private CallData mData;
    private static FHCallFragment callFragment = null;

    private boolean isConfirm = false;

    private boolean isBusy = false;

    public static FHCallFragment newInstance(String liveData) {
        Bundle args = new Bundle();
        args.putString("liveData", liveData);
        if (callFragment == null) {
            synchronized (FHCallFragment.class) {
                if (callFragment == null) {
                    callFragment = new FHCallFragment();
                }
            }
        }
        callFragment.setArguments(args);
        return callFragment;
    }

    @SuppressLint("ValidFragment")
    private FHCallFragment() {

    }

    public void setEventListener(FHLiveListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        callView = inflater.inflate(FHResource.getInstance().getId(getActivity(),"layout","fragment_fh_call"), null);
        return callView;
    }

    private RelativeLayout fragment_call;
    private FHBaseCallView baseCallView;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String liveData = getArguments().getString("liveData");
        mData = GsonUtil.fromJson(liveData, CallData.class);
        fragment_call = callView.findViewById(FHResource.getInstance().getId(getActivity(),"id","fragment_call"));
        if(FHVideoManager.getInstance().getCallView() != null && FHVideoManager.getInstance().getCallView().getView() != null){
            baseCallView = FHVideoManager.getInstance().getCallView();
        }else {
            baseCallView = FHCallView.getInstance(getActivity());
        }
        addCallView(baseCallView.getView());
        call();
    }
    private void addCallView(View callView){
        if(callView != null){
            try {
                if(callView.getParent() != null){
                    ViewGroup viewParent = (ViewGroup) callView.getParent();
                    viewParent.removeAllViews();
                }
                fragment_call.removeView(callView);
                fragment_call.addView(callView);
            }catch (Exception e){
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        isShow = false;
    }

    @Override
    public void onResume() {
        isShow = true;
        super.onResume();
        getFocus();
        if (isConfirm) {
            isConfirm = false;
            receiveEvent(FHLiveClientParams.CALL_EVENT_ANSWER, getResString("fh_metting"));
        }
    }
    /**
     * 获取String.xml中字符串
     * @param name 字符串id名
     * @return
     */
    protected String getResString(String name){
        return FHResource.getInstance().getString(getActivity(),name);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(baseCallView != null)
            baseCallView.release();
        listener = null;
        callFragment = null;
        isConfirm = false;
        isShow = false;
    }

    private void call() {
        if (!FHBankParams.isConnected()) { //判断网络状态提示
            listener.showError(getResString("fh_net_weak_hint"), true);
            return;
        }
        receiveEvent(FHBankParams.FH_CALL_TYPE_CALL,
                getResString("fh_waiting"));
        FHVideoManager.getInstance().call(getActivity(), mData,
                new FHBusiCallBack() {
                    @Override
                    public void onSuccess(String str) {
                        CusCallInfo callInfo = GsonUtil.fromJson(str,CusCallInfo.class);
                        if(callInfo != null){
                            receiveEvent(callInfo.getSucctype(), str);

                        }
                    }

                    @Override
                    public void onError(String error) {
                        if (error.equals(FHBankParams.GET_ADDRESS_LOCATION_ERROR))
                            baseCallView.onError("定位失败，请开启定位权限或检查网络");
                        else
                            baseCallView.onError(error);
                    }

                });
    }

    public void setCallType(String type, String msg) {
        switch (type) {
            case FHLiveClientParams.CALL_EVENT_BUSY://忙碌
                isBusy = true;
                break;
            case  FHLiveClientParams.CALL_EVENT_ANSWER://接听
                FHAPlayer.getInstance().stopPlayer();
                listener.showLive(mSessionid);
                break;
            case FHLiveClientParams.CALL_EVENT_REFUSE://拒接
                isBusy = true;
                break;

            default:
                break;

        }
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

    private boolean isShow = false;//是否在前台
    private String mSessionid = "";
    private boolean isswitchclose = false;//用于app处于后台状态时 柜员同意呼叫

    public void receiveEvent(String event, String msg) {
        if(baseCallView != null)
            baseCallView.onCallEvent(event,msg);

        switch (event){
            case FHLiveClientParams.CALL_EVENT_ANSWER://接听
                confirm(msg);
                break;
            case FHLiveClientParams.CALL_EVENT_REFUSE://拒绝
                setCallType(event, getResString("fh_teller_busy"));
                break;
            case FHLiveClientParams.CALL_EVENT_RING://振铃中
            case FHLiveClientParams.CALL_EVENT_QUEUE://排队中
            case FHLiveClientParams.CALL_EVENT_BUSY://忙碌中
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    String hintmsg = jsonObject.optString("hintmsg");
                    setCallType(event, hintmsg);
                } catch (JSONException e) {
                }
                break;

        }

    }

    private void confirm(String msg) {//柜员接通
        isswitchclose = true;
        mSessionid = msg;
        if (!StringUtil.isEmpty(msg)) {
            if (isShow) {
                FHAPlayer.getInstance().stopPlayer();
                listener.showLive(mSessionid);
                if(baseCallView != null)
                    baseCallView.release();
            } else {
                isConfirm = true;
            }
        }
    }
}
