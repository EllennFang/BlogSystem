package com.ellenfang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ellenfang.constants.SystemConstants;
import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.entity.Article;
import com.ellenfang.domain.vo.ArticleDetailVo;
import com.ellenfang.domain.vo.ArticleListVo;
import com.ellenfang.domain.vo.HotArticleVo;
import com.ellenfang.domain.vo.PageVo;
import com.ellenfang.mapper.ArticleMapper;
import com.ellenfang.service.ArticleService;
import com.ellenfang.service.CategoryService;
import com.ellenfang.utils.BeanCopyUtils;
import com.ellenfang.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult hotArticleList() {
        // 查询热门文章，封装成 ResponseResult 返回

        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        // 条件1：必须是正式文章（status 状态0表示已发布）
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        // 条件2：按照浏览量进行排序（降序）
        queryWrapper.orderByDesc(Article::getViewCount);
        // 条件3：最多只能查询10条
        Page<Article> page = new Page(1, 10);
        page(page, queryWrapper);

        List<Article> articleList = page.getRecords();

        // bean拷贝
        List<HotArticleVo> articleVos = new ArrayList<>();
        /*for (Article article : articleList) {
            HotArticleVo vo = new HotArticleVo();
            BeanUtils.copyProperties(article, vo);
            articleVos.add(vo);
        }*/
        articleVos = BeanCopyUtils.copyBeanList(articleList, HotArticleVo.class);

        return ResponseResult.okResult(articleVos);
    }

    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        // 查询条件
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        // 如果有 categoryId 就要查询时需要和传入的相同
        queryWrapper.eq(Objects.nonNull(categoryId) && categoryId > 0, Article::getCategoryId, categoryId);

        // 文章状态是正常发布的
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);

        // 对 isTop 进行降序
        queryWrapper.orderByDesc(Article::getIsTop);

        // 分页查询
        Page<Article> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        List<Article> articleList = page.getRecords();

        // 查询 categoryName
        // 根据 categoryId 查询 categoryName
        /*for(Article article : articleList) {
            Long id = article.getCategoryId();
            Category category = categoryService.getById(id);
            article.setCategoryName(category.getName());
        }*/

        articleList = articleList.stream()
                .map(article -> article.setCategoryName(categoryService.getById(article.getCategoryId()).getName()))
                .collect(Collectors.toList());

        // 封装成 vo
        List<ArticleListVo> articleListVos = BeanCopyUtils.copyBeanList(articleList, ArticleListVo.class);

        PageVo pageVo = new PageVo(articleListVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticleDetail(Long id) {
        // 根据 id 查询文章
        Article article = getById(id);
        // 从 redis 中获取 viewCount 浏览量
        Integer viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
        article.setViewCount(viewCount.longValue());
        // 转换成 vo
        ArticleDetailVo articleDetailVo = BeanCopyUtils.copyBean(article, ArticleDetailVo.class);
        // 根据分类 id 查询分类名
        articleDetailVo.setCategoryName(categoryService.getById(articleDetailVo.getCategoryId()).getName());

        return ResponseResult.okResult(articleDetailVo);
    }

    @Override
    public ResponseResult updateViewCount(Long id) {
        // 更新 redis 中对应 id 的浏览量
        redisCache.incrementCacheMapValue("article:viewCount", id.toString(), 1);
        return ResponseResult.okResult();
    }
}