package com.project.movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.movie.domain.FoodItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 美食service
 * @date 2025/03/18 09:23
 */
public interface FoodItemService extends IService<FoodItem> {
    List<FoodItem> getRecommandItem(List<String> recommendations);
}
