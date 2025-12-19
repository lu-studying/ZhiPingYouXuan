package com.demo.dp.dto;

/**
 * 创建标签请求 DTO。
 */
public class TagCreateRequest {

    /**
     * 标签名称，例如：辣、清淡、适合聚会、环境好。
     */
    private String name;

    /**
     * 标签类型：user / shop / review。
     */
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}


