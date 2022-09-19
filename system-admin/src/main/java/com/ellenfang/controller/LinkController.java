package com.ellenfang.controller;

import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.entity.Link;
import com.ellenfang.domain.vo.LinkVo;
import com.ellenfang.domain.vo.PageVo;
import com.ellenfang.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @GetMapping("/list")
    public ResponseResult<PageVo> getAllLinkByPage(Integer pageNum, Integer pageSize, String name, String status) {
        return linkService.getAllLinkByPage(pageNum, pageSize, name, status);
    }

    @PostMapping
    public ResponseResult addLinke(@RequestBody Link link) {
        return linkService.addLink(link);
    }

    @GetMapping("/{id}")
    public ResponseResult<LinkVo> queryLinkById(@PathVariable(value = "id") Long id) {
        return linkService.queryLinkById(id);
    }

    @PutMapping
    public ResponseResult updateLink(@RequestBody Link link) {
        return linkService.updateLink(link);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteLink(@PathVariable(value = "id") Long id) {
        return linkService.deleteLink(id);
    }
}
