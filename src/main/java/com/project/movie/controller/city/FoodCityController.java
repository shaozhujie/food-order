package com.project.movie.controller.city;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.movie.common.enums.BusinessType;
import com.project.movie.common.enums.ResultCode;
import com.project.movie.domain.FoodCity;
import com.project.movie.domain.FoodRegion;
import com.project.movie.domain.Result;
import com.project.movie.domain.User;
import com.project.movie.service.FoodCityService;
import com.project.movie.service.FoodRegionService;
import com.project.movie.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 城市controller
 * @date 2025/03/13 08:36
 */
@Controller
@ResponseBody
@RequestMapping("city")
public class FoodCityController {

    @Autowired
    private FoodCityService foodCityService;
    @Autowired
    private UserService userService;
    @Autowired
    private FoodRegionService foodRegionService;

    /** 分页获取城市 */
    @PostMapping("getFoodCityPage")
    public Result getFoodCityPage(@RequestBody FoodCity foodCity) {
        Page<FoodCity> page = new Page<>(foodCity.getPageNumber(),foodCity.getPageSize());
        QueryWrapper<FoodCity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(foodCity.getName()),FoodCity::getName,foodCity.getName());
        Page<FoodCity> foodCityPage = foodCityService.page(page, queryWrapper);
        return Result.success(foodCityPage);
    }

    @GetMapping("getFoodCityList")
    public Result getFoodCityList() {
        List<FoodCity> list = foodCityService.list();
        return Result.success(list);
    }

    /** 根据id获取城市 */
    @GetMapping("getFoodCityById")
    public Result getFoodCityById(@RequestParam("id")String id) {
        FoodCity foodCity = foodCityService.getById(id);
        return Result.success(foodCity);
    }

    /** 保存城市 */
    @PostMapping("saveFoodCity")
    public Result saveFoodCity(@RequestBody FoodCity foodCity) {
        boolean save = foodCityService.save(foodCity);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑城市 */
    @PostMapping("editFoodCity")
    public Result editFoodCity(@RequestBody FoodCity foodCity) {
        boolean save = foodCityService.updateById(foodCity);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除城市 */
    @GetMapping("removeFoodCity")
    public Result removeFoodCity(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                QueryWrapper<User> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(User::getCity,id);
                int count = userService.count(wrapper);
                if (count > 0) {
                    return Result.fail("城市下存在用户无法删除");
                }
                QueryWrapper<FoodRegion> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(FoodRegion::getCityId,id);
                int counted = foodRegionService.count(queryWrapper);
                if (counted > 0) {
                    return Result.fail("城市下存在区域无法删除");
                }
                foodCityService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("城市id不能为空！");
        }
    }

}
