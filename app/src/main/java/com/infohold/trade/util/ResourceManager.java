/*
 * Created by zhangchong on 4/19/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade.util;

import android.app.Activity;
import android.content.SharedPreferences;

import com.infohold.trade.config.Constant;
import com.infohold.trade.model.Bulletin;
import com.infohold.trade.model.UserInfo;

public class ResourceManager {

    private static ResourceManager resourceManager = new ResourceManager();

    private Bulletin selectedBulletin;

    private ResourceManager() {

    }

    public static ResourceManager getInstance() {
        return resourceManager;
    }

    public UserInfo getLoginUser(Activity activity) {

        UserInfo userInfo = new UserInfo();
        SharedPreferences preferences = activity.getSharedPreferences(Constant.USER_INFO,Activity.MODE_PRIVATE);
        String userId = preferences.getString(Constant.USER_INFO_ID,"");
        String userName = preferences.getString(Constant.USER_INFO_NAME, "");
        String userGrade = preferences.getString(Constant.USER_INFO_GRADE, "");
        userInfo.setStoreId(userId);
        userInfo.setStorename(userName);
        userInfo.setMemberGrade(userGrade);
        return userInfo;
    }

    public Bulletin getSelectedBulletin() {
        return selectedBulletin;
    }

    public void setSelectedBulletin(Bulletin selectedBulletin) {
        this.selectedBulletin = selectedBulletin;
    }
}
