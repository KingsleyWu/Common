package com.joywe.common.image;

import android.graphics.Bitmap;
import android.support.annotation.StringRes;

import com.smart.common.util.Utils;


public class ImageBean {

    int resId;
    String name;
    Bitmap image;

    public ImageBean(@StringRes int resId, Bitmap image) {
        name = Utils.getApplication().getString(resId);
        this.image = image;
    }

    public ImageBean(String name, Bitmap image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
