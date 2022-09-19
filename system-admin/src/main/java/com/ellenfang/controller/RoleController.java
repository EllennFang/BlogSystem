package com.ellenfang.controller;

import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.dto.AddRoleDto;
import com.ellenfang.domain.dto.RoleChangeStatusDto;
import com.ellenfang.domain.dto.UpdateRoleDto;
import com.ellenfang.domain.entity.Role;
import com.ellenfang.domain.vo.AdminRoleVo;
import com.ellenfang.domain.vo.PageVo;
import com.ellenfang.domain.vo.RoleVo;
import com.ellenfang.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, String roleName, Integer status) {
        return roleService.list(pageNum, pageSize, roleName, status);
    }

    @GetMapping("/listAllRole")
    public ResponseResult<AdminRoleVo> listAllRole() {
        return roleService.listAllRole();
    }

    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(@RequestBody RoleChangeStatusDto roleDto) {
        return roleService.changeStatus(roleDto);
    }

    @PostMapping
    public ResponseResult addRole(@RequestBody AddRoleDto addRoleDto) {
        return roleService.addRole(addRoleDto);
    }

    @GetMapping("/{id}")
    public ResponseResult<RoleVo> queryRoleById(@PathVariable(value = "id") Long id) {
        return roleService.queryRoleById(id);
    }

    @PutMapping
    public ResponseResult updateRole(@RequestBody UpdateRoleDto updateRoleDto) {
        return roleService.updateRole(updateRoleDto);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteRole(@PathVariable(value = "id") Long id) {
        return roleService.deleteRole(id);
    }
}
