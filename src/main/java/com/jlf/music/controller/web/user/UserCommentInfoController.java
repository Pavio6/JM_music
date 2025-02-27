package com.jlf.music.controller.web.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.Result;
import com.jlf.music.controller.dto.CommentDTO;
import com.jlf.music.controller.qry.CommentQry;
import com.jlf.music.controller.vo.CommentTreeVo;
import com.jlf.music.service.CommentInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/comment/")
public class UserCommentInfoController {
    @Resource
    private CommentInfoService commentInfoService;

    /**
     * TODO 添加前对评论信息进行敏感词分析
     * 添加评论
     *
     * @param commentDTO 评论信息
     * @return Boolean
     */
    @PostMapping
    public Result<Boolean> addComment(@RequestBody CommentDTO commentDTO) {
        return Result.success(commentInfoService.addComment(commentDTO));
    }


    /**
     * 分页获取评论列表
     * @param commentQry 分页和请求参数
     * @return List<SongCommentTreeVo>
     */
    @GetMapping
    public Result<IPage<List<CommentTreeVo>>> getComments(CommentQry commentQry) {
        return Result.success(commentInfoService.getComments(commentQry));
    }

    /**
     * 点赞/取消点赞评论
     * @param commentId 评论id
     * @return void
     */
    @PutMapping("/like/{commentId}")
    public Result<Boolean> likeComment(@PathVariable Long commentId) {
        return Result.success(commentInfoService.likeComment(commentId));
    }
}
