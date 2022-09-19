package com.ellenfang.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.entity.Category;
import com.ellenfang.domain.vo.CategoryVo;
import com.ellenfang.domain.vo.ExcelCategoryVo;
import com.ellenfang.domain.vo.PageVo;
import com.ellenfang.enums.AppHttpCodeEnum;
import com.ellenfang.service.CategoryService;
import com.ellenfang.utils.BeanCopyUtils;
import com.ellenfang.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/content/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/listAllCategory")
    public ResponseResult<List<CategoryVo>> listAllCategory() {
        List<CategoryVo> list = categoryService.listAllCategory();
        return ResponseResult.okResult(list);
    }

    @PreAuthorize("@ps.hasPermission('content:category:export')")
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        try {
            // 设置下载文件的请求头
            WebUtils.setDownLoadHeader("分类.xlsx", response);
            // 获取需要导出的数据/
            List<Category> categoryList = categoryService.list();
            List<ExcelCategoryVo> excelCategoryVos = BeanCopyUtils.copyBeanList(categoryList, ExcelCategoryVo.class);
            // 把数据写入到 Excel 中
            EasyExcel.write(response.getOutputStream(), ExcelCategoryVo.class).autoCloseStream(Boolean.FALSE).sheet("分类导出")
                    .doWrite(excelCategoryVos);
        } catch (Exception e) {
            e.printStackTrace();
            // 如果抛出异常也要响应 json
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
            WebUtils.renderString(response, JSON.toJSONString(result));
        }
    }

    @GetMapping("/list")
    public ResponseResult<PageVo> listCategoryByPage(Integer pageNum, Integer pageSize,String name, String status) {
        return categoryService.listCategoryByPage(pageNum, pageSize, name, status);
    }

    @PostMapping
    public ResponseResult addCategoory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    @GetMapping("/{id}")
    public ResponseResult<CategoryVo> queryCategoryById(@PathVariable(value = "id") Long id) {
        return categoryService.queryCategoryById(id);
    }

    @PutMapping
    public ResponseResult updateCategory(@RequestBody CategoryVo category) {
        return categoryService.updateCategory(category);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteCategory(@PathVariable(value = "id") Long id) {
        return categoryService.deleteCategory(id);
    }
}
