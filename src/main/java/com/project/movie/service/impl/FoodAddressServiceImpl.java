package com.project.movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.movie.domain.FoodAddress;
import com.project.movie.mapper.FoodAddressMapper;
import com.project.movie.service.FoodAddressService;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 地址service实现类
 * @date 2025/03/13 10:19
 */
@Service
public class FoodAddressServiceImpl extends ServiceImpl<FoodAddressMapper, FoodAddress> implements FoodAddressService {
}
