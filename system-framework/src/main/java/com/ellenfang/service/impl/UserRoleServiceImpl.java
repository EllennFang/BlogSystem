package com.ellenfang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ellenfang.domain.entity.UserRole;
import com.ellenfang.mapper.UserRoleMapper;
import com.ellenfang.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户和角色关联表(UserRole)表服务实现类
 *
 * @author EllenFang
 * @since 2022-09-18 15:30:09
 */
@Service("userRoleService")
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}

