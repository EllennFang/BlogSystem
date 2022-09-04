package com.ellenfang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ellenfang.domain.entity.Tag;
import com.ellenfang.mapper.TagMapper;
import com.ellenfang.service.TagService;
import org.springframework.stereotype.Service;

/**
 * 标签(Tag)表服务实现类
 *
 * @author makejava
 * @since 2022-07-26 16:15:37
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

}

