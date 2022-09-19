package com.ellenfang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ellenfang.constants.SystemConstants;
import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.entity.Menu;
import com.ellenfang.domain.vo.MenuSelectVo;
import com.ellenfang.domain.vo.MenuVo;
import com.ellenfang.domain.vo.RoleMenuTreeVo;
import com.ellenfang.enums.AppHttpCodeEnum;
import com.ellenfang.mapper.MenuMapper;
import com.ellenfang.mapper.RoleMenuMapper;
import com.ellenfang.service.MenuService;
import com.ellenfang.utils.BeanCopyUtils;
import com.ellenfang.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @since 2022-09-05 18:23:16
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    RoleMenuMapper roleMenuMapper;

    @Override
    public ResponseResult<MenuVo> list(Integer status, String menuName) {
        // 查询菜单列表，不需要分页
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        // 若参数 status(状态) 不为空，则对菜单进行对应状态的查询
        queryWrapper.eq(!ObjectUtils.isEmpty(status), Menu::getStatus, status);
        // 若参数 menuName(菜单名) 不为空，则对菜单名进行模糊查询
        queryWrapper.eq(StringUtils.hasText(menuName), Menu::getMenuName, menuName);
        // 查询结果按照父菜单id(parentId) 和 显示顺序(orderNum)进行排序
        queryWrapper.orderByAsc(Menu::getParentId, Menu::getOrderNum);
        List<Menu> menus = list(queryWrapper);
        // 封装为 vo 并返回
        List<MenuVo> menuVos = BeanCopyUtils.copyBeanList(menus, MenuVo.class);
        return ResponseResult.okResult(menuVos);
    }

    @Override
    public List<String> selectPermsByUserId(Long id) {
        // 如果是管理员，返回所有的权限（类型为菜单或按钮，状态为正常）
        if (SecurityUtils.isAdmin()) {
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Menu::getMenuType, SystemConstants.MENU, SystemConstants.BUTTON);
            wrapper.eq(Menu::getStatus, SystemConstants.STATUS_NORMAL);
            List<Menu> menuList = list(wrapper);
            List<String> perms = menuList.stream()
                    .map(Menu::getPerms)
                    .collect(Collectors.toList());
            return perms;
        }
        // 否则返回其所具有的权限
        return getBaseMapper().selectPermsByUserId(id);
    }

    @Override
    public ResponseResult addMenu(Menu menu) {
        MenuMapper menuMapper = getBaseMapper();
        menuMapper.insert(menu);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult<MenuVo> selectMenuById(Long id) {
        MenuMapper menuMapper = getBaseMapper();
        Menu menu = menuMapper.selectById(id);
        MenuVo menuVo = BeanCopyUtils.copyBean(menu, MenuVo.class);
        return ResponseResult.okResult(menuVo);
    }

    @Override
    public ResponseResult updateMenu(Menu menu) {
        // 如果修改菜单的父级菜单是自己的id，就返回错误提示
        if (menu.getParentId().equals(menu.getId())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "修改" + menu.getMenuName() + "失败，父级菜单不能选择自己");
        }
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        updateById(menu);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteMenu(Integer menuId) {
        // 根据 id 删除菜单，若此菜单存在子菜单则删除失败
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getParentId, menuId);
        List<Menu> menuList = list(wrapper);
        if (menuList.size() > 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "存在子菜单不允许删除");
        }
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getId, menuId);
        remove(queryWrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult<RoleMenuTreeVo> roleMenuTreeSelectByRoleId(Long id) {
        MenuMapper menuMapper = getBaseMapper();
        List<Menu> menus = null;
        // 如果是管理员，菜单即为所有
        if (id.equals(1L)) {
            menus = menuMapper.selectAllRouterMenu();
        } else {
            // 根据 角色id 查询对应的菜单列表,并转换为树的形式
            menus = menuMapper.roleMenuTreeSelectByRoleId(id);
        }
        List<MenuSelectVo> menuSelectVos = BeanCopyUtils.copyBeanList(menus, MenuSelectVo.class);
        List<MenuSelectVo> menuTree = buildMenuSelectTree(menuSelectVos, 0L);
        // 根据 上面所查询出来的菜单列表，查询相关联的菜单权限id列表
        List<Long> checkedKeys = new LinkedList<>();
        menus.stream()
                .forEach((Menu menu) -> {
                    if (menu.getMenuType().equals("C")) {
                        checkedKeys.add(menu.getId());
                    }
                });
        // 封装为最终的 vo
        RoleMenuTreeVo roleMenuTreeVo = new RoleMenuTreeVo(menuTree, checkedKeys);
        return ResponseResult.okResult(roleMenuTreeVo);
    }

    @Override
    public ResponseResult<MenuSelectVo> treeSelect() {
        // 查询所有的菜单
        List<Menu> menus = list(null);
        // 封装为 vo
        List<MenuSelectVo> menuSelectVos = BeanCopyUtils.copyBeanList(menus, MenuSelectVo.class);
        // 转换为树的形式
        List<MenuSelectVo> selectTree = buildMenuSelectTree(menuSelectVos, 0L);
        return ResponseResult.okResult(selectTree);
    }

    @Override
    public List<Menu> selectRouterMenuTreeByUserId(Long userId) {
        MenuMapper menuMapper = getBaseMapper();
        List<Menu> menus = null;
        // 如果是管理员，查询所有路由（类型为目录或菜单，状态为正常）
        if (SecurityUtils.isAdmin()) {
            menus = menuMapper.selectAllRouterMenu();
        } else {
            // 否则返回 查询用户所具有的 Menu
            menus = menuMapper.selectRouterMenuTreeByUserId(userId);
        }
        // 将查询的 menu 集合构建为 tree 的形式
        // 思路：先找出第一层的菜单，然后去找他们的子菜单并设置到 children 属性中
        List<Menu> menuTree = buildMenuTree(menus, 0L);
        return menuTree;
    }

    private List<Menu> buildMenuTree(List<Menu> menus, Long parentId) {
        List<Menu> menuTree = menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> menu.setChildren(getChildren(menu, menus)))
                .collect(Collectors.toList());
        return menuTree;
    }

    private List<MenuSelectVo> buildMenuSelectTree(List<MenuSelectVo> menuSelectVos, Long parentId) {
        List<MenuSelectVo> menuTree = menuSelectVos.stream()
                .filter(menuSelectVo -> menuSelectVo.getParentId().equals(parentId))
                .map(menuSelectVo -> menuSelectVo.setChildren(getSelectChildren(menuSelectVo, menuSelectVos)))
                .collect(Collectors.toList());
        return menuTree;
    }

    /**
     * 获取当前 menu 的子 menu 集合并注入
     * @param menu 当前 menu
     * @param menus 完整的 menu 集合
     * @return 返回 当前 menu 的子 menu 集合（其中子集合中的子集合（多层级）关系也同时递归处理）
     */
    private List<Menu> getChildren(Menu menu, List<Menu> menus) {
        List<Menu> childrenList = menus.stream()
                .filter(m -> m.getParentId().equals(menu.getId()))
                .map(m -> m.setChildren(getChildren(m, menus)))
                .collect(Collectors.toList());
        return childrenList;
    }

    private List<MenuSelectVo> getSelectChildren(MenuSelectVo menuSelectVo, List<MenuSelectVo> menuSelectVos) {
        List<MenuSelectVo> childrenList = menuSelectVos.stream()
                .filter(m -> m.getParentId().equals(menuSelectVo.getId()))
                .map(m -> m.setChildren(getSelectChildren(m, menuSelectVos)))
                .collect(Collectors.toList());
        return childrenList;
    }
}

