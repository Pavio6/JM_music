package com.jlf.music.utils;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.UUID;

public class SaltUtils {

    // 私有构造函数，防止实例化
    private SaltUtils() {
        throw new IllegalStateException("SaltUtils class");
    }

    /**
     * 生成随机盐（UUID），用于密码加密时加入额外的安全性
     * 盐是随机生成的，可以防止相同的密码生成相同的加密结果
     * @return 返回一个唯一的盐值，类型为String
     */
    public static String createSalt() {
        // 使用UUID生成一个全局唯一的ID作为盐
        UUID randomUuId = UUID.randomUUID();
        return randomUuId.toString();  // 将UUID转换为字符串
    }

    /**
     * 使用MD5算法加密密码，并加入盐进行加密，返回Base64编码的加密结果
     * MD5加密后再与盐组合，可以防止彩虹表攻击
     *
     * @param password 密码（明文）
     * @param salt     盐，用于增加加密强度
     * @return 返回加密后的密码，类型为String
     * @throws RuntimeException 当MD5加密过程出现异常时抛出
     */
    public static String md5Password(String password, String salt) {
        try {
            // 获取MD5加密算法的实例
            MessageDigest digest = MessageDigest.getInstance("MD5");
            // 将密码和盐值合并后进行加密
            byte[] md5Password = digest.digest((password + salt).getBytes());
            // 使用Base64编码加密后的字节数组，使其可以以可读的形式返回
            Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(md5Password);  // 返回Base64编码后的加密密码
        } catch (Exception e) {
            throw new RuntimeException("加密失败!");  // 若加密过程出现异常，则抛出运行时异常
        }
    }

    /**
     * 验证密码与存储的加密密码是否一致，使用盐进行密码对比
     * 比较密码是否正确，即通过对比加密后的密码与存储的加密结果
     *
     * @param password    用户输入的明文密码
     * @param salt        使用的盐
     * @param passwordMd5 数据库中存储的加密密码
     * @return 返回布尔值，密码是否匹配（true表示匹配，false表示不匹配）
     */
    public static boolean verify(String password, String salt, String passwordMd5) {
        // 使用输入的密码和盐进行加密，然后与数据库存储的加密密码进行比较
        return md5Password(password, salt).equals(passwordMd5);
    }

}
