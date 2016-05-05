/*
 * Created by zhangchong on 4/19/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Member implements Parcelable {
    private String userId;
    private String userName;
    private String userPic;
    private String orgname;
    private String card;
    private String phone;
    private String sex;

    //默认构造方法，否则fastJson解析错误
    public Member() {
    }

    protected Member(Parcel in) {
        userId = in.readString();
        userName = in.readString();
        userPic = in.readString();
        orgname = in.readString();
        card = in.readString();
        phone = in.readString();
        sex = in.readString();
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(userPic);
        dest.writeString(orgname);
        dest.writeString(card);
        dest.writeString(phone);
        dest.writeString(sex);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSexDisplay() {
        if ("0".equals(this.sex)) {
            return "女";
        }else if ("1".equals(this.sex)) {
            return "男";
        }
        return "";
    }
}
