package com.jlf.music.utils;

/**
 * 解析MinIO路径的工具类
 */
public class MinIOPathParser {
    // 解析桶名和文件名的方法
    public static BucketAndFileName parseBucketAndFileName(String filePath) {
        // 找到协议和主机部分之后的路径起始位置
        int pathStartIndex = filePath.indexOf('/', filePath.indexOf("://") + 3);
        if (pathStartIndex == -1) {
            throw new IllegalArgumentException("Invalid MinIO URL: " + filePath);
        }
        // 桶和文件名 路径
        String path = filePath.substring(pathStartIndex + 1);
        // 截取桶的名字 从path开始 到第一个 /
        String bucketName = path.substring(0, path.indexOf('/'));
        // 截取文件名
        String fileName = path.substring(path.indexOf('/') + 1);
        return new BucketAndFileName(bucketName, fileName);
    }

    // 封装桶名和文件名的类
    // 记录类是一种不可变的数据载体 主要用于存储和传递数据
    public record BucketAndFileName(String bucketName, String fileName) {}
}
