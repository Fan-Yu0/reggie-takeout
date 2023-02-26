package com.xm.reggie.common;


/**
 * 基于ThreadLocal封装工具类，用于存储当前线程的数据
 * @author YU
 */
public class BaseContext {
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的用户id
     */
    public static void setCurrentUser(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取当前线程的用户id
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
