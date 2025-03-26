package com.project.movie.controller.like;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.movie.common.enums.BusinessType;
import com.project.movie.common.enums.ResultCode;
import com.project.movie.config.utils.ShiroUtils;
import com.project.movie.domain.FoodItem;
import com.project.movie.domain.FoodLike;
import com.project.movie.domain.Result;
import com.project.movie.domain.User;
import com.project.movie.service.FoodItemService;
import com.project.movie.service.FoodLikeService;
import com.project.movie.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 收藏controller
 * @date 2025/03/19 09:54
 */
@Controller
@ResponseBody
@RequestMapping("like")
public class FoodLikeController {

    @Autowired
    private FoodLikeService foodLikeService;
    @Autowired
    private FoodItemService foodItemService;
    @Autowired
    private UserService userService;

    /** 分页获取收藏 */
    @PostMapping("getFoodLikePage")
    public Result getFoodLikePage(@RequestBody FoodLike foodLike) {
        User userInfo = ShiroUtils.getUserInfo();
        foodLike.setUserId(userInfo.getId());
        Page<FoodLike> page = new Page<>(foodLike.getPageNumber(),foodLike.getPageSize());
        QueryWrapper<FoodLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(foodLike.getUserId()),FoodLike::getUserId,foodLike.getUserId())
                .eq(StringUtils.isNotBlank(foodLike.getItemId()),FoodLike::getItemId,foodLike.getItemId())
                .like(StringUtils.isNotBlank(foodLike.getCreateBy()),FoodLike::getCreateBy,foodLike.getCreateBy())
                .eq(foodLike.getCreateTime() != null,FoodLike::getCreateTime,foodLike.getCreateTime())
                .like(StringUtils.isNotBlank(foodLike.getUpdateBy()),FoodLike::getUpdateBy,foodLike.getUpdateBy())
                .eq(foodLike.getUpdateTime() != null,FoodLike::getUpdateTime,foodLike.getUpdateTime())
                .orderByDesc(FoodLike::getCreateTime);
        Page<FoodLike> foodLikePage = foodLikeService.page(page, queryWrapper);
        for (FoodLike like : foodLikePage.getRecords()) {
            FoodItem foodItem = foodItemService.getById(like.getItemId());
            like.setName(foodItem.getName());
            like.setImage(foodItem.getImage());
            like.setPrice(foodItem.getPrice());
            like.setIntroduce(foodItem.getIntroduce());
            User user = userService.getById(foodItem.getUserId());
            like.setUserName(user.getUserName());
            like.setOpen(user.getOpen());
            like.setLogo(user.getLogo());
        }
        return Result.success(foodLikePage);
    }

    /** 根据id获取收藏 */
    @GetMapping("getFoodLikeById")
    public Result getFoodLikeById(@RequestParam("id")String id) {
        FoodLike foodLike = foodLikeService.getById(id);
        return Result.success(foodLike);
    }

    /** 保存收藏 */
    @PostMapping("saveFoodLike")
    public Result saveFoodLike(@RequestBody FoodLike foodLike) {
        User user = ShiroUtils.getUserInfo();
        foodLike.setUserId(user.getId());
        QueryWrapper<FoodLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FoodLike::getUserId,user.getId())
                .eq(FoodLike::getItemId,foodLike.getItemId());
        int count = foodLikeService.count(queryWrapper);
        if (count > 0) {
            return Result.fail("你已经收藏过啦,快去发现其他美食吧");
        } else {
            boolean save = foodLikeService.save(foodLike);
            if (save) {
                return Result.success();
            } else {
                return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
            }
        }
    }

    /** 编辑收藏 */
    @PostMapping("editFoodLike")
    public Result editFoodLike(@RequestBody FoodLike foodLike) {
        boolean save = foodLikeService.updateById(foodLike);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除收藏 */
    @GetMapping("removeFoodLike")
    public Result removeFoodLike(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                foodLikeService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("收藏id不能为空！");
        }
    }

}
