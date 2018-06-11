package com.joywe.common;

import com.joywe.common.base.BaseApplication;
import com.smart.common.util.Utils;

public class CommonApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
