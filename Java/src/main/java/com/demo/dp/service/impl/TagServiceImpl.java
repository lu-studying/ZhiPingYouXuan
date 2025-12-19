package com.demo.dp.service.impl;

import com.demo.dp.domain.entity.ShopTag;
import com.demo.dp.domain.entity.Tag;
import com.demo.dp.domain.entity.UserTag;
import com.demo.dp.mapper.ShopMapper;
import com.demo.dp.mapper.ShopTagMapper;
import com.demo.dp.mapper.TagMapper;
import com.demo.dp.mapper.UserMapper;
import com.demo.dp.mapper.UserTagMapper;
import com.demo.dp.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签服务实现类：实现标签及其与用户/商家的绑定逻辑。
 */
@Service
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final ShopTagMapper shopTagMapper;
    private final UserTagMapper userTagMapper;
    private final ShopMapper shopMapper;
    private final UserMapper userMapper;

    public TagServiceImpl(TagMapper tagMapper,
                          ShopTagMapper shopTagMapper,
                          UserTagMapper userTagMapper,
                          ShopMapper shopMapper,
                          UserMapper userMapper) {
        this.tagMapper = tagMapper;
        this.shopTagMapper = shopTagMapper;
        this.userTagMapper = userTagMapper;
        this.shopMapper = shopMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public Tag createTag(String name, String type) {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setType(type);
        tagMapper.insert(tag);
        return tag;
    }

    @Override
    public List<Tag> listByType(String type) {
        return tagMapper.findByType(type);
    }

    @Override
    public List<Tag> listAll() {
        return tagMapper.findAll();
    }

    @Override
    @Transactional
    public void assignTagsToShop(Long shopId, List<Long> tagIds) {
        // 校验商家是否存在
        if (shopMapper.findById(shopId) == null) {
            throw new RuntimeException("商家不存在，ID: " + shopId);
        }
        // 清空原有绑定
        shopTagMapper.deleteByShopId(shopId);
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        // 重建绑定
        List<ShopTag> list = new ArrayList<>();
        for (Long tagId : tagIds) {
            ShopTag st = new ShopTag();
            st.setShopId(shopId);
            st.setTagId(tagId);
            st.setWeight(1.0);
            list.add(st);
        }
        shopTagMapper.insertBatch(list);
    }

    @Override
    public List<Tag> listTagsOfShop(Long shopId) {
        List<ShopTag> links = shopTagMapper.findByShopId(shopId);
        List<Tag> result = new ArrayList<>();
        for (ShopTag link : links) {
            Tag tag = tagMapper.findById(link.getTagId());
            if (tag != null) {
                result.add(tag);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public void assignTagsToUser(Long userId, List<Long> tagIds) {
        // 校验用户是否存在
        if (userMapper.findById(userId) == null) {
            throw new RuntimeException("用户不存在，ID: " + userId);
        }
        // 清空原有绑定
        userTagMapper.deleteByUserId(userId);
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        // 重建绑定
        List<UserTag> list = new ArrayList<>();
        for (Long tagId : tagIds) {
            UserTag ut = new UserTag();
            ut.setUserId(userId);
            ut.setTagId(tagId);
            ut.setWeight(1.0);
            list.add(ut);
        }
        userTagMapper.insertBatch(list);
    }

    @Override
    public List<Tag> listTagsOfUser(Long userId) {
        List<UserTag> links = userTagMapper.findByUserId(userId);
        List<Tag> result = new ArrayList<>();
        for (UserTag link : links) {
            Tag tag = tagMapper.findById(link.getTagId());
            if (tag != null) {
                result.add(tag);
            }
        }
        return result;
    }
}


