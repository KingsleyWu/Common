package com.smart.common.data;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Map;

/**
 * 
 * @author 钟何亮
 * @date 2017年6月13日
 * @project LunznTool
 * @package com.smart.data
 * @filename DataFormat.java
 * @version 1.0
 *  数据操作类
 * @since 通用工具jar
 */
public class DataFormat {
    
    /**
     * 非空判断，字符串null返回false
     * @param obj
     * @return 空，返回true, ""这种空字符串返回true，否则返回false
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        
        if ((isString(obj)) || (isChar(obj))) {
            return (obj + "").trim().length() == 0;
          }
        
        if (obj instanceof CharSequence) {
            return (((CharSequence)obj) + "").trim().length() == 0;
        }
        
        if (obj instanceof Collection) {
            return ((Collection)obj).isEmpty();
        }
        
        if (obj instanceof Map) {
            return ((Map)obj).isEmpty();
        }
        
        if (obj instanceof Object[]) {
            Object[] object = (Object[])obj;
            if (object.length == 0) {
                return true;
            }
            boolean empty = true;
            for (int i = 0; i < object.length; i++) {
                if (!isEmpty(object[i])) {
                    empty = false;
                    break;
                }
            }
            return empty;
        }
        return false;
    }

    /**
     * 非空判断，字符串null返回true
     * @param obj
     * @return 空，返回false, ""这种空字符串返回false，否则返回true
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }
    
    /**
     * 转换成boolean值
     * @param obj
     * @return 如果转换错误，默认返回false
     */
    public static boolean getBoolean(Object obj) {
        if (isEmpty(obj)) {
            return false;
        }

        if (isBoolean(obj)) {
            return (Boolean) obj;
        }

        if ((isChar(obj)) || (isString(obj))) {
            String bln = ((String) obj).toUpperCase();
            return ("Y".equals(bln)) || ("TRUE".equals(bln)) || ("1".equals(bln));
        }

        return isNumeric(obj) && getDouble0(obj) > 0.0D;

    }
    
    /**
     * 转换成Byte值
     * @param obj
     * @return 
     */
    public static byte getByte(Object obj) {
        return getDouble0(obj).byteValue();
    }
    
    /**
     * 转换成char值
     * @param obj
     * @return 
     */
    public static char getChar(Object obj) {
        if (isBoolean(obj)) {
            return getBoolean(obj) ? 'Y' : 'N';
        }
        
        if (isChar(obj)) {
            return (Character) obj;
        }
        
        if (isNumeric(obj)) {
            return (char)getByte(obj);
        }
        
        String str = getString(obj);
        return isEmpty(str) ? ' ' : str.charAt(0);
    }
    
    /**
     * 转换成double值
     * @param obj
     * @return 
     */
    public static double getDouble(Object obj) {
        return getDouble0(obj);
    }
    
