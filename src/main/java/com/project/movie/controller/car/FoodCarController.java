package com.project.movie.controller.car;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.movie.common.enums.ResultCode;
import com.project.movie.config.utils.ShiroUtils;
import com.project.movie.domain.FoodCar;
import com.project.movie.domain.FoodItem;
import com.project.movie.domain.Result;
import com.project.movie.domain.User;
import com.project.movie.service.FoodCarService;
import com.project.movie.service.FoodItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 购物车controller
 * @date 2025/03/19 11:56
 */
@Controller
@ResponseBody
@RequestMapping("car")
public class FoodCarController {

    @Autowired
    private FoodCarService foodCarService;
    @Autowired
    private FoodItemService foodItemService;

    /** 分页获取购物车 */
    @PostMapping("getFoodCarPage")
    public Result getFoodCarPage(@RequestBody FoodCar foodCar) {
        Page<FoodCar> page = new Page<>(foodCar.getPageNumber(),foodCar.getPageSize());
        QueryWrapper<FoodCar> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(foodCar.getUserId()),FoodCar::getUserId,foodCar.getUserId())
                .eq(StringUtils.isNotBlank(foodCar.getFoodId()),FoodCar::getFoodId,foodCar.getFoodId())
                .like(StringUtils.isNotBlank(foodCar.getCreateBy()),FoodCar::getCreateBy,foodCar.getCreateBy())
                .eq(foodCar.getCreateTime() != null,FoodCar::getCreateTime,foodCar.getCreateTime())
                .like(StringUtils.isNotBlank(foodCar.getUpdateBy()),FoodCar::getUpdateBy,foodCar.getUpdateBy())
                .eq(foodCar.getUpdateTime() != null,FoodCar::getUpdateTime,foodCar.getUpdateTime());
        Page<FoodCar> foodCarPage = foodCarService.page(page, queryWrapper);
        return Result.success(foodCarPage);
    }

    @GetMapping("getFoodCarList")
    public Result getFoodCarList() {
        User user = ShiroUtils.getUserInfo();
        QueryWrapper<FoodCar> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FoodCar::getUserId,user.getId()).orderByDesc(FoodCar::getCreateTime);
        List<FoodCar> carList = foodCarService.list(queryWrapper);
        List<FoodItem> foodItems = new ArrayList<>();
        for (FoodCar foodCar : carList) {
            FoodItem foodItem = foodItemService.getById(foodCar.getFoodId());
            foodItems.add(foodItem);
        }
        return Result.success(foodItems);
    }

    /** 根据id获取购物车 */
    @GetMapping("getFoodCarById")
    public Result getFoodCarById(@RequestParam("id")String id) {
        FoodCar foodCar = foodCarService.getById(id);
        return Result.success(foodCar);
    }

    /** 保存购物车 */
    @PostMapping("saveFoodCar")
    public Result saveFoodCar(@RequestBody FoodCar foodCar) {
        User userInfo = ShiroUtils.getUserInfo();
        foodCar.setUserId(userInfo.getId());
        QueryWrapper<FoodCar> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FoodCar::getFoodId,foodCar.getFoodId())
                .eq(FoodCar::getUserId,userInfo.getId());
        int count = foodCarService.count(queryWrapper);
        if (count > 0) {
            return Result.success();
        }
        boolean save = foodCarService.save(foodCar);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑购物车 */
    @PostMapping("editFoodCar")
    public Result editFoodCar(@RequestBody FoodCar foodCar) {
        boolean save = foodCarService.updateById(foodCar);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除购物车 */
    @GetMapping("removeFoodCar")
    public Result removeFoodCar(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                foodCarService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("购物车id不能为空！");
        }
    }

}
