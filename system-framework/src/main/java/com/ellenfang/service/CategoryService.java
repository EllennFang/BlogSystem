package com.ellenfang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.entity.Category;
import com.ellenfang.domain.vo.CategoryVo;
import com.ellenfang.domain.vo.PageVo;

import java.util.List;


/**
 * 分类表(Category)表服务接口
 *
 * @author makejava
 * @since 2022-07-12 20:57:10
 */
public interface CategoryService extends IService<Category> {

    ResponseResult getCategoryList();

    List<CategoryVo> listAllCategory();

    ResponseResult<PageVo> listCategoryByPage(Integer pageNum, Integer pageSize, String name, String status);

    ResponseResult addCategory(Category category);

    ResponseResult<CategoryVo> queryCategoryById(Long id);

    ResponseResult updateCategory(CategoryVo category);

    ResponseResult deleteCategory(Long id);
}

