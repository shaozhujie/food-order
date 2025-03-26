package com.project.movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.movie.domain.FoodRegion;
import com.project.movie.mapper.FoodRegionMapper;
import com.project.movie.service.FoodRegionService;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 区域service实现类
 * @date 2025/03/17 09:45
 */
@Service
public class FoodRegionServiceImpl extends ServiceImpl<FoodRegionMapper, FoodRegion> implements FoodRegionService {
}
