package com.ellenfang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ellenfang.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;


/**
 * 用户表(User)表数据库访问层
 *
 * @author makejava
 * @since 2022-07-13 17:11:25
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}

