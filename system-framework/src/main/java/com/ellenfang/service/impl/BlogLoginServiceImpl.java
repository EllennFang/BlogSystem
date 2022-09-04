package com.ellenfang.service.impl;

import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.entity.LoginUser;
import com.ellenfang.domain.entity.User;
import com.ellenfang.domain.vo.BlogUserLoginVo;
import com.ellenfang.domain.vo.UserInfoVo;
import com.ellenfang.service.BlogLoginService;
import com.ellenfang.utils.BeanCopyUtils;
import com.ellenfang.utils.JwtUtil;
import com.ellenfang.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BlogLoginServiceImpl implements BlogLoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 判断是否认证通过（如果用户不存在或者密码错误，authenticate 为空）
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("用户名或者密码错误");
        }

        // 获取 userid 生成 token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        Long userId = loginUser.getUser().getId();
        String jwt = JwtUtil.createJWT(userId.toString());

        // 把用户信息存入 redis
        redisCache.setCacheObject("bloglogin:" + userId, loginUser);

        // 把 token 和 userinfo 封装成 vo 返回
        // 把 User 转化为 UserInfoVo
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVo.class);
        BlogUserLoginVo blogUserLoginVo = new BlogUserLoginVo(jwt, userInfoVo);

        return ResponseResult.okResult(blogUserLoginVo);
    }

    @Override
    public ResponseResult logout() {
        // 获取token 解析获取userid
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 获取userid
        Long userId = loginUser.getUser().getId();
        // 删除redis中的用户信息
        redisCache.deleteObject("bloglogin:" + userId);
        return ResponseResult.okResult();
    }
}