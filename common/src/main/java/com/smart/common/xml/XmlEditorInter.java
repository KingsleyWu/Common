package com.smart.common.xml;

public interface XmlEditorInter {
    
    /**
     * 保存 key-value键值
     * @param key 数据key名称
     * @param value 数值
     */
    public EditorInter edit();
    
    /**
     * 获取key对应的int值
     * @param key 数值key名称
     * @param defaultValue int默认值
     * @return key对应的int值
     */
    public int getInt(String key, int defaultValue);
    
    /**
     * 获取key对应的String值
     * @param key 数值key名称
     * @return key对应的String值
     */
    public String getString(String key);
    
    /**
     * 获取key对应的bool值
     * @param key 数值key名称
     * @param defaultValue bool默认值
     * @return key对应的boolean值
     */
    public boolean getBoolean(String key, boolean defaultValue);
    
    /**
     * 获取key对应的long值
     * @param key 数值key名称
     * @param defaultValue long默认值
     * @return key对应的long值
     */
    public long getLong(String key, long defaultValue);
    
    /**
     * 获取key对应的float值
     * @param key 数值key名称
     * @param defaultValue float默认值
     * @return key对应的float值
     */
    public float getFloat(String key, float defaultValue);
    
    /**
     * 获取key对应的double值
     * @param key 数值key名称
     * @param defaultValue double默认值
     * @return key对应的double值
     */
    public double getDouble(String key, double defaultValue);
}
