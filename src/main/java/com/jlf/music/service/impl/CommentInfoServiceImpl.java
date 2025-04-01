package com.jlf.music.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.common.enumerate.TargetType;
import com.jlf.music.controller.dto.CommentDTO;
import com.jlf.music.controller.qry.CommentQry;
import com.jlf.music.controller.vo.CommentTreeVo;
import com.jlf.music.entity.CommentInfo;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.*;
import com.jlf.music.service.CommentInfoService;
import com.jlf.music.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jlf.music.common.constant.RedisConstant.COMMENT_LIKED_KEY_PREFIX;

@Service
public class CommentInfoServiceImpl extends ServiceImpl<CommentInfoMapper, CommentInfo>
        implements CommentInfoService {
    @Resource
    private CommentInfoMapper commentInfoMapper;
    @Resource
    private AlbumInfoMapper albumInfoMapper;
    @Resource
    private SongInfoMapper songInfoMapper;
    @Resource
    private PlaylistInfoMapper playlistInfoMapper;
    @Resource
    private SongMvMapper songMvMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 添加评论
     *
     * @param commentDTO 歌曲评论信息
     * @return Boolean
     */
    @Override
    @Transactional
    public Boolean addComment(CommentDTO commentDTO) {
        // 判断评论内容是否为空
        if (StrUtil.isBlank(commentDTO.getContent())) {
            throw new ServiceException("评论内容不能为空");
        }
        CommentInfo commentInfo = new CommentInfo();
        // 类型为歌曲
        if (ObjectUtil.equals(commentDTO.getTargetType(), TargetType.SONG)) {
            // 判断歌曲是否存在
            if (songInfoMapper.selectById(commentDTO.getTargetId()) == null) {
                throw new ServiceException("歌曲不存在");
            }
        } else if (ObjectUtil.equals(commentDTO.getTargetType(), TargetType.PLAYLIST)) {
            if (playlistInfoMapper.selectById(commentDTO.getTargetId()) == null) {
                throw new ServiceException("歌单不存在");
            }
            commentInfo.setTargetType(commentDTO.getTargetType().getValue());
        } else if (ObjectUtil.equals(commentDTO.getTargetType(), TargetType.ALBUM)) {
            if (albumInfoMapper.selectById(commentDTO.getTargetId()) == null) {
                throw new ServiceException("专辑不存在");
            }
        } else if (ObjectUtil.equals(commentDTO.getTargetType(), TargetType.SONG_MV)) {
            if (songMvMapper.selectById(commentDTO.getTargetId()) == null) {
                throw new ServiceException("歌曲视频不存在");
            }
        } else {
            throw new ServiceException("不存在该类型");
        }
        commentInfo.setTargetType(commentDTO.getTargetType().getValue())
                .setTargetId(commentDTO.getTargetId())
                .setContent(commentDTO.getContent())
                .setUserId(SecurityUtils.getUserId());
        // 检查是否存在父评论
        if (commentDTO.getParentCommentId() != null) {
            // 查询是否存在父评论id 的评论
            CommentInfo parentComment = commentInfoMapper.selectById(commentDTO.getParentCommentId());
            if (parentComment == null) {
                throw new ServiceException("不存在该父评论");
            }
            if (parentComment.getLevel() >= 3) {
                throw new ServiceException("最多支持三级评论");
            }
            // 设置评论的父评论id
            commentInfo.setParentCommentId(commentDTO.getParentCommentId());
            // 设置新评论层级
            commentInfo.setLevel(parentComment.getLevel() + 1);
        } else {
            // 顶级评论
            commentInfo.setLevel(1);
        }
        return commentInfoMapper.insert(commentInfo) > 0;
    }

    /**
     * 分页获取评论列表
     *
     * @param commentQry 分页和请求参数
     * @return List<SongCommentTreeVo>
     */
    @Override
    public IPage<List<CommentTreeVo>> getComments(CommentQry commentQry) {
        // 获取类型value
        Integer targetType = commentQry.getTargetType().getValue();
        if (targetType == null) {
            throw new ServiceException("评论类型参数错误");
        }
        // 创建分页对象
        Page<CommentInfo> page = new Page<>(commentQry.getPageNum(), commentQry.getPageSize());
        // 分页查询顶级评论
        IPage<CommentTreeVo> topLevelCommentsPage = commentInfoMapper.selectTopLevelComments(page, targetType, commentQry.getTargetId());

        // 获取所有顶级评论
        List<CommentTreeVo> topLevelComments = topLevelCommentsPage.getRecords();
        // 循环所有顶级评论 设置子评论
        for (CommentTreeVo topLevelComment : topLevelComments) {
            topLevelComment.setChildren(getChildComments(topLevelComment.getCommentId()));
        }

        // 封装结果
        IPage<List<CommentTreeVo>> resultPage = new Page<>(topLevelCommentsPage.getCurrent(), topLevelCommentsPage.getSize(), topLevelCommentsPage.getTotal());
        resultPage.setRecords(List.of(topLevelComments));
        return resultPage;
    }

    /**
     * 递归查询子评论
     *
     * @param parentCommentId 父评论 ID
     * @return 子评论列表
     */
    private List<CommentTreeVo> getChildComments(Long parentCommentId) {
        List<CommentTreeVo> childComments = commentInfoMapper.selectChildComments(parentCommentId);
        for (CommentTreeVo childComment : childComments) {
            childComment.setChildren(getChildComments(childComment.getCommentId()));
        }
        return childComments;
    }

    /**
     * 点赞评论
     *
     * @param commentId 评论id
     * @return void
     */
    @Override
    public Boolean likeComment(Long commentId) {
        // 获取用户id
        Long userId = SecurityUtils.getUserId();
        String key = COMMENT_LIKED_KEY_PREFIX + commentId;
        // ZSet有序集合 每个成员都有一个分数
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        if (score == null) {
            // 如果未点赞，则进行点赞操作
            // 1. 更新数据库中的点赞计数
            boolean updateSuccess = updateLikeCount(commentId, 1);
            if (updateSuccess) {
                // 2. 将用户ID添加到Redis的Sorted Set中
                stringRedisTemplate.opsForZSet().add(key, userId.toString(), System.currentTimeMillis());
            }
        } else {
            // 如果已点赞，则取消点赞操作
            // 1. 更新数据库中的点赞计数
            updateLikeCount(commentId, -1);
            // 2. 将用户ID从Redis的Sorted Set中移除
            stringRedisTemplate.opsForZSet().remove(key, userId.toString());
        }
        return true;
    }

    /**
     * 更新数据库中的点赞计数
     *
     * @param commentId 评论ID
     * @param delta     增量值（+1 或 -1）
     * @return 更新是否成功
     */
    private boolean updateLikeCount(Long commentId, int delta) {
        LambdaQueryWrapper<CommentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentInfo::getCommentId, commentId);
        CommentInfo comment = commentInfoMapper.selectOne(wrapper);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        int currentLikes = comment.getLikeCount();
        int newLikes = currentLikes + delta;
        if (newLikes < 0) {
            newLikes = 0; // 防止点赞数变成负数
        }
        comment.setLikeCount(newLikes);
        return commentInfoMapper.updateById(comment) > 0;
    }
}

