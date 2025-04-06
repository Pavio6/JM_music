package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.entity.PlaylistTags;
import com.jlf.music.mapper.PlaylistTagsMapper;
import com.jlf.music.service.PlaylistTagsService;
import org.springframework.stereotype.Service;

/**
 * @author JLF
 * @date 2025/4/2 10:15
 * @description xxx
 */
@Service
public class PlaylistTagsServiceImpl extends ServiceImpl<PlaylistTagsMapper, PlaylistTags>
        implements PlaylistTagsService {
}
