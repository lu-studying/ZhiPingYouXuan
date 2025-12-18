package com.demo.dp.service.impl;

import com.demo.dp.domain.entity.Shop;
import com.demo.dp.mapper.ShopMapper;
import com.demo.dp.service.ShopService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商家服务实现类：实现商家相关的业务逻辑。
 * 
 * <p>该类实现了 {@link ShopService} 接口中定义的所有业务方法，
 * 负责处理商家数据的增删改查操作。
 * 
 * <p>使用 {@code @Service} 注解标识为Spring服务组件，会被Spring容器自动管理。
 * 使用依赖注入的方式获取 {@link ShopMapper} 实例，用于数据库操作。
 * 
 * @author System
 * @version 1.0
 */
@Service
public class ShopServiceImpl implements ShopService {

    /**
     * 商家数据访问对象，用于执行数据库操作。
     * 通过构造函数注入，确保不可变性和线程安全。
     */
    private final ShopMapper shopMapper;

    /**
     * 构造函数，通过依赖注入获取ShopMapper实例。
     * 
     * @param shopMapper 商家数据访问对象，由Spring容器自动注入
     */
    public ShopServiceImpl(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }

    /**
     * 分页查询商家列表的实现方法。
     * 
     * <p>计算分页偏移量：offset = page * size
     * 例如：page=0, size=10 -> offset=0（第一页，从第0条开始）
     *      page=1, size=10 -> offset=10（第二页，从第10条开始）
     * 
     * @param page 页码，从0开始
     * @param size 每页大小
     * @param category 分类筛选条件，可选
     * @param keyword 搜索关键词，可选
     * @param minScore 最低评分，可选
     * @param maxPrice 最高人均价格，可选
     * @return 符合条件的商家列表
     */
    @Override
    public List<Shop> listShops(int page, int size, String category, String keyword, Double minScore, Double maxPrice) {
        // 计算数据库查询的偏移量：offset = 页码 * 每页大小
        // 例如：第1页（page=0）每页10条（size=10），offset=0，表示从第0条开始取10条
        int offset = page * size;
        
        // 调用Mapper方法执行数据库查询，返回符合条件的商家列表
        return shopMapper.findByConditions(offset, size, category, keyword, minScore, maxPrice);
    }

    /**
     * 统计符合条件的商家总数的实现方法。
     * 
     * <p>使用与listShops相同的筛选条件，但只返回总数，不返回具体数据。
     * 主要用于分页计算总页数。
     * 
     * @param category 分类筛选条件，可选
     * @param keyword 搜索关键词，可选
     * @param minScore 最低评分，可选
     * @param maxPrice 最高人均价格，可选
     * @return 符合条件的商家总数
     */
    @Override
    public long countShops(String category, String keyword, Double minScore, Double maxPrice) {
        // 调用Mapper方法执行COUNT查询，返回符合条件的商家总数
        return shopMapper.countByConditions(category, keyword, minScore, maxPrice);
    }

    /**
     * 根据ID查询商家详情的实现方法。
     * 
     * <p>直接调用Mapper的findById方法，如果商家不存在则返回null。
     * 
     * @param id 商家ID
     * @return 商家对象，不存在则返回null
     */
    @Override
    public Shop getById(Long id) {
        // 调用Mapper方法根据主键ID查询商家信息
        return shopMapper.findById(id);
    }

    /**
     * 创建新商家的实现方法。
     * 
     * <p>使用 {@code @Transactional} 注解保证事务一致性：
     * 如果插入过程中发生异常，会自动回滚，保证数据一致性。
     * 
     * <p>创建流程：
     * 1. 设置默认值（status、avgScore）
     * 2. 设置创建时间和更新时间
     * 3. 执行数据库插入操作
     * 4. 返回包含主键ID的商家对象
     * 
     * @param shop 要创建的商家对象
     * @return 创建成功的商家对象（包含数据库生成的主键ID）
     */
    @Override
    @Transactional
    public Shop createShop(Shop shop) {
        // 如果状态未设置，设置为1（正常状态）
        // status字段含义：1=正常，0=已删除/下线
        if (shop.getStatus() == null) {
            shop.setStatus(1); // 默认正常状态
        }
        
        // 如果平均评分未设置，设置为0.0（新商家初始评分为0）
        // 后续会根据用户点评自动计算平均评分
        if (shop.getAvgScore() == null) {
            shop.setAvgScore(0.0); // 默认评分
        }
        
        // 设置创建时间为当前时间
        shop.setCreatedAt(LocalDateTime.now());
        
        // 设置更新时间为当前时间（创建时更新时间等于创建时间）
        shop.setUpdatedAt(LocalDateTime.now());
        
        // 执行数据库插入操作，MyBatis会自动将生成的主键ID设置到shop对象的id属性中
        shopMapper.insert(shop);
        
        // 返回包含主键ID的商家对象
        return shop;
    }

