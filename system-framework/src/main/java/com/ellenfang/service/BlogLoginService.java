package com.ellenfang.service;

import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.entity.User;

public interface BlogLoginService {
    ResponseResult login(User user);

    ResponseResult logout();
}