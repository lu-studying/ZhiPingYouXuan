package com.demo.dp.service;

import com.demo.dp.domain.entity.AiCallLog;

import java.util.List;

/**
 * AI 调用日志服务接口：定义 AI 调用日志相关的业务操作。
 * 
 * <p>该接口定义了 AI 调用日志查询相关的业务方法，包括：
 * <ul>
 *   <li>分页查询 AI 调用日志列表（支持多条件筛选）</li>
 *   <li>统计符合条件的 AI 调用日志总数</li>
 * </ul>
 * 
 * @author System
 * @version 1.0
 */
public interface AiCallLogService {

    /**
     * 分页查询 AI 调用日志列表（支持多条件筛选）。
     * 
     * <p>该方法会根据传入的筛选条件查询符合条件的 AI 调用日志，并返回指定页的数据。
     * 筛选条件包括：
     * <ul>
     *   <li>调用类型筛选：按 type 字段筛选（generate/recommend）</li>
     *   <li>状态筛选：按 status 字段筛选（1=成功，0=失败）</li>
     *   <li>用户筛选：按 userId 字段筛选</li>
     * </ul>
     * 
     * <p>查询结果按创建时间降序排列（最新的在前）。
     * 
     * @param page 页码，从0开始计数。例如：0表示第一页，1表示第二页
     * @param size 每页返回的记录数。建议值：10、20、50等
     * @param type 调用类型筛选条件，可选。例如："generate"、"recommend"。传入null或空字符串表示不筛选
     * @param status 状态筛选条件，可选。1=成功，0=失败。传入null表示不筛选
     * @param userId 用户ID筛选条件，可选。传入null表示不筛选
     * @return 符合条件的 AI 调用日志列表，如果没有任何符合条件的记录则返回空列表
     */
    List<AiCallLog> listLogs(int page, int size, String type, Integer status, Long userId);

    /**
     * 统计符合条件的 AI 调用日志总数，用于分页计算。
     * 
     * <p>该方法使用与 {@link #listLogs(int, int, String, Integer, Long)} 相同的筛选条件，
     * 但只返回符合条件的日志总数，不返回具体数据。
     * 
     * <p>通常与 {@link #listLogs(int, int, String, Integer, Long)} 配合使用，
     * 用于计算总页数：totalPages = (total + size - 1) / size
     * 
     * @param type 调用类型筛选条件，可选。与 {@link #listLogs(int, int, String, Integer, Long)} 中的参数含义相同
     * @param status 状态筛选条件，可选。与 {@link #listLogs(int, int, String, Integer, Long)} 中的参数含义相同
     * @param userId 用户ID筛选条件，可选。与 {@link #listLogs(int, int, String, Integer, Long)} 中的参数含义相同
     * @return 符合条件的日志总数，如果没有符合条件的记录则返回0
     */
    long countLogs(String type, Integer status, Long userId);
}

