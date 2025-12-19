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
     * <p>说明：仅简单创建 name + type（user/shop/review），后续可扩展描述等字段。
     */
    @PostMapping("/tags")
    public ResponseEntity<?> createTag(@RequestBody TagCreateRequest req) {
        if (req.getName() == null || req.getName().isBlank()) {
            return error(400, "标签名称不能为空");
        }
        if (req.getType() == null || req.getType().isBlank()) {
            return error(400, "标签类型不能为空");
        }
        Tag tag = tagService.createTag(req.getName().trim(), req.getType().trim());
        return ResponseEntity.ok(tag);
    }

    /**
     * 查询某类型的标签列表。
     *
     * <p>路径：GET /api/tags?type=shop
     */
    @GetMapping("/tags")
    public ResponseEntity<List<Tag>> listTags(@RequestParam(required = false) String type) {
        if (type == null || type.isBlank()) {
            return ResponseEntity.ok(tagService.listAll());
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


