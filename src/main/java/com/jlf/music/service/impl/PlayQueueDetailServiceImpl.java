package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.entity.PlayQueueDetail;
import com.jlf.music.mapper.PlayQueueDetailMapper;
import com.jlf.music.service.PlayQueueDetailService;
import org.springframework.stereotype.Service;

@Service
public class PlayQueueDetailServiceImpl extends ServiceImpl<PlayQueueDetailMapper, PlayQueueDetail>
        implements PlayQueueDetailService {
}
