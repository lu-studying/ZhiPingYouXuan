package com.demo.dp.mapper;

import com.demo.dp.domain.entity.UserTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户标签关联 Mapper，对应表 user_tag。
 *
 * <p>主要功能：
 * <ul>
 *     <li>为用户批量绑定标签</li>
 *     <li>查询用户的标签关联</li>
 *     <li>删除用户的所有标签关联</li>
 * </ul>
 */
@Mapper
public interface UserTagMapper {

    /**
     * 查询指定用户的标签关联列表。
     *
     * @param userId 用户ID
     * @return 关联列表
     */
    List<UserTag> findByUserId(@Param("userId") Long userId);

    /**
     * 批量插入用户标签关联。
     *
     * @param list 关联列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<UserTag> list);

    /**
     * 删除指定用户的所有标签关联。
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Long userId);
}


