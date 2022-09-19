package com.ellenfang.domain.vo;

import com.ellenfang.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUpdateUserInfo {
    // 用户所关联的角色id列表
    List<Long> roleIds;
    // 所有角色的列表
    List<Role> roles;
    // 用户信息
    UserInfoVo user;
}
