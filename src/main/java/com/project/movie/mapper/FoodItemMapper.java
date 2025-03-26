package com.project.movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.movie.domain.FoodItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 美食mapper
 * @date 2025/03/18 09:23
 */
public interface FoodItemMapper extends BaseMapper<FoodItem> {
    List<FoodItem> getRecommandItem(@Param("ew") List<String> recommendations);
}
