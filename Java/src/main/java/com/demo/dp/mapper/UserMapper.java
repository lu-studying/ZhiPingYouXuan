package com.demo.dp.mapper;

import com.demo.dp.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 Mapper 接口。
 * SQL 定义在 mapper/UserMapper.xml。
 */
@Mapper
public interface UserMapper {
    /**
     * 根据手机号查询用户
     */
    User findByMobile(@Param("mobile") String mobile);

    /**
     * 根据邮箱查询用户
     */
    User findByEmail(@Param("email") String email);

    /**
     * 插入新用户
     */
    int insert(User user);

    /**
     * 根据 ID 查询用户
     */
    User findById(@Param("id") Long id);
}

