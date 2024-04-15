package com.fhvideo.phone.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fhvideo.phone.R;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private List<Integer> paths = new ArrayList<Integer>();
    private OnAdapterClick lisenter;
    public HomeAdapter(List<Integer> list,OnAdapterClick click){
        paths = list;
        lisenter= click;
    }
    public void refresh(List<Integer> list){
        paths = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.layout_item_home
                ,viewGroup,false);
        ViewHolder holder = new ViewHolder(view,viewGroup.getContext());
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if(paths.size()<=i)
            return;
        int path = paths.get(i);
        if(path<=0)
            return;
        Glide.with(viewHolder.getmContext())
                .load(paths.get(i))
                .into(viewHolder.iv_img);
       // viewHolder.iv_img.setBackgroundResource(paths.get(i));
        if(i==0){
            viewHolder.iv_white.setVisibility(View.VISIBLE);
        }else {
            viewHolder.iv_white.setVisibility(View.GONE);

        }
        if(i==1){
            viewHolder.iv_ai.setVisibility(View.VISIBLE);
            viewHolder.iv_ai.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lisenter.onItemClick("",1);
                }
            });
            viewHolder.iv_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lisenter.onItemClick("",1);
                }
            });
        }else {
            viewHolder.iv_ai.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_img,iv_white,iv_ai;
        private Context mContext;
        public Context getmContext(){
            return mContext;
        }
        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            mContext = context;
            iv_img = itemView.findViewById(R.id.iv_img);
            iv_white= itemView.findViewById(R.id.iv_white);
            iv_ai =  itemView.findViewById(R.id.iv_ai);
        }
    }
}
