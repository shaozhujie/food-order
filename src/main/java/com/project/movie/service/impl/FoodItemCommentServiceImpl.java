package com.project.movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.movie.domain.FoodItemComment;
import com.project.movie.mapper.FoodItemCommentMapper;
import com.project.movie.service.FoodItemCommentService;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 美食评论service实现类
 * @date 2025/03/19 10:01
 */
@Service
public class FoodItemCommentServiceImpl extends ServiceImpl<FoodItemCommentMapper, FoodItemComment> implements FoodItemCommentService {
}