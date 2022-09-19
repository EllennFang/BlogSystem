package com.ellenfang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.dto.AddRoleDto;
import com.ellenfang.domain.dto.RoleChangeStatusDto;
import com.ellenfang.domain.dto.UpdateRoleDto;
import com.ellenfang.domain.entity.Role;
import com.ellenfang.domain.entity.RoleMenu;
import com.ellenfang.domain.vo.AdminRoleVo;
import com.ellenfang.domain.vo.PageVo;
import com.ellenfang.domain.vo.RoleVo;
import com.ellenfang.mapper.RoleMapper;
import com.ellenfang.service.RoleMenuService;
import com.ellenfang.service.RoleService;
import com.ellenfang.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author makejava
 * @since 2022-09-05 18:48:45
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    RoleMenuService roleMenuService;

    @Override
    public ResponseResult changeStatus(RoleChangeStatusDto roleDto) {
        LambdaUpdateWrapper<Role> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Role::getId, roleDto.getRoleId()).set(Role::getStatus, roleDto.getStatus());
        update(null, lambdaUpdateWrapper);
        return ResponseResult.okResult();
    }

    @Override
    public List<String> selectRoleKeyByUserId(Long id) {
        // 判断是否为管理员（id=1），如果是，返回集合中只需要有 admin
        if (id == 1L) {
            List<String> roleKeys = new ArrayList<>();
            roleKeys.add("admin");
            return roleKeys;
        }
        // 否则查询用户具有的角色信息
        return getBaseMapper().selectRoleKeyByUserId(id);
    }

    @Override
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, String roleName, Integer status) {
        // 查询条件
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(roleName), Role::getRoleName, roleName);
        queryWrapper.eq(!ObjectUtils.isEmpty(status), Role::getStatus, status);
        queryWrapper.orderByAsc(Role::getRoleSort);
        // 分页查询
        Page<Role> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page, queryWrapper);
        List<Role> roles = page.getRecords();
        // 转化为 vo
        List<AdminRoleVo> adminRoleVos = BeanCopyUtils.copyBeanList(roles, AdminRoleVo.class);
        PageVo pageVo = new PageVo(adminRoleVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult addRole(AddRoleDto addRoleDto) {
        // 将 dto 转化为实体类，并持久化
        Role role = BeanCopyUtils.copyBean(addRoleDto, Role.class);
        save(role);

        // 创建出角色与菜单的关系链
        List<Long> menuIds = addRoleDto.getMenuIds();
        List<RoleMenu> roleMenus = menuIds.stream()
                .map(menuId -> new RoleMenu(role.getId(), menuId))
                .collect(Collectors.toList());
        // 增添新的角色菜单关系
        roleMenuService.saveBatch(roleMenus);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult<RoleVo> queryRoleById(Long id) {
        Role role = getBaseMapper().selectById(id);
        RoleVo roleVo = BeanCopyUtils.copyBean(role, RoleVo.class);
        return ResponseResult.okResult(roleVo);
    }

    @Override
    public ResponseResult updateRole(UpdateRoleDto updateRoleDto) {
        Role role = BeanCopyUtils.copyBean(updateRoleDto, Role.class);
        RoleMapper roleMapper = getBaseMapper();
        roleMapper.updateById(role);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteRole(Long id) {
        removeById(id);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult<AdminRoleVo> listAllRole() {
        // 查询所有的角色
        List<Role> roles = list();
        // 封装为 vo
        List<AdminRoleVo> adminRoleVos = BeanCopyUtils.copyBeanList(roles, AdminRoleVo.class);
        return ResponseResult.okResult(adminRoleVos);
    }
}

