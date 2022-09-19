package com.ellenfang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.dto.AddUserDto;
import com.ellenfang.domain.dto.UpdateUserDto;
import com.ellenfang.domain.entity.User;
import com.ellenfang.domain.vo.AdminUpdateUserInfo;
import com.ellenfang.domain.vo.PageVo;


/**
 * 用户表(User)表服务接口
 *
 * @author makejava
 * @since 2022-07-16 19:44:51
 */
public interface UserService extends IService<User> {

    ResponseResult userInfo();

    ResponseResult updateUserInfo(User user);

    ResponseResult register(User user);

    ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, String userName, String phonenumber, String status);

    ResponseResult addUser(AddUserDto addUserDto);

    ResponseResult deleteUser(Long id);

    ResponseResult<AdminUpdateUserInfo> adminGetUserInfo(Long id);

    ResponseResult updateUser(UpdateUserDto updateUserDto);
}

