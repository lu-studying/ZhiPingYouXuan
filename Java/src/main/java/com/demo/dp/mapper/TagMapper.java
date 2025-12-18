package com.demo.dp.mapper;

import com.demo.dp.domain.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签 Mapper 接口。
 * SQL 定义在 mapper/TagMapper.xml。
 */
@Mapper
public interface TagMapper {
    /**
     * 根据 ID 查询标签
     */
    Tag findById(@Param("id") Long id);

    /**
     * 根据类型查询标签列表
     */
    List<Tag> findByType(@Param("type") String type);

    /**
     * 查询所有标签
     */
    List<Tag> findAll();

    /**
     * 插入新标签
     */
    int insert(Tag tag);
}

