package com.jlf.music.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.jlf.music.common.enumerate.UploadFileType;
import com.jlf.music.config.MinioInfo;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.service.FileService;
import com.jlf.music.utils.MinIOPathParser;
import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    // 定义允许的图片格式
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
    // 允许的音频文件格式列表
    private static final List<String> ALLOWED_AUDIO_EXTENSIONS = Arrays.asList(".mp3", ".wav", ".ogg");
    // 允许的歌词文件格式列表
    private static final List<String> ALLOWED_LYRICS_EXTENSIONS = List.of(".lrc");
    @Resource
    private MinioClient minioClient;
    @Resource
    private MinioInfo minioInfo;


    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return Boolean
     */
    @Override
    public Boolean deleteFile(String filePath) {
        try {
            MinIOPathParser.BucketAndFileName bucketAndFileName = MinIOPathParser.parseBucketAndFileName(filePath);
            String fileName = bucketAndFileName.fileName();
            String bucketName = bucketAndFileName.bucketName();
            try {
                // 先检查文件是否存在
                minioClient.statObject(
                        StatObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .build()
                );
            } catch (ErrorResponseException e) {
                if (e.errorResponse().code().equals("NoSuchKey")) {
                    log.warn("文件不存在，无需删除: {}", filePath);
                    return true; // 或根据业务需求返回 false
                }
                throw e; // 其他异常继续抛出
            }
            // MinIO默认删除机制：删除一个不存在的文件，默认不会抛出异常，而是静默返回成功
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            log.info("删除成功, 文件路径为: {}", filePath);
            return true;
        } catch (Exception e) {
            log.error("删除失败", e);
            return false;
        }
    }

    /**
     * 上传图片类资源
     *
     * @param file           文件
     * @param uploadFileType 文件类型
     * @return 文件名
     */
    @Override
    public String uploadImageFile(MultipartFile file, UploadFileType uploadFileType) {
        if (file.isEmpty()) {
            throw new ServiceException("上传的文件为空");
        }
        // 获取文件名
        String originalFilename = generateFileName(file.getOriginalFilename());
        if (StrUtil.isEmpty(originalFilename)) {
            throw new ServiceException("无法获取文件名称");
        }
        // 获取文件扩展名 .jpg 等等
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 检查是否支持该扩展名
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new ServiceException("不支持的文件格式，仅支持：" + String.join(", ", ALLOWED_IMAGE_EXTENSIONS));
        }
        try {
            // 根据文件类型获取桶的名称
            String bucketName = uploadFileType.getBucketName();
            // 检查桶是否存在，如果不存在则创建
            ensureBucketExists(bucketName);
            // 生成唯一的文件名
            String fileName = generateFileName(originalFilename);

            // 上传文件到MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName) // 存储桶
                            .object(fileName) // 上传到minio服务器上后 文件的名称
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            log.info("图片上传成功");
            // 返回文件访问 URL
            return generateFileUrl(bucketName, fileName);
        } catch (Exception e) {
            log.error("图片上传失败", e);
            throw new ServiceException("图片上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传歌曲音频歌词资源
     * 音频：".mp3", ".wav", ".ogg"
     * 歌词：".lrc"
     * @param file           文件
     * @param uploadFileType 文件类型
     * @return 文件名
     */
    @Override
    public String uploadSongFile(MultipartFile file, UploadFileType uploadFileType) {
        if (file.isEmpty()) {
            throw new ServiceException("上传的文件为空");
        }
        // 获取文件名
        String originalFilename = generateFileName(file.getOriginalFilename());
        if (StrUtil.isEmpty(originalFilename)) {
            throw new ServiceException("无法获取文件名称");
        }
        // 获取文件扩展名 .jpg 等等
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 根据文件类型检查文件格式
        if (uploadFileType == UploadFileType.SONG_AUDIO) {
            if (!ALLOWED_AUDIO_EXTENSIONS.contains(fileExtension.toLowerCase())) {
                throw new IllegalArgumentException("不支持的音频文件格式，仅支持：" + String.join(", ", ALLOWED_AUDIO_EXTENSIONS));
            }
        } else if (uploadFileType == UploadFileType.SONG_LYRICS) {
            if (!ALLOWED_LYRICS_EXTENSIONS.contains(fileExtension.toLowerCase())) {
                throw new IllegalArgumentException("不支持的歌词文件格式，仅支持：" + String.join(", ", ALLOWED_LYRICS_EXTENSIONS));
            }
        }
        try {
            // 根据文件类型获取桶的名称
            String bucketName = uploadFileType.getBucketName();
            // 检查桶是否存在，如果不存在则创建
            ensureBucketExists(bucketName);
            // 生成唯一的文件名
            String fileName = generateFileName(originalFilename);

            // 上传文件到MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName) // 存储桶
                            .object(fileName) // 上传到minio服务器上后 文件的名称
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            log.info("歌曲资源上传成功");
            // 返回文件访问 URL
            return generateFileUrl(bucketName, fileName);
        } catch (Exception e) {
            log.error("歌曲资源上传失败", e);
            throw new ServiceException("歌曲资源上传失败: " + e.getMessage());
        }
    }

    /**
     * 下载文件
     *
     * @param bucketName 存储桶
     * @param fileName 文件名
     * @return InputStream
     */
    @Override
    public InputStream downloadFile(String bucketName, String fileName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("下载文件失败: " + e.getMessage());
        }
    }

    /**
     * 生成唯一的文件名
     */
    private String generateFileName(String originalFileName) {
        // 使用时间戳和UUID生成唯一文件名
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuidStr = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        return timestamp + "_" + uuidStr + "_" +    originalFileName;
    }

    /**
     * 确保桶存在，如果不存在则创建
     */
    private void ensureBucketExists(String bucketName) throws
            MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        // 桶是否存在
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());
        // 桶不存在
        if (!bucketExists) {
            // 创建桶
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
            log.info("Bucket创建成功: {}", bucketName);

            // 设置Bucket访问权限为public
            setBucketPublic(bucketName);
            log.info("Bucket访问权限设置为public: {}", bucketName);
        }
    }

    /**
     * 生成文件访问 URL
     */
    private String generateFileUrl(String bucketName, String fileName) {
        return String.format("%s/%s/%s",
                minioInfo.getEndpoint(), // MinIO 服务地址
                bucketName,                // 桶名称
                fileName);                 // 文件名
    }

    /**
     * 设置Bucket访问权限为public
     *
     * @param bucketName 桶名称
     */
    private void setBucketPublic(String bucketName) {
        // 设置Bucket策略为public
        String bucketPolicy = "{"
                + "\"Version\":\"2012-10-17\","
                + "\"Statement\":["
                + "    {"
                + "        \"Effect\":\"Allow\","
                + "        \"Principal\":\"*\","
                + "        \"Action\":\"s3:GetObject\","
                + "        \"Resource\":\"arn:aws:s3:::" + bucketName + "/*\""
                + "    }"
                + "]"
                + "}";

        try {
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucketName)
                            .config(bucketPolicy)
                            .build()
            );
        } catch (ErrorResponseException | NoSuchAlgorithmException | ServerException | XmlParserException |
                 InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException |
                 IOException e) {
            throw new ServiceException("设置桶的访问权限失败: " + e.getMessage());
        }
    }
}
