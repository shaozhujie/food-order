package com.project.movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.movie.domain.FoodMessage;
import com.project.movie.mapper.FoodMessageMapper;
import com.project.movie.service.FoodMessageService;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 资讯service实现类
 * @date 2025/03/19 11:28
 */
@Service
public class FoodMessageServiceImpl extends ServiceImpl<FoodMessageMapper, FoodMessage> implements FoodMessageService {
}
