package com.demo.dp.mapper;

import com.demo.dp.domain.entity.ShopTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商家标签关联 Mapper，对应表 shop_tag。
 *
 * <p>主要功能：
 * <ul>
 *     <li>为商家批量绑定标签</li>
 *     <li>查询商家的标签关联</li>
 *     <li>删除商家的所有标签关联</li>
 * </ul>
 */
@Mapper
public interface ShopTagMapper {

    /**
     * 查询指定商家的标签关联列表。
     *
     * @param shopId 商家ID
     * @return 关联列表
     */
    List<ShopTag> findByShopId(@Param("shopId") Long shopId);

    /**
     * 批量插入商家标签关联。
     *
     * @param list 关联列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<ShopTag> list);

    /**
     * 删除指定商家的所有标签关联。
     *
     * @param shopId 商家ID
     * @return 影响行数
     */
    int deleteByShopId(@Param("shopId") Long shopId);
}


