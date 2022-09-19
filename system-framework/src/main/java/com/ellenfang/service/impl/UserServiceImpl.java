package com.ellenfang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.dto.AddUserDto;
import com.ellenfang.domain.dto.UpdateUserDto;
import com.ellenfang.domain.entity.Role;
import com.ellenfang.domain.entity.User;
import com.ellenfang.domain.entity.UserRole;
import com.ellenfang.domain.vo.AdminUpdateUserInfo;
import com.ellenfang.domain.vo.PageVo;
import com.ellenfang.domain.vo.UserInfoVo;
import com.ellenfang.enums.AppHttpCodeEnum;
import com.ellenfang.exception.SystemException;
import com.ellenfang.mapper.RoleMapper;
import com.ellenfang.mapper.UserMapper;
import com.ellenfang.mapper.UserRoleMapper;
import com.ellenfang.service.RoleService;
import com.ellenfang.service.UserRoleService;
import com.ellenfang.service.UserService;
import com.ellenfang.utils.BeanCopyUtils;
import com.ellenfang.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2022-07-16 19:44:51
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    RoleMapper roleMapper;

    @Override
    public ResponseResult userInfo() {
        // 获取当前用户 id
        Long userId = SecurityUtils.getUserId();
        // 根据用户 id 查询用户信息
        User user = getById(userId);
        // 封装成 UserInfoVo
        UserInfoVo vo = BeanCopyUtils.copyBean(user,UserInfoVo.class);
        return ResponseResult.okResult(vo);
    }

    @Override
    public ResponseResult updateUserInfo(User user) {
        updateById(user);
        return ResponseResult.okResult();
    }

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public ResponseResult register(User user) {
        // 对数据进行非空的判断
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getPassword())){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }

        // 对数据进行是否存在（重复）的判断
        if (userNameExist(user.getUserName())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if(nickNameExist(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_EXIST);
        }
        if(emailExist(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }

        // 对密码进行加密
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);

        // 存入数据库
        save(user);

        return ResponseResult.okResult();
    }

    /**
     * 添加用户，与注册用户类似，不过添加用户是由后台系统完成，可以绑定手机与邮箱
     * 新增用户时可以直接关联角色
     * @param addUserDto 新增用户 Dto
     * @return 处理结果 json
     */
    @Override
    public ResponseResult addUser(AddUserDto addUserDto) {
        // 对传入数据进行非空处理
        if (!StringUtils.hasText(addUserDto.getUserName())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if (!StringUtils.hasText(addUserDto.getPassword())) {
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if(!StringUtils.hasText(addUserDto.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        if(!StringUtils.hasText(addUserDto.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }
        if(!StringUtils.hasText(addUserDto.getPhonenumber())) {
            throw new SystemException(AppHttpCodeEnum.PHONENUMBER_NOT_NULL);
        }
        if (!StringUtils.hasText(addUserDto.getSex())) {
            throw new SystemException(AppHttpCodeEnum.SEX_NOT_NULL);
        }

        // 用户名必须之前不存在
        if (userNameExist(addUserDto.getUserName())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        // 手机号必须之前不存在
        if (phoneNumberExist(addUserDto.getPhonenumber())) {
            throw new SystemException(AppHttpCodeEnum.PHONENUMBER_EXIST);
        }
        // 邮箱必须之前不存在
        if (emailExist(addUserDto.getEmail())) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }

        // 对密码进行加密
        String encodePassword = passwordEncoder.encode(addUserDto.getPassword());
        addUserDto.setPassword(encodePassword);

        // 转换为 User
        User user = BeanCopyUtils.copyBean(addUserDto, User.class);
        // 持久化
        save(user);

        // 存入用户与角色的关联关系
        List<Long> roleIds = addUserDto.getRoleIds();
        roleIds.stream()
                .forEach(roleId -> userRoleService.save(new UserRole(user.getId(), roleId)));

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateUser(UpdateUserDto updateUserDto) {
        User user = BeanCopyUtils.copyBean(updateUserDto, User.class);
        UserMapper userMapper = getBaseMapper();
        userMapper.updateById(user);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteUser(Long id) {
        removeById(id);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult<AdminUpdateUserInfo> adminGetUserInfo(Long id) {
        // 根据用户id查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, id);
        UserMapper userMapper = getBaseMapper();
        User user = userMapper.selectOne(queryWrapper);
        // 封装为 vo
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);

        // 根据用户id查询用户相关链的角色id列表
        LambdaQueryWrapper<UserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoleLambdaQueryWrapper.eq(UserRole::getUserId, id);
        List<UserRole> userRoles = userRoleService.list(userRoleLambdaQueryWrapper);
        List<Long> roleIds = userRoles.stream().map(userRole -> userRole.getRoleId()).collect(Collectors.toList());

        // 根据roleId列表查询对应的角色信息
        List<Role> roles = null;
        if (roleIds.size() > 0) {
            roles = roleMapper.selectBatchIds(roleIds);
        }

        // 封装为 vo
        AdminUpdateUserInfo adminUpdateUserInfo = new AdminUpdateUserInfo();
        adminUpdateUserInfo.setUser(userInfoVo);
        adminUpdateUserInfo.setRoles(roles);
        adminUpdateUserInfo.setRoleIds(roleIds);

        return ResponseResult.okResult(adminUpdateUserInfo);
    }

    @Override
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, String userName, String phonenumber, String status) {
        // 根据相关条件查询用户列表
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        // 若参数：userName 不为空，则按照用户名进行模糊查询
        queryWrapper.like(StringUtils.hasText(userName), User::getUserName, userName);
        // 若参数：phonenumber 不为空，则按照手机号进行查询
        queryWrapper.eq(StringUtils.hasText(phonenumber), User::getPhonenumber,phonenumber);
        // 若参数：status 不为空，则按照状态进行查询
        queryWrapper.eq(StringUtils.hasText(status), User::getStatus, status);

        // 分页查询
        Page<User> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);

        // 封装为 vo
        PageVo pageVo = new PageVo(page.getRecords(), page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    private boolean emailExist(String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        return count(queryWrapper) > 0;
    }

    private boolean nickNameExist(String nickName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getNickName, nickName);
        return count(queryWrapper) > 0;
    }

    private boolean userNameExist(String userName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userName);
        return count(queryWrapper) > 0;
    }

    private boolean phoneNumberExist(String phoneNumber) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhonenumber, phoneNumber);
        return count(queryWrapper) > 0;
    }
}

