package com.fhvideo.phone.fragment;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fhvideo.FHLiveClient;
import com.fhvideo.FHLiveMacro;
import com.fhvideo.FHLiveSessionParams;
import com.fhvideo.adviser.FHTellerParams;
import com.fhvideo.bank.FHPermission;
import com.fhvideo.bank.utils.FHStatusBarUtil;
import com.fhvideo.bank.utils.SharedPreUtil;
import com.fhvideo.bean.GsonUtil;
import com.fhvideo.bean.UiEvent;
import com.fhvideo.fhcommon.FHBusiCallBack;
import com.fhvideo.fhcommon.params.FHVideoParams;
import com.fhvideo.fhcommon.utils.StringUtil;
import com.fhvideo.phone.AppCallParams;
import com.fhvideo.phone.BuildConfig;
import com.fhvideo.phone.CallExtData;
import com.fhvideo.phone.FHLiveHelper;
import com.fhvideo.phone.R;
import com.fhvideo.phoneui.FHVideoManager;
import com.fhvideo.phoneui.LiveData;
import com.fhvideo.phoneui.utils.ToastUtil;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class LoginFragment  extends FHBaseFragment {
    /**
     * 初始化前配置
     */
    @Override
    public void preInit() {
        mFragment = this;
        listener = this;
    }

    /**
     * 获取布局
     */
    @Override
    public int getLayout() {
        return R.layout.fragment_login;
    }
    private InputMethodManager imm;

    /**
     * 初始化布局
     */
    @Override
    public void initView() {
        imm = (InputMethodManager)getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);

        FHStatusBarUtil.setStatusBarDarkTheme(getActivity(), true);

        initLogin();
        initSeeting();
        initWheel();
    }


    private AppCallParams callParams;
    private LiveData demoData = null;
    private String linkType = "0";
    private String oldURL = "";

    /**
     * 初始化事件和数据
     */
    @Override
    public void initEventAndData() {
        initParams();
        initWheelList();
    }




    //登录
    private ImageView iv_login_back,iv_login_logo

            ;
    private TextView tv_login
            ;
    private RelativeLayout rl_login;
    private EditText et_id,et_pwd,et_route_account,et_route_tellerid,et_route_skill;
    private void initLogin() {
        iv_login_back = mView.findViewById(R.id.iv_login_back);
        iv_login_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(et_id.getWindowToken(), 0);
                EventBus.getDefault().post(new UiEvent("", FHLiveHelper.HIDE_LOGIN));
            }
        });

        tv_login = mView.findViewById(R.id.tv_login);
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FHLiveHelper.isLogin)
                    return;
                if (chatMode == 1){
                    FHPermission.getInstance().checkVoicePermission(getActivity());
                }else {
                    //申请权限
                    FHPermission.getInstance().checkPermission(getActivity());
                }

                login();
            }
        });

        rl_login = mView.findViewById(R.id.rl_login);
        rl_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        et_id = mView.findViewById(R.id.et_id);
        et_pwd = mView.findViewById(R.id.et_pwd);

        et_route_account= mView.findViewById(R.id.et_route_account);
        et_route_tellerid= mView.findViewById(R.id.et_route_tellerid);
        et_route_skill= mView.findViewById(R.id.et_route_skill);

    }

    private void login() {
        final String uid = et_id.getText().toString().trim();
        final String pwd = et_pwd.getText().toString().trim();

        if (StringUtil.isEmpty(uid)) {
            ToastUtil.getInatance(getActivity()).show(getStr(R.string.cus_uid) + getStr(R.string.not_null));
            return;
        }
        if (StringUtil.isEmpty(pwd)) {
            ToastUtil.getInatance(getActivity()).show("密码" + getStr(R.string.not_null));
            return;
        }
        if (!uid.matches(PASSWORD_REGEX)) {
            ToastUtil.getInatance(getActivity()).show(getStr(R.string.cus_uid_matches));
            return;
        }
        FHLiveSessionParams.setNetworkArea(et_network_area.getText().toString().trim()+"");
        imm.hideSoftInputFromWindow(et_id.getWindowToken(), 0);

        showPro(true);
        FHLiveHelper.getHelper().login(uid, 0,"token", new FHBusiCallBack() {
            @Override
            public void onSuccess(String str) {
                showPro(false);
                demoData.setUid(uid);
                demoData.setPwd(pwd);
                demoData.setChatMode(chatMode);
                demoData.setToken(token);

                FHLiveHelper.isLogin = true;
                FHLiveHelper.getHelper().setLiveData(demoData);
                EventBus.getDefault().post(new UiEvent("", FHLiveHelper.HIDE_LOGIN));
            }

            @Override
            public void onError(String error) {
                showPro(false);
                EventBus.getDefault().post(new UiEvent(error,FHLiveHelper.SHOW_HINT));
            }
        });
    }


    private final String ROUTE_TELLERID = "tellerid"//座席号
            ,ROUTE_ACCOUNT = "account"//机构号
            ,ROUTE_SKILL = "skill"//技能码
            ;



    private ImageView iv_setting_back
            ,iv_login_setting
            ;
    private TextView tv_submit

            ,tv_priority,tv_priority_value,tv_route_type_title
            ,tv_network,tv_video_quality,tv_chat_mode
            ,tv_rtc_channel,tv_im_channel
            ,tv_call_type,tv_server_version_value,tv_server_version
            ;
    private EditText et_url
            ,et_token,et_ext_data
            ,et_ai_account,et_ai_busi_code
            ,et_invite_code
            ,et_call_business_type
            ,et_channelid
            ,et_scenarioid
            ,et_network_area
            ;
    private RelativeLayout rl_setting;
    private void initSeeting() {

        rl_setting = mView.findViewById(R.id.rl_setting);
        rl_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        iv_setting_back = mView.findViewById(R.id.iv_setting_back);
        iv_setting_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initRoute();
                rl_setting.setVisibility(View.GONE);
                imm.hideSoftInputFromWindow(et_id.getWindowToken(), 0);

            }
        });

        tv_submit = mView.findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uptChangeParams();
                uptCallParams();
            }
        });

        et_url = mView.findViewById(R.id.et_url);

        tv_server_version_value = mView.findViewById(R.id.tv_server_version_value);

        tv_server_version = mView.findViewById(R.id.tv_server_version);
        tv_server_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeWheelType = CHANGE_SERVER_VERSION;
                showWheel(true);
            }
        });


        tv_priority_value = mView.findViewById(R.id.tv_priority_value);

        tv_priority = mView.findViewById(R.id.tv_priority);
        tv_priority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeWheelType = CHANGE_PRIORITY;
                showWheel(true);

            }
        });

        tv_network = mView.findViewById(R.id.tv_network);
        tv_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeWheelType = CHANGE_NETWORK;
                showWheel(true);
            }
        });
        tv_video_quality = mView.findViewById(R.id.tv_video_quality);
        tv_video_quality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeWheelType = CHANGE_QUALITY;
                showWheel(true);
            }
        });
        tv_chat_mode = mView.findViewById(R.id.tv_chat_mode);
        tv_chat_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeWheelType = CHANGE_CHATMODE;
                showWheel(true);
            }
        });

        tv_rtc_channel= mView.findViewById(R.id.tv_rtc_channel);
        tv_rtc_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeWheelType = CHANGE_RTC;
                showWheel(true);
            }
        });
        tv_im_channel= mView.findViewById(R.id.tv_im_channel);
        tv_im_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeWheelType = CHANGE_IM;
                showWheel(true);
            }
        });

        et_token = mView.findViewById(R.id.et_token);
        et_ext_data = mView.findViewById(R.id.et_ext_data);

        et_ai_account= mView.findViewById(R.id.et_ai_account);
        et_ai_busi_code= mView.findViewById(R.id.et_ai_busi_code);

        iv_login_setting  = mView.findViewById(R.id.iv_login_setting);
        iv_login_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initWheelList();
                rl_setting.setVisibility(View.VISIBLE);
            }
        });

        et_invite_code= mView.findViewById(R.id.et_invite_code);

        tv_call_type  = mView.findViewById(R.id.tv_call_type);
        tv_call_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeWheelType = CHANGE_CALLTYPE;
                showWheel(true);
            }
        });
        et_call_business_type = mView.findViewById(R.id.et_call_business_type);

        et_scenarioid = mView.findViewById(R.id.et_scenarioid);
        et_channelid = mView.findViewById(R.id.et_channelid);
        et_network_area= mView.findViewById(R.id.et_network_area);

    }
    private void initCheck(){

    }

    private String token = "token";
    private int chatMode = 3;
    private void uptChangeParams(){
        token = et_token.getText().toString().trim()+"";
        uptChange();
        SharedPreUtil.getInstance(getActivity()).saveKeyIntValue("changeRTC", changeRTC);
        SharedPreUtil.getInstance(getActivity()).saveKeyIntValue("changeIM", changeIM);
        SharedPreUtil.getInstance(getActivity()).saveKeyStringValue("ImIntranet", listNetWork.get(changeNetWork));
        SharedPreUtil.getInstance(getActivity()).saveKeyStringValue("changeToken", token);
        SharedPreUtil.getInstance(getActivity()).saveKeyIntValue("changeNetWork", changeNetWork);
        SharedPreUtil.getInstance(getActivity()).saveKeyIntValue("changeQuality", changeQuality);
        SharedPreUtil.getInstance(getActivity()).saveKeyIntValue("changeChatMode", changeChatMode);
        SharedPreUtil.getInstance(getActivity()).saveKeyIntValue("changeCallType", changeCallType);
        SharedPreUtil.getInstance(getActivity()).saveKeyIntValue("changeServerVersion", changeServerVersion);

    }
    private List<String> listPriority = new ArrayList<String>()
            ,listNetWork = new ArrayList<String>()
            ,listQuality = new ArrayList<String>()
            ,listChatMode = new ArrayList<String>()
            ,listRTC = new ArrayList<String>()
            ,listIM = new ArrayList<String>()
            ,listCallType = new ArrayList<String>()
            ,listServerVersion = new ArrayList<String>()
            ;
    private int changePriority = 0
            ,changeNetWork = 0
            ,changeQuality = 1
            ,changeChatMode = 0
            ,changeRTC = 0
            ,changeIM = 0
            ,changeCallType = 0
            ,changeServerVersion = 0
            ;
    private String changeWheelType ;
    private final String CHANGE_PRIORITY = "changePriority"//选择优先级
            ,CHANGE_NETWORK = "changeNetWork"//选择内外网
            ,CHANGE_QUALITY = "changeQuality"//选择分辨率
            ,CHANGE_CHATMODE = "changeChatMode"//选择音视频呼叫
            ,CHANGE_RTC = "changeRtc"//选择rtc通道
            ,CHANGE_IM = "changeIM"//选择IM通道
            ,CHANGE_CALLTYPE = "changeCallType"//选择callType通道
            ,CHANGE_SERVER_VERSION = "changeServerVersion"//选择服务器版本
            ;

    private void initWheelList(){
        listPriority.add("1");
        listPriority.add("2");
        listPriority.add("3");
        listPriority.add("4");
        listPriority.add("5");

        listNetWork.add("外网");
        listNetWork.add("内网");

        listQuality.add("360P");
        listQuality.add("540P");
        listQuality.add("720P");

        listChatMode.add("视频");
        listChatMode.add("音频");
        listChatMode.add("本端视频对端音频");

        listCallType.add("普通呼叫");
        listCallType.add("AI呼叫");
        listCallType.add("多客户呼叫");
        listCallType.add("邀请码加入");
        listCallType.add("本端视频对端音频");

        listServerVersion.add("跟随当前版本");
        listServerVersion.add("2.8.3");
        listServerVersion.add("2.8.402");
        listServerVersion.add("2.8.404");
        listServerVersion.add("2.8.405");

        changeNetWork =SharedPreUtil.getInstance(getActivity()).getKeyIntValue("changeNetWork", 0);
        changeQuality =SharedPreUtil.getInstance(getActivity()).getKeyIntValue("changeQuality", 1);
        changeChatMode =SharedPreUtil.getInstance(getActivity()).getKeyIntValue("changeChatMode", 0);
        token =SharedPreUtil.getInstance(getActivity()).getKeyStringValue("changeToken", "token");
        changeCallType =SharedPreUtil.getInstance(getActivity()).getKeyIntValue("changeCallType", 0);
        changeServerVersion =SharedPreUtil.getInstance(getActivity()).getKeyIntValue("changeServerVersion", 0);

        if(changeNetWork>=listNetWork.size())
            changeNetWork = 0;
        if(changeQuality>=listQuality.size())
            changeQuality = 1;
        if(changeChatMode>=listChatMode.size())
            changeChatMode = 0;
        if(changeCallType>=listCallType.size())
            changeCallType = 0;
        if(changeServerVersion>=listServerVersion.size())
            changeServerVersion = 0;
        tv_network.setText(listNetWork.get(changeNetWork));
        tv_video_quality.setText(listQuality.get(changeQuality));
        tv_chat_mode.setText(listChatMode.get(changeChatMode));
        et_token.setText(token);
        tv_call_type.setText(listCallType.get(changeCallType));
        tv_server_version_value.setText(listServerVersion.get(changeServerVersion));


        listRTC.clear();
        String baseRtc = BuildConfig.BASE_RTC+"";
        if(baseRtc.equals("N")){
            listRTC.add(channel_N);
            listRTC.add(channel_T1);
            listRTC.add(channel_T2);
            listRTC.add(channel_Z);
        }else if(baseRtc.equals("N1")){
            listRTC.add(channel_N);
        }else if(baseRtc.equals("T")){
            listRTC.add(channel_T1);
            listRTC.add(channel_Z);
        }else if(baseRtc.equals("T1")){
            listRTC.add(channel_T1);
            listRTC.add(channel_T2);
        }else if(baseRtc.equals("Z")){
            listRTC.add(channel_Z);
        }

        listIM.clear();
        if(baseRtc.equals("N")){
            listIM.add(channel_N);
            listIM.add(channel_T1);
            listIM.add(channel_T2);
        }else if(baseRtc.equals("N1")){
            listIM.add(channel_N);
        }else{
            listIM.add(channel_T1);
            listIM.add(channel_T2);
        }
        changeRTC =SharedPreUtil.getInstance(getActivity()).getKeyIntValue("changeRTC", 0);
        changeIM =SharedPreUtil.getInstance(getActivity()).getKeyIntValue("changeIM", 0);
        if(changeIM>=listIM.size())
            changeIM = 0;
        if(changeRTC>=listRTC.size())
            changeRTC = 0;
        tv_rtc_channel.setText(listRTC.get(changeRTC));
        tv_im_channel.setText(listIM.get(changeIM));
        uptChange();

    }
    private String callType = FHLiveMacro.FH_LIVE_CALL_TYPE_NORMAL;
    private void uptChange() {
        //chatmode
        if(listChatMode.get(changeChatMode).equals("音频")){
            chatMode = 1;
        }else if(listChatMode.get(changeChatMode).equals("视频")){
            chatMode =3;
        }else {
            chatMode =4;
        }
        demoData.setChatMode(chatMode);

        //分辨率
        if(listQuality.get(changeQuality).equals("360P")){
            FHTellerParams.setVideoQuality(FHVideoParams.QUALITY_360P);
        }else if(listQuality.get(changeQuality).equals("720P")){
            FHTellerParams.setVideoQuality(FHVideoParams.QUALITY_720P);
        }else {
            FHTellerParams.setVideoQuality(FHVideoParams.QUALITY_540P);
        }
        //内外网
        if(listNetWork.get(changeNetWork).equals("内网")){
            FHVideoParams.setImIntranet(true);
        }else {
            FHVideoParams.setImIntranet(false);
        }
        switch (listCallType.get(changeCallType)){
            case "AI呼叫":
                callType = FHLiveMacro.FH_LIVE_CALL_TYPE_AI;
                break;
            case "多客户呼叫":
                callType = FHLiveMacro.FH_LIVE_CALL_TYPE_MORE_CUSTOM;
                break;
            case "邀请码加入":
                callType = FHLiveMacro.FH_LIVE_CALL_TYPE_INVITE_CODE;
                break;
            case "本端视频对端音频":
                callType = FHLiveMacro.FH_LIVE_CALL_TYPE_LOCALPREVIEW;
                break;
            default:
                callType = FHLiveMacro.FH_LIVE_CALL_TYPE_NORMAL;
                break;

        }
        switch (listServerVersion.get(changeServerVersion)){
            case "跟随当前版本":
                FHLiveClient.getInstance().setServiceVersion(-1);
                break;
            case "2.8.3":
                FHLiveClient.getInstance().setServiceVersion(FHLiveMacro.V_2_8_3);
                break;
            case "2.8.402":
                FHLiveClient.getInstance().setServiceVersion(FHLiveMacro.V_2_8_4_02);
                break;
            case "2.8.404":
                FHLiveClient.getInstance().setServiceVersion(FHLiveMacro.V_2_8_4_04);
                break;
            case "2.8.405":
                FHLiveClient.getInstance().setServiceVersion(FHLiveMacro.V_2_8_4_05);
                break;
            default:
                FHLiveClient.getInstance().setServiceVersion(-1);
                break;

        }
        //token
        demoData.setToken(token);
        uptIMChannel(listIM.get(changeIM));
        uptRtcChannel(listRTC.get(changeRTC));
    }

    private final String channel_T1 = "T1"
            ,channel_T2 = "T2"
            ,channel_N = "N"
            ,channel_Z = "Z"
            ;
    private void uptRtcChannel(String type){
        switch (type){
            case channel_T1:
                FHLiveSessionParams.setVideoChannelType(FHLiveMacro.FH_LIVE_CHANNEL_TYPE_T1);
                break;
            case channel_T2:
                FHLiveSessionParams.setVideoChannelType(FHLiveMacro.FH_LIVE_CHANNEL_TYPE_T2);
                break;
            case channel_N:
                FHLiveSessionParams.setVideoChannelType(FHLiveMacro.FH_LIVE_CHANNEL_TYPE_N);
                break;
            case channel_Z:
                FHLiveSessionParams.setVideoChannelType(FHLiveMacro.FH_LIVE_CHANNEL_TYPE_J);
                break;
        }
    }
    private void uptIMChannel(String type){
        FHVideoParams.setIsPrivateIM(false);
        switch (type){
            case channel_T1:
                FHLiveSessionParams.setImChannelType(FHLiveMacro.FH_LIVE_CHANNEL_TYPE_T1);
                break;
            case channel_T2:
                FHLiveSessionParams.setImChannelType(FHLiveMacro.FH_LIVE_CHANNEL_TYPE_T2);
                break;
            case channel_N:
                FHLiveSessionParams.setImChannelType(FHLiveMacro.FH_LIVE_CHANNEL_TYPE_N);
                break;

        }
    }

    private LoopView wheel_picker;
    private ImageView iv_wheel_bg;
    private RelativeLayout rl_wheel;
    private TextView tv_setting_bot_cancel,tv_setting_bot_submit,tv_wheel_title;
    private void initWheel(){
        iv_wheel_bg = mView.findViewById(R.id.iv_wheel_bg);
        iv_wheel_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        tv_wheel_title= mView.findViewById(R.id.tv_wheel_title);
        rl_wheel = mView.findViewById(R.id.rl_wheel);
        tv_setting_bot_cancel = mView.findViewById(R.id.tv_setting_bot_cancel);
        tv_setting_bot_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheel_picker.setCurrentPosition(position);
                showWheel(false);
            }
        });
        tv_setting_bot_submit = mView.findViewById(R.id.tv_setting_bot_submit);
        tv_setting_bot_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = wheel_picker.getSelectedItem();
                showWheel(false);
            }
        });
        wheel_picker = mView.findViewById(R.id.wheel_picker);
        wheel_picker.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
            }
        });

    }

    private int position = 0;
    private void showWheel(boolean show){
        if(show){

            String wheelTitle ="请选择优先级";
            List<String> data= null;
            switch (changeWheelType){
                case CHANGE_PRIORITY:
                    wheelTitle = "请选择优先级";
                    data = listPriority;
                    position = changePriority;
                    break;
                case CHANGE_NETWORK:
                    wheelTitle = "请选择内外网";
                    data = listNetWork;
                    position = changeNetWork;
                    break;
                case CHANGE_QUALITY:
                    wheelTitle = "请选择分辨率";
                    data = listQuality;
                    position = changeQuality;
                    break;
                case CHANGE_CHATMODE:
                    wheelTitle = "请选择音视频";
                    data = listChatMode;
                    position = changeChatMode;
                    break;
                case CHANGE_RTC:
                    wheelTitle = "请选择音视频通道";
                    data = listRTC;
                    position = changeRTC;
                    break;
                case CHANGE_IM:
                    wheelTitle = "请选择IM通道";
                    data = listIM;
                    position = changeIM;
                    break;
                case CHANGE_CALLTYPE:
                    wheelTitle = "请选择呼叫类型";
                    data = listCallType;
                    position = changeCallType;
                    break;
                case CHANGE_SERVER_VERSION:
                    wheelTitle = "请选择服务器版本";
                    data = listServerVersion;
                    position = changeServerVersion;
                    break;
            }
            tv_wheel_title.setText(wheelTitle);
            wheel_picker.setItems(data);
            wheel_picker.setInitPosition(position);
            wheel_picker.setCurrentPosition(position);

            iv_wheel_bg.setVisibility(View.VISIBLE);
            rl_wheel.setVisibility(View.VISIBLE);
        }else {
            switch (changeWheelType){
                case CHANGE_PRIORITY:
                    changePriority = position;
                    tv_priority_value.setText(listPriority.get(changePriority));
                    break;
                case CHANGE_NETWORK:
                    changeNetWork =position;
                    tv_network.setText(listNetWork.get(changeNetWork));
                    break;
                case CHANGE_QUALITY:
                    changeQuality = position;
                    tv_video_quality.setText(listQuality.get(changeQuality));

                    break;
                case CHANGE_CHATMODE:
                    changeChatMode = position;
                    tv_chat_mode.setText(listChatMode.get(changeChatMode));

                    break;
                case CHANGE_RTC:
                    changeRTC = position;
                    tv_rtc_channel.setText(listRTC.get(changeRTC));

                    break;
                case CHANGE_IM:
                    changeIM = position;
                    tv_im_channel.setText(listIM.get(changeIM));

                    break;
                case CHANGE_CALLTYPE:
                    changeCallType = position;
                    tv_call_type.setText(listCallType.get(changeCallType));

                    break;
                case CHANGE_SERVER_VERSION:
                    changeServerVersion = position;
                    tv_server_version_value.setText(listServerVersion.get(changeServerVersion));

                    break;
            }
            iv_wheel_bg.setVisibility(View.GONE);
            rl_wheel.setVisibility(View.GONE);
        }

    }

    //设置
    private static final String PASSWORD_REGEX = "[A-Z0-9a-z@._-]+";
    private String getStr(int id) {
        return getResources().getString(id);
    }
    private void uptCallParams() {

        AppCallParams params = new AppCallParams();
        //路由
        String route = "";

        route = et_route_account.getText().toString().trim()+"";
        if(StringUtil.isEmpty(route)){
            route = "";
        }else {
            linkType = "0";
        }
        params.setAccoutType(route);
        route = et_route_skill.getText().toString().trim()+"";
        if(StringUtil.isEmpty(route)){
            route = "";
        }else {
            linkType = "2";
        }
        params.setSkill(route);
        route = et_route_tellerid.getText().toString().trim()+"";
        if(StringUtil.isEmpty(route)){
            route = "";
        }else {
            linkType = "3";
        }
        params.setTellerid(route);
        if(StringUtil.isEmpty(params.getAccoutType())
                &&StringUtil.isEmpty(params.getSkill())
                &&StringUtil.isEmpty(params.getTellerid())
        ){
            linkType = "0";
        }
        params.setLinktype(linkType);

        params.setAiAccountType(et_ai_account.getText().toString().trim()+"");
        //优先级
        params.setQueueRank(changePriority+1);
        demoData.setCallType(callType);
        callParams = params;
        demoData.setParams(GsonUtil.toJson(params));
        CallExtData extData = new CallExtData();

        extData.setBusiId(et_ai_busi_code.getText().toString().trim()+"");
        demoData.setExtData(et_ext_data.getText().toString().trim());
//        demoData.setExtData(GsonUtil.toJson(extData)+"");

        FHLiveHelper.getHelper().setCallParams(params);
        //服务器地址
        String url = et_url.getText().toString().trim();
        if (StringUtil.isEmpty(url)) {
            ToastUtil.getInatance(getActivity()).show(getStr(R.string.server_url) + getStr(R.string.not_null));
            return;
        }
        callParams.setScenarioId(et_scenarioid.getText().toString().trim()+"");

        callParams.setChannelId(et_channelid.getText().toString().trim()+"");
        SharedPreUtil.getInstance(getActivity()).saveKeyStringValue("demo_url", url);

        //更换服务器地址
        com.fhvideo.bean.URLCons.setServer(url);
        if(!oldURL.equals(url)){
            et_id.setText("");
            et_pwd.setText("");
            demoData.setUid("");
            demoData.setPwd("");
            FHLiveHelper.isLogin = false;
            ToastUtil.getInatance(getActivity()).show("服务器地址已改变，请重新登录！");
        }
        oldURL = url;
        demoData.setInviteCode(et_invite_code.getText().toString().trim());
        demoData.setBusinessType(et_call_business_type.getText().toString().trim());
        FHLiveHelper.getHelper().setLiveData(demoData);
        rl_setting.setVisibility(View.GONE);
        imm.hideSoftInputFromWindow(et_id.getWindowToken(), 0);
    }


    private void initParams() {
        //url
        String url = SharedPreUtil.getInstance(getActivity()).getKeyStringValue("demo_url", BuildConfig.BASE_URL);
        et_url.setText(url);
        oldURL = et_url.getText().toString().trim();
        demoData = FHLiveHelper.getHelper().getLiveData();
        if (demoData != null) {
            if(StringUtil.isEmpty(demoData.getExtData())){
                et_ext_data.setText("");
                demoData.setExtData("");
            }else {
                et_ext_data.setText(demoData.getExtData()+"");
                CallExtData callExtData = GsonUtil.fromJson(demoData.getExtData(),CallExtData.class);
                if(callExtData != null && StringUtil.isEmpty(callExtData.getBusiId())){
                    et_ai_busi_code.setText(callExtData.getBusiId());
                }
            }
            et_id.setText(demoData.getUid());
            et_pwd.setText(demoData.getPwd());
             callParams = FHLiveHelper.getHelper().getCallParams();
            if(callParams == null)
                return;
            //路由
            initRoute();
            rl_setting.setVisibility(View.GONE);
            if(callParams != null &&!StringUtil.isEmpty(callParams.getAiAccountType()))
                et_ai_account.setText(callParams.getAiAccountType()+"");
            //优先级
            if(callParams.getQueueRank()>0 && callParams.getQueueRank()<=5){
                changePriority = callParams.getQueueRank()-1;
            }else {
                changePriority = 0;
            }
            callParams.setQueueRank(changePriority+1);
            tv_priority_value.setText(callParams.getQueueRank()+"");

        }else {
            demoData = new LiveData();
        }

    }

    private void initRoute() {
        linkType = "0";
        if(callParams != null && !StringUtil.isEmpty(callParams.getLinktype()))
            linkType =callParams.getLinktype();
        if(callParams != null &&!StringUtil.isEmpty(callParams.getAccoutType())) {
            et_route_account.setText(callParams.getAccoutType() + "");

        }
        if(callParams != null &&!StringUtil.isEmpty(callParams.getSkill()))
            et_route_skill.setText(callParams.getSkill()+"");
        if(callParams != null &&!StringUtil.isEmpty(callParams.getTellerid()))
            et_route_tellerid.setText(callParams.getTellerid()+"");


    }

    private void showPro(boolean show){
        EventBus.getDefault().post(new UiEvent(show,FHLiveHelper.SHOW_PROGRESS_BAR));
    }

}
