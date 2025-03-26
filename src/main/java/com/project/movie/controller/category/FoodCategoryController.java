package com.project.movie.controller.category;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.movie.common.enums.BusinessType;
import com.project.movie.common.enums.ResultCode;
import com.project.movie.domain.FoodCategory;
import com.project.movie.domain.FoodItem;
import com.project.movie.domain.Result;
import com.project.movie.service.FoodCategoryService;
import com.project.movie.service.FoodItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 美食分类controller
 * @date 2025/03/13 09:12
 */
@Controller
@ResponseBody
@RequestMapping("category")
public class FoodCategoryController {

    @Autowired
    private FoodCategoryService foodCategoryService;
    @Autowired
    private FoodItemService foodItemService;

    /** 分页获取美食分类 */
    @PostMapping("getFoodCategoryPage")
    public Result getFoodCategoryPage(@RequestBody FoodCategory foodCategory) {
        Page<FoodCategory> page = new Page<>(foodCategory.getPageNumber(),foodCategory.getPageSize());
        QueryWrapper<FoodCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(foodCategory.getName()),FoodCategory::getName,foodCategory.getName());
        Page<FoodCategory> foodCategoryPage = foodCategoryService.page(page, queryWrapper);
        return Result.success(foodCategoryPage);
    }

    @GetMapping("getFoodCategoryList")
    public Result getFoodCategoryList() {
        List<FoodCategory> categoryList = foodCategoryService.list();
        return Result.success(categoryList);
    }

    /** 根据id获取美食分类 */
    @GetMapping("getFoodCategoryById")
    public Result getFoodCategoryById(@RequestParam("id")String id) {
        FoodCategory foodCategory = foodCategoryService.getById(id);
        return Result.success(foodCategory);
    }

    /** 保存美食分类 */
    @PostMapping("saveFoodCategory")
    public Result saveFoodCategory(@RequestBody FoodCategory foodCategory) {
        boolean save = foodCategoryService.save(foodCategory);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑美食分类 */
    @PostMapping("editFoodCategory")
    public Result editFoodCategory(@RequestBody FoodCategory foodCategory) {
        boolean save = foodCategoryService.updateById(foodCategory);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除美食分类 */
    @GetMapping("removeFoodCategory")
    public Result removeFoodCategory(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                QueryWrapper<FoodItem> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(FoodItem::getTypeId,id);
                int count = foodItemService.count(queryWrapper);
                if (count > 0) {
                    return Result.fail("分类下存在美食无法删除");
                }
                foodCategoryService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("美食分类id不能为空！");
        }
    }

}
