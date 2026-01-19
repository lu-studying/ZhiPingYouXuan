package com.demo.dp.controller;

import com.demo.dp.domain.entity.Tag;
import com.demo.dp.dto.AssignTagsRequest;
import com.demo.dp.dto.TagCreateRequest;
import com.demo.dp.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 标签及偏好体系相关接口。
 *
 * <p>主要能力：
 * <ul>
 *     <li>标签管理（创建、按类型查询、查询全部）</li>
 *     <li>为商家绑定标签、查询商家标签</li>
 *     <li>为用户绑定标签、查询用户标签</li>
 * </ul>
 */
@RestController
@RequestMapping("/api")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * 创建标签。
     *
     * <p>路径：POST /api/tags
     * <p>说明：
     * - type=user 时必须登录，且创建后仅绑定当前用户，其他用户不可见/不可用。
     * - type=shop/review 按原逻辑创建。
     */
    @PostMapping("/tags")
    public ResponseEntity<?> createTag(@RequestBody TagCreateRequest req, Authentication authentication) {
        if (req.getName() == null || req.getName().isBlank()) {
            return error(400, "标签名称不能为空");
        }
        if (req.getType() == null || req.getType().isBlank()) {
            return error(400, "标签类型不能为空");
        }

        String type = req.getType().trim();
        String name = req.getName().trim();

        // user 类型标签，必须登录且只绑定当前用户
        if ("user".equalsIgnoreCase(type)) {
            if (authentication == null) {
                return error(401, "请先登录");
            }
            Long userId = Long.parseLong(authentication.getName());
            Tag tag = tagService.createTag(name, type);
            tagService.bindTagToUser(userId, tag.getId());
            return ResponseEntity.ok(tag);
        }

        // 其他类型保持原逻辑
        Tag tag = tagService.createTag(name, type);
        return ResponseEntity.ok(tag);
    }

    /**
     * 查询某类型的标签列表。
     *
     * <p>路径：GET /api/tags?type=shop
     * <p>业务规则：type=user 时仅返回“当前用户已绑定的标签”，其他用户看不到。
     */
    @GetMapping("/tags")
    public ResponseEntity<?> listTags(@RequestParam(required = false) String type,
                                      Authentication authentication) {
        if (type == null || type.isBlank()) {
            return ResponseEntity.ok(tagService.listAll());
        }

        // 用户私有标签：必须登录，仅返回自己的
        if ("user".equalsIgnoreCase(type)) {
            if (authentication == null) {
                return error(401, "请先登录");
            }
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.ok(tagService.listTagsOfUser(userId));
        }

        return ResponseEntity.ok(tagService.listByType(type));
    }

    /**
     * 为商家绑定标签（覆盖式）。
     *
     * <p>路径：POST /api/shops/{shopId}/tags
     * <p>Body：{"tagIds":[1,2,3]}
     */
    @PostMapping("/shops/{shopId}/tags")
    public ResponseEntity<?> assignTagsToShop(@PathVariable Long shopId,
                                              @RequestBody AssignTagsRequest req) {
        tagService.assignTagsToShop(shopId, req.getTagIds());
        return ResponseEntity.ok(Map.of("message", "商家标签绑定成功"));
    }

    /**
     * 查询商家已绑定的标签列表。
     *
     * <p>路径：GET /api/shops/{shopId}/tags
     */
    @GetMapping("/shops/{shopId}/tags")
    public ResponseEntity<List<Tag>> listTagsOfShop(@PathVariable Long shopId) {
        return ResponseEntity.ok(tagService.listTagsOfShop(shopId));
    }

    /**
     * 为当前登录用户绑定标签（覆盖式）。
     *
     * <p>路径：POST /api/users/me/tags
     */
    @PostMapping("/users/me/tags")
    public ResponseEntity<?> assignTagsToCurrentUser(@RequestBody AssignTagsRequest req,
                                                     Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        tagService.assignTagsToUser(userId, req.getTagIds());
        return ResponseEntity.ok(Map.of("message", "用户标签绑定成功"));
    }

    /**
     * 查询当前登录用户的标签列表。
     *
     * <p>路径：GET /api/users/me/tags
     */
    @GetMapping("/users/me/tags")
    public ResponseEntity<List<Tag>> listTagsOfCurrentUser(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(tagService.listTagsOfUser(userId));
    }

    private ResponseEntity<Map<String, Object>> error(int status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", status);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}


