package com.demo.dp.mapper;

import com.demo.dp.domain.entity.ReviewKeyword;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 点评关键词 Mapper，对应表 review_keyword。
 *
 * <p>功能：
 * <ul>
 *     <li>批量插入关键词</li>
 *     <li>按店铺与关键词召回点评（用于推荐）</li>
 * </ul>
 */
@Mapper
public interface ReviewKeywordMapper {

    /**
     * 批量插入关键词。
     *
     * @param keywords 关键词列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<ReviewKeyword> keywords);

    /**
     * 按店铺与关键词召回点评（结果返回点评ID）。
     *
     * @param shopId  商家ID
     * @param keyword 关键词
     * @param limit   返回数量
     * @return 点评ID列表
     */
    List<Long> findReviewIdsByShopAndKeyword(@Param("shopId") Long shopId,
                                             @Param("keyword") String keyword,
                                             @Param("limit") int limit);
}

