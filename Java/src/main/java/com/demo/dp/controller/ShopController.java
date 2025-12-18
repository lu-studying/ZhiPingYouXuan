package com.demo.dp.controller;

import com.demo.dp.domain.entity.Shop;
import com.demo.dp.dto.ShopCreateRequest;
import com.demo.dp.dto.ShopUpdateRequest;
import com.demo.dp.service.ShopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商家管理控制器：提供商家相关的REST API接口。
 * 
 * <p>该控制器负责处理HTTP请求，调用Service层处理业务逻辑，并返回JSON格式的响应。
 * 
 * <p>所有接口路径都以 {@code /api/shops} 为前缀。
 * 
 * <p>主要功能：
 * <ul>
 *   <li>GET /api/shops - 获取商家列表（支持分页、搜索、筛选）</li>
 *   <li>GET /api/shops/{id} - 获取商家详情</li>
 *   <li>POST /api/shops - 创建新商家</li>
 *   <li>PUT /api/shops/{id} - 更新商家信息</li>
 *   <li>DELETE /api/shops/{id} - 删除商家（软删除）</li>
 * </ul>
 * 
 * @author System
 * @version 1.0
 */
@RestController
@RequestMapping("/api/shops")
public class ShopController {

    /**
     * 日志记录器，用于记录操作日志和错误日志。
     * 使用SLF4J日志框架，便于后续切换日志实现（如Logback、Log4j2等）。
     */
    private static final Logger log = LoggerFactory.getLogger(ShopController.class);

    /**
     * 商家服务对象，用于处理商家相关的业务逻辑。
     * 通过构造函数注入，确保不可变性和线程安全。
     */
    private final ShopService shopService;

