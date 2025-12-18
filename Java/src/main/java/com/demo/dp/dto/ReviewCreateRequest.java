package com.demo.dp.dto;

/**
 * 创建点评请求 DTO。
 * images 使用 JSON 字符串（前端可传数组转字符串），后端可按需解析。
 */
public class ReviewCreateRequest {
    private Integer rating;
    private String content;
    private String images;

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
}

