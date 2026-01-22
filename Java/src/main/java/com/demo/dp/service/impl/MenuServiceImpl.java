package com.demo.dp.service.impl;

import com.demo.dp.domain.entity.Menu;
import com.demo.dp.mapper.MenuMapper;
import com.demo.dp.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单服务实现类：实现菜单相关的业务逻辑。
 * 
 * @author System
 * @version 1.0
 */
@Service
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    public MenuServiceImpl(MenuMapper menuMapper) {
        this.menuMapper = menuMapper;
    }

    @Override
    public List<Menu> getRecommendedMenus(Long shopId, Integer limit) {
        if (shopId == null || shopId <= 0) {
            return List.of();
        }
        return menuMapper.findRecommendedByShopId(shopId, limit);
    }

    @Override
    public List<Menu> getMenusByShopId(Long shopId) {
        if (shopId == null || shopId <= 0) {
            return List.of();
        }
        return menuMapper.findByShopId(shopId);
    }

    @Override
    public Menu getById(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        return menuMapper.findById(id);
    }

    @Override
    @Transactional
    public Menu createMenu(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("菜单对象不能为空");
        }
        if (menu.getShopId() == null || menu.getShopId() <= 0) {
            throw new IllegalArgumentException("商家ID不能为空");
        }
        if (menu.getName() == null || menu.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("菜品名称不能为空");
        }

        // 设置默认值
        if (menu.getIsRecommended() == null) {
            menu.setIsRecommended(0);
        }
        if (menu.getSortOrder() == null) {
            menu.setSortOrder(0);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }

        menuMapper.insert(menu);
        return menu;
    }

    @Override
    @Transactional
    public Menu updateMenu(Menu menu) {
        if (menu == null || menu.getId() == null || menu.getId() <= 0) {
            throw new IllegalArgumentException("菜单ID不能为空");
        }

        Menu existing = menuMapper.findById(menu.getId());
        if (existing == null) {
            throw new RuntimeException("菜单不存在");
        }

        menuMapper.update(menu);
        return menuMapper.findById(menu.getId());
    }

    @Override
    @Transactional
    public void deleteMenu(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("菜单ID不能为空");
        }

        Menu existing = menuMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("菜单不存在");
        }

        menuMapper.delete(id);
    }
}

