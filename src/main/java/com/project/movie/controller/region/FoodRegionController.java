package com.project.movie.controller.region;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 区域controller
 * @date 2025/03/17 09:45
 */
@Controller
@ResponseBody
@RequestMapping("region")
public class FoodRegionController {

    @Autowired
    private FoodRegionService foodRegionService;
    @Autowired
    private FoodCityService foodCityService;
    @Autowired
    private UserService userService;

    /** 分页获取区域 */
    @PostMapping("getFoodRegionPage")
    public Result getFoodRegionPage(@RequestBody FoodRegion foodRegion) {
        Page<FoodRegion> page = new Page<>(foodRegion.getPageNumber(),foodRegion.getPageSize());
        QueryWrapper<FoodRegion> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(foodRegion.getCityId()),FoodRegion::getCityId,foodRegion.getCityId())
                .like(StringUtils.isNotBlank(foodRegion.getName()),FoodRegion::getName,foodRegion.getName());
        Page<FoodRegion> foodRegionPage = foodRegionService.page(page, queryWrapper);
        for (FoodRegion region : foodRegionPage.getRecords()) {
            FoodCity city = foodCityService.getById(region.getCityId());
            region.setCityName(city.getName());
        }
        return Result.success(foodRegionPage);
    }

    @GetMapping("getFoodRegionList")
    public Result getFoodRegionList(@RequestParam("cityId") String cityId) {
        QueryWrapper<FoodRegion> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(FoodRegion::getCityId,cityId);
        List<FoodRegion> foodRegionList = foodRegionService.list(queryWrapper);
        for (FoodRegion region : foodRegionList) {
            FoodCity city = foodCityService.getById(region.getCityId());
            region.setCityName(city.getName());
        }
        return Result.success(foodRegionList);
    }

    /** 根据id获取区域 */
    @GetMapping("getFoodRegionById")
    public Result getFoodRegionById(@RequestParam("id")String id) {
        FoodRegion foodRegion = foodRegionService.getById(id);
        return Result.success(foodRegion);
    }

    /** 保存区域 */
    @PostMapping("saveFoodRegion")
    public Result saveFoodRegion(@RequestBody FoodRegion foodRegion) {
        boolean save = foodRegionService.save(foodRegion);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑区域 */
    @PostMapping("editFoodRegion")
    public Result editFoodRegion(@RequestBody FoodRegion foodRegion) {
        boolean save = foodRegionService.updateById(foodRegion);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除区域 */
    @GetMapping("removeFoodRegion")
    public Result removeFoodRegion(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(User::getRegionId,id);
                int count = userService.count(queryWrapper);
                if (count > 0) {
                    return Result.fail("区域下存在用户，无法删除");
                }
                foodRegionService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("区域id不能为空！");
        }
    }

}
