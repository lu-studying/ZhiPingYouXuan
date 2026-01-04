package com.demo.dp.mapper;

import com.demo.dp.domain.entity.Shop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商家数据访问层（Mapper）接口。
 * 
 * <p>该接口定义了商家相关的数据库操作方法，使用MyBatis框架进行ORM映射。
 * 具体的SQL语句定义在对应的XML映射文件 {@code ShopMapper.xml} 中。
 * 
 * <p>使用 {@code @Mapper} 注解标识为MyBatis的Mapper接口，
 * Spring Boot会自动扫描并注册该接口。
 * 
 * <p>方法说明：
 * <ul>
 *   <li>findById：根据主键ID查询单个商家</li>
 *   <li>insert：插入新商家记录</li>
 *   <li>update：更新商家记录</li>
 *   <li>findByConditions：根据多条件分页查询商家列表</li>
 *   <li>countByConditions：根据多条件统计商家总数</li>
 * </ul>
 * 
 * @author System
 * @version 1.0
 */
@Mapper
public interface ShopMapper {
    
    /**
     * 根据主键ID查询商家信息。
     * 
     * <p>该方法会根据主键ID查询对应的商家记录，返回完整的商家对象。
     * 如果商家不存在，则返回null。
     * 
     * <p>SQL映射：在 {@code ShopMapper.xml} 中定义，使用 {@code <select>} 标签。
     * 
     * @param id 商家主键ID，必须大于0
     * @return 商家对象，包含所有字段信息。如果商家不存在则返回null
     */
    Shop findById(@Param("id") Long id);

    /**
     * 插入新商家记录到数据库。
     * 
     * <p>该方法会将商家对象插入到数据库表中，并自动生成主键ID。
     * 插入成功后，主键ID会自动填充到传入的shop对象的id属性中。
     * 
     * <p>SQL映射：在 {@code ShopMapper.xml} 中定义，使用 {@code <insert>} 标签，
     * 并设置 {@code useGeneratedKeys="true"} 和 {@code keyProperty="id"} 以支持主键自动生成。
     * 
     * @param shop 要插入的商家对象，必须包含以下必填字段：
     *             <ul>
     *               <li>name：商家名称</li>
     *               <li>category：商家分类</li>
     *               <li>address：商家地址</li>
     *             </ul>
     * @return 受影响的行数，通常为1表示插入成功，0表示插入失败
     */
    int insert(Shop shop);

    /**
     * 更新商家记录。
     * 
     * <p>该方法会根据商家对象的id字段更新对应的数据库记录。
     * 会更新所有字段（除了id和createdAt），包括：
     * name, category, address, lng, lat, avg_price, avg_score, status, updated_at
     * 
     * <p>注意：该方法会更新所有字段，如果某个字段为null，也会将数据库中的对应字段更新为null。
     * 如果需要部分更新，应该在Service层先查询现有数据，然后合并后再调用此方法。
     * 
     * <p>SQL映射：在 {@code ShopMapper.xml} 中定义，使用 {@code <update>} 标签。
     * 
     * @param shop 要更新的商家对象，必须包含id字段，其他字段为要更新的新值
     * @return 受影响的行数，通常为1表示更新成功，0表示更新失败（如ID不存在）
     */
    int update(Shop shop);

