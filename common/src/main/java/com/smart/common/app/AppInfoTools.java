package com.smart.common.app;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.smart.common.data.DataFormat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 获取应用信息工具类
 * @author xiaozhijun
 * @date 2017年07月12日
 * @project LunznTool
 * @package com.smart.app
 * @package AppInfoTools.java
 */
public class AppInfoTools {
    
    /**
     * 获取指定应用里面的渠道信息
     * @param mContext 上下文
     * @param packageName 指定应用的包名
     * @param metaKey 存储渠道meta名称
     * @return 渠道名称
     */
    public static String getAppChannel(Context mContext, String packageName, String metaKey) {
        String result = null;
        try {
            ApplicationInfo apInfo =
                mContext.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            if (apInfo != null) {
                result = apInfo.metaData.getString(metaKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 获取当前activity名称
     * @param mContext 上下文
     * @return
     */
    public static String getTopActivityName(Context mContext) {
        ComponentName cn = getTopActivityComponentName(mContext);
        return cn.getClassName();
    }
    
    /**
     * 获取当前activity名称
     * @param mContext 上下文
     * @return
     */
    public static ComponentName getTopActivityComponentName(Context mContext) {
        ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = null;
        if (am != null) {
            cn = am.getRunningTasks(1).get(0).topActivity;
        }
        return cn;
    }
    
    /**
     * 通过应用包名获取指定apk版本信息
     * @param mContext 上下文
     * @param packageName 包名
     * @return
     */
    public static String getApkVersion(Context mContext, String packageName) {
        String version = null;
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pInfo = null;
        // 获取包应用信息
        try {
            pInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (pInfo != null) {
            version = pInfo.versionName;
        }
        return version;
    }
    
    /**
     * 通过应用包名获取apk应用信息
     * @param mContext 上下文
     * @param packageName 包名
     * @return apk应用信息
     */
    public static PackageInfo getApkPackageInfo(Context mContext, String packageName) {
        PackageInfo pInfo = null;
        if (packageName != null) {
            PackageManager pm = mContext.getPackageManager();
            try {
                pInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pInfo;
    }
    
    /**
     * 通过文件地址获取本地apk的资源文件
     * 
     * @param context 上下文
     * @param apkPath 本地apk文件路径
     * @return 本地apk的资源文件包括AssetManager、DisplayMetrics以及Configuration
     */
    public static Resources getResources(Context context, String apkPath) {
        try {
            // assetManager的类名
            String pathAssetManager = "android.content.res.AssetManager";
            Class<?> assetMagCls = Class.forName(pathAssetManager);
            // AssetManager 的构造方法
            Constructor<?> assetMagCt = assetMagCls.getConstructor((Class[])null);
            // 获取assestMag实例
            Object assetMag = assetMagCt.newInstance((Object[])null);
            @SuppressWarnings("rawtypes")
            Class[] typeArgs = new Class[1];
            typeArgs[0] = String.class;
            // 获取AssetManager的addAssetPath方法名称
            Method addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath", typeArgs);
            Object[] valueArgs = new Object[1];
            valueArgs[0] = apkPath;
            // 为AssetManager的addAssetPath方法提供文件路径参数
            addAssetPathMtd.invoke(assetMag, valueArgs);
            Resources res = context.getResources();
            typeArgs = new Class[3];
            typeArgs[0] = assetMag.getClass();
            typeArgs[1] = res.getDisplayMetrics().getClass();
            typeArgs[2] = res.getConfiguration().getClass();
            Constructor<Resources> resCt = Resources.class.getConstructor(typeArgs);
            valueArgs = new Object[3];
            valueArgs[0] = assetMag;
            valueArgs[1] = res.getDisplayMetrics();
            valueArgs[2] = res.getConfiguration();
            // AssetManager、DisplayMetrics以及Configuration信息放置Resources文件中
            res = (Resources)resCt.newInstance(valueArgs);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 通过指定路径获取本地apk文件的包信息
     * 
     * @param context 上下文
     * @param path 本地apk文件路径
     * @return apk文件的包信息
     */
    public static PackageInfo getInfo(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        return pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
    }
    
    /**
     * 获取指定包名的签名
     * 
     * @param context 上下文
     * @param packageName 包名
     * @return 签名信息
     */
    public static String getSignedPubKey(Context context, String packageName) {
        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo packageInfo =
                context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            return sign.toCharsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    /**
     * 判断指定签名与指定的包名的签名是否相同
     * 
     * @param packageName 包名
     * @param key 要与之比较的签名信息
     * @return 比较结果
     */
    public static boolean isSignatureEqualByPackageName(Context context, String packageName, String key) {
        String signature = getSignedPubKey(context, packageName);
        return key.equals(signature);
    }
    
    /**
     * 判断指定服务是否正在运行
     * 
     * @param mContext 上下文
     * @param serviceName
     *            服务名称（完整的服务名称，包括包名，如com.smart.comprehensive.service.XiriCommandService）
     * @return 判断结果
     */
    public static boolean isServiceRunning(String serviceName, Context mContext) {
        ActivityManager manager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceName.equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 判断指定报名的应用是否已安装
     * @param context 上下文
     * @param packageName 指定包名
     * @return 判断结果
     */
    public static boolean isApplicationInstalled(Context context, String packageName) {
        return getApkPackageInfo(context, packageName) != null;
    }
    
    /**
     * 将如3.1.1.254转换成311254
     * 
     * @param dotString 要转换的字符串 如3.1.2.2
     * @return 去除.转换成整数
     */
    public static String convertDotString2IntString(String dotString) {
        String result = null;
        if (!DataFormat.isEmpty(dotString)) {
            String[] arr = dotString.split("\\.");
            if (!DataFormat.isEmpty(arr)) {
                StringBuilder sBuilder = new StringBuilder();
                for (String str : arr) {
                    sBuilder.append(str);
                }
                result = sBuilder.toString();
            }
        }
        return result;
    }
    
    /**
     * 将指定包名的apk的versioncode与传入的apk versioncode进行比较，
     * 如果指定包名的apk versioncode比传入的targetCode高则返回正数，相等则返回0，低则返回负数
     * 
     * @param packageName 要比较的apk的包名
     * @param targetCode 要与apk进行比较的targetCode
     * @return 0(==) <0 (<) >0 (>)
     */
    public static int compareApkCode(Context mContext, String packageName, int targetCode) {
        int result = 0;
        try {
            PackageInfo pInfo = getApkPackageInfo(mContext, packageName);
            if (pInfo != null) {
                int versionCode = pInfo.versionCode;
                result = versionCode - targetCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 比较.形式分割的版本号
     * 
     * @param sourceVersion 要比较的.字符串1
     * @param targetVersion 要比较的.字符串2
     * @return 返回比较结果，按位比较大小，不管长度
     */
    public static int compareApkVersion(String sourceVersion, String targetVersion) {
        String formatSource = convertDotString2IntString(sourceVersion);
        String formatTarget = convertDotString2IntString(targetVersion);
        if (formatSource != null && formatTarget != null) {
            return formatSource.compareTo(formatTarget);
        } else if (formatTarget == null) {
            return 1;
        } else {
            return -1;
        }
    }
    
    /**
     * 获取MetaData数据
     * @param context
     * @param metaDataName
     * @param defValue
     * @return
     */
    public static String getApplicationStringMetaDataValue(Context context, String metaDataName, String defValue) {
	    if (metaDataName == null) {
	    	return null;
	    }
	    String value = null;
	    PackageManager packageManager = context.getPackageManager();
	    ApplicationInfo applicationInfo;
	    try {
	        applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
	 
	        if (applicationInfo != null && applicationInfo.metaData != null) {
	            value = applicationInfo.metaData.getString(metaDataName);
	        }
	    } catch (NameNotFoundException e) {
            e.printStackTrace();
	    }
	 
	    return (value == null ? defValue : value);
	}
}
