package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.controller.vo.CommentTreeVo;
import com.jlf.music.entity.CommentInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentInfoMapper extends BaseMapper<CommentInfo> {
    /**
     * 分页查询顶级评论（一级评论）
     * @param page 分页信息
     * @param targetType 评论类型 code
     * @param targetId 目标 id
     * @return 分页后的顶级评论列表
     */
    IPage<CommentTreeVo> selectTopLevelComments(IPage<CommentInfo> page, @Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    /**
     * 查询指定父评论下的子评论
     *
     * @param parentCommentId 父评论 ID
     * @return 子评论列表
     */
    List<CommentTreeVo> selectChildComments(@Param("parentCommentId") Long parentCommentId);
}
