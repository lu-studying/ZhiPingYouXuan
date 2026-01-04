package com.demo.dp.service;

import com.demo.dp.domain.entity.Review;

import java.util.List;

/**
 * 点评服务接口：定义点评相关的业务操作。
 * 
 * 主要功能：
 * - 查询商家点评列表（分页）
 * - 获取商家点评总数
 * - 创建新点评
 * - 点赞点评
 */
public interface ReviewService {

    /**
     * 分页查询商家点评
     * 
     * @param shopId 商家ID
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 点评列表
     */
    List<Review> listByShop(Long shopId, int page, int size);

    /**
     * 获取商家点评总数
     * 
     * @param shopId 商家ID
     * @return 点评总数
     */
    long countByShopId(Long shopId);

    /**
     * 创建新点评
     * 
     * @param userId 用户ID
     * @param shopId 商家ID
     * @param rating 评分（1-5）
     * @param content 点评内容
     * @param images 图片列表（JSON字符串）
     * @return 创建成功的点评对象
     */
    Review createReview(Long userId, Long shopId, Integer rating, String content, String images);

    /**
     * 点赞指定点评。
     *
     * <p>MVP 实现为简单的计数自增，暂未做"同一用户只能点一次"的去重控制，
     * 后续可通过新增 like 记录表(user_id, review_id) 实现防重复点赞。
     *
     * @param userId   点赞用户ID（当前登录用户）
     * @param reviewId 点评ID
     */
    void likeReview(Long userId, Long reviewId);

    /**
     * 分页查询用户点评列表。
     * 
     * @param userId 用户ID
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 点评列表
     */
    List<Review> listByUser(Long userId, int page, int size);

    /**
     * 获取用户点评总数。
     * 
     * @param userId 用户ID
     * @return 点评总数
     */
    long countByUserId(Long userId);
}
