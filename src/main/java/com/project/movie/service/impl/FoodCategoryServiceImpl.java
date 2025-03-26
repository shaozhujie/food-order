package com.project.movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.movie.domain.FoodCategory;
import com.project.movie.mapper.FoodCategoryMapper;
import com.project.movie.service.FoodCategoryService;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 美食分类service实现类
 * @date 2025/03/13 09:12
 */
@Service
public class FoodCategoryServiceImpl extends ServiceImpl<FoodCategoryMapper, FoodCategory> implements FoodCategoryService {
}