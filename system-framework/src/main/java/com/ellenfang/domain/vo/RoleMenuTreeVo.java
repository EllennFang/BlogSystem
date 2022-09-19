package com.ellenfang.domain.vo;

import com.ellenfang.domain.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleMenuTreeVo {
    // 对应角色所关联的菜单树
    private List<MenuSelectVo> menus;
    // 角色所关联的菜单权限 id 列表
    private List<Long> checkedKeys;
}
