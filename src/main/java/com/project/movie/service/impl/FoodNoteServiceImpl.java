package com.project.movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.movie.domain.FoodNote;
import com.project.movie.mapper.FoodNoteMapper;
import com.project.movie.service.FoodNoteService;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 美食笔记service实现类
 * @date 2025/03/19 10:55
 */
@Service
public class FoodNoteServiceImpl extends ServiceImpl<FoodNoteMapper, FoodNote> implements FoodNoteService {
}
