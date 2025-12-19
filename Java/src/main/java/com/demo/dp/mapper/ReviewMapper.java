package com.demo.dp.mapper;

import com.demo.dp.domain.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 点评 Mapper 接口。
 * SQL 定义在 mapper/ReviewMapper.xml。
 */
@Mapper
public interface ReviewMapper {
    /**
     * 根据商家 ID 分页查询点评
     */
    List<Review> findByShopId(@Param("shopId") Long shopId, 
                              @Param("offset") int offset, 
                              @Param("limit") int limit);

    /**
     * 统计商家点评总数
     */
    long countByShopId(@Param("shopId") Long shopId);

    /**
     * 插入新点评
     */
    int insert(Review review);

    /**
     * 根据 ID 查询点评
     */
    Review findById(@Param("id") Long id);

    /**
     * 基于关键词和权重的推荐查询（按店铺）。
     *
     * @param shopId  商家ID
     * @param keyword 关键词（可空，为空时仅按排序返回）
     * @param limit   返回数量上限
     * @return 点评列表（已按评分/点赞/时间排序）
     */
    List<Review> findTopByShopAndKeyword(@Param("shopId") Long shopId,
                                         @Param("keyword") String keyword,
                                         @Param("limit") int limit);

    /**
     * 推荐兜底：仅按点赞/评分/时间排序返回 TOP N。
     *
     * @param shopId 商家ID
     * @param limit  返回数量上限
     * @return 点评列表
     */
    List<Review> findTopByShop(@Param("shopId") Long shopId,
                               @Param("limit") int limit);

    /**
     * 点赞计数自增：like_count = like_count + 1。
     *
     * @param id 点评ID
     * @return 受影响行数（1表示成功，0表示未找到）
     */
    int increaseLikeCount(@Param("id") Long id);
}

