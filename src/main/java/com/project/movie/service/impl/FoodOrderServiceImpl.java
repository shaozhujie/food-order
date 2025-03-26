package com.project.movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.movie.domain.FoodOrder;
import com.project.movie.mapper.FoodOrderMapper;
import com.project.movie.service.FoodOrderService;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 订单service实现类
 * @date 2025/03/20 12:08
 */
@Service
public class FoodOrderServiceImpl extends ServiceImpl<FoodOrderMapper, FoodOrder> implements FoodOrderService {
}
