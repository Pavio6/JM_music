package com.jlf.music.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.controller.dto.SongFormDTO;
import com.jlf.music.controller.qry.SongQry;
import com.jlf.music.controller.vo.SongDetailVo;
import com.jlf.music.controller.vo.SongLyricsAndAudioVo;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.entity.SongInfo;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SongInfoService extends IService<SongInfo> {
    /**
     * 分页或通过名称模糊查询歌曲信息
     *
     * @param songQry 分页参数
     * @return songVo
     */
    IPage<SongBasicInfoVo> getSongsByPage(SongQry songQry);


    /**
     * 根据songId获取歌曲音频和歌词文件url
     *
     * @param songId 歌曲ID
     * @return SongLyricsAndAudioVo
     */
    SongLyricsAndAudioVo getSongAudioAndLyricsById(Long songId);

    /**
     * 下载歌曲
     *
     * @param songId 歌曲id
     * @return ResponseEntity<InputStreamResource>
     */
    ResponseEntity<InputStreamResource> downloadSong(Long songId);

    /**
     * 获取新歌榜
     *
     * @return 歌曲列表
     */
    List<SongBasicInfoVo> getNewSongs();

    /**
     * 获取热歌榜
     *
     * @return 歌曲列表
     */
    List<SongBasicInfoVo> getHotSongs();


    /**
     * 获取飙升榜
     *
     * @return 歌曲列表
     */
    List<SongBasicInfoVo> getRisingSongs();

    /**
     * 更新歌曲
     */
    Boolean updateSong(Long songId, MultipartFile songLyricsFile, MultipartFile songFile, MultipartFile songCoverFile, SongFormDTO songFormDTO);

    /**
     * 获取歌曲详情
     *
     * @param songId 歌曲id
     */
    SongDetailVo getSongDetail(Long songId);

    /**
     * 新增歌曲
     */
    Boolean addSong(SongFormDTO songFormDTO, MultipartFile songLyricsFile, MultipartFile songFile, MultipartFile songCoverFile, MultipartFile mvFilePath480p, MultipartFile mvFilePath720p, MultipartFile mvFilePath1080p);

    /**
     * 根据id增加播放量
     *
     * @param targetId   歌曲/歌单id
     * @param targetType 歌曲/歌单
     * @return Boolean
     */
    Boolean incrementPlayCountByTargetId(Long targetId, String targetType);

    /**
     * 删除歌曲
     */
    Boolean deleteSongById(Long songId);

    void deleteSongInfo(Long songId);
}
