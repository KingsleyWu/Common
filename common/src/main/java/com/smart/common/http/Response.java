package com.smart.common.http;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Response实体类
 * @param <T>
 */
public class Response<T> implements Serializable{
    public String code;
    public String msg;
    public T data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * data 返回一个Map类型，直接通过键值对获取value
     * @param json 解析的数据
     * @return  当前对象
     */
    public static Response fromJson2(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json,Response.class);
    }

    /**
     * data 返回一个传入类型的对象
     * @param json  解析的数据
     * @param clz
     * @return
     */
    public static Response fromJson(String json, Class clz) {
        Gson gson = new Gson();
        Type objectType = type(Response.class, clz);
        return gson.fromJson(json, objectType);
    }

    /**
     * 把当前对象转发为Json格式
     * @param clazz
     * @return
     */
    public String toJson(Class<T> clazz) {
        Gson gson = new Gson();
        Type objectType = type(Response.class, clazz);
        return gson.toJson(this, objectType);
    }

    /**
     * 获取GSON解析的type
     * @param raw 传入最外层对象反射
     * @param args 传入对象中的所有泛型
     * @return
     */
    private static ParameterizedType type(final Class raw, final Type... args) {

        return new ParameterizedType() {
            @Override
            public Type getRawType() {
                return raw;
            }

            @Override
            public Type[] getActualTypeArguments() {
                return args;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }
}
