package com.mobile.makemoney;

import android.app.Application;

public class App extends Application {
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//        initX5app(this);
    }

    public static App getInstance() {
        return instance;
    }

}
