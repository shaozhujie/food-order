package com.project.movie.controller.leave;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.movie.common.enums.BusinessType;
import com.project.movie.common.enums.ResultCode;
import com.project.movie.domain.FoodLeave;
import com.project.movie.domain.Result;
import com.project.movie.service.FoodLeaveService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 留言controller
 * @date 2025/03/13 10:25
 */
@Controller
@ResponseBody
@RequestMapping("leave")
public class FoodLeaveController {

    @Autowired
    private FoodLeaveService foodLeaveService;

    /** 分页获取留言 */
    @PostMapping("getFoodLeavePage")
    public Result getFoodLeavePage(@RequestBody FoodLeave foodLeave) {
        Page<FoodLeave> page = new Page<>(foodLeave.getPageNumber(),foodLeave.getPageSize());
        QueryWrapper<FoodLeave> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(foodLeave.getName()),FoodLeave::getName,foodLeave.getName())
                .like(StringUtils.isNotBlank(foodLeave.getTel()),FoodLeave::getTel,foodLeave.getTel())
                .like(StringUtils.isNotBlank(foodLeave.getContent()),FoodLeave::getContent,foodLeave.getContent())
                .like(StringUtils.isNotBlank(foodLeave.getCreateBy()),FoodLeave::getCreateBy,foodLeave.getCreateBy())
                .eq(foodLeave.getCreateTime() != null,FoodLeave::getCreateTime,foodLeave.getCreateTime())
                .like(StringUtils.isNotBlank(foodLeave.getUpdateBy()),FoodLeave::getUpdateBy,foodLeave.getUpdateBy())
                .eq(foodLeave.getUpdateTime() != null,FoodLeave::getUpdateTime,foodLeave.getUpdateTime())
                .orderByDesc(FoodLeave::getCreateTime);
        Page<FoodLeave> foodLeavePage = foodLeaveService.page(page, queryWrapper);
        return Result.success(foodLeavePage);
    }

    /** 根据id获取留言 */
    @GetMapping("getFoodLeaveById")
    public Result getFoodLeaveById(@RequestParam("id")String id) {
        FoodLeave foodLeave = foodLeaveService.getById(id);
        return Result.success(foodLeave);
    }

    /** 保存留言 */
    @PostMapping("saveFoodLeave")
    public Result saveFoodLeave(@RequestBody FoodLeave foodLeave) {
        boolean save = foodLeaveService.save(foodLeave);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑留言 */
    @PostMapping("editFoodLeave")
    public Result editFoodLeave(@RequestBody FoodLeave foodLeave) {
        boolean save = foodLeaveService.updateById(foodLeave);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除留言 */
    @GetMapping("removeFoodLeave")
    public Result removeFoodLeave(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                foodLeaveService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("留言id不能为空！");
        }
    }

}