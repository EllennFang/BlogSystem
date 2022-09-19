package com.ellenfang.controller;

import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.entity.Menu;
import com.ellenfang.domain.vo.MenuSelectVo;
import com.ellenfang.domain.vo.MenuVo;
import com.ellenfang.domain.vo.RoleMenuTreeVo;
import com.ellenfang.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/list")
    public ResponseResult<MenuVo> list(Integer status, String menuName) {
        return menuService.list(status, menuName);
    }

    @PostMapping
    public ResponseResult addMenu(@RequestBody Menu menu) {
        return menuService.addMenu(menu);
    }

    @GetMapping("/{id}")
    public ResponseResult<MenuVo> selectMenuById(@PathVariable(value = "id") Long id) {
        return menuService.selectMenuById(id);
    }

    @PutMapping
    public ResponseResult updateMenu(@RequestBody Menu menu) {
        return menuService.updateMenu(menu);
    }

    @DeleteMapping("/{menuId}")
    public ResponseResult deleteMenu(@PathVariable(value = "menuId")Integer menuId) {
        return menuService.deleteMenu(menuId);
    }

    @GetMapping("/roleMenuTreeselect/{id}")
    public ResponseResult<RoleMenuTreeVo> roleMenuTreeSelectByRoleId(@PathVariable(value = "id") Long id) {
        return menuService.roleMenuTreeSelectByRoleId(id);
    }

    @GetMapping("/treeselect")
    public ResponseResult<MenuSelectVo> treeSelect() {
        return menuService.treeSelect();
    }
}
