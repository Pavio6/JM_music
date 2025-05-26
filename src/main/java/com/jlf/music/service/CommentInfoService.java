package com.jlf.music.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.controller.dto.CommentDTO;
import com.jlf.music.controller.qry.CommentQry;
import com.jlf.music.controller.vo.CommentTreeVo;
import com.jlf.music.entity.CommentInfo;

public interface CommentInfoService extends IService<CommentInfo> {

    /**
     * 添加评论
     *
     * @param commentDTO 歌曲评论信息
     * @return Boolean
     */
    Boolean addComment(CommentDTO commentDTO);

    /**
     * 分页获取评论列表
     *
     * @param commentQry 分页和请求参数
     * @return List<SongCommentTreeVo>
     */
    IPage<CommentTreeVo> getComments(CommentQry commentQry);

    /**
     * 点赞评论
     *
     * @param commentId 评论id
     * @param isLike 是否喜欢
     * @return void
     */
    Boolean likeComment(Long commentId, Boolean isLike);

}
