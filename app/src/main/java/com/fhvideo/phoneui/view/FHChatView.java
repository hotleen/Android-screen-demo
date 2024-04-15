package com.fhvideo.phoneui.view;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fhvideo.bean.GsonUtil;
import com.fhvideo.fhcommon.bean.ChatMsg;
import com.fhvideo.fhcommon.utils.StringUtil;
import com.fhvideo.phoneui.adapter.FHChatAdapter;
import com.fhvideo.phoneui.busi.FHVideoListener;
import com.fhvideo.phoneui.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 聊天区域
 */
public class FHChatView extends FHBaseView {
    private static FHChatView instance;
    public static FHChatView getInstance(){
        if(instance == null)
            instance = new FHChatView();
        return instance;
    }

    public void setVideoListener(FHVideoListener listener){
        this.listener = listener;
    }

    private ImageView iv_chat_close;
    private TextView tv_chat_hint,tv_chat_btn,tv_chat_send;
    private ListView lv_chat;
    private RelativeLayout rl_chat_et;
    private EditText et_chat;
    public RelativeLayout rl_chat;
    private FHVideoListener listener;

    private List<ChatMsg> msgs ;
    private FHChatAdapter chatAdapter;
    private InputMethodManager imm;

    @Override
    public void init(Activity context, View view) {
        super.init(context, view);
        imm = (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);
        initRlChat(view);
        initRlChatET(view);
        initList(context);
    }

    private void initRlChat(View view) {
        rl_chat = getView("rl_chat", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        rl_chat.setZ(110);

        iv_chat_close = getView("iv_chat_close");
        tv_chat_hint = getView("tv_chat_hint");
        tv_chat_btn = getView("tv_chat_btn");
        lv_chat = getView("lv_chat");
        tv_chat_btn.setOnClickListener(this);
        iv_chat_close.setOnClickListener(this);
    }

    private void initList(Activity context) {
        msgs = new ArrayList<ChatMsg>();
        chatAdapter = new FHChatAdapter(context,msgs);
        lv_chat.setAdapter(chatAdapter);
    }

    private void initRlChatET(View view) {
        rl_chat_et = getView("rl_chat_et");
        rl_chat_et.setZ(110);
        et_chat = getView("et_chat");
        tv_chat_send = getView("tv_chat_send");
        tv_chat_send.setOnClickListener(this);

        et_chat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if(StringUtil.isEmpty(editable.toString())){
                    tv_chat_send.setText(getResString("fh_back"));
                }else {
                    tv_chat_send.setText(getResString("fh_send"));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == tv_chat_btn.getId()){
            if (rl_chat_et.getVisibility() != View.VISIBLE) {
                rl_chat_et.setVisibility(View.VISIBLE);
                tv_chat_btn.setVisibility(View.GONE);
                et_chat.setFocusable(true);
                et_chat.setFocusableInTouchMode(true);
                et_chat.requestFocus();
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }else if(v.getId() == iv_chat_close.getId()){
            onClickChat();
        }else if(v.getId() == tv_chat_send.getId()){

            rl_chat_et.setVisibility(View.GONE);
            tv_chat_btn.setVisibility(View.VISIBLE);
            imm.hideSoftInputFromWindow(et_chat.getWindowToken(), 0);
            if (tv_chat_send.getText().toString().equals(getResString("fh_send"))) {
                if(!et_chat.getText().toString().trim().matches(PASSWORD_REGEX)) {
                    ToastUtil.getInatance(mContext).show("请去除文字聊天中的表情、颜文字等特殊字符！");
                    return;
                }
                tv_chat_hint.setVisibility(View.GONE);

                //发送聊天消息
                listener.sendMsg(et_chat.getText().toString().trim()+"");
                ChatMsg chatMsg = new ChatMsg();
                chatMsg.setType(0);
                chatMsg.setTime(new Date().getTime());
                chatMsg.setMsg(et_chat.getText().toString().trim());
                msgs.add(chatMsg);
                et_chat.setText("");
                chatAdapter.refresh(msgs);
                lv_chat.setSelection(msgs.size()-1);
            }
        }

    }

    private static final String PASSWORD_REGEX = "[\\u4e00-\\u9fa5A-Z0-9a-z! @#$%^&*.~/\\{\\}|()'\"?><,.`\\+-=_\\[\\]:;！@#￥%&*（）{}，。/？：；“”、|~～]+";




    public void onClickChat() {
        if(!isInit)
            return;
        if(rl_chat.getVisibility() == View.VISIBLE){
            hidden();
        }else {
            imm.hideSoftInputFromWindow(et_chat.getWindowToken(), 0);
            rl_chat_et.setVisibility(View.GONE);
            tv_chat_btn.setVisibility(View.VISIBLE);
            rl_chat.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void release() {
        super.release();
        instance = null;
    }

    @Override
    public void hidden() {//隐藏聊天区域
        if(!isInit)
            return;
        imm.hideSoftInputFromWindow(et_chat.getWindowToken(), 0);
        rl_chat_et.setVisibility(View.GONE);
        rl_chat.setVisibility(View.GONE);
        tv_chat_btn.setVisibility(View.VISIBLE);

    }

    public void hideSoftInputFromWindow() {//隐藏软键盘
        if(!isInit)
            return;
        imm.hideSoftInputFromWindow(et_chat.getWindowToken(), 0);
    }

    public void onNewMsg(String data){//新消息
        if(!isInit)
            return;
        tv_chat_hint.setVisibility(View.GONE);
        ChatMsg chatMsg = GsonUtil.fromJson(data, ChatMsg.class);
        if(chatMsg != null){
            chatMsg.setType(1);
            msgs.add(chatMsg);
            chatAdapter.refresh(msgs);
            lv_chat.setSelection(msgs.size()-1);
        }

    }
}