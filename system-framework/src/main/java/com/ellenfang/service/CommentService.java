package com.ellenfang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.entity.Comment;


/**
 * 评论表(Comment)表服务接口
 *
 * @author makejava
 * @since 2022-07-16 17:00:48
 */
public interface CommentService extends IService<Comment> {

    ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize);

    ResponseResult addComment(Comment comment);
}

