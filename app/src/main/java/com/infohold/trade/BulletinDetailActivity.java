/*
 * Created by zhangchong on 4/19/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;

import com.infohold.trade.config.Constant;
import com.infohold.trade.model.Bulletin;
import com.infohold.trade.view.ProgressWebView;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BulletinDetailActivity extends BaseActivity {


    private static final String TAG = BulletinDetailActivity.class.getName();

    @Bind(R.id.bulletin_detail_web_view)
    ProgressWebView webView;

    @Bind(R.id.toolbar_common)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bulletin bulletin =  intent.getParcelableExtra("data");

        String title = getString(R.string.action_bulletin_detail);
        if (bulletin != null) title = bulletin.getActivityName();

        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BulletinDetailActivity.this.finish();
            }
        });

        webView.loadUrl(Constant.ServiceBulletinDetail + bulletin.getActivityId());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new ZCWebViewClient());

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
