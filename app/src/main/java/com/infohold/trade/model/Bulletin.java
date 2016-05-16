/*
 * Created by zhangchong on 4/19/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Bulletin implements Parcelable {

    private String activityId;
    private String activityName;
    private String activityPic;
    private String contText;
    private String Dtime;

    public Bulletin() {
    }

    protected Bulletin(Parcel in) {
        activityId = in.readString();
        activityName = in.readString();
        activityPic = in.readString();
        contText = in.readString();
        Dtime = in.readString();
    }

    public static final Creator<Bulletin> CREATOR = new Creator<Bulletin>() {
        @Override
        public Bulletin createFromParcel(Parcel in) {
            return new Bulletin(in);
        }

        @Override
        public Bulletin[] newArray(int size) {
            return new Bulletin[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(activityId);
        dest.writeString(activityName);
        dest.writeString(activityPic);
        dest.writeString(Dtime);
        dest.writeString(contText);
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityPic() {
        return activityPic;
    }

    public void setActivityPic(String activityPic) {
        this.activityPic = activityPic;
    }

    public String getDtime() {
        return Dtime;
    }

    public void setDtime(String dtime) {
        Dtime = dtime;
    }

    public String getContText() {
        return contText;
    }

    public void setContText(String contText) {
        this.contText = contText;
    }
}