    /**
     * 更新商家信息的实现方法。
     * 
     * <p>使用 {@code @Transactional} 注解保证事务一致性。
     * 
     * <p>更新策略：部分更新
     * - 先从数据库查询现有商家信息
     * - 只更新传入的非null字段，其他字段保持原值
     * - 这样可以实现部分字段更新，而不需要传入所有字段
     * 
     * <p>更新流程：
     * 1. 检查商家是否存在
     * 2. 从数据库查询现有商家信息
     * 3. 合并数据：只更新非null字段
     * 4. 更新updatedAt字段
     * 5. 执行数据库更新操作
     * 6. 重新查询并返回更新后的完整商家信息
     * 
     * @param shop 包含要更新字段的商家对象，必须包含id字段
     * @return 更新后的完整商家对象
     * @throws RuntimeException 如果商家不存在或更新失败
     */
    @Override
    @Transactional
    public Shop updateShop(Shop shop) {
        // 首先检查商家是否存在，如果不存在则抛出异常
        Shop existing = shopMapper.findById(shop.getId());
        if (existing == null) {
            throw new RuntimeException("商家不存在，ID: " + shop.getId());
        }
        
        // 合并数据：只更新非空字段，实现部分更新功能
        // 如果传入的字段为null，则不更新该字段，保持数据库中的原值
        
        // 更新商家名称（如果提供了新名称）
        if (shop.getName() != null) {
            existing.setName(shop.getName());
        }
        
        // 更新商家分类（如果提供了新分类）
        if (shop.getCategory() != null) {
            existing.setCategory(shop.getCategory());
        }
        
        // 更新商家地址（如果提供了新地址）
        if (shop.getAddress() != null) {
            existing.setAddress(shop.getAddress());
        }
        
        // 更新经度（如果提供了新经度）
        if (shop.getLng() != null) {
            existing.setLng(shop.getLng());
        }
        
        // 更新纬度（如果提供了新纬度）
        if (shop.getLat() != null) {
            existing.setLat(shop.getLat());
        }
        
        // 更新人均价格（如果提供了新价格）
        if (shop.getAvgPrice() != null) {
            existing.setAvgPrice(shop.getAvgPrice());
        }
        
        // 更新平均评分（如果提供了新评分）
        // 注意：通常平均评分应该根据用户点评自动计算，这里允许手动更新可能是为了管理后台的特殊需求
        if (shop.getAvgScore() != null) {
            existing.setAvgScore(shop.getAvgScore());
        }
        
        // 更新状态（如果提供了新状态）
        // 状态字段：1=正常，0=已删除/下线
        if (shop.getStatus() != null) {
            existing.setStatus(shop.getStatus());
        }
        
        // 更新修改时间为当前时间
        existing.setUpdatedAt(LocalDateTime.now());
        
        // 执行数据库更新操作
        shopMapper.update(existing);
        
        // 重新查询并返回更新后的完整商家信息，确保返回的数据是最新的
        return shopMapper.findById(shop.getId());
    }

    /**
     * 删除商家（软删除）的实现方法。
     * 
     * <p>使用 {@code @Transactional} 注解保证事务一致性。
     * 
     * <p>软删除策略：
     * - 不物理删除数据库记录
     * - 将status字段设置为0，表示已删除/下线
     * - 保留历史数据，便于数据恢复和统计分析
     * 
     * <p>删除流程：
     * 1. 检查商家是否存在
     * 2. 将status设置为0（软删除标记）
     * 3. 更新updatedAt字段
     * 4. 执行数据库更新操作
     * 
     * <p>删除后，该商家不会在正常的查询列表中显示，
     * 因为查询时会过滤status=1的记录。
     * 
     * @param id 要删除的商家ID
     * @throws RuntimeException 如果商家不存在或删除失败
     */
    @Override
    @Transactional
    public void deleteShop(Long id) {
        // 首先查询商家是否存在
        Shop shop = shopMapper.findById(id);
        if (shop == null) {
            throw new RuntimeException("商家不存在，ID: " + id);
        }
        
        // 软删除：将status设置为0，表示已删除/下线
        // 不物理删除数据，保留历史记录
        shop.setStatus(0); // 软删除
        
        // 更新修改时间为当前时间
        shop.setUpdatedAt(LocalDateTime.now());
        
        // 执行数据库更新操作，将status更新为0
        shopMapper.update(shop);
    }
}