    /**
     * 转换成Double类型的值
     * @param obj
     * @return
     */
    public static Double getDouble0(Object obj) {
        double result;
        if (null == obj) {
            result = 0.0D;
        } else {
            if (isBoolean(obj)) {
                result = ((Boolean) obj ? 1 : 0) + 0.0D;
            } else {
                if (isChar(obj)) {
                    result = (Character) obj + 0.0D;
                } else {
                    if (isNumeric(obj)) {
                        result = Double.parseDouble(obj.toString());
                    } else {
                        if (isString(obj)) {
                            String temp = obj.toString();
                            try {
                                result = Double.parseDouble(temp);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                result = 0.0D;
                            }
                        } else {
                            result = 0.0D;
                        }
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * 转换成float值
     * @param obj
     * @return 
     */
    public static float getFloat(Object obj) {
        return getDouble0(obj).floatValue();
    }
    
    /**
     * 转换成整形值
     * @param obj
     * @return 正确返回整形值，错误，返回-1
     */
    public static int getInteger(Object obj) {
        return getDouble0(obj).intValue();
    }
    
    /**
     * 转换成long值
     * @param obj
     * @return 
     */
    public static long getLong(Object obj) {
        return getDouble0(obj).longValue();
    }
    
    /**
     * 转换成short值
     * @param obj
     * @return 
     */
    public static short getShort(Object obj) {
        return getDouble0(obj).shortValue();
        
    }
    
    /**
     * 转换成String值
     * @param obj
     * @return 字符串值，如果是空的，则返回""
     */
    public static String getString(Object obj) {
        String result;
        if (isEmpty(obj)) {
            result = "";
        } else {
            if (isString(obj)) {
                result = (String)obj;
            } else {
                if (isBoolean(obj)) {
                    result = getBoolean(obj) ? "true" : "false";
                } else {
                    result = String.valueOf(obj);
                }
            }
        }
        return result;
    }
    
    /**
     * 是否是Array类型的值
     * @param obj 需要判断的值
     * @return 如果是，返回true，否则返回false
     */
    public static boolean isArray(Object obj) {
        if (obj == null) {
            return false;
        }
        return obj.getClass().isArray();
    }
    
    /**
     * 是否是boolean类型的值
     * @param obj 需要判断的值
     * @return 如果是，返回true，否则返回false
     */
    public static boolean isBoolean(Object obj) {
        return obj instanceof Boolean;
    }
    
    /**
     * 是否是Byte类型的值
     * @param obj 需要判断的值
     * @return 如果是，返回true，否则返回false
     */
    public static boolean isByte(Object obj) {
        return obj instanceof Byte;
    }
    
    /**
     * 是否是Char类型的值
     * @param obj 需要判断的值
     * @return 如果是，返回true，否则返回false
     */
    public static boolean isChar(Object obj) {
        return obj instanceof Character;
    }
    
    /**
     * 是否是Double类型的值
     * @param obj 需要判断的值
     * @return 如果是，返回true，否则返回false
     */
    public static boolean isDouble(Object obj) {
        return obj instanceof Double;
    }
    
    /**
     * 是否是Float类型的值
     * @param obj 需要判断的值
     * @return 如果是，返回true，否则返回false
     */
    public static boolean isFloat(Object obj) {
        return obj instanceof Float;
    }
    
    /**
     * 是否是int类型的值
     * @param obj 需要判断的值
     * @return 如果是，返回true，否则返回false
     */
    public static boolean isInteger(Object obj) {
        return obj instanceof Integer;
    }
    
    /**
     * 是否是long类型的值
     * @param obj 需要判断的值
     * @return 如果是，返回true，否则返回false
     */
    public static boolean isLong(Object obj) {
        return obj instanceof Long;
    }
    
    /**
     * 是否是Numeric类型的值
     * @param obj 需要判断的值
     * @return 如果是，返回true，否则返回false
     */
    public static boolean isNumeric(Object obj) {
        boolean result = (isInteger(obj)) || (isShort(obj)) || (isLong(obj)) || (isFloat(obj));
        result = (result) || (isDouble(obj)) || (isByte(obj)) || (isChar(obj));
        return result;
    }
    
    /**
     * 是否是short类型的值
     * @param obj 需要判断的值
     * @return 如果是，返回true，否则返回false
     */
    public static boolean isShort(Object obj) {
        return obj instanceof Short;
    }
    
    /**
     * 是否是String类型的值
     * @param obj 需要判断的值
     * @return 如果是，返回true，否则返回false
     */
    public static boolean isString(Object obj) {
        return ((obj instanceof String)) || ((obj instanceof StringBuffer)) || ((obj instanceof StringBuilder));
    }
    
    /**
     * 转换文件大小单位(B，K，M，G)
     * @param fileSize 文件大小
     * @return 带单位的文件大小，保留2位小数
     */
    public static String formetFileSize(long fileSize) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileSize < 1024) {
            fileSizeString = df.format((double)fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format((double)fileSize / 1024) + "K";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double)fileSize / 1048576) + "M";
        } else {
            fileSizeString = df.format((double)fileSize / 1073741824) + "G";
        }
        return fileSizeString;
    }
    
    /**
     * 对比以"."为分隔符的版本号，可以对比包含多个小数点的数据
     * @param in1 对比目标数据
     * @param in2 对比原始数据
     * @return -1，数据错误，0，没有更新，1，有更新
     */
    public static int compareVersion(String in1, String in2) {
        String[] ins1 = in1.split("\\.");
        String[] ins2 = in2.split("\\.");
        for (int i = 0; i < ins1.length; i++) {
            int v1 = Integer.valueOf(ins1[i]);
            if (ins2.length >= i + 1) {
                int v2 = Integer.valueOf(ins2[i]);
                if (v1 > v2) {
                    return 1;
                } else if (v1 < v2) {
                    return -1;
                }
            } else {
                return 1;
            }
        }
        if (ins1.length < ins2.length) {
            return -1;
        }
        return 0;
    }
}
