/*
 * Created by zhangchong on 4/19/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.infohold.trade.config.Constant;

public class ScanResult implements Parcelable{

    private String scanResultStr;
    private String bulletinId;
    private String memberId;

    public ScanResult() {

    }

    void parse() {
        if (this.scanResultStr != null && this.scanResultStr.startsWith(Constant.SCAN_BAR_CODE_PREFIX)) {
            memberId = this.scanResultStr.substring(Constant.SCAN_BAR_CODE_PREFIX.length(), this.scanResultStr.length());
        }
    }

    public ScanResult(String scanResultStr) {
        this.scanResultStr = scanResultStr;
        parse();
    }

    protected ScanResult(Parcel in) {
        scanResultStr = in.readString();
        bulletinId = in.readString();
        memberId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(scanResultStr);
        dest.writeString(bulletinId);
        dest.writeString(memberId);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getScanResultStr() {
        return scanResultStr;
    }

    public void setScanResultStr(String scanResultStr) {
        this.scanResultStr = scanResultStr;
    }

    public String getBulletinId() {
        return bulletinId;
    }

    public void setBulletinId(String bulletinId) {
        this.bulletinId = bulletinId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
