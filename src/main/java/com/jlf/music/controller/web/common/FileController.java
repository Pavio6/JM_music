package com.jlf.music.controller.web.common;

import com.jlf.music.common.enumerate.UploadFileType;
import com.jlf.music.common.Result;
import com.jlf.music.service.FileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/common/file")
public class FileController {
    @Resource
    private FileService fileService;


    /**
     * 移除文件从minio服务器中
     */
    @DeleteMapping("/delete")
    public Result<Boolean> deleteFile(
            @RequestParam("filePath") String filePath) {
        return Result.success(fileService.deleteFile(filePath));
    }

    /**
     * 上传图片类资源
     * ".jpg", ".jpeg", ".png", ".gif"
     */
    @PostMapping("/upload/image")
    public Result<String> uploadImageFile(@RequestParam("file") MultipartFile file,
                                          @RequestParam("uploadFileType") UploadFileType uploadFileType) {
        String fileUrl = fileService.uploadImageFile(file, uploadFileType);
        return Result.success(fileUrl);
    }

    /**
     * 上传歌曲类资源
     * 音频：".mp3", ".wav", ".ogg"
     * 歌词：".lrc"
     */
    @PostMapping("/upload/song")
    public Result<String> uploadSongFile(@RequestParam("file") MultipartFile file,
                                         @RequestParam("uploadFileType") UploadFileType uploadFileType) {
        String fileUrl = fileService.uploadSongFile(file, uploadFileType);
        return Result.success(fileUrl);
    }
}
