/**
 * 
 */
package com.smart.common.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import com.smart.common.interfaces.SetTimeCallback;
import com.smart.common.net.SntpClient;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

public class Utils {

    /** 时间服务器的域名 */
    private static String[] sNtpServers = {"ntp.sjtu.edu.cn", "hk.pool.ntp.org"};
    
    /**
     * 关闭流
     * @param closeable 流
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行命令
     * @param commond 命令
     * @return
     */
    public static Process execRuntimeProcess(String commond) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(commond);
        } catch (IOException e) {
            DebugUtil.i("exec Runtime commond:" + commond + ", IOException" + e);
            e.printStackTrace();
        }
        DebugUtil.i("exec Runtime commond:" + commond + ", Process:" + p);
        return p;
    }
    
    /** 
    * 清除应用缓存的用户数据，同时停止所有服务和Alarm定时task 
    * String cmd = "pm clear " + packageName; 
    * String cmd = "pm clear " + packageName  + " HERE"; 
    * Runtime.getRuntime().exec(cmd) 
    * @param packageName 
    * @return 
    */
    public static Process clearAppUserData(String packageName) {
        Process p = execRuntimeProcess("pm clear " + packageName);
        if (p == null) {
            DebugUtil.i("Clear app data packageName:" + packageName + ", FAILED !");
        } else {
            DebugUtil.i("Clear app data packageName:" + packageName + ", SUCCESS !");
        }
        return p;
    }
    
    /**
     *  获取转换文件大小单位后的值
     * @param fileSize
     * @return
     */
    public static String getFileSizeFormet(long fileSize) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        fileSize *= 1024;
        if (fileSize == 0) {
            fileSizeString = "0";
        } else if (fileSize < 1024) {
            fileSizeString = ""+fileSize;
        } else if (fileSize < 1048576) {
            fileSizeString = ""+fileSize / 1024;
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double)fileSize / 1048576);
        } else {
            fileSizeString = df.format((double)fileSize / 1073741824);
        }
        return fileSizeString;
    }
    
    /**
     * 获取文件大小单位
     * @param fileSize
     * @return 返回 KB/S  B/s MB/S G/S
     */
    public static String getFileUnit(long fileSize) {
        String fileSizeString = "";
        fileSize *= 1024;
        if (fileSize < 1024) {
            fileSizeString = "B/S";
        } else if (fileSize < 1048576) {
            fileSizeString = "KB/S";
        } else if (fileSize < 1073741824) {
            fileSizeString = "MB/S";
        } else {
            fileSizeString = "GB/S";
        }
        return fileSizeString;
    }

    /**
     * 初始化log
     * @param isNeedLog context
     */
    public static void openDebug(boolean isNeedLog){
        DebugUtil.sOpenDebug = isNeedLog;
    }


    /**
     * 是否有网络连接
     * @param context context
     * @return 有网络返回true 否则返回false
     */
    public static boolean isNetConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] networkInfos = connectivity.getAllNetworkInfo();
            if (networkInfos != null) {
                for (NetworkInfo networkInfo : networkInfos) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * 是否是WIFI连接
     * @param context context
     * @return 是返回true 否则返回false
     */
    public static boolean isConnectedToWifi(Context context) {
        return isConnectedToNetByType(context,  ConnectivityManager.TYPE_WIFI);
    }

    /**
     * 是否是以太网连接
     * @param context context
     * @return 是返回true 否则返回false
     */
    public static boolean isConnectedToEthernet(Context context) {
        return isConnectedToNetByType(context,  ConnectivityManager.TYPE_ETHERNET);
    }

    /**
     * 查询网络连接方式是否为type连接方式
     * @param context context
     * @param type ConnectivityManager.TYPE_？
     * @return 是返回true 否则返回false
     */
    public static boolean isConnectedToNetByType(Context context, int type) {
        ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] networkInfos = connectivity.getAllNetworkInfo();
            if (networkInfos != null) {
                for (NetworkInfo networkInfo : networkInfos) {
                    if (networkInfo.getType() == type && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * 安装apk
     * @param context context
     * @param fileName fileName
     */
    public static void installApplication(Context context, String fileName) {
        DebugUtil.d("apk filepath = " + fileName);

        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            File file = new File(fileName);
            if (file.canRead()) {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);
            }
        } catch (Exception var6) {
            var6.printStackTrace();

            try {
                Process proc1 = Runtime.getRuntime().exec("adb connect 127.0.0.1");
                proc1.waitFor();
                Process proc = Runtime.getRuntime().exec("adb -s 127.0.0.1:5555 install -r " + fileName);
                proc.waitFor();
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }

    }


    /**
     * 设置系统时钟
     */
    public static void setSystemCLock(final SetTimeCallback callback) {
        ThreadUtil.getSinglePool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    SntpClient client = new SntpClient();
                    long now = 0L;
                    // 依次请求时间服务器
                    for (String sNtpServer : sNtpServers) {
                        // 请求NTP服务器三次
                        for (int j = 0; j < 3; j++) {
                            if (client.requestTime(sNtpServer, 3000)) {
                                now =
                                        client.getNtpTime() + SystemClock.elapsedRealtime() - client.getNtpTimeReference();
                                break;
                            }
                        }
                        // 如果请求到了，则退出循环
                        if (now > 0) {
                            break;
                        }
                    }
                    // 通过NTP服务器获取时间失败
                    if (now == 0) {
                        DebugUtil.d("get time from NTP error, now from baidu.com");
                        //取得资源对象
                        URL url;
                        try {
                            url = new URL("http://www.baidu.com");
                            //生成连接对象
                            URLConnection uc = url.openConnection();
                            //发出连接
                            uc.connect();
                            //取得网站日期时间
                            now = uc.getDate();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (now > 0) {
                        DebugUtil.d("===== set system time  ===" + DateFormat.format("yyyy-MM-dd kk:mm:ss", now));
                        boolean setCurrentTimeMillis = SystemClock.setCurrentTimeMillis(now);
                        if (callback != null) {
                            callback.setTimeCallback(setCurrentTimeMillis);
                        }
                    } else {
                        if (callback != null) {
                            callback.setTimeCallback(false);
                        }
                        DebugUtil.e("get time error");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.setTimeCallback(false);
                    }
                }
            }
        });
    }


    private static Application sApplication;

    /**
     * 初始化 utils.
     *
     * @param context context
     */
    public static void init(@NonNull final Context context) {
        init((Application) context.getApplicationContext());
    }

    /**
     * 初始化 utils.
     *
     * @param app application
     */
    public static void init(@NonNull final Application app) {
        Utils.sApplication = app;
    }

    /**
     * 获取Application
     *
     * @return Application
     */
    public static Application getApplication() {
        if (sApplication != null) {
            return sApplication;
        }
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.activityThread");
            Object at = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(at);
            if (app == null) {
                throw new NullPointerException("u should init first");
            }
            init((Application) app);
            return sApplication;
        } catch (NoSuchMethodException | IllegalAccessException
                | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("u should init first");
    }

    /**
     * 是否有SD卡
     * @return 有则返回true 否则false
     */
    private static boolean existSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * cache文件夹路径
     */
    private static String sCacheDir;

    /**
     * 获取cache文件夹路径
     * @return cache文件夹路径
     */
    public static String getAppCacheDir() {
        initCacheDir();
        return sCacheDir;
    }

    /**
     * 获取网络缓存文件夹路径
     * @return 网络缓存文件夹路径
     */
    public static String getAppNetCache(){
        if (sCacheDir == null){
            throw new RuntimeException("u should init first");
        }
        return sCacheDir + File.separator + "NetCache";
    }

    private static void initCacheDir() {
        if (sApplication != null) {
            if (sApplication.getExternalCacheDir() != null && existSDCard()) {
                sCacheDir = sApplication.getExternalCacheDir().toString();
            } else {
                sCacheDir = sApplication.getCacheDir().toString();
            }
        }else {
            throw new IllegalArgumentException("u should init first");
        }
    }
    
}
