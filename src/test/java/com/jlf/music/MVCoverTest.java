package com.jlf.music;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Slf4j
@SpringBootTest
public class MVCoverTest {
    /**
     * 获取mp4文件的第一帧 作为.jpg图片
     */
    @Test
    public void testExtractCoverFromMp4() {
        String inputFilePath = "C:\\Users\\93198\\Desktop\\song-resource\\MV\\林俊杰-对的时间点(超清).mp4";
        String outputDirPath = "C:\\Users\\93198\\Desktop\\mv"; // 输出目录

        // 创建输出目录（如果不存在）
        File outputDir = new File(outputDirPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // 生成输出文件名（从原文件名获取）
        File inputFile = new File(inputFilePath);
        String outputFileName = inputFile.getName().replaceAll("\\.mp4$", "") + "_cover.jpg";
        String outputFilePath = outputDirPath + File.separator + outputFileName;

        try {
            extractCoverFromMp4(inputFilePath, outputFilePath);

            // 验证文件是否创建成功
            File outputFile = new File(outputFilePath);
            assert outputFile.exists() : "Cover image was not created!";

            System.out.println("Cover successfully extracted to: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 从MP4文件中提取第一帧作为封面
     *
     * @param videoPath  MP4文件路径
     * @param outputPath 输出图片路径（包含文件名）
     */
    private void extractCoverFromMp4(String videoPath, String outputPath) {
        FFmpegFrameGrabber grabber = null;
        try {
            System.out.println("Starting to extract cover from: " + videoPath);

            // 初始化帧抓取器
            grabber = new FFmpegFrameGrabber(videoPath);
            grabber.start();

            // 跳过前几帧，有时第一帧可能是黑屏
            grabber.setFrameNumber(5);

            // 抓取一帧
            Frame frame = grabber.grabImage();

            if (frame != null) {
                // 将Frame转换为BufferedImage
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage bufferedImage = converter.convert(frame);

                // 保存为图片文件
                File output = new File(outputPath);
                ImageIO.write(bufferedImage, "jpg", output);

                System.out.println("Successfully extracted cover to: " + outputPath);
            } else {
                System.err.println("Failed to grab frame from video");
            }
        } catch (Exception e) {
            System.err.println("Error extracting cover: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭抓取器
            if (grabber != null) {
                try {
                    grabber.stop();
                    grabber.release();
                } catch (FFmpegFrameGrabber.Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    // -------------------------------

    /**
     * 另一种方法：直接从视频元数据中提取封面（如果存在）
     */
    @Test
    public void testExtractCoverFromMetadata() {
        String inputFilePath = "C:\\Users\\93198\\Desktop\\song-resource\\MV\\林俊杰-对的时间点(高清).mp4"; // 放在resources目录下的测试视频
        String outputFilePath = "C:\\Users\\93198\\Desktop"; // 输出的封面图片路径

        try {
            File videoFile = ResourceUtils.getFile(inputFilePath);
            extractCoverFromMetadata(videoFile.getAbsolutePath(), outputFilePath);

            File outputFile = new File(outputFilePath);
            if (outputFile.exists()) {
                System.out.println("Metadata cover successfully extracted to: " + outputFile.getAbsolutePath());
            } else {
                System.out.println("No metadata cover found in the video file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从MP4文件的元数据中提取封面图片
     *
     * @param videoPath  MP4文件路径
     * @param outputPath 输出图片路径
     */
    private void extractCoverFromMetadata(String videoPath, String outputPath) {
        FFmpegFrameGrabber grabber = null;
        try {
            grabber = new FFmpegFrameGrabber(videoPath);
            grabber.start();

            // 尝试从元数据中获取图片
            if (grabber.getVideoMetadata("cover_art") != null) {
                byte[] coverArt = grabber.getVideoMetadata("cover_art").getBytes();
                if (coverArt != null && coverArt.length > 0) {
                    File output = new File(outputPath);
                    java.nio.file.Files.write(output.toPath(), coverArt);
                }
            } else {
                System.out.println("No cover art found in metadata");
            }
        } catch (Exception e) {
            System.err.println("Error extracting metadata cover: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (grabber != null) {
                try {
                    grabber.stop();
                    grabber.release();
                } catch (FFmpegFrameGrabber.Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}