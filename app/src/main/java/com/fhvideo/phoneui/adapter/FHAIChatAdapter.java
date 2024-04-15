package com.fhvideo.phoneui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fhvideo.FHResource;
import com.fhvideo.phoneui.FHAiMsg;

import java.util.List;

/**
 * 聊天
 */

public class FHAIChatAdapter extends BaseAdapter {
    private Activity activity;
    private List<FHAiMsg> msgs;
    public FHAIChatAdapter(Activity context, List<FHAiMsg> list){
        activity = context;
        msgs = list;
    }

    public void refresh(List<FHAiMsg> list){
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
            convertView = LayoutInflater.from(activity).inflate(FHResource.getInstance().getId(activity,"layout","layout_fh_ai_chat_item"), viewGroup, false);
            viewHolder.tv_chat_content = (TextView) convertView.findViewById(FHResource.getInstance().getId(activity,"id","tv_ai_chat_content"));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FHAiMsg msg = msgs.get(i);
        if(msg != null){
            viewHolder.tv_chat_content.setText(msg.getMsg());
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    private class ViewHolder{
        TextView tv_chat_content;
    }
}