package com.mobile.makemoney;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;

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

    public void initX5app(Context context) {
        //设置非wifi条件下允许下载X5内核
        QbSdk.setDownloadWithoutWifi(true);

        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，true表x5内核加载成功，否则表加载失败，会自动切换到系统内核。
                Log.d("app", " 内核加载 " + arg0);
            }
            @Override
            public void onCoreInitFinished() {
            }
        };

        //x5内核初始化接口
        QbSdk.initX5Environment(context,  cb);
    }
}
