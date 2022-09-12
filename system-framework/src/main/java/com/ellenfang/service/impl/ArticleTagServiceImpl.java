package com.ellenfang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ellenfang.domain.entity.ArticleTag;
import com.ellenfang.mapper.ArticleTagMapper;
import com.ellenfang.service.ArticleTagService;
import org.springframework.stereotype.Service;

/**
 * 文章标签关联表(ArticleTag)表服务实现类
 *
 * @author makejava
 * @since 2022-09-08 22:35:56
 */
@Service("articleTagService")
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {

}

