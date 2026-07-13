package com.jlf.music.utils;

/**
 * 存储当前线程的管理员登录信息
 */
public class AdminHolder {
    private static final ThreadLocal<AdminContext> tl = new ThreadLocal<>();

    /**
     * 设置当前线程的变量值
     */
    public static void saveAdmin(AdminContext adminContext){
        tl.set(adminContext);
    }

    /**
     * @return 返回当前线程所对应的变量值
     */
    public static AdminContext getAdmin(){
        return tl.get();
    }

    /**
     * 删除当前线程所对应的变量值
     */
    public static void removeAdmin(){
        tl.remove();
    }
}
