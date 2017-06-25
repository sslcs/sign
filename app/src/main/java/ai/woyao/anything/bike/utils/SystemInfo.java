package ai.woyao.anything.bike.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.Locale;

import ai.woyao.anything.bike.net.encode.SDKPswCoder;

/**
 * 设备参数信息
 *
 * @author jen
 */
public class SystemInfo {
    /**
     * mac地址的缓存文件名
     */
    private static final String sMacCacheFileName = "50b8f7b920efe75c986a4a4e8ccee4af";
    private static final String sMacPSW = "B9gfhupt";
    /**
     * mac地址(去除了冒号)
     */
    private static String sMac;

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Android ID
     */
    private static String sAndroidId;

    // ///////////////////////////////////////////////////
    /**
     * 本地语言类型
     */
    private static String sLocaleLanguage;
    /**
     * 厂商
     */
    private static String sManufacturer;
    /**
     * 运营商名称 carrierName
     */
    private static String sOperatorName;
    /**
     * 手机类型 , 2012-11-15
     */
    private static int sPhoneType = -1;
    /**
     * 手机网络类型，用于判断2、2.5、2.75、3或4G等环境 ,2012-11-15
     */
    private static int sNetworkType = -1;
    /**
     * imei
     */
    private static String sImei;
    /**
     * imsi
     */
    private static String sImsi;

    /**
     * 获取本地语言 11-5-23
     *
     * @return
     */
    public static String getLocaleLanguage_Country() {
        try {
            if (sLocaleLanguage == null) {
                Locale l = Locale.getDefault();
                sLocaleLanguage = String.format("%s-%s", l.getLanguage(),
                        l.getCountry());
            }
        } catch (Throwable e) {
            // TODO: handle exception
        }

        return sLocaleLanguage;
    }

    /**
     * 初始化AndroidID
     *
     * @param context
     * @return
     */
    public static String initAndroidId(Context context) {
        try {
            String andId = android.provider.Settings.Secure.getString(
                    context.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);

            if (andId != null) {
                andId = andId.trim();
                andId = andId.toLowerCase();
                return andId;
            }

        } catch (Throwable e) {
            // handle Throwable

        }
        return "";
    }

    /**
     * 获取Android Id
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        try {
            if (sAndroidId == null) {
                sAndroidId = initAndroidId(context);
            }

            if (sAndroidId != null) {
                return sAndroidId;
            }

        } catch (Throwable e) {
            // handle Throwable
        }
        return "";
    }

    /**
     * 初始化imei 2012-11-15
     *
     * @param context
     * @return
     */
    public static String initImei(Context context) {
        String imei = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            if (telephonyManager != null) {
                try {
                    // IMEI
                    String imeiStr = telephonyManager.getDeviceId();
                    if (imeiStr != null) {
                        imei = imeiStr;

                        if (imei != null) {
                            imei = imei.trim();

                            if (imei.indexOf(" ") > -1) {
                                imei.replace(" ", "");
                            }

                            if (imei.indexOf("-") > -1) {
                                imei = imei.replace("-", "");
                            }

                            if (imei.indexOf("\n") > -1) {
                                imei = imei.replace("\n", "");
                            }
                            String meidStr = "MEID:";
                            int stratIndex = imei.indexOf(meidStr);
                            if (stratIndex > -1) {
                                imei = imei.substring(stratIndex
                                        + meidStr.length());
                            }

                            imei = imei.trim();
                            imei = imei.toLowerCase();

                            if (imei.length() < 10) {
                                imei = null;
                            }
                        }

                    }

                } catch (Throwable e) {
                    // handle Throwable

                }

            }
        } catch (Throwable e) {
            // handle Throwable

        }

