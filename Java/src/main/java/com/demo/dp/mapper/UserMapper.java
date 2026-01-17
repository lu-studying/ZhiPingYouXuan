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

    /**
     * 分页查询用户列表（支持关键词搜索）。
     *
     * @param offset 查询偏移量
     * @param limit 每页数量
     * @param keyword 搜索关键词（在手机号和邮箱中搜索），可选
     * @return 用户列表
     */
    java.util.List<User> findByConditions(@Param("offset") int offset,
                                               @Param("limit") int limit,
                                               @Param("keyword") String keyword);

    /**
     * 统计符合条件的用户总数（用于分页计算）。
     * 
     * @param keyword 搜索关键词（在手机号和邮箱中搜索），可选
     * @return 用户总数
     */
    long countByConditions(@Param("keyword") String keyword);

    /**
     * 更新用户信息
     * 
     * @param user 用户对象（包含需要更新的字段）
     * @return 更新的行数
     */
    int update(User user);

    /**
     * 根据昵称查询用户（用于查重，排除指定用户ID）
     * 
     * @param nickname 昵称
     * @param excludeUserId 排除的用户ID（用于更新时排除自己）
     * @return 用户对象，如果不存在则返回 null
     */
    User findByNickname(@Param("nickname") String nickname, @Param("excludeUserId") Long excludeUserId);
}

