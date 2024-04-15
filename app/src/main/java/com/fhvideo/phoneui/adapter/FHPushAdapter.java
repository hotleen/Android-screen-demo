package com.fhvideo.phoneui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fhvideo.FHResource;
import com.fhvideo.bank.bean.PushFile;
import com.fhvideo.bean.UiEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 推送
 */

public class FHPushAdapter extends BaseAdapter {
    private Context activity;
    private List<PushFile> msgs;

    public FHPushAdapter(Context context, List<PushFile> list) {
        activity = context;
        msgs = list;
    }

    public void refresh(List<PushFile> list) {
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
            convertView = LayoutInflater.from(activity).inflate(FHResource.getInstance().getId(activity,"layout","layout_fh_push_item"), viewGroup, false);
            viewHolder.tv_push_item = convertView.findViewById(FHResource.getInstance().getId(activity,"id","tv_push_item"));
            viewHolder.iv_push_item =  convertView.findViewById(FHResource.getInstance().getId(activity,"id","iv_push_item"));
            viewHolder.rl_push_item = convertView.findViewById(FHResource.getInstance().getId(activity,"id","rl_push_item"));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final PushFile msg = msgs.get(i);
        if (msg != null) {
            Glide.with(activity.getApplicationContext())
                    .load(msg.getPic())
                    .into(viewHolder.iv_push_item);
            viewHolder.tv_push_item.setText(msg.getName());
            viewHolder.rl_push_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new UiEvent(msg.getUrl(), true, "show_push"));
                }
            });
        }
        return convertView;
    }

    private class ViewHolder {
        TextView tv_push_item;
        ImageView iv_push_item;
        RelativeLayout rl_push_item;
    }
}
