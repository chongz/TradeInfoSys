/*
 * Created by zhangchong on 4/19/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class ZCWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (Uri.parse(url).getHost().startsWith("http://ourdomain")) {
            return false;
        }

        return true;
    }
}
