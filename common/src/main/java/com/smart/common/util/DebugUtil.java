package com.smart.common.util;

import android.util.Log;

import com.smart.common.data.DataFormat;

/**
 *
 */
public class DebugUtil {

    /**
     * 标识是否需要打印log
     */
    public static boolean sOpenDebug = true;

    /**
     * 标识是否需要打印log
     */
    public static String sDebugAppName = "Debug";

    /**
     * 调用的类名
     */
    private static String sClassName;

    /**
     * 调用的方法名
     */
    private static String sMethodName;

    /**
     * 调用的行号
     */
    private static int sLineNumber;

    /**
     * 用类名作为tag打印debug日志
     *
     * @param message 日志内容
     */
    public static void d(String message) {
        getsMethodNames(new Throwable().getStackTrace());
        if (sOpenDebug) {
            Log.d(sClassName, createLog(message));
        }
        SmartLog.logInfo(sClassName, createLog(message));
    }

    /**
     * 打印debug信息
     *
     * @param tag     tag
     * @param message 日志内容
     */
    public static void d(String tag, String message) {
        getsMethodNames(new Throwable().getStackTrace());
        if (sOpenDebug) {
            Log.d(tag, createLog(message));
        }
        SmartLog.logInfo(sClassName, createLog(message));
    }


    /**
     * 打印debug信息 无SmartLog
     *
     * @param tag     tag
     * @param message 日志内容
     */
    public static void v(String tag, String message) {
        if (sOpenDebug) {
            getsMethodNames(new Throwable().getStackTrace());
            Log.v(tag, createLog(message));
        }
    }

    /**
     * 用类名作为tag打印debug日志
     *
     * @param object 日志内容
     */
    public static void d(Object object) {
        getsMethodNames(new Throwable().getStackTrace());
        if (!DataFormat.isEmpty(object)) {
            if (sOpenDebug) {
                Log.d(sClassName, createLog(object.toString()));
            }
            SmartLog.logInfo(sClassName, createLog(object.toString()));
        } else {
            if (sOpenDebug) {
                Log.d(sClassName, createLog(object));
            }
            SmartLog.logInfo(sClassName, createLog(object));
        }
    }

    /**
     * 用类名作为tag打印debug日志
     *
     * @param object 日志内容
     */
    public static void v(Object object) {
        if (sOpenDebug) {
            getsMethodNames(new Throwable().getStackTrace());
            if (!DataFormat.isEmpty(object)) {
                Log.v(sClassName, createLog(object.toString()));
            } else {
                Log.v(sClassName, createLog(object));
            }
        }
    }

    /**
     * 用类名作为tag打印info日志
     *
     * @param message 日志内容
     */
    public static void i(String message) {
        getsMethodNames(new Throwable().getStackTrace());
        if (sOpenDebug) {
            Log.i(sClassName, createLog(message));
        }
        SmartLog.logInfo(sClassName, createLog(message));
    }

    /**
     * 打印info信息
     *
     * @param tag     tag
     * @param message 日志内容
     */
    public static void i(String tag, String message) {
        getsMethodNames(new Throwable().getStackTrace());
        if (sOpenDebug) {
            Log.i(tag, createLog(message));
        }
        SmartLog.logInfo(sClassName, createLog(message));
    }

    /**
     * 用类名作为tag打印debug日志
     *
     * @param object 日志内容
     */
    public static void i(Object object) {
        getsMethodNames(new Throwable().getStackTrace());
        if (!DataFormat.isEmpty(object)) {
            if (sOpenDebug) {
                Log.i(sClassName, createLog(object.toString()));
            }
            SmartLog.logInfo(sClassName, createLog(object.toString()));
        } else {
            if (sOpenDebug) {
                Log.i(sClassName, createLog(object));
            }
            SmartLog.logInfo(sClassName, createLog(object));
        }

    }

    /**
     * 用类名作为tag打印error日志
     *
     * @param message 日志内容
     */
    public static void e(String message) {
        getsMethodNames(new Throwable().getStackTrace());
        if (sOpenDebug) {
            Log.e(sClassName, createLog(message));
        }
        SmartLog.logError(sClassName, createLog(message));
    }

    /**
     * 打印error信息
     *
     * @param tag     tag
     * @param message 日志内容
     */
    public static void e(String tag, String message) {
        getsMethodNames(new Throwable().getStackTrace());
        if (sOpenDebug) {
            Log.e(tag, createLog(message));
        }
        SmartLog.logError(sClassName, createLog(message));
    }

    /**
     * 用类名作为tag打印error日志
     *
     * @param object 日志内容
     */
    public static void e(Object object) {
        getsMethodNames(new Throwable().getStackTrace());
        if (!DataFormat.isEmpty(object)) {
            if (sOpenDebug) {
                Log.e(sClassName, createLog(object.toString()));
            }
            SmartLog.logError(sClassName, createLog(object.toString()));
        } else {
            if (sOpenDebug) {
                Log.e(sClassName, createLog(object));
            }
            SmartLog.logError(sClassName, createLog(object));
        }
    }

    private static void getsMethodNames(StackTraceElement[] sElements) {
        sClassName = sElements[1].getFileName().replace(".java", "");
        sMethodName = sElements[1].getMethodName();
        sLineNumber = sElements[1].getLineNumber();
    }

    private static String createLog(Object obj) {
        return sDebugAppName + "[" +
                sMethodName +
                ":" +
                sLineNumber +
                "] " +
                obj;
    }
}
