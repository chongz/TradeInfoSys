/*
 * Created by zhangchong on 4/19/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade;

import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;

public class ScanBarcodeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);
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
