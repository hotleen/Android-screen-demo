package com.fhvideo.phoneui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fhvideo.FHResource;
import com.fhvideo.bank.utils.DateUtil;
import com.fhvideo.fhcommon.bean.ChatMsg;

import java.util.List;

/**
 * 聊天
 */

public class FHChatAdapter extends BaseAdapter {
    private Activity activity;
    private List<ChatMsg> msgs;
    public FHChatAdapter(Activity context, List<ChatMsg> list){
        activity = context;
        msgs = list;
    }

    public void refresh(List<ChatMsg> list){
        msgs = list;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return msgs.size();
    }

    @Override
    public Object getItem(int i) {
        return msgs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if(getItemViewType(i)==0){
                convertView = LayoutInflater.from(activity).inflate(FHResource.getInstance().getId(activity,"layout","layout_fh_chat_right"), viewGroup, false);
            }else {
                convertView = LayoutInflater.from(activity).inflate(FHResource.getInstance().getId(activity,"layout","layout_fh_chat_left"), viewGroup, false);
            }
            viewHolder.tv_chat_content = (TextView) convertView.findViewById(FHResource.getInstance().getId(activity,"id","tv_chat_content"));
            viewHolder.tv_chat_time = (TextView) convertView.findViewById(FHResource.getInstance().getId(activity,"id","tv_chat_time"));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ChatMsg msg = msgs.get(i);
        if(msg != null){
            viewHolder.tv_chat_content.setText(msg.getMsg());
            viewHolder.tv_chat_time.setText(DateUtil.formatTime1(msg.getTime()));
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(msgs.get(position).getType()==0){
            return 0;
        }else{
            return 1;
        }
    }

    private class ViewHolder{
        TextView tv_chat_content;
        TextView tv_chat_time;
    }
}