package com.jlf.music.service;

import com.jlf.music.common.enumerate.UploadFileType;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileService {


    /**
     * 删除文件
     * @param filePath 文件路径
     * @return Boolean
     */
    Boolean deleteFile(String filePath);

    /**
     * 上传图片类资源
     * @param file 文件
     * @param uploadFileType 文件类型
     * @return 文件名
     */
    String uploadImageFile(MultipartFile file, UploadFileType uploadFileType);

    /**
     * 上传歌曲音频歌词资源
     * @param file 文件
     * @param uploadFileType 文件类型
     * @return 文件名
     */
    String uploadSongFile(MultipartFile file, UploadFileType uploadFileType);

    /**
     * 下载歌曲文件
     *
     * @param bucketName 存储桶
     * @param fileName 文件名
     * @return InputStream
     */
    InputStream downloadFile(String bucketName, String fileName);
}
