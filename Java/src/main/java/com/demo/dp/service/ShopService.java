package com.demo.dp.service;

import com.demo.dp.domain.entity.Shop;

import java.util.List;

/**
 * 商家服务接口：定义商家相关的业务操作。
 * 
 * <p>该接口定义了商家管理相关的所有业务方法，包括：
 * <ul>
 *   <li>查询商家列表（支持分页、搜索、分类筛选、评分筛选、价格筛选）</li>
 *   <li>查询商家详情</li>
 *   <li>创建新商家</li>
 *   <li>更新商家信息（支持部分更新）</li>
 *   <li>删除商家（软删除，设置status=0，不物理删除数据）</li>
 * </ul>
 * 
 * <p>所有涉及数据修改的操作都应该在实现类中使用 {@code @Transactional} 注解保证事务一致性。
 * 
 * @author System
 * @version 1.0
 */
public interface ShopService {

    /**
     * 分页查询商家列表，支持多条件筛选。
     * 
     * <p>该方法会根据传入的筛选条件查询符合条件的商家，并返回指定页的数据。
     * 筛选条件包括：
     * <ul>
     *   <li>分类筛选：按商家分类（如"火锅"、"川菜"等）筛选</li>
     *   <li>关键词搜索：在商家名称和地址中搜索包含关键词的商家</li>
     *   <li>评分筛选：只返回平均评分大于等于指定值的商家</li>
     *   <li>价格筛选：只返回人均价格小于等于指定值的商家</li>
     * </ul>
     * 
     * <p>查询结果按平均评分降序、创建时间降序排列。
     * 
     * @param page 页码，从0开始计数。例如：0表示第一页，1表示第二页
     * @param size 每页返回的记录数。建议值：10、20、50等
     * @param category 分类筛选条件，可选。例如："火锅"、"川菜"、"日料"等。传入null或空字符串表示不筛选
     * @param keyword 搜索关键词，可选。会在商家名称和地址字段中进行模糊匹配。传入null或空字符串表示不搜索
     * @param minScore 最低评分，可选。只返回平均评分大于等于该值的商家。传入null表示不筛选
     * @param maxPrice 最高人均价格，可选。只返回人均价格小于等于该值的商家。传入null表示不筛选
     * @return 符合条件的商家列表，如果没有任何符合条件的商家则返回空列表
     */
    List<Shop> listShops(int page, int size, String category, String keyword, Double minScore, Double maxPrice);

    /**
     * 获取符合条件的商家总数，用于分页计算。
     * 
     * <p>该方法使用与 {@link #listShops(int, int, String, String, Double, Double)} 相同的筛选条件，
     * 但只返回符合条件的商家总数，不返回具体数据。
     * 
     * <p>通常与 {@link #listShops(int, int, String, String, Double, Double)} 配合使用，
     * 用于计算总页数：totalPages = (total + size - 1) / size
     * 
     * @param category 分类筛选条件，可选。与 {@link #listShops(int, int, String, String, Double, Double)} 中的参数含义相同
     * @param keyword 搜索关键词，可选。与 {@link #listShops(int, int, String, String, Double, Double)} 中的参数含义相同
     * @param minScore 最低评分，可选。与 {@link #listShops(int, int, String, String, Double, Double)} 中的参数含义相同
     * @param maxPrice 最高人均价格，可选。与 {@link #listShops(int, int, String, String, Double, Double)} 中的参数含义相同
     * @return 符合条件的商家总数，如果没有符合条件的商家则返回0
     */
    long countShops(String category, String keyword, Double minScore, Double maxPrice);

    /**
     * 根据商家ID查询商家详情。
     * 
     * <p>该方法会根据主键ID查询商家信息，包括所有字段。
     * 如果商家不存在或已被删除（status=0），则返回null。
     * 
     * @param id 商家ID，必须大于0
     * @return 商家对象，包含所有字段信息。如果商家不存在则返回null
     */
    Shop getById(Long id);

    /**
     * 创建新商家。
     * 
     * <p>该方法会创建一个新的商家记录，并自动设置以下默认值：
     * <ul>
     *   <li>status：如果未设置，默认为1（正常状态）</li>
     *   <li>avgScore：如果未设置，默认为0.0（新商家初始评分为0）</li>
     *   <li>createdAt：自动设置为当前时间</li>
     *   <li>updatedAt：自动设置为当前时间</li>
     * </ul>
     * 
     * <p>创建成功后，商家对象会自动填充数据库生成的主键ID。
     * 
     * @param shop 商家对象，必须包含以下必填字段：
     *             <ul>
     *               <li>name：商家名称，不能为空</li>
     *               <li>category：商家分类，不能为空</li>
     *               <li>address：商家地址，不能为空</li>
     *             </ul>
     *             可选字段：
     *             <ul>
     *               <li>lng：经度</li>
     *               <li>lat：纬度</li>
     *               <li>avgPrice：人均价格</li>
     *             </ul>
     * @return 创建成功的商家对象，包含数据库自动生成的主键ID和所有字段信息
     * @throws RuntimeException 如果必填字段为空或创建失败
     */
    Shop createShop(Shop shop);

    /**
     * 更新商家信息，支持部分字段更新。
     * 
     * <p>该方法会根据传入的商家对象更新对应ID的商家信息。
     * 只会更新传入的非null字段，未传入的字段保持不变。
     * 
     * <p>更新逻辑：
     * <ul>
     *   <li>首先检查商家是否存在，如果不存在则抛出异常</li>
     *   <li>从数据库查询现有商家信息</li>
     *   <li>只更新传入的非null字段，其他字段保持原值</li>
     *   <li>自动更新updatedAt字段为当前时间</li>
     *   <li>返回更新后的完整商家信息</li>
     * </ul>
     * 
     * @param shop 商家对象，必须包含id字段。其他字段可选，只更新非null的字段
     * @return 更新后的完整商家对象
     * @throws RuntimeException 如果商家不存在（ID不存在）或更新失败
     */
    Shop updateShop(Shop shop);

    /**
     * 删除商家（软删除）。
     * 
     * <p>该方法不会物理删除数据库记录，而是将商家的status字段设置为0，
     * 表示该商家已被删除或下线。这样设计的好处是：
     * <ul>
     *   <li>保留历史数据，便于数据恢复</li>
     *   <li>保留关联数据（如历史订单、历史点评）的完整性</li>
     *   <li>支持数据统计分析</li>
     * </ul>
     * 
     * <p>删除后，该商家不会在正常的查询列表中显示（因为查询时会过滤status=1的记录）。
     * 
     * @param id 商家ID，必须大于0
     * @throws RuntimeException 如果商家不存在（ID不存在）或删除失败
     */
    void deleteShop(Long id);
}

