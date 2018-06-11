package com.smart.common.xml;
public interface EditorInter {
    
    /**
     * 存储键值对
     * @param key 数据key
     * @param value 数据value
     */
    public void put(String key, String value);
    
    /**
     * 删除key对应的存储数据
     * @param key 数据key名称
     */
    public void delete(String key);
    
    /**
     * 提交数据覆盖源文件
     */
    public void commit();
}
