package com.ellenfang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ellenfang.domain.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;


/**
 * 用户和角色关联表(UserRole)表数据库访问层
 *
 * @author EllenFang
 * @since 2022-09-18 15:30:08
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

}