    /**
     * 根据多条件分页查询商家列表。
     * 
     * <p>该方法支持多条件组合查询，并使用分页功能。
     * 筛选条件包括：
     * <ul>
     *   <li>分类筛选：按category字段精确匹配</li>
     *   <li>关键词搜索：在name和address字段中进行模糊匹配（LIKE查询）</li>
     *   <li>评分筛选：只返回avg_score >= minScore的记录</li>
     *   <li>价格筛选：只返回avg_price <= maxPrice的记录</li>
     * </ul>
     * 
     * <p>查询结果按以下规则排序：
     * <ol>
     *   <li>首先按avg_score降序（评分高的在前）</li>
     *   <li>评分相同时按created_at降序（新创建的在前）</li>
     * </ol>
     * 
     * <p>只返回status=1（正常状态）的商家，已删除的商家（status=0）不会出现在结果中。
     * 
     * <p>SQL映射：在 {@code ShopMapper.xml} 中定义，使用 {@code <select>} 标签和动态SQL（{@code <if>}、{@code <where>}）。
     * 
     * @param offset 查询偏移量，表示从第几条记录开始查询。计算公式：offset = page * size
     *               例如：page=0, size=10 -> offset=0（从第0条开始）
     *                    page=1, size=10 -> offset=10（从第10条开始）
     * @param limit 每页返回的记录数，即LIMIT子句的值
     *               例如：10表示每页返回10条记录
     * @param category 分类筛选条件，可选。传入null或空字符串表示不筛选
     *                 例如："火锅"、"川菜"、"日料"等
     * @param keyword 搜索关键词，可选。会在name和address字段中进行模糊匹配
     *                 传入null或空字符串表示不搜索
     *                 例如："海底捞"会在名称和地址中搜索包含"海底捞"的记录
     * @param minScore 最低评分，可选。只返回avg_score >= minScore的记录
     *                 传入null表示不筛选
     *                 例如：4.0表示只返回评分大于等于4.0的商家
     * @param maxPrice 最高人均价格，可选。只返回avg_price <= maxPrice的记录
     *                 传入null表示不筛选
     *                 例如：200.0表示只返回人均价格小于等于200元的商家
     * @return 符合条件的商家列表，如果没有任何符合条件的商家则返回空列表
     */
    List<Shop> findByConditions(@Param("offset") int offset,
                                @Param("limit") int limit,
                                @Param("category") String category,
                                @Param("keyword") String keyword,
                                @Param("minScore") Double minScore,
                                @Param("maxPrice") Double maxPrice);

    /**
     * 根据多条件统计商家总数。
     * 
     * <p>该方法使用与 {@link #findByConditions(int, int, String, String, Double, Double)} 相同的筛选条件，
     * 但只返回符合条件的商家总数，不返回具体数据。
     * 
     * <p>主要用于分页计算，计算总页数：totalPages = (total + size - 1) / size
     * 
     * <p>筛选条件与 {@link #findByConditions(int, int, String, String, Double, Double)} 完全相同：
     * <ul>
     *   <li>分类筛选：按category字段精确匹配</li>
     *   <li>关键词搜索：在name和address字段中进行模糊匹配</li>
     *   <li>评分筛选：只统计avg_score >= minScore的记录</li>
     *   <li>价格筛选：只统计avg_price <= maxPrice的记录</li>
     * </ul>
     * 
     * <p>只统计status=1（正常状态）的商家，已删除的商家（status=0）不计入总数。
     * 
     * <p>SQL映射：在 {@code ShopMapper.xml} 中定义，使用 {@code <select>} 标签和COUNT函数，
     * 以及动态SQL（{@code <if>}、{@code <where>}）。
     * 
     * @param category 分类筛选条件，可选。与 {@link #findByConditions(int, int, String, String, Double, Double)} 中的参数含义相同
     * @param keyword 搜索关键词，可选。与 {@link #findByConditions(int, int, String, String, Double, Double)} 中的参数含义相同
     * @param minScore 最低评分，可选。与 {@link #findByConditions(int, int, String, String, Double, Double)} 中的参数含义相同
     * @param maxPrice 最高人均价格，可选。与 {@link #findByConditions(int, int, String, String, Double, Double)} 中的参数含义相同
     * @return 符合条件的商家总数，如果没有符合条件的商家则返回0
     */
    long countByConditions(@Param("category") String category,
                          @Param("keyword") String keyword,
                          @Param("minScore") Double minScore,
                          @Param("maxPrice") Double maxPrice);

    /**
     * 统计指定日期范围内创建的商家总数（status=1）。
     *
     * @param startDate 开始时间（包含）
     * @param endDate 结束时间（包含）
     * @return 商家总数
     */
    long countByDateRange(@Param("startDate") java.time.LocalDateTime startDate,
                         @Param("endDate") java.time.LocalDateTime endDate);
}