    /**
     * 构造函数，通过依赖注入获取ShopService实例。
     * 
     * @param shopService 商家服务对象，由Spring容器自动注入
     */
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    /**
     * 获取商家列表接口，支持分页、搜索、多条件筛选。
     * 
     * <p>请求示例：
     * <ul>
     *   <li>GET /api/shops - 获取第一页，每页10条</li>
     *   <li>GET /api/shops?page=0&size=10 - 获取第一页，每页10条</li>
     *   <li>GET /api/shops?category=火锅 - 筛选火锅类商家</li>
     *   <li>GET /api/shops?keyword=海底捞 - 搜索包含"海底捞"的商家</li>
     *   <li>GET /api/shops?minScore=4.0&maxPrice=200 - 筛选评分>=4.0且价格<=200的商家</li>
     * </ul>
     * 
     * <p>响应格式：
     * <pre>
     * {
     *   "content": [...],      // 商家列表
     *   "total": 100,          // 总记录数
     *   "page": 0,             // 当前页码
     *   "size": 10,            // 每页大小
     *   "totalPages": 10       // 总页数
     * }
     * </pre>
     * 
     * @param page 页码，从0开始。默认值为0（第一页）
     * @param size 每页返回的记录数。默认值为10
     * @param category 分类筛选条件，可选。例如："火锅"、"川菜"、"日料"等
     * @param keyword 搜索关键词，可选。会在商家名称和地址中搜索
     * @param minScore 最低评分，可选。只返回评分>=该值的商家
     * @param maxPrice 最高人均价格，可选。只返回价格<=该值的商家
     * @return ResponseEntity包含分页结果，HTTP状态码200表示成功
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listShops(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double maxPrice) {
        
        // 调用Service层方法获取符合条件的商家列表
        List<Shop> shops = shopService.listShops(page, size, category, keyword, minScore, maxPrice);
        
        // 调用Service层方法获取符合条件的商家总数（用于分页计算）
        long total = shopService.countShops(category, keyword, minScore, maxPrice);
        
        // 构建返回结果Map
        Map<String, Object> result = new HashMap<>();
        result.put("content", shops);  // 商家列表数据
        result.put("total", total);    // 总记录数
        result.put("page", page);      // 当前页码
        result.put("size", size);     // 每页大小
        
        // 计算总页数：总页数 = (总记录数 + 每页大小 - 1) / 每页大小
        // 例如：100条记录，每页10条 -> (100 + 10 - 1) / 10 = 10页
        result.put("totalPages", (total + size - 1) / size);
        
        // 返回HTTP 200状态码和结果数据
        return ResponseEntity.ok(result);
    }

    /**
     * 获取商家详情接口。
     * 
     * <p>根据商家ID查询商家详细信息，包括所有字段。
     * 
     * <p>请求示例：
     * <ul>
     *   <li>GET /api/shops/1 - 获取ID为1的商家详情</li>
     * </ul>
     * 
     * <p>响应格式：
     * <ul>
     *   <li>成功（200）：返回商家对象的JSON</li>
     *   <li>失败（404）：商家不存在</li>
     * </ul>
     * 
     * @param id 商家ID，从URL路径中获取。必须大于0
     * @return ResponseEntity包含商家对象，HTTP状态码200表示成功，404表示商家不存在
     */
    @GetMapping("/{id}")
    public ResponseEntity<Shop> getShop(@PathVariable Long id) {
        // 调用Service层方法根据ID查询商家
        Shop shop = shopService.getById(id);
        
        // 如果商家不存在，返回HTTP 404状态码
        if (shop == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 返回HTTP 200状态码和商家对象
        return ResponseEntity.ok(shop);
    }

    /**
     * 创建新商家接口。
     * 
     * <p>请求示例：
     * <pre>
     * POST /api/shops
     * Content-Type: application/json
     * 
     * {
     *   "name": "海底捞火锅",
     *   "category": "火锅",
     *   "address": "北京市朝阳区xxx",
     *   "lng": 116.397128,
     *   "lat": 39.916527,
     *   "avgPrice": 150.0
     * }
     * </pre>
     * 
     * <p>响应格式：
     * <ul>
     *   <li>成功（201）：返回创建的商家对象（包含生成的ID）</li>
     *   <li>失败（400）：参数校验失败</li>
     *   <li>失败（500）：服务器内部错误</li>
     * </ul>
     * 
     * @param req 创建商家请求对象，包含以下字段：
     *            <ul>
     *              <li>name：商家名称（必填）</li>
     *              <li>category：商家分类（必填）</li>
     *              <li>address：商家地址（必填）</li>
     *              <li>lng：经度（可选）</li>
     *              <li>lat：纬度（可选）</li>
     *              <li>avgPrice：人均价格（可选）</li>
     *            </ul>
     * @return ResponseEntity包含创建的商家对象，HTTP状态码201表示创建成功
     */
    @PostMapping
    public ResponseEntity<?> createShop(@RequestBody ShopCreateRequest req) {
        try {
            // 参数校验：检查必填字段是否为空
            // 商家名称不能为空
            if (req.getName() == null || req.getName().trim().isEmpty()) {
                return error(400, "商家名称不能为空");
            }
            
            // 商家分类不能为空
            if (req.getCategory() == null || req.getCategory().trim().isEmpty()) {
                return error(400, "商家分类不能为空");
            }
            
            // 商家地址不能为空
            if (req.getAddress() == null || req.getAddress().trim().isEmpty()) {
                return error(400, "商家地址不能为空");
            }

            // 创建Shop实体对象，将DTO对象转换为实体对象
            Shop shop = new Shop();
            
            // 设置商家名称，使用trim()去除首尾空格
            shop.setName(req.getName().trim());
            
            // 设置商家分类，使用trim()去除首尾空格
            shop.setCategory(req.getCategory().trim());
            
            // 设置商家地址，使用trim()去除首尾空格
            shop.setAddress(req.getAddress().trim());
            
            // 设置经度（可选字段，可能为null）
            shop.setLng(req.getLng());
            
            // 设置纬度（可选字段，可能为null）
            shop.setLat(req.getLat());
            
            // 设置人均价格（可选字段，可能为null）
            shop.setAvgPrice(req.getAvgPrice());
            
            // 调用Service层方法创建商家，返回包含主键ID的商家对象
            Shop saved = shopService.createShop(shop);
            
            // 记录成功日志，包含商家ID和名称，便于后续问题排查
            log.info("创建商家成功, shopId={}, name={}", saved.getId(), saved.getName());
            
            // 返回HTTP 201状态码（Created）和创建的商家对象
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            // 捕获所有异常，记录错误日志
            log.error("创建商家失败", e);
            
            // 返回HTTP 500状态码（Internal Server Error）和错误信息
            return error(500, "创建商家失败: " + e.getMessage());
        }
    }

    /**
     * 更新商家信息接口，支持部分字段更新。
     * 
     * <p>请求示例：
     * <pre>
     * PUT /api/shops/1
     * Content-Type: application/json
     * 
     * {
     *   "name": "海底捞火锅（新店）",
     *   "avgPrice": 180.0
     * }
     * </pre>
     * 
     * <p>注意：只更新请求体中提供的非null字段，未提供的字段保持不变。
     * 
     * <p>响应格式：
     * <ul>
     *   <li>成功（200）：返回更新后的完整商家对象</li>
     *   <li>失败（404）：商家不存在</li>
     *   <li>失败（400）：业务逻辑错误（如商家不存在）</li>
     *   <li>失败（500）：服务器内部错误</li>
     * </ul>
     * 
     * @param id 商家ID，从URL路径中获取。必须大于0
     * @param req 更新商家请求对象，包含要更新的字段（可选）：
     *            <ul>
     *              <li>name：商家名称（可选）</li>
     *              <li>category：商家分类（可选）</li>
     *              <li>address：商家地址（可选）</li>
     *              <li>lng：经度（可选）</li>
     *              <li>lat：纬度（可选）</li>
     *              <li>avgPrice：人均价格（可选）</li>
     *              <li>avgScore：平均评分（可选）</li>
     *              <li>status：状态（可选，1=正常，0=已删除）</li>
     *            </ul>
     * @return ResponseEntity包含更新后的商家对象，HTTP状态码200表示成功
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateShop(@PathVariable Long id, @RequestBody ShopUpdateRequest req) {
        try {
            // 确保使用URL路径中的ID，而不是请求体中的ID（防止ID不一致）
            req.setId(id); // 确保使用路径中的ID
            
            // 先检查商家是否存在
            Shop existing = shopService.getById(id);
            if (existing == null) {
                return error(404, "商家不存在");
            }

            // 创建Shop实体对象，用于部分更新
            Shop shop = new Shop();
            shop.setId(id);
            
            // 只更新非空字段，实现部分更新功能
            // 如果请求体中某个字段为null，则不更新该字段，保持数据库中的原值
            
            // 更新商家名称（如果提供了新名称）
            if (req.getName() != null) {
                shop.setName(req.getName().trim());
            }
            
            // 更新商家分类（如果提供了新分类）
            if (req.getCategory() != null) {
                shop.setCategory(req.getCategory().trim());
            }
            
            // 更新商家地址（如果提供了新地址）
            if (req.getAddress() != null) {
                shop.setAddress(req.getAddress().trim());
            }
            
            // 更新经度（如果提供了新经度）
            if (req.getLng() != null) {
                shop.setLng(req.getLng());
            }
            
            // 更新纬度（如果提供了新纬度）
            if (req.getLat() != null) {
                shop.setLat(req.getLat());
            }
            
            // 更新人均价格（如果提供了新价格）
            if (req.getAvgPrice() != null) {
                shop.setAvgPrice(req.getAvgPrice());
            }
            
            // 更新平均评分（如果提供了新评分）
            if (req.getAvgScore() != null) {
                shop.setAvgScore(req.getAvgScore());
            }
            
            // 更新状态（如果提供了新状态）
            if (req.getStatus() != null) {
                shop.setStatus(req.getStatus());
            }

            // 调用Service层方法更新商家，返回更新后的完整商家对象
            Shop updated = shopService.updateShop(shop);
            
            // 记录成功日志
            log.info("更新商家成功, shopId={}", id);
            
            // 返回HTTP 200状态码和更新后的商家对象
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            // 捕获业务逻辑异常（如商家不存在），返回HTTP 400状态码
            log.error("更新商家失败, shopId={}", id, e);
            return error(400, e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常，返回HTTP 500状态码
            log.error("更新商家失败, shopId={}", id, e);
            return error(500, "更新商家失败: " + e.getMessage());
        }
    }

    /**
     * 删除商家接口（软删除）。
     * 
     * <p>请求示例：
     * <ul>
     *   <li>DELETE /api/shops/1 - 删除ID为1的商家</li>
     * </ul>
     * 
     * <p>注意：这是软删除，不会物理删除数据库记录，只是将status设置为0。
     * 删除后，该商家不会在正常的查询列表中显示。
     * 
     * <p>响应格式：
     * <ul>
     *   <li>成功（200）：返回 {"message": "删除成功"}</li>
     *   <li>失败（404）：商家不存在</li>
     *   <li>失败（400）：业务逻辑错误（如商家不存在）</li>
     *   <li>失败（500）：服务器内部错误</li>
     * </ul>
     * 
     * @param id 商家ID，从URL路径中获取。必须大于0
     * @return ResponseEntity包含删除结果，HTTP状态码200表示成功
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShop(@PathVariable Long id) {
        try {
            // 先检查商家是否存在
            Shop existing = shopService.getById(id);
            if (existing == null) {
                return error(404, "商家不存在");
            }
            
            // 调用Service层方法执行软删除（将status设置为0）
            shopService.deleteShop(id);
            
            // 记录成功日志
            log.info("删除商家成功, shopId={}", id);
            
            // 返回HTTP 200状态码和成功消息
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (RuntimeException e) {
            // 捕获业务逻辑异常（如商家不存在），返回HTTP 400状态码
            log.error("删除商家失败, shopId={}", id, e);
            return error(400, e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常，返回HTTP 500状态码
            log.error("删除商家失败, shopId={}", id, e);
            return error(500, "删除商家失败: " + e.getMessage());
        }
    }

    /**
     * 构建错误响应的辅助方法。
     * 
     * <p>统一错误响应格式：
     * <pre>
     * {
     *   "code": 400,
     *   "message": "错误信息"
     * }
     * </pre>
     * 
     * @param status HTTP状态码，例如：400（Bad Request）、404（Not Found）、500（Internal Server Error）
     * @param message 错误信息，用于描述具体的错误原因
     * @return ResponseEntity包含错误信息的Map，HTTP状态码为传入的status参数
     */
    private ResponseEntity<Map<String, Object>> error(int status, String message) {
        // 创建错误响应体Map
        Map<String, Object> body = new HashMap<>();
        
        // 设置错误码
        body.put("code", status);
        
        // 设置错误信息
        body.put("message", message);
        
        // 返回指定HTTP状态码和错误信息
        return ResponseEntity.status(status).body(body);
    }
}

