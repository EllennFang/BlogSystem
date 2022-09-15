package com.ellenfang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.dto.AddArticleDto;
import com.ellenfang.domain.dto.UpdateArticleDto;
import com.ellenfang.domain.entity.Article;
import com.ellenfang.domain.vo.PageVo;

public interface ArticleService extends IService<Article> {

    ResponseResult hotArticleList();

    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId);

    ResponseResult getArticleDetail(Long id);

    ResponseResult updateViewCount(Long id);

    ResponseResult add(AddArticleDto article);

    ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, String title, String summary);

    ResponseResult getAdminArticleDetail(Integer id);

    ResponseResult updateArticle(UpdateArticleDto updateArticleDto);

    ResponseResult deleteArticle(Integer id);
}
