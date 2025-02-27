package com.jlf.music;

import com.jlf.music.common.enumerate.UploadFileType;
import com.jlf.music.service.FileService;
import jakarta.annotation.Resource;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.datatype.Artwork;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * ---文件上传测试---
 * MockMultipartFile 类位于 org.springframework.mock.web 包下，它实现了 MultipartFile 接口
 * 通过该类可以创建一个模拟的文件上传对象，方便进行单元测试或集成测试。
 * MockMultipartFile(String name, @Nullable String originalFilename, @Nullable String contentType, InputStream contentStream)
 * name：文件在HTML表单中的名称
 * originalFilename：此参数代表文件的原始名称，即文件在客户端的实际文件名，包含文件扩展名
 * contentType：该参数指定文件的 MIME 类型，MIME 类型用于标识文件的类型和格式
 * contentStream：该参数是一个输入流，包含了文件的实际内容
 * MIME类型：
 * "text/plain" 纯文本
 * - 图像(image)类型
 * "image/jpeg" .jpg图像文件
 * "image/png" .png图像文件
 * "image/gif" .gif图像文件
 * - 音频(audio)类型
 * "audio/mpeg" .mp3 MP3音频文件
 * "audio/ogg" .ogg OGG Vorbis音频文件
 * - 视频(video)类型
 * "video/mp4" .mp4 MP4视频文件
 * "video/ogg" .ogv Ogg视频文件
 */

@SpringBootTest
public class FileUploadTest {
    @Resource
    private FileService fileService;

    /**
     * 上传歌曲歌词文件到minio
     */
    @Test
    public void uploadSongLyrics() throws IOException {
        File file = new File("C:\\Users\\93198\\Desktop\\song-resource\\林俊杰\\林俊杰 - 光阴副本.lrc");
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile mockMultipartFile = new MockMultipartFile(
                file.getName(),
                file.getName(),
                "text/plain",
                fileInputStream
        );
        String s = fileService.uploadImageFile(mockMultipartFile, UploadFileType.SONG_LYRICS);
        System.out.println("s = " + s);
        System.out.println("s = " + s);
    }

    /**
     * 上传歌曲音频文件到minio
     * 歌曲格式为 mp3/ogg
     */
    @Test
    public void uploadSongAudio() throws IOException {
        File file = new File("C:\\Users\\93198\\Desktop\\song-resource\\林俊杰\\林俊杰 - 光阴副本.mp3");
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile mockMultipartFile = new MockMultipartFile(
                file.getName(),
                file.getName(),
                "audio/mpeg",
                fileInputStream
        );
        String s = fileService.uploadImageFile(mockMultipartFile, UploadFileType.SONG_AUDIO);
        System.out.println("s = " + s);
        System.out.println("s = " + s);
    }

    /**
     * 从MP3文件中解析出封面图
     */
    @Test
    public void extractCover() throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        // 读取 MP3 文件
        AudioFile audioFile = AudioFileIO.read(new File("C:\\Users\\93198\\Desktop\\song-resource\\林俊杰\\林俊杰 - 光阴副本.mp3"));
        // 获取 MP3 文件的标签信息
        Tag tag = audioFile.getTag();
        // 获取封面图片
        Artwork artwork = tag.getFirstArtwork();
        if (artwork == null) {
            System.out.println("MP3 文件中未找到封面图片");
        }
        // 将封面图片保存到指定路径
        assert artwork != null;
        byte[] imageData = artwork.getBinaryData();
        // 确定图片的格式，这里假设为 JPEG
        String imageFormat = "jpg";
        // 指定保存图片的路径
        String savePath = "C:\\Users\\93198\\Desktop\\cover-光阴副本." + imageFormat;
        try (FileOutputStream fos = new FileOutputStream(savePath)) {
            fos.write(imageData);
        }
        System.out.println("封面图片已保存到: " + savePath);
    }

    /**
     * 上传歌曲封面图到minio
     */
    @Test
    public void uploadSongCover() throws IOException {
        File file = new File("C:\\Users\\93198\\Desktop\\光阴副本-cover.jpg");
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile mockMultipartFile = new MockMultipartFile(
                file.getName(),
                file.getName(),
                "image/jpeg",
                fileInputStream
        );
        String s = fileService.uploadImageFile(mockMultipartFile, UploadFileType.SONG_COVER);
        System.out.println("s = " + s);
        System.out.println("s = " + s);
    }
    /**
     * 上传用户头像到minio
     */
    @Test
    public void uploadUserAvatar() throws IOException {
        File file = new File("C:\\Users\\93198\\Desktop\\test-avatar.jpg");
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile mockMultipartFile = new MockMultipartFile(
                file.getName(),
                file.getName(),
                "image/jpeg",
                fileInputStream
        );
        String s = fileService.uploadImageFile(mockMultipartFile, UploadFileType.USER_AVATAR);
        System.out.println("s = " + s);
        System.out.println("s = " + s);
    }
}
