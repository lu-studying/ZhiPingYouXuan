package com.demo.dp.domain.entity;

import java.time.LocalDateTime;

/**
 * 用户实体，对应表 user。
 * 说明：
 * - MyBatis 使用，字段命名与数据库表对应（下划线转驼峰由 MyBatis 配置处理）。
 * - 建议在数据库层对 mobile/email 设置唯一索引，应用层再做防重校验。
 */
public class User {
    private Long id; // 主键

    private String mobile; // 手机号，可空

    private String email;  // 邮箱，可空

    private String passwordHash; // 密码哈希

    private String nickname; // 昵称

    private String avatar; // 头像 URL

    private Integer status; // 状态：1 正常，0 禁用

    private LocalDateTime createdAt; // 创建时间

    private LocalDateTime updatedAt; // 更新时间

    // region getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    // endregion
}

