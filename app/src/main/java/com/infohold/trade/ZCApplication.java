/*
 * Created by zhangchong on 4/19/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade;

import android.app.Application;
import android.content.Context;

public class ZCApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
