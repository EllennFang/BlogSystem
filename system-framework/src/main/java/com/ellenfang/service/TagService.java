package com.ellenfang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.dto.TagListDto;
import com.ellenfang.domain.entity.Tag;
import com.ellenfang.domain.vo.PageVo;
import com.ellenfang.domain.vo.TagVo;

import java.util.List;


/**
 * 标签(Tag)表服务接口
 *
 * @author makejava
 * @since 2022-07-26 16:15:37
 */
public interface TagService extends IService<Tag> {

    ResponseResult<PageVo> pageTagList(Integer pageNum, Integer pageSize, TagListDto tagListDto);

    ResponseResult addTag(Tag tag);

    ResponseResult deleteTag(Integer id);

    ResponseResult<TagVo> getTagById(Integer id);

    ResponseResult updateTag(Tag tag);

    List<TagVo> listAllTag();
}

