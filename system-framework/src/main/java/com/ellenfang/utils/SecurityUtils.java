package com.ellenfang.utils;

import com.ellenfang.domain.entity.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Security 工具类
 * 用于获取用户相关的信息
 */
public class SecurityUtils {

    /**
     * 获取当前用户的 LoginUser
     * @return 若已经登录则返回 LoginUser，否则返回 null
     */
    public static LoginUser getLoginUser() {
        Authentication authentication = getAuthentication();
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) return null;
        return (LoginUser) authentication.getPrincipal();
    }

    /**
     * 获取Authentication
     * @return 返回当前的 Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 判断当前用户是否为管理员
     * @return 返回判断结果
     */
    public static Boolean isAdmin(){
        Long id = getLoginUser().getUser().getId();
        return id != null && id.equals(1L);
    }

    /**
     * 获取用户 id
     * @return 若当前线程已经登录，则返回用户 id，否则返回 null
     */
    public static Long getUserId() {
        if (getLoginUser() == null) return null;
        return getLoginUser().getUser().getId();
    }
}