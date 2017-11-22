package com.lucky.sign;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;


/**
 * Created by Reven
 * on 17-11-05.
 */
public class AppInstance extends Application {
    private static AppInstance mInstance = null;

    public static AppInstance getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        // 7.0文件读写权限兼容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }
}
