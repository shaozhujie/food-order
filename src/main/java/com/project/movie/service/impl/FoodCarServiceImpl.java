package com.project.movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.movie.domain.FoodCar;
import com.project.movie.mapper.FoodCarMapper;
import com.project.movie.service.FoodCarService;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 购物车service实现类
 * @date 2025/03/19 11:56
 */
@Service
public class FoodCarServiceImpl extends ServiceImpl<FoodCarMapper, FoodCar> implements FoodCarService {
}
