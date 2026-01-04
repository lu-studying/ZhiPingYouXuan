package com.demo.dp.mapper;

import com.demo.dp.domain.entity.AiCallLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 大模型调用日志 Mapper 接口，对应表 ai_call_log。
 *
 * <p>功能：
 * <ul>
 *     <li>插入调用日志（记录 prompt、响应引用、耗时、状态等）</li>
 *     <li>根据 ID 查询（调试或审计场景）</li>
 * </ul>
 */
@Mapper
public interface AiCallLogMapper {

    /**
     * 插入一条调用日志。
     *
     * @param log 日志实体，必须包含 type、prompt、status
     * @return 影响行数（1 表示成功）
     */
    int insert(AiCallLog log);

    /**
     * 根据主键查询调用日志。
     *
     * @param id 日志 ID
     * @return 日志实体，不存在则返回 null
     */
    AiCallLog findById(@Param("id") Long id);

    /**
     * 统计所有 AI 调用日志总数。
     *
     * @return 调用日志总数
     */
    long countAll();

    /**
     * 根据状态统计 AI 调用日志数量。
     *
     * @param status 状态：1=成功，0=失败
     * @return 符合条件的调用日志数量
     */
    long countByStatus(@Param("status") Integer status);

    /**
     * 分页查询 AI 调用日志列表（支持多条件筛选）。
     *
     * @param offset 查询偏移量
     * @param limit 每页数量
     * @param type 调用类型（可选）：generate/recommend
     * @param status 状态（可选）：1=成功，0=失败
     * @param userId 用户ID（可选）
     * @return 调用日志列表
     */
    java.util.List<AiCallLog> findByConditions(@Param("offset") int offset,
                                               @Param("limit") int limit,
                                               @Param("type") String type,
                                               @Param("status") Integer status,
                                               @Param("userId") Long userId);

    /**
     * 统计符合条件的 AI 调用日志总数（用于分页计算）。
     *
     * @param type 调用类型（可选）
     * @param status 状态（可选）
     * @param userId 用户ID（可选）
     * @return 符合条件的调用日志总数
     */
    long countByConditions(@Param("type") String type,
                          @Param("status") Integer status,
                          @Param("userId") Long userId);
}

