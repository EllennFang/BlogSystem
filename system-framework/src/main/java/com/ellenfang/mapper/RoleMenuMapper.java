package com.ellenfang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ellenfang.domain.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;


/**
 * 角色和菜单关联表(RoleMenu)表数据库访问层
 *
 * @author EllenFang
 * @since 2022-09-17 16:37:24
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

}

