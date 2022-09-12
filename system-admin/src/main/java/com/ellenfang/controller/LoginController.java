package com.ellenfang.controller;

import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.entity.LoginUser;
import com.ellenfang.domain.entity.Menu;
import com.ellenfang.domain.entity.User;
import com.ellenfang.domain.vo.AdminUserInfoVo;
import com.ellenfang.domain.vo.RoutersVo;
import com.ellenfang.domain.vo.UserInfoVo;
import com.ellenfang.enums.AppHttpCodeEnum;
import com.ellenfang.exception.SystemException;
import com.ellenfang.service.LoginService;
import com.ellenfang.service.MenuService;
import com.ellenfang.service.RoleService;
import com.ellenfang.utils.BeanCopyUtils;
import com.ellenfang.utils.RedisCache;
import com.ellenfang.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RedisCache redisCache;

    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody User user){
        if(!StringUtils.hasText(user.getUserName())){
            //提示 必须要传用户名
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return loginService.login(user);
    }

    @PostMapping("/user/logout")
    public ResponseResult logout() {
        return loginService.logout();
    }

    @GetMapping("getInfo")
    public ResponseResult<AdminUserInfoVo> getInfo() {
        // 获取当前登录的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 根据用户 id 查询用户权限
        List<String> perms = menuService.selectPermsByUserId(loginUser.getUser().getId());
        // 根据用户 id 查询用户角色
        List<String> roleKeyList = roleService.selectRoleKeyByUserId(loginUser.getUser().getId());

        // 获取用户信息
        User user = loginUser.getUser();
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);

        // 封装数据返回
        AdminUserInfoVo adminUserInfoVo = new AdminUserInfoVo(perms, roleKeyList, userInfoVo);
        return ResponseResult.okResult(adminUserInfoVo);
    }

    @GetMapping("getRouters")
    public ResponseResult<RoutersVo> getRouters() {
        // 获取用户 id
        Long userId = SecurityUtils.getUserId();
        // 查询 menu，结果是 tree 形式（子夫菜单）
        List<Menu> menus = menuService.selectRouterMenuTreeByUserId(userId);
        // 封装数据返回
        return ResponseResult.okResult(new RoutersVo(menus));
    }

}