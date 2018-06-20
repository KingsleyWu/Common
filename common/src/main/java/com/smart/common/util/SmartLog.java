package com.smart.common.util;

import android.content.Context;
import android.text.format.DateFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

/**
 * 日志记录
 *
 * @author LiZhengGuang
 * @version 1.0
 * @date 2014-1-20
 * @package SmartLog.java
 */
public class SmartLog {
    /**
     * Log文件保存位置
     */
    public static String LOG_FILE = null;

    /**
     * 文件写数据流对象
     */
    private static FileOutputStream sFos;

    /**
     * 状态
     */
    public static boolean sLogState = true;

    /**
     * 默认构造函数
     */
    private SmartLog() {
    } // End VoiceLog

    /**
     * 准备开始写日志数据
     *
     * @param context context
     */
    public static void startLog(Context context) {
        LOG_FILE = context.getFilesDir().getPath() + File.separator +"SmartLog.log";
        startLog();
    } // End logInfo

    /**
     * 准备开始写日志数据
     */
    private static void startLog(){
        if (sLogState) {
            try {
                File file = new File(LOG_FILE);
                DebugUtil.d("路径是：" + LOG_FILE);
                if (file.isFile()) {
                    // 100M后进行删除文件
                    if (file.length() >= 1024 * 1024 * 10) {
                        file.delete();
                    }
                } else {
                    file.getParentFile().mkdirs();
                }
                // 追加
                sFos = new FileOutputStream(LOG_FILE, true);
                synchronized (sFos) {
                    StringBuilder logTitle = new StringBuilder("");
                    logTitle.append("\r\n").append("\r\n");
                    logTitle.append("=====================================================").append("\r\n");
                    logTitle.append("  Time:   ");
                    logTitle.append(DateFormat.format("yyyy-MM-dd kk:mm:ss", System.currentTimeMillis()).toString()).append("\r\n");
                    logTitle.append("=====================================================").append("\r\n").append("\r\n");
                    sFos.write(logTitle.toString().getBytes(Charset.forName("UTF-8")));
                    sFos.flush();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                Utils.close(sFos);
            }
        }
    }

    /**
     * 准备开始写日志数据
     *
     * @param logFile logFile 如：context.getFilesDir().getPath() + File.separator +"SmartLog.log";
     */
    public static void startLog(String logFile){
        LOG_FILE = logFile;
        startLog();
    }

    /**
     * 准备开始写日志数据
     * @param context context
     * @param logFileName logFile的名字 如：SmartLog.log
     */
    public static void startLog(Context context,String logFileName){
        LOG_FILE = context.getFilesDir().getPath() + File.separator +logFileName;
        startLog();
    }

    /**
     * 写日志数据
     *
     * @param fun 类名
     * @param msg
     */
    public static void logInfo(String fun, String msg) {
        if (sLogState && LOG_FILE != null) {
            try {
                File file = new File(LOG_FILE);
                if (file.isFile()) {
                    // 100M后进行删除文件
                    if (file.length() >= 1024 * 1024 * 10) {
                        file.delete();
                    }
                } else {
                    file.getParentFile().mkdirs();
                }
                // 追加
                sFos = new FileOutputStream(LOG_FILE, true);
                synchronized (sFos) {
                    sFos.write(getMessage("I", fun, msg).getBytes(Charset.forName("UTF-8")));
                    sFos.flush();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                Utils.close(sFos);
            }
        }
    } // End logInfo

    /**
     * 写错误日志数据
     *
     * @param fun 类名
     * @param msg
     */
    public static void logError(String fun, String msg) {
        if (sLogState && LOG_FILE != null) {
            try {
                File file = new File(LOG_FILE);
                if (file.isFile()) {
                    // 100M后进行删除文件
                    if (file.length() >= 1024 * 1024 * 10) {
                        file.delete();
                    }
                } else {
                    file.getParentFile().mkdirs();
                }
                // 追加
                sFos = new FileOutputStream(LOG_FILE, true);
                synchronized (sFos) {
                    sFos.write(getMessage("E", fun, msg).getBytes(Charset.forName("UTF-8")));
                    sFos.flush();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                Utils.close(sFos);
            }
        }
    } // End logError

    /**
     * 拼接消息
     *
     * @param state
     * @param tag
     * @param msg
     * @return
     */
    private static String getMessage(String state, String tag, String msg) {
        StringBuilder result = new StringBuilder("");
        result.append(DateFormat.format("yyyy-MM-dd kk:mm:ss", System.currentTimeMillis()).toString()).append("  ");
        result.append(state).append("  ");
        int tagLenght = tag.length();
        if (tagLenght < 20) {
            StringBuilder tagBuilder = new StringBuilder(tag);
            for (int i = 0; i < 20 - tagLenght; i++) {
                tagBuilder.append(" ");
            }
            tag = tagBuilder.toString();
        }
        result.append(tag).append("  ").append(msg);
        result.append("\r\n");
        return result.toString();
    } // End getMessage
} // End class VoiceLog