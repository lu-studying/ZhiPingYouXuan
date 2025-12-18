package com.demo.dp.service;

import com.demo.dp.domain.entity.User;

import java.util.Optional;

/**
 * 用户服务接口：定义用户相关的业务操作。
 * 
 * 主要功能：
 * - 用户注册
 * - 用户查询（根据手机号/邮箱）
 */
public interface UserService {

    /**
     * 用户注册
     * 
     * @param mobileOrEmail 手机号或邮箱
     * @param password 密码（明文，内部会进行哈希处理）
     * @return 注册成功的用户对象
     * @throws IllegalArgumentException 当手机号或邮箱已存在时抛出
     */
    User register(String mobileOrEmail, String password);

    /**
     * 根据手机号查询用户
     * 
     * @param mobile 手机号
     * @return 用户对象，如果不存在则返回空
     */
    Optional<User> findByMobile(String mobile);

    /**
     * 根据邮箱查询用户
     * 
     * @param email 邮箱
     * @return 用户对象，如果不存在则返回空
     */
    Optional<User> findByEmail(String email);
}
