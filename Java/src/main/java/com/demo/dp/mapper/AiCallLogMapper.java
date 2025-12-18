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
}

