package com.fhvideo.phone.fragment;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fhvideo.FHLiveClient;
import com.fhvideo.bean.UiEvent;
import com.fhvideo.fhcommon.FHBusiCallBack;
import com.fhvideo.phone.FHLiveHelper;
import com.fhvideo.phone.R;
import com.fhvideo.phone.adapter.HomeAdapter;
import com.fhvideo.phone.adapter.OnAdapterClick;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends FHBaseFragment {
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
        return R.layout.fragment_home;
    }

    /**
     * 初始化布局
     */
    @Override
    public void initView() {
        initTop();
        initList();
        initBot();
        initHint();
        initTestView();
    }
    private RelativeLayout test_view;

    private void initTestView() {
        test_view =mView.findViewById(R.id.test_view);
    }


    /**
     * 初始化事件和数据
     */
    @Override
    public void initEventAndData() {

    }


    @Override
    public void mainEvent(UiEvent event) {
        super.mainEvent(event);
        switch (event.getType()) {
            case FHLiveHelper.HIDE_LOGIN:
                if (FHLiveHelper.isLogin) {
                    iv_logout.setVisibility(View.VISIBLE);
                } else {
                    iv_logout.setVisibility(View.GONE);
                }
                break;

        }
    }

    private TextView tv_home_title;
    private ImageView iv_logout;

    private void initTop() {
        tv_home_title = mView.findViewById(R.id.tv_home_title);
        iv_logout = mView.findViewById(R.id.iv_logout);
        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHint("确定要退出当前账号吗？");
            }
        });
        tv_home_title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                test_view.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    private RelativeLayout fh_rl_hint;
    private TextView hintmsg, hint_right, tv_hint_sub, hint_left;

    private void initHint() {
        fh_rl_hint = mView.findViewById(R.id.fh_rl_hint);
        hintmsg = mView.findViewById(R.id.tv_hint_msg);
        hint_left = mView.findViewById(R.id.tv_hint_cancel);
        hint_right = mView.findViewById(R.id.tv_hint_confirm);
        tv_hint_sub = mView.findViewById(R.id.tv_hint_sub);
        tv_hint_sub.setVisibility(View.VISIBLE);
        hint_left.setVisibility(View.GONE);
        hint_right.setVisibility(View.GONE);
        tv_hint_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fh_rl_hint.setVisibility(View.GONE);
            }
        });
    }

    private void showHint(final String str) {
        hintmsg.setText(str);
        fh_rl_hint.setVisibility(View.VISIBLE);
        tv_hint_sub.setVisibility(View.GONE);
        hint_left.setVisibility(View.VISIBLE);
        hint_right.setVisibility(View.VISIBLE);
        hint_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fh_rl_hint.setVisibility(View.GONE);
            }
        });
        hint_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fh_rl_hint.setVisibility(View.GONE);
                if (str.equals("确定要退出当前账号吗？")) {
                    iv_logout.setVisibility(View.GONE);
                    FHLiveHelper.getHelper().logout();
                    EventBus.getDefault().post(new UiEvent("", FHLiveHelper.HIDE_LOGIN));
                } else if (str.equals("确定呼叫座席？")) {
                    FHLiveHelper.getHelper().callTeller(getActivity());
                } else if (str.equals("是否进去正在进行的会话？")){
                    //呼叫座席
                    FHLiveHelper.getHelper().callTeller(getActivity());
                }
            }
        });
        tv_hint_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fh_rl_hint.setVisibility(View.GONE);

            }
        });
    }

    private RelativeLayout rl_bot_home, rl_bot_my;
    private ImageView iv_bot_home, iv_bot_my;
    private TextView tv_bot_home, tv_bot_my;

    private void initBot() {
        rl_bot_home = mView.findViewById(R.id.rl_bot_home);
        iv_bot_home = mView.findViewById(R.id.iv_bot_home);
        tv_bot_home = mView.findViewById(R.id.tv_bot_home);

        Glide.with(getActivity())
                .load(R.mipmap.ic_fh_bot_home_b)
                .into(iv_bot_home);
        tv_bot_home.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_333));

        rl_bot_my = mView.findViewById(R.id.rl_bot_my);
        rl_bot_my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new UiEvent("", FHLiveHelper.SHOW_MY));
            }
        });
        iv_bot_my = mView.findViewById(R.id.iv_bot_my);
        tv_bot_my = mView.findViewById(R.id.tv_bot_my);
    }

    private RecyclerView listView;
    private HomeAdapter adapter;
    private List<Integer> list = new ArrayList<Integer>();

    private void initList() {
        list.add(R.mipmap.ic_fh_home_1);
        list.add(R.mipmap.ic_fh_home_2);
        list.add(R.mipmap.ic_fh_home_3);
        adapter = new HomeAdapter(list, new OnAdapterClick() {
            @Override
            public void onItemClick(String callType, int index) {

                if (index == 1) {
                    FHLiveHelper.getHelper().setCallType(FHLiveHelper.CALL_TYPE_AI);
                }
                getCurrentSession();
            }
        });
        listView = mView.findViewById(R.id.list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(adapter);
    }

    private void getCurrentSession(){
        FHLiveClient.getInstance().getCurrentSession(new FHBusiCallBack() {
            @Override
            public void onSuccess(String s) {
                if (s.equals("true")){
                    showHint("是否进去正在进行的会话？");
                }else {
                    FHLiveHelper.getHelper().callTeller(getActivity());
                }
            }

            @Override
            public void onError(String s) {

            }
        });
    }
}
