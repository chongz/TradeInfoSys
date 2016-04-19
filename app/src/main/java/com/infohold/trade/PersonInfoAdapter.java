/*
 * Created by zhangchong on 4/19/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.infohold.trade.config.Constant;
import com.infohold.trade.model.PersonInfoData;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PersonInfoAdapter extends ArrayAdapter<PersonInfoData> {

    private int resourceId;

    public PersonInfoAdapter(Context context, int resource, List<PersonInfoData> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        if (position == 0) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.activity_person_info_top, null);
            PersonInfoData infoData = getItem(position);
            ImageView portraitView = (ImageView) view.findViewById(R.id.person_info_image_view);
            Glide.with(getContext()).load(Constant.ServicePicAddress + infoData.getValue()).placeholder(R.drawable.citystore).into(portraitView);
            return view;
        }

        ViewHolder holder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(this.resourceId, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }else{
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        PersonInfoData infoData = getItem(position);
        holder.nameView.setText(infoData.getName());
        holder.valueView.setText(infoData.getValue());

        return view;

    }

    static class ViewHolder {
        @Bind(R.id.person_info_item_key)
        TextView nameView;

        @Bind(R.id.person_info_item_value)
        TextView valueView;

        public ViewHolder(View view) {
            ButterKnife.bind(this,view);
        }
    }
}
