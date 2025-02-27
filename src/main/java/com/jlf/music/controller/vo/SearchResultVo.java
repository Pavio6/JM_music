package com.jlf.music.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class SearchResultVo {
    private List<SongSimpleInfoVo> songs;
    private List<SingerSearchVo> singers;
    private List<AlbumSearchVo> albums;
}
