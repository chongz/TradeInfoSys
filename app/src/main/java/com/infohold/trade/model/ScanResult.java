/*
 * Created by zhangchong on 4/19/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ScanResult implements Parcelable{
    private String id;
    private String bulletinId;

    public ScanResult() {
    }

    protected ScanResult(Parcel in) {
        id = in.readString();
        bulletinId = in.readString();
    }

    public static final Creator<ScanResult> CREATOR = new Creator<ScanResult>() {
        @Override
        public ScanResult createFromParcel(Parcel in) {
            return new ScanResult(in);
        }

        @Override
        public ScanResult[] newArray(int size) {
            return new ScanResult[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBulletinId() {
        return bulletinId;
    }

    public void setBulletinId(String bulletinId) {
        this.bulletinId = bulletinId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(bulletinId);
    }
}
