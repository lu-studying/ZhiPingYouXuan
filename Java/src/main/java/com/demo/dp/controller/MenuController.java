package com.demo.dp.controller;

import com.demo.dp.domain.entity.Menu;
import com.demo.dp.service.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单管理控制器：提供菜单相关的REST API接口。
 * 
 * <p>所有接口路径都以 {@code /api/menus} 为前缀。
 * 
 * @author System
 * @version 1.0
 */
@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private static final Logger log = LoggerFactory.getLogger(MenuController.class);

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * 获取商家的推荐菜单列表。
     * 
     * <p>请求示例：
     * <ul>
     *   <li>GET /api/menus/shop/1/recommended - 获取商家ID为1的推荐菜单</li>
     *   <li>GET /api/menus/shop/1/recommended?limit=5 - 获取商家ID为1的前5个推荐菜单</li>
     * </ul>
     * 
     * @param shopId 商家ID
     * @param limit 返回数量限制，可选。默认返回所有推荐菜单
     * @return ResponseEntity包含推荐菜单列表
     */
    @GetMapping("/shop/{shopId}/recommended")
    public ResponseEntity<List<Menu>> getRecommendedMenus(
            @PathVariable Long shopId,
            @RequestParam(required = false) Integer limit) {
        try {
            List<Menu> menus = menuService.getRecommendedMenus(shopId, limit);
            return ResponseEntity.ok(menus);
        } catch (Exception e) {
            log.error("获取推荐菜单失败, shopId={}", shopId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取商家的所有菜单列表。
     * 
     * <p>请求示例：
     * <ul>
     *   <li>GET /api/menus/shop/1 - 获取商家ID为1的所有菜单</li>
     * </ul>
     * 
     * @param shopId 商家ID
     * @return ResponseEntity包含菜单列表
     */
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Menu>> getMenusByShopId(@PathVariable Long shopId) {
        try {
            List<Menu> menus = menuService.getMenusByShopId(shopId);
            return ResponseEntity.ok(menus);
        } catch (Exception e) {
            log.error("获取菜单列表失败, shopId={}", shopId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取菜单详情。
     * 
     * @param id 菜单ID
     * @return ResponseEntity包含菜单对象，404表示菜单不存在
     */
    @GetMapping("/{id}")
    public ResponseEntity<Menu> getMenu(@PathVariable Long id) {
        Menu menu = menuService.getById(id);
        if (menu == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(menu);
    }

    /**
     * 创建新菜单。
     * 
     * @param menu 菜单对象
     * @return ResponseEntity包含创建的菜单对象，HTTP状态码201表示创建成功
     */
    @PostMapping
    public ResponseEntity<?> createMenu(@RequestBody Menu menu) {
        try {
            Menu saved = menuService.createMenu(menu);
            log.info("创建菜单成功, menuId={}, name={}", saved.getId(), saved.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            log.error("创建菜单失败: {}", e.getMessage());
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.error("创建菜单失败", e);
            return error(500, "创建菜单失败: " + e.getMessage());
        }
    }

    /**
     * 更新菜单信息。
     * 
     * @param id 菜单ID
     * @param menu 菜单对象
     * @return ResponseEntity包含更新后的菜单对象
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenu(@PathVariable Long id, @RequestBody Menu menu) {
        try {
            menu.setId(id);
            Menu updated = menuService.updateMenu(menu);
            log.info("更新菜单成功, menuId={}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("更新菜单失败, menuId={}", id, e);
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.error("更新菜单失败, menuId={}", id, e);
            return error(500, "更新菜单失败: " + e.getMessage());
        }
    }

    /**
     * 删除菜单（软删除）。
     * 
     * @param id 菜单ID
     * @return ResponseEntity包含删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenu(@PathVariable Long id) {
        try {
            menuService.deleteMenu(id);
            log.info("删除菜单成功, menuId={}", id);
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (RuntimeException e) {
            log.error("删除菜单失败, menuId={}", id, e);
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除菜单失败, menuId={}", id, e);
            return error(500, "删除菜单失败: " + e.getMessage());
        }
    }

    private ResponseEntity<Map<String, Object>> error(int status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", status);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}

