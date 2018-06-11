package com.smart.common.util;

import android.text.format.DateFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

/**
 * 日志记录
 * @author LiZhengGuang
 * @date 2014-1-20
 * @project COM.SMART.MV.VOICE.PROJECT.A20
 * @package com.smart.mv.voice.log
 * @package VoiceLog.java
 * @version 1.0
 */
public class SmartLog {
    /** Log文件保存位置 */
    public static String LOG_FILE = null;

    /**
     * 文件写数据流对象
     */
    private static FileOutputStream _fos;

    /**
     * 状态
     */
    private static boolean _logState = true;

    /**
     * 默认构造函数
     */
    private SmartLog() {
    } // End VoiceLog
    
    /**
     * 准备开始写日志数据
     * @param path
     */
    public static void startLog(String path) {
        LOG_FILE = path;
        if (_logState) {
            try {
                File file = new File(LOG_FILE);
                DebugUtil.d("路径是："+LOG_FILE);
                if (file.isFile()) {
                    // 100M后进行删除文件
                    if (file.length() >= 1024 * 1024 * 10) {
                        file.delete();
                    }
                } else {
                    file.getParentFile().mkdirs();
                }
                // 追加
                _fos = new FileOutputStream(LOG_FILE, true);
                synchronized (_fos) {
                    StringBuilder logTitle = new StringBuilder("");
                    logTitle.append("\r\n").append("\r\n");
                    logTitle.append("=====================================================").append("\r\n");
                    logTitle.append("  Time:   ");
                    logTitle.append(DateFormat.format("yyyy-MM-dd kk:mm:ss", System.currentTimeMillis()).toString()).append("\r\n");
                    logTitle.append("=====================================================").append("\r\n").append("\r\n");
                    _fos.write(logTitle.toString().getBytes(Charset.forName("UTF-8")));
                    _fos.flush();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                Utils.close(_fos);
            }
        }
    } // End logInfo
    
    /**
     * 写日志数据
     * @param fun 类名
     * @param msg
     */
    public static void logInfo(String fun, String msg) {
        if (_logState) {
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
                _fos = new FileOutputStream(LOG_FILE, true);
                synchronized (_fos) {
                    _fos.write(getMessage("I", fun, msg).getBytes(Charset.forName("UTF-8")));
                    _fos.flush();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                Utils.close(_fos);
            }
        }
    } // End logInfo
    
    /**写错误日志数据
     * 
     * @param fun 类名
     * @param msg
     */
    public static void logError(String fun, String msg) {
        if (_logState) {
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
                _fos = new FileOutputStream(LOG_FILE, true);
                synchronized (_fos) {
                    _fos.write(getMessage("E", fun, msg).getBytes(Charset.forName("UTF-8")));
                    _fos.flush();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                Utils.close(_fos);
            }
        }
    } // End logError
    
    /**
     * 拼接消息
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
            for (int i = 0; i < 20-tagLenght; i++) {
                tag += " ";
            }
        }
        result.append(tag).append("  ").append(msg);
        result.append("\r\n");
        return result.toString();
    } // End getMessage
} // End class VoiceLog