/*
 * Created by zhangchong on 4/19/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.infohold.trade.config.Constant;
import com.infohold.trade.model.Bulletin;
import com.infohold.trade.model.UserInfo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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

    public Bitmap getBitmap(String path) {
        BufferedInputStream bufferedInputStream;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(path)));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(bufferedInputStream, null, options);
            bufferedInputStream.close();
            int i = 0;
            Bitmap bitmap = null;

            while(true) {
                if ((options.outWidth >> i <= 1000) && (options.outHeight >> i <= 1000)) {
                    bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(path)));
                    options.inSampleSize = (int) Math.pow(2.0D, i);
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(bufferedInputStream, null, options);
                    break;
                }
                i += 1;
            }

            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
