package ai.woyao.anything.bike;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;

import ai.woyao.anything.bike.utils.ChannelUtils;
import ai.woyao.anything.bike.utils.SystemInfo;

/**
 * Created by Jimny
 * on 14-7-25.
 */
public class AppInstance extends Application {
    private static AppInstance mInstance = null;
    private String mAppVersion;
    private String mAppVersionNum;
    private String mImei;
    private String mImsi;
    private String mMac;
    private String mAndroidId;
    private String mPlatform;
    private String mSystemVersion;
    private String mModel;
    private String mNetInfo;
    private String mNetType;
    private String mChannel;

    public static AppInstance getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        MobclickAgent.setDebugMode(!BuildConfig.DEBUG);

        // 7.0文件读写权限兼容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    public String getAppVersion() {
        if (TextUtils.isEmpty(mAppVersion)) {
            mAppVersion = BuildConfig.VERSION_NAME;
        }
        return mAppVersion;
    }

    public String getAppVersionNum() {
        if (TextUtils.isEmpty(mAppVersionNum)) {
            mAppVersionNum = String.valueOf(BuildConfig.VERSION_CODE);
        }
        return mAppVersionNum;
    }

    public String getImei() {
        if (TextUtils.isEmpty(mImei)) {
            mImei = SystemInfo.getImei(this);
        }
        return mImei;
    }

    public String getImsi() {
        if (TextUtils.isEmpty(mImsi)) {
            mImsi = SystemInfo.getImsi(this);
        }
        return mImsi;
    }

    public String getMac() {
        if (TextUtils.isEmpty(mMac)) {
            mMac = SystemInfo.getMac(this);
        }
        return mMac;
    }

    public String getAndroidId() {
        if (TextUtils.isEmpty(mAndroidId)) {
            mAndroidId = SystemInfo.getAndroidId(this);
        }
        return mAndroidId;
    }

    public String getPlatform() {
        if (TextUtils.isEmpty(mPlatform)) {
            mPlatform = "android";
        }
        return mPlatform;
    }

    public String getSystemVersion() {
        if (TextUtils.isEmpty(mSystemVersion)) {
            mSystemVersion = SystemInfo.getDeviceOsRelease();
        }
        return mSystemVersion;
    }

    public String getModel() {
        if (TextUtils.isEmpty(mModel)) {
            mModel = SystemInfo.getDeviceModel();
        }
        return mModel;
    }

    public String getNetInfo() {
        if (TextUtils.isEmpty(mNetInfo)) {
            mNetInfo = SystemInfo.getOperatorName(this);
        }
        return mNetInfo;
    }

    public String getNetType() {
        if (TextUtils.isEmpty(mNetType)) {
            mNetType = String.valueOf(SystemInfo.getNetworkType(this));
        }
        return mNetType;
    }

    public String getChannel() {
        if (TextUtils.isEmpty(mChannel)) {
            mChannel = ChannelUtils.getChannel(this);
        }
        return mChannel;
    }
}
