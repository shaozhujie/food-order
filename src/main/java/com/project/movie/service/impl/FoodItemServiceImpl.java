package com.project.movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.movie.domain.FoodItem;
import com.project.movie.mapper.FoodItemMapper;
import com.project.movie.service.FoodItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 美食service实现类
 * @date 2025/03/18 09:23
 */
@Service
public class FoodItemServiceImpl extends ServiceImpl<FoodItemMapper, FoodItem> implements FoodItemService {
    @Override
    public List<FoodItem> getRecommandItem(List<String> recommendations) {
        return baseMapper.getRecommandItem(recommendations);
    }
}
