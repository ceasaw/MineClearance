package com.hellotheworld.mineclearance.Common.Rank.Adapter;


import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hellotheworld.mineclearance.R;
import com.hellotheworld.mineclearance.Common.Rank.Bean.RankBean;

import java.util.List;

public class RankViewAdapter extends BaseAdapter {
    //声明一个链表和Context对象
    private List<RankBean> mList;
    private Context mContext;

    public RankViewAdapter(List<RankBean> mList, Context mContext) {
        this.mList = mList;//todo: 此处需要排序，防止传入bug数据
        this.mContext = mContext;
    }

    @Override
    //获取数据的数量
    public int getCount() {
        return mList.size();
    }

    @Override
    //获取数据的内容
    public RankBean getItem(int position) {
        return mList.get(position);
    }

    @Override
    //获取数据的id
    public long getItemId(int position) {
        return position;

    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //加载一个适配器界面
        convertView = LayoutInflater.from(mContext).inflate(R.layout.rank_item, parent, false);
        //实例化元件
        TextView name = convertView.findViewById(R.id.rank_item_name);
        TextView time = convertView.findViewById(R.id.rank_item_time);
        TextView rank = convertView.findViewById(R.id.rank_item_rank);

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"font/MFBanHei_Noncommercial-Regular.otf");
        name.setTypeface(typeface);
        time.setTypeface(typeface);
        rank.setTypeface(typeface);
        //元件获取数据
        name.setText(mList.get(position).getName());
        time.setText(String.valueOf(mList.get(position).getTime())+"s");
        rank.setText(String.valueOf(position + 1));

        if(position == 0){
            rank.setTextColor(mContext.getResources().getColor(R.color.color_gold));
        }else if(position == 1){
            rank.setTextColor(mContext.getResources().getColor(R.color.color_silver));
        }else if(position == 2){
            rank.setTextColor(mContext.getResources().getColor(R.color.color_copper));
        }

        return convertView;
    }
}
