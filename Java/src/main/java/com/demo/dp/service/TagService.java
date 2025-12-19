package com.demo.dp.service;

import com.demo.dp.domain.entity.Tag;

import java.util.List;

/**
 * 标签服务接口：封装标签与标签绑定相关业务。
 */
public interface TagService {

    /**
     * 创建标签。
     *
     * @param name 标签名称
     * @param type 标签类型：user/shop/review
     * @return 创建成功的标签
     */
    Tag createTag(String name, String type);

    /**
     * 查询某类型的所有标签。
     *
     * @param type 标签类型：user/shop/review
     * @return 标签列表
     */
    List<Tag> listByType(String type);

    /**
     * 查询所有标签。
     */
    List<Tag> listAll();

    /**
     * 为商家绑定标签（会清空原有绑定，再按传入列表重建）。
     *
     * @param shopId 商家ID
     * @param tagIds 标签ID列表
     */
    void assignTagsToShop(Long shopId, List<Long> tagIds);

    /**
     * 查询商家已绑定的标签列表。
     *
     * @param shopId 商家ID
     * @return 标签列表
     */
    List<Tag> listTagsOfShop(Long shopId);

    /**
     * 为用户绑定标签（会清空原有绑定，再按传入列表重建）。
     *
     * @param userId 用户ID
     * @param tagIds 标签ID列表
     */
    void assignTagsToUser(Long userId, List<Long> tagIds);

    /**
     * 查询用户已绑定的标签列表。
     *
     * @param userId 用户ID
     * @return 标签列表
     */
    List<Tag> listTagsOfUser(Long userId);
}


