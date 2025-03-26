package com.project.movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.movie.domain.FoodInfo;
import com.project.movie.mapper.FoodInfoMapper;
import com.project.movie.service.FoodInfoService;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 系统信息service实现类
 * @date 2025/03/24 12:37
 */
@Service
public class FoodInfoServiceImpl extends ServiceImpl<FoodInfoMapper, FoodInfo> implements FoodInfoService {
}