        return imei;
    }

    /**
     * 获取imei地址 2012-11-15
     *
     * @param context
     * @return
     */
    public static String getImei(Context context) {
        try {
            if (sImei == null) {
                sImei = initImei(context);
            }

            if (sImei != null) {
                return sImei;
            }

        } catch (Throwable e) {
            // handle Throwable
        }
        return "";
    }

    /**
     * 初始化imsi 2012-11-15
     *
     * @param context
     * @return
     */
    public static String initImsi(Context context) {
        String imsi = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            if (telephonyManager != null) {
                try {
                    // IMSI
                    String imsiStr = telephonyManager.getSubscriberId();
                    if (imsiStr != null) {
                        imsi = imsiStr.trim();
                        imsi = imsi.toLowerCase();
                        if (imsi.length() < 10) {
                            imsi = null;
                        }
                    }

                } catch (Throwable e) {
                    // handle Throwable

                }

            }
        } catch (Throwable e) {
            // handle Throwable

        }

        return imsi;
    }

    /**
     * 获取imsi地址 2012-11-15
     *
     * @param context
     * @return
     */
    public static String getImsi(Context context) {
        try {
            if (sImsi == null) {
                sImsi = initImsi(context);
            }

            if (sImsi != null) {
                return sImsi;
            }

        } catch (Throwable e) {
            // handle Throwable
        }
        return "";
    }

    /**
     * 初始化mac地址 2012-11-15
     *
     * @param context
     * @return
     */
    public static String initMac(Context context) {
        try {
            if (hasPermission(context, Manifest.permission.ACCESS_WIFI_STATE)) {
                WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifi.getConnectionInfo();
                String str = info.getMacAddress();

                if (str != null) {
                    str = str.trim();

                    str = str.replace(":", "");// 去掉:号

                    str = str.toLowerCase(Locale.ENGLISH);

                    if (str.length() > 0) {
                        saveMacToCacheFile(context, str);
                    }

                    return str;
                }
            }
        } catch (Throwable e) {
            // handle Throwable
        }
        return "";
    }

    /**
     * 从缓存文件中获取mac，这取决于上一次写入缓存的mac数据
     *
     * @param context
     * @return
     */
    public static String initMacFromCacheFile(Context context) {
        FileReader fr = null;
        BufferedReader br = null;
        try {
            File file = context.getFileStreamPath(sMacCacheFileName);
            if (file == null) {
                return "";
            }

            if (!file.exists()) {
                return "";
            }

            fr = new FileReader(file);

            br = new BufferedReader(fr);

            String macToDecode = br.readLine();

            if (macToDecode == null) {
                return "";
            }

            String mac = SDKPswCoder.decode(macToDecode, sMacPSW);

            return mac;

        } catch (Throwable e) {
            // TODO: handle throwable

        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Throwable e2) {
                // TODO: handle throwable

            }
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (Throwable e2) {
                // TODO: handle throwable

            }
        }
        return "";
    }

    /**
     * 将收集到的mac地址加密保存到cache文件中，因为极有可能拿到的mac为空
     *
     * @param context
     */
    public static void saveMacToCacheFile(Context context, String mac) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            if (mac == null) {
                return;
            }
            mac = mac.trim();
            if (mac.length() <= 0) {
                return;
            }

            String encodedMac = SDKPswCoder.encode(mac, sMacPSW);

            File file = context.getFileStreamPath(sMacCacheFileName);
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write(encodedMac);
            bw.flush();

        } catch (Throwable e) {
            // TODO: handle throwable

        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }

            } catch (Throwable e2) {
                // TODO: handle throwable

            }
            try {
                if (bw != null) {
                    bw.close();
                }

            } catch (Throwable e2) {
                // TODO: handle throwable

            }
        }
    }

    /**
     * 获取mac地址 2012-11-15
     *
     * @param context
     * @return
     */
    public static String getMac(Context context) {
        try {
            if (sMac == null || sMac.length() <= 0) {
                sMac = initMac(context);

            }

            if (sMac == null || sMac.length() <= 0) {
                //如果拿到的mac为空，才从缓存文件中拿
                sMac = initMacFromCacheFile(context);

            }

            if (sMac != null) {
                return sMac;
            }

        } catch (Throwable e) {
            // handle Throwable
        }
        return "";
    }

    /**
     * 获取品牌 DeviceVendor 如HTC
     */
    public static String getManufacturerInfo() {
        // Auto-generated method stub

        try {
            if (sManufacturer == null) {
                Field f = Build.class.getField("MANUFACTURER");
                if (f != null) {
                    sManufacturer = f.get(Build.class).toString().trim();

                }
            }
        } catch (Throwable e) {
            // handle Throwable
        }

        if (sManufacturer == null) {
            return Build.BRAND;
        }

        return sManufacturer;
    }

    /**
     * 获取设备操作系统 PhoneOS 如  2.3    11-5-23
     *
     * @return
     */
    public static String getDeviceOsRelease() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号 DeviceDetail 11-5-23
     *
     * @return
     */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 初始化 运营商信息 11-5-23
     *
     * @param context
     */
    public static void initOperatorName(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            if (telephonyManager != null) {
                try {
                    /*
                     * 运营商的名称 cn
					 */
                    String str = telephonyManager.getNetworkOperatorName();

                    if (str == null) {
                        sOperatorName = "";

                    } else {
                        sOperatorName = str;
                    }

                } catch (Throwable e) {
                    // handle Throwable
                }

            }
        } catch (Throwable e) {
            // handle Throwable
        }
    }

    /**
     * 获取运营商名字
     *
     * @param context
     * @return
     */
    public static String getOperatorName(Context context) {
        if (sOperatorName == null) {
            initOperatorName(context);
        }

        if (sOperatorName == null) {
            return "";
        }

        return sOperatorName;
    }

    /**
     * 获取手机类型 2012-11-15
     *
     * @param context
     * @return
     */
    public static int getPhoneType(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            if (telephonyManager != null) {
                return telephonyManager.getPhoneType();
            }
        } catch (Throwable e) {
            // TODO: handle exception
        }
        return TelephonyManager.PHONE_TYPE_NONE;
    }

    /**
     * 获取手机网络类型 2012-11-15
     *
     * @param context
     * @return
     */
    public static int getNetworkType(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            if (telephonyManager != null) {
                return telephonyManager.getNetworkType();
            }
        } catch (Throwable e) {
            // TODO: handle exception
        }
        return TelephonyManager.NETWORK_TYPE_UNKNOWN;
    }

    private static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
