package com.ellenfang.controller;

import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.dto.AddArticleDto;
import com.ellenfang.domain.dto.UpdateArticleDto;
import com.ellenfang.domain.vo.PageVo;
import com.ellenfang.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping
    public ResponseResult add(@RequestBody AddArticleDto article) {
        return articleService.add(article);
    }

    @GetMapping("/list")
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, String title, String summary) {
        return articleService.list(pageNum, pageSize, title, summary);
    }

    @GetMapping("/{id}")
    public ResponseResult adminArticleDetail(@PathVariable(value = "id") Integer id) {
        return articleService.getAdminArticleDetail(id);
    }

    @PutMapping
    public ResponseResult updateArticle(@RequestBody UpdateArticleDto updateArticleDto) {
        return articleService.updateArticle(updateArticleDto);
    }
}
