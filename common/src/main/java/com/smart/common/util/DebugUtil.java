package com.smart.common.util;

import android.util.Log;

import com.smart.common.data.DataFormat;

/**
 *
 */
public class DebugUtil {

    /** 标识是否需要打印log */
    public static boolean sOpenDebug = true;

    /** 调用的类名 */
    private static String sClassName;

    /** 调用的方法名 */
    private static String sMethodName;

    /** 调用的行号 */
    private static int sLineNumber;

    /**
     * 用类名作为tag打印debug日志
     * @param message 日志内容
     */
    public static void d(String message) {
        getsMethodNames(new Throwable().getStackTrace());
        if (sOpenDebug && !DataFormat.isEmpty(message)) {
            Log.d(sClassName, createLog(message));
        }
        SmartLog.logInfo(sClassName, createLog(message));
    }

    /**
     * 打印debug信息
     * @param tag tag
     * @param message 日志内容
     */
    public static void d(String tag, String message) {
        if (sOpenDebug && !DataFormat.isEmpty(message)) {
            getsMethodNames(new Throwable().getStackTrace());
            Log.d(tag, createLog(message));
        }
    }

    /**
     * 用类名作为tag打印info日志
     * @param message 日志内容
     */
    public static void i(String message) {
        getsMethodNames(new Throwable().getStackTrace());
        if (sOpenDebug && !DataFormat.isEmpty(message)) {
            Log.i(sClassName, createLog(message));
        }
        SmartLog.logInfo(sClassName, createLog(message));
    }

    /**
     * 打印info信息
     * @param tag tag
     * @param message 日志内容
     */
    public static void i(String tag, String message) {
        if (sOpenDebug && !DataFormat.isEmpty(message)) {
            getsMethodNames(new Throwable().getStackTrace());
            Log.i(tag, createLog(message));
        }
    }

    /**
     * 用类名作为tag打印error日志
     * @param message 日志内容
     */
    public static void e(String message) {
        if (sOpenDebug && !DataFormat.isEmpty(message)) {
            getsMethodNames(new Throwable().getStackTrace());
            Log.e(sClassName, createLog(message));
        }
        SmartLog.logError(sClassName, createLog(message));
    }

    /**
     * 打印error信息
     * @param tag tag
     * @param message 日志内容
     */
    public static void e(String tag, String message) {
        if (sOpenDebug && !DataFormat.isEmpty(message)) {
            getsMethodNames(new Throwable().getStackTrace());
            Log.e(tag, createLog(message));
        }
    }

    private static void getsMethodNames(StackTraceElement[] sElements) {
        sClassName = sElements[1].getFileName().replace(".java", "");
        sMethodName = sElements[1].getMethodName();
        sLineNumber = sElements[1].getLineNumber();
    }

    private static String createLog(String log) {
        return "Debug [" +
                sMethodName +
                ":" +
                sLineNumber +
                "] " +
                log;
    }
}
