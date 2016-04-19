/*
 * Created by zhangchong on 4/19/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.infohold.trade.config.Constant;
import com.infohold.trade.model.Bulletin;
import com.infohold.trade.util.ResourceManager;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BulletinAdapter extends ArrayAdapter<Bulletin> {

    private int resourceId;

    public BulletinAdapter(Context context, int resource, List<Bulletin> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view;
        final Bulletin bulletin = getItem(position);
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(this.resourceId, null);
            viewHolder = new ViewHolder(view);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        Glide.with(getContext()).load(Constant.ServicePicAddress + bulletin.getActivityPic()).placeholder(R.drawable.citystore).into(viewHolder.imageView);
        viewHolder.textView.setText(bulletin.getActivityName());
        viewHolder.btn.setText("参加活动");
        viewHolder.btn.setFocusable(false);

        viewHolder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!((MainActivity)getContext()).shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                    }

                    ((MainActivity)getContext()).requestPermissions(new String[]{android.Manifest.permission.CAMERA},Constant.REQUEST_CODE_ASK_PERMISSIONS_FROM_ADAPTER);
                }

                ResourceManager.getInstance().setSelectedBulletin(bulletin);
            }

        });


        return view;

    }

    static class ViewHolder {
        @Bind(R.id.bulletin_item_imageView)
        ImageView imageView;

        @Bind(R.id.bulletin_item_textView)
        TextView textView;

        @Bind(R.id.bulletin_item_button)
        Button btn;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
