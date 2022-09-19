package com.ellenfang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.dto.AddRoleDto;
import com.ellenfang.domain.dto.RoleChangeStatusDto;
import com.ellenfang.domain.dto.UpdateRoleDto;
import com.ellenfang.domain.entity.Role;
import com.ellenfang.domain.vo.AdminRoleVo;
import com.ellenfang.domain.vo.PageVo;
import com.ellenfang.domain.vo.RoleVo;

import java.util.List;


/**
 * 角色信息表(Role)表服务接口
 *
 * @author makejava
 * @since 2022-09-05 18:48:45
 */
public interface RoleService extends IService<Role> {

    ResponseResult changeStatus(RoleChangeStatusDto roleDto);

    List<String> selectRoleKeyByUserId(Long id);

    ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, String roleName, Integer status);

    ResponseResult addRole(AddRoleDto addRoleDto);

    ResponseResult<RoleVo> queryRoleById(Long id);

    ResponseResult updateRole(UpdateRoleDto updateRoleDto);

    ResponseResult deleteRole(Long id);

    ResponseResult<AdminRoleVo> listAllRole();
}

