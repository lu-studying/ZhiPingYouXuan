package com.demo.dp.service.impl;

import com.demo.dp.domain.entity.AiCallLog;
import com.demo.dp.mapper.AiCallLogMapper;
import com.demo.dp.service.AiCallLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI 调用日志服务实现类：实现 AI 调用日志相关的业务逻辑。
 * 
 * <p>该类负责查询 AI 调用日志列表和统计数据，包括：
 * <ul>
 *   <li>分页查询 AI 调用日志列表（支持多条件筛选）</li>
 *   <li>统计符合条件的 AI 调用日志总数</li>
 * </ul>
 * 
 * @author System
 * @version 1.0
 */
@Service
public class AiCallLogServiceImpl implements AiCallLogService {

    /**
     * 日志记录器，用于输出调用链路和错误信息。
     */
    private static final Logger log = LoggerFactory.getLogger(AiCallLogServiceImpl.class);

    private final AiCallLogMapper aiCallLogMapper;

    /**
     * 构造函数：注入 AI 调用日志数据访问层的 Mapper。
     *
     * @param aiCallLogMapper AI 调用日志数据访问层
     */
    public AiCallLogServiceImpl(AiCallLogMapper aiCallLogMapper) {
        this.aiCallLogMapper = aiCallLogMapper;
    }

    /**
     * 分页查询 AI 调用日志列表（支持多条件筛选）。
     * 
     * <p>该方法会根据传入的筛选条件查询符合条件的 AI 调用日志，并返回指定页的数据。
     * 查询结果按创建时间降序排列（最新的在前）。
     * 
     * @param page 页码，从0开始计数
     * @param size 每页返回的记录数
     * @param type 调用类型筛选条件，可选
     * @param status 状态筛选条件，可选
     * @param userId 用户ID筛选条件，可选
     * @return 符合条件的 AI 调用日志列表
     */
    @Override
    public List<AiCallLog> listLogs(int page, int size, String type, Integer status, Long userId) {
        int offset = page * size;
        return aiCallLogMapper.findByConditions(offset, size, type, status, userId);
    }

    /**
     * 统计符合条件的 AI 调用日志总数，用于分页计算。
     * 
     * <p>该方法使用与 {@link #listLogs(int, int, String, Integer, Long)} 相同的筛选条件，
     * 但只返回符合条件的日志总数，不返回具体数据。
     * 
     * @param type 调用类型筛选条件，可选
     * @param status 状态筛选条件，可选
     * @param userId 用户ID筛选条件，可选
     * @return 符合条件的日志总数
     */
    @Override
    public long countLogs(String type, Integer status, Long userId) {
        return aiCallLogMapper.countByConditions(type, status, userId);
    }
}

