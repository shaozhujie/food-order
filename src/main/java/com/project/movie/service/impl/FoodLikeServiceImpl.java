package com.project.movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.movie.domain.FoodLike;
import com.project.movie.mapper.FoodLikeMapper;
import com.project.movie.service.FoodLikeService;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 收藏service实现类
 * @date 2025/03/19 09:54
 */
@Service
public class FoodLikeServiceImpl extends ServiceImpl<FoodLikeMapper, FoodLike> implements FoodLikeService {
}
