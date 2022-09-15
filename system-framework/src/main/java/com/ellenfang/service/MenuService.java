package com.ellenfang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.entity.Menu;
import com.ellenfang.domain.vo.MenuVo;

import java.util.List;


/**
 * 菜单权限表(Menu)表服务接口
 *
 * @since 2022-09-05 18:23:14
 */
public interface MenuService extends IService<Menu> {

    List<String> selectPermsByUserId(Long id);

    List<Menu> selectRouterMenuTreeByUserId(Long userId);

    ResponseResult<MenuVo> list(Integer status, String menuName);

    ResponseResult addMenu(Menu menu);

    ResponseResult<MenuVo> selectMenuById(Long id);

    ResponseResult updateMenu(Menu menu);

    ResponseResult deleteMenu(Integer menuId);
}

