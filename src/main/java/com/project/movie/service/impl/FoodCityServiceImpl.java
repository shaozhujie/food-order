package com.project.movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.movie.domain.FoodCity;
import com.project.movie.mapper.FoodCityMapper;
import com.project.movie.service.FoodCityService;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 城市service实现类
 * @date 2025/03/13 08:36
 */
@Service
public class FoodCityServiceImpl extends ServiceImpl<FoodCityMapper, FoodCity> implements FoodCityService {
}
