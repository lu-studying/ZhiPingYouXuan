package com.demo.dp.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色相关 Mapper。
 *
 * 目前仅用于根据用户 ID 查询其角色编码列表，用来写入 JWT。
 */
@Mapper
public interface RoleMapper {

    /**
     * 根据用户 ID 查询其拥有的角色编码列表。
     *
     * @param userId 用户 ID
     * @return 角色编码列表，例如 ["ADMIN", "MERCHANT", "USER"]
     */
    List<String> findRoleCodesByUserId(@Param("userId") Long userId);
}


