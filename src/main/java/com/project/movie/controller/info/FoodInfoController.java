package com.project.movie.controller.info;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.movie.common.enums.BusinessType;
import com.project.movie.common.enums.ResultCode;
import com.project.movie.domain.FoodInfo;
import com.project.movie.domain.Result;
import com.project.movie.service.FoodInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 系统信息controller
 * @date 2025/03/24 12:37
 */
@Controller
@ResponseBody
@RequestMapping("info")
public class FoodInfoController {

    @Autowired
    private FoodInfoService foodInfoService;

    /** 分页获取系统信息 */
    @PostMapping("getFoodInfoPage")
    public Result getFoodInfoPage(@RequestBody FoodInfo foodInfo) {
        Page<FoodInfo> page = new Page<>(foodInfo.getPageNumber(),foodInfo.getPageSize());
        QueryWrapper<FoodInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(foodInfo.getName()),FoodInfo::getName,foodInfo.getName())
                .eq(StringUtils.isNotBlank(foodInfo.getIntroduce()),FoodInfo::getIntroduce,foodInfo.getIntroduce())
                .eq(StringUtils.isNotBlank(foodInfo.getTitle()),FoodInfo::getTitle,foodInfo.getTitle())
                .eq(StringUtils.isNotBlank(foodInfo.getImage()),FoodInfo::getImage,foodInfo.getImage());
        Page<FoodInfo> foodInfoPage = foodInfoService.page(page, queryWrapper);
        return Result.success(foodInfoPage);
    }

    @GetMapping("getInfo")
    public Result getInfo() {
        QueryWrapper<FoodInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().last("limit 1");
        FoodInfo info = foodInfoService.getOne(queryWrapper);
        return Result.success(info);
    }

    /** 根据id获取系统信息 */
    @GetMapping("getFoodInfoById")
    public Result getFoodInfoById(@RequestParam("id")String id) {
        FoodInfo foodInfo = foodInfoService.getById(id);
        return Result.success(foodInfo);
    }

    /** 保存系统信息 */
    @PostMapping("saveFoodInfo")
    public Result saveFoodInfo(@RequestBody FoodInfo foodInfo) {
        boolean save = foodInfoService.save(foodInfo);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑系统信息 */
    @PostMapping("editFoodInfo")
    public Result editFoodInfo(@RequestBody FoodInfo foodInfo) {
        boolean save = foodInfoService.updateById(foodInfo);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除系统信息 */
    @GetMapping("removeFoodInfo")
    public Result removeFoodInfo(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                foodInfoService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("系统信息id不能为空！");
        }
    }

}
