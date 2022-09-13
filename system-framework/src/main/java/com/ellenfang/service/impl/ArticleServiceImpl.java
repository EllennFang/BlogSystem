package com.ellenfang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ellenfang.constants.SystemConstants;
import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.dto.AddArticleDto;
import com.ellenfang.domain.dto.UpdateArticleDto;
import com.ellenfang.domain.entity.Article;
import com.ellenfang.domain.entity.ArticleTag;
import com.ellenfang.domain.vo.*;
import com.ellenfang.mapper.ArticleMapper;
import com.ellenfang.service.ArticleService;
import com.ellenfang.service.ArticleTagService;
import com.ellenfang.service.CategoryService;
import com.ellenfang.utils.BeanCopyUtils;
import com.ellenfang.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

    @Autowired
    private ArticleTagService articleTagService;

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
    public ResponseResult getAdminArticleDetail(Integer id) {
        // 根据 id 查询文章
        Article article = getById(id);
        // 从 redis 中获取 viewCount 浏览量
        Integer viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
        article.setViewCount(viewCount.longValue());
        // 转换为 vo
        AdminArticleDetailVo adminArticleDetailVo = BeanCopyUtils.copyBean(article, AdminArticleDetailVo.class);
        // 根据 id 获取相应的标签
        LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId, id);
        List<ArticleTag> articleTags = articleTagService.list(queryWrapper);
        List<Long> tags = articleTags.stream()
                .map(ArticleTag::getTagId).collect(Collectors.toList());
        // 将相应的标签集合添加进vo并且返回
        adminArticleDetailVo.setTags(tags);
        return ResponseResult.okResult(adminArticleDetailVo);
    }

    @Override
    public ResponseResult updateArticle(UpdateArticleDto updateArticleDto) {
        //TODO 修改 article 数据

        //TODO 获取 Dto 中的标签集合，并修改（删除原本有现在没有的，添加原本没有现在有的）

        return null;
    }

    @Override
    public ResponseResult updateViewCount(Long id) {
        // 更新 redis 中对应 id 的浏览量
        redisCache.incrementCacheMapValue("article:viewCount", id.toString(), 1);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult add(AddArticleDto articleDto) {
        // 添加博客
        Article article = BeanCopyUtils.copyBean(articleDto, Article.class);
        save(article);

        // 创建与该博客相连的标签链
        List<ArticleTag> articleTags = articleDto.getTags().stream()
                .map(tagId -> new ArticleTag(article.getId(), tagId))
                .collect(Collectors.toList());

        // 将上边的关系添加到 文章-标签 关联表
        articleTagService.saveBatch(articleTags);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, String title, String summary) {
        // 查询条件
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(title), Article::getTitle, title);
        queryWrapper.like(StringUtils.hasText(summary), Article::getSummary, summary);
        queryWrapper.eq(Article::getStatus, SystemConstants.STATUS_NORMAL);
        Page<Article> page = new Page(pageNum, pageSize);

        page(page, queryWrapper);
        List<Article> articleList = page.getRecords();

        // 封装成 vo
        List<AdminArticleListVo> adminArticleListVos = BeanCopyUtils.copyBeanList(articleList, AdminArticleListVo.class);
        PageVo pageVo = new PageVo(adminArticleListVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }
}
