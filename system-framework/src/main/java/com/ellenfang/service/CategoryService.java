package com.ellenfang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.entity.Category;


/**
 * 分类表(Category)表服务接口
 *
 * @author makejava
 * @since 2022-07-12 20:57:10
 */
public interface CategoryService extends IService<Category> {

    ResponseResult getCategoryList();
}

