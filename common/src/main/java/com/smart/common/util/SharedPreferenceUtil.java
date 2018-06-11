package com.smart.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Set;

/**
 * 关于SharedPreference的操作方法，必须在Application里面调用init()方法
 * @author 钟何亮
 * @date 2017年1月17日
 * @project COM.LZ.M02.LAUNCHER
 * @package com.lz.m02.launcher.util
 * @filename SharedPreferenceUtil.java
 * @version [版本号]
 */
public class SharedPreferenceUtil {

    /** 实例 */
    private static SharedPreferences mSharedPreferences = null;
    
    private static Editor mEditor = null;
    
    /**
     * 使用这个工具类之前必须在Application里面调用这个初始化方法
     * @param context 上下文
     * @return 返回工具类的实例
     */
    public static void init(Context context) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences("users", Context.MODE_PRIVATE);
        }
    }
    
    /**
     * 往users.xml里面保存 int 数据
     * @param key 键值对名称
     * @param value 保存的值
     */
    public static void set(String key, int value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(key, value);
        mEditor.apply();
    }
    
    /**
     * 往users.xml里面保存 long 数据
     * @param key 键值对名称
     * @param value 保存的值
     */
    public static void set(String key, long value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putLong(key, value);
        mEditor.apply();
    }
    
    /**
     * 往users.xml里面保存 float 数据
     * @param key 键值对名称
     * @param value 保存的值
     */
    public static void set(String key, float value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putFloat(key, value);
        mEditor.apply();
    }
    
    /**
     * 往users.xml里面保存 boolean 数据
     * @param key 键值对名称
     * @param value 保存的值
     */
    public static void set(String key, boolean value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(key, value);
        mEditor.apply();
    }
    
    /**
     * 往users.xml里面保存 String 数据
     * @param key 键值对名称
     * @param value 保存的值
     */
    public static void set(String key, String value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(key, value);
        mEditor.apply();
    }
    
    /**
     * 往users.xml里面保存 Set<String> 数据
     * @param key 键值对名称
     * @param value 保存的值
     */
    public static void set(String key, Set<String> value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putStringSet(key, value);
        mEditor.apply();
    }
    
    /**
     * 从users.xml里面获取 字段为 key 的 int 数据
     * @param key 键值对名称
     * @param def 默认返回的值
     * @return key对应的 int 数据
     */
    public static int getInt(String key, int def) {
        return mSharedPreferences.getInt(key, def);
    }
    
    /**
     * 从users.xml里面获取 字段为 key 的 long 数据
     * @param key 键值对名称
     * @param def 默认返回的值
     * @return key对应的 long 数据
     */
    public static long getLong(String key, long def) {
        return mSharedPreferences.getLong(key, def);
    }
    
    /**
     * 从users.xml里面获取 字段为 key 的 float 数据
     * @param key 键值对名称
     * @param def 默认返回的值
     * @return key对应的 float 数据
     */
    public static float getFloat(String key, float def) {
        return mSharedPreferences.getFloat(key, def);
    }

    /**
     * 从users.xml里面获取 字段为 key 的 boolean 数据
     * @param key 键值对名称
     * @return key对应的 boolean 数据，如果不存在，返回false
     */
    public static boolean getBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }
    
    /**
     * 从users.xml里面获取 字段为 key 的 boolean 数据
     * @param key 键值对名称
     * @param def 默认返回的值
     * @return key对应的 boolean 数据
     */
    public static boolean getBoolean(String key, boolean def) {
        return mSharedPreferences.getBoolean(key, def);
    }
    
    /**
     * 从users.xml里面获取 字段为 key 的 String 数据，默认返回值为 ""
     * @param key 键值对名称
     * @return key对应的 String 数据
     */
    public static String getString(String key) {
        return getString(key, "");
    }
    
    /**
     * 从users.xml里面获取 字段为 key 的 String 数据
     * @param key 键值对名称
     * @param def 默认返回的值
     * @return key对应的 String 数据
     */
    public static String getString(String key, String def) {
        return mSharedPreferences.getString(key, def);
    }
    
    /**
     * 从users.xml里面获取 字段为 key 的 Set<String> 数据
     * @param key 键值对名称
     * @param def 默认返回的值
     * @return key对应的 Set<String> 数据
     */
    public static Set<String> getStringSet(String key, Set<String> def) {
        return mSharedPreferences.getStringSet(key, def);
    }
    
    /**
     * 从users.xml里面删除字段为 keys 数据
     * @param keys
     */
    public static void removeAllKeys(String[] keys) {
        mEditor = mSharedPreferences.edit();
        for (String key : keys) {
            mEditor.remove(key);
        }
        mEditor.apply();
    }
}
