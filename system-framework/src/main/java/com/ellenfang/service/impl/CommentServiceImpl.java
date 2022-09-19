package com.ellenfang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ellenfang.constants.SystemConstants;
import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.dto.AddCommentDto;
import com.ellenfang.domain.entity.Comment;
import com.ellenfang.domain.entity.LoginUser;
import com.ellenfang.domain.vo.CommentVo;
import com.ellenfang.domain.vo.PageVo;
import com.ellenfang.enums.AppHttpCodeEnum;
import com.ellenfang.exception.SystemException;
import com.ellenfang.mapper.CommentMapper;
import com.ellenfang.service.CommentService;
import com.ellenfang.service.UserService;
import com.ellenfang.utils.BeanCopyUtils;
import com.ellenfang.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author makejava
 * @since 2022-07-16 17:00:48
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserService userService;

    @Override
    public ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize) {
        // 查询对应文章的根评论

        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        // 对 articleId 进行判断（只有文章评论才需要判断）
        queryWrapper.eq(SystemConstants.ARTICLE_COMMENT.equals(commentType), Comment::getArticleId, articleId);
        // 根评论 rootId 为 -1
        queryWrapper.eq(Comment::getRootId, -1);

        // 评论类型
        queryWrapper.eq(Comment::getType, commentType);

        // 分页查询
        Page<Comment> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);

        List<CommentVo> commentVoList = toCommentVoList(page.getRecords());

        // 查询所有根评论对应的子评论集合，并且赋值给对应的 vo 属性
        for (CommentVo commentVo : commentVoList) {
            // 查询对应的子评论
            List<CommentVo> children = getChildren(commentVo.getId());
            // 赋值
            commentVo.setChildren(children);
        }

        return ResponseResult.okResult(new PageVo(commentVoList, page.getTotal()));
    }

    @Override
    public ResponseResult addComment(AddCommentDto addCommentDto) {
        Comment comment = BeanCopyUtils.copyBean(addCommentDto, Comment.class);
        // 判断用户是否登录
        if (SecurityUtils.getLoginUser() == null) {
            // 说明用户没用登录，返回错误信息
            throw new SystemException(AppHttpCodeEnum.NEED_LOGIN);
        }
        // 评论内容不能为空
        if (!StringUtils.hasText(comment.getContent())) {
            throw new SystemException(AppHttpCodeEnum.CONTENT_NOT_NULL);
        }

        save(comment);
        return ResponseResult.okResult();
    }

    /**
     * 辅助函数：查询对应根评论的子评论集合
     * @param id 根评论id
     * @return 返回对应子评论集合
     */
    private List<CommentVo> getChildren(Long id) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId, id);
        queryWrapper.orderByAsc(Comment::getCreateTime);

        List<Comment> commentList = list(queryWrapper);
        List<CommentVo> commentVoList = toCommentVoList(commentList);

        return commentVoList;
    }

    /**
     * 辅助函数
     * @param list 传入 Comment 集合
     * @return 返回转化后的 CommentVo 集合
     */
    private List<CommentVo> toCommentVoList(List<Comment> list) {
        List<CommentVo> commentVoList = BeanCopyUtils.copyBeanList(list, CommentVo.class);

        // 遍历 vo 集合
        for (CommentVo commentVo : commentVoList) {
            // 通过 creatBy 查询用户的昵称并赋值
            String nickName = userService.getById(commentVo.getCreateBy()).getNickName();
            commentVo.setUsername(nickName);
            // 通过 toCommentUserId 查询用户的昵称并赋值
            // 如果 toCommentUserId 不为-1才进行查询
            if (commentVo.getToCommentUserId() != -1) {
                String toCommentUserName = userService.getById(commentVo.getToCommentUserId()).getNickName();
                commentVo.setToCommentUserName(toCommentUserName);
            }
            // 通过 creatBy 查询用户头像并赋值
            String avatar = userService.getById(commentVo.getCreateBy()).getAvatar();
            commentVo.setAvatar(avatar);
        }
        return commentVoList;
    }
}

