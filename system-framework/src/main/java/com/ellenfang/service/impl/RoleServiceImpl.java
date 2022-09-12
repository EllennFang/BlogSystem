package com.ellenfang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ellenfang.domain.entity.Role;
import com.ellenfang.mapper.RoleMapper;
import com.ellenfang.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author makejava
 * @since 2022-09-05 18:48:45
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

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
}

