package com.demo.dp.dto;

import java.util.List;

/**
 * 绑定标签请求 DTO：用于为用户或商家绑定一组标签ID。
 */
public class AssignTagsRequest {

    /**
     * 标签ID列表。
     */
    private List<Long> tagIds;

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }
}


