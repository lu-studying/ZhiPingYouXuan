package com.demo.dp.service;

import com.demo.dp.domain.entity.Menu;

import java.util.List;

/**
 * 菜单服务接口：定义菜单相关的业务操作。
 * 
 * @author System
 * @version 1.0
 */
public interface MenuService {

    /**
     * 根据商家ID查询推荐菜单列表。
     * 
     * @param shopId 商家ID
     * @param limit 返回数量限制，可选。如果为null或<=0，则返回所有推荐菜单
     * @return 推荐菜单列表，按sortOrder升序、createdAt降序排列
     */
    List<Menu> getRecommendedMenus(Long shopId, Integer limit);

    /**
     * 根据商家ID查询所有菜单（包括推荐和非推荐）。
     * 
     * @param shopId 商家ID
     * @return 菜单列表，按isRecommended降序、sortOrder升序、createdAt降序排列
     */
    List<Menu> getMenusByShopId(Long shopId);

    /**
     * 根据主键ID查询菜单信息。
     * 
     * @param id 菜单ID
     * @return 菜单对象，如果不存在则返回null
     */
    Menu getById(Long id);

    /**
     * 创建新菜单。
     * 
     * @param menu 菜单对象
     * @return 创建成功的菜单对象，包含数据库自动生成的主键ID
     */
    Menu createMenu(Menu menu);

    /**
     * 更新菜单信息，支持部分字段更新。
     * 
     * @param menu 菜单对象，必须包含id字段
     * @return 更新后的完整菜单对象
     */
    Menu updateMenu(Menu menu);

    /**
     * 删除菜单（软删除）。
     * 
     * @param id 菜单ID
     */
    void deleteMenu(Long id);
}

