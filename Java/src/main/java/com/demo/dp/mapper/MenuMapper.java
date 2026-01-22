package com.demo.dp.mapper;

import com.demo.dp.domain.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单数据访问层（Mapper）接口。
 * 
 * <p>该接口定义了菜单相关的数据库操作方法，使用MyBatis框架进行ORM映射。
 * 具体的SQL语句定义在对应的XML映射文件 {@code MenuMapper.xml} 中。
 * 
 * @author System
 * @version 1.0
 */
@Mapper
public interface MenuMapper {
    
    /**
     * 根据商家ID查询推荐菜单列表。
     * 
     * @param shopId 商家ID
     * @param limit 返回数量限制，可选。如果为null或<=0，则返回所有推荐菜单
     * @return 推荐菜单列表，按sortOrder升序、createdAt降序排列
     */
    List<Menu> findRecommendedByShopId(@Param("shopId") Long shopId, @Param("limit") Integer limit);
    
    /**
     * 根据商家ID查询所有菜单（包括推荐和非推荐）。
     * 
     * @param shopId 商家ID
     * @return 菜单列表，按isRecommended降序、sortOrder升序、createdAt降序排列
     */
    List<Menu> findByShopId(@Param("shopId") Long shopId);
    
    /**
     * 根据主键ID查询菜单信息。
     * 
     * @param id 菜单主键ID
     * @return 菜单对象，如果不存在则返回null
     */
    Menu findById(@Param("id") Long id);
    
    /**
     * 插入新菜单记录。
     * 
     * @param menu 要插入的菜单对象
     * @return 受影响的行数
     */
    int insert(Menu menu);
    
    /**
     * 更新菜单记录。
     * 
     * @param menu 要更新的菜单对象，必须包含id字段
     * @return 受影响的行数
     */
    int update(Menu menu);
    
    /**
     * 删除菜单（软删除，设置status=0）。
     * 
     * @param id 菜单ID
     * @return 受影响的行数
     */
    int delete(@Param("id") Long id);
}

