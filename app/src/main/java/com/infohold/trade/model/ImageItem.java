/*
 * Created by zhangchong on 5/6/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.infohold.trade.util.FileUtils;
import com.infohold.trade.util.ResourceManager;

public class ImageItem implements Parcelable {
    private boolean isSelected;
    private int type;
    private String imagePath;
    private Bitmap bitmap;

    public ImageItem(String imagePath,boolean selected,int type) {
        this.imagePath = imagePath;
        this.isSelected = selected;
        this.type = type;

        if (this.type == 0) {
            this.imagePath = FileUtils.SDPATH + imagePath;
        }

        bitmap = ResourceManager.getInstance().getBitmap(this.imagePath);
    }


    protected ImageItem(Parcel in) {
        isSelected = in.readByte() != 0;
        type = in.readInt();
        imagePath = in.readString();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel in) {
            return new ImageItem(in);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeInt(type);
        dest.writeString(imagePath);
        dest.writeParcelable(bitmap, flags);
    }
}
