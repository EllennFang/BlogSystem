package com.ellenfang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ellenfang.domain.entity.RoleMenu;
import com.ellenfang.mapper.RoleMenuMapper;
import com.ellenfang.service.RoleMenuService;
import org.springframework.stereotype.Service;

/**
 * 角色和菜单关联表(RoleMenu)表服务实现类
 *
 * @author EllenFang
 * @since 2022-09-17 16:37:26
 */
@Service("roleMenuService")
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

}

