package com.jlf.music.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.controller.qry.SongQry;
import com.jlf.music.controller.vo.SongLyricsAndAudioVo;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.entity.SongInfo;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SongInfoService extends IService<SongInfo> {
    /**
     * 分页或通过名称模糊查询歌曲信息
     * @param songQry 分页参数
     * @return songVo
     */
    IPage<SongBasicInfoVo> getSongsByPage(SongQry songQry);

    /**
     * 获取最近的歌曲信息
     * @return List<SongBasicInfoVo>
     */
    List<SongBasicInfoVo> getLatestSongs();

    /**
     * 根据songId获取歌曲音频和歌词文件url
     * @param songId 歌曲ID
     * @return SongLyricsAndAudioVo
     */
    SongLyricsAndAudioVo getSongAudioAndLyricsById(Long songId);

    /**
     * 下载歌曲
     * @param songId 歌曲id
     * @return ResponseEntity<InputStreamResource>
     */
    ResponseEntity<InputStreamResource> downloadSong(Long songId);
}
