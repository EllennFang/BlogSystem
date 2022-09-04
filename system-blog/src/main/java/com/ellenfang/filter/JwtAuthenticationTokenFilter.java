package com.ellenfang.filter;

import com.alibaba.fastjson.JSON;
import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.entity.LoginUser;
import com.ellenfang.enums.AppHttpCodeEnum;
import com.ellenfang.utils.JwtUtil;
import com.ellenfang.utils.RedisCache;
import com.ellenfang.utils.WebUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 定义Jwt认证过滤器：
 *      获取token
 *      解析token获取其中的userid
 *      从redis中获取用户信息
 *      存入 SecurityContextHolder
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中的 token
        String token = httpServletRequest.getHeader("token");
        if (!StringUtils.hasText(token)) {
            // 说明接口不需要登录， 直接放行
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        // 解析获取 userid
        Claims claims = null;
        try {
            claims = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            e.printStackTrace();
            // token 超时， token 非法
            // 响应告诉前端需要重新登录
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
            WebUtils.renderString(httpServletResponse, JSON.toJSONString(result));
            return;
        }
        String userid = claims.getSubject();

        // 从 redis 中获取用户信息
        LoginUser loginUser = redisCache.getCacheObject("bloglogin:" + userid);
        // 如果获取不到
        if (Objects.isNull(loginUser)) {
            // 说明登录过期（redis key过期）或已经退出登录
            // 提示重新登录
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
            WebUtils.renderString(httpServletResponse, JSON.toJSONString(result));
            return;
        }

        // 存入 SecurityContextHolder
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
