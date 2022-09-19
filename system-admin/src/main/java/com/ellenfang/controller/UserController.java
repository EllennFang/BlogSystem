package com.ellenfang.controller;

import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.dto.AddUserDto;
import com.ellenfang.domain.dto.UpdateUserDto;
import com.ellenfang.domain.vo.AdminUpdateUserInfo;
import com.ellenfang.domain.vo.AdminUserInfoVo;
import com.ellenfang.domain.vo.PageVo;
import com.ellenfang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, String userName, String phonenumber, String status) {
        return userService.list(pageNum, pageSize, userName, phonenumber, status);
    }

    @PostMapping
    public ResponseResult addUser(@RequestBody AddUserDto addUserDto) {
        return userService.addUser(addUserDto);
    }

    @DeleteMapping("/{id}")
    public ResponseResult eleteUser(@PathVariable(value = "id") Long id) {
        return userService.deleteUser(id);
    }

    @GetMapping("/{id}")
    public ResponseResult<AdminUpdateUserInfo> adminGetUserInfo(@PathVariable(value = "id") Long id) {
        return userService.adminGetUserInfo(id);
    }

    @PutMapping
    public ResponseResult updateUser(@RequestBody UpdateUserDto updateUserDto) {
        return userService.updateUser(updateUserDto);
    }
}
