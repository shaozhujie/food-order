package com.project.movie.controller.comment;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.movie.common.enums.BusinessType;
import com.project.movie.common.enums.ResultCode;
import com.project.movie.config.utils.ShiroUtils;
import com.project.movie.domain.FoodItem;
import com.project.movie.domain.FoodItemComment;
import com.project.movie.domain.Result;
import com.project.movie.domain.User;
import com.project.movie.service.FoodItemCommentService;
import com.project.movie.service.FoodItemService;
import com.project.movie.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 美食评论controller
 * @date 2025/03/19 10:01
 */
@Controller
@ResponseBody
@RequestMapping("comment")
public class FoodItemCommentController {

    @Autowired
    private FoodItemCommentService foodItemCommentService;
    @Autowired
    private UserService userService;
    @Autowired
    private FoodItemService foodItemService;

    /** 分页获取美食评论 */
    @PostMapping("getFoodItemCommentPage")
    public Result getFoodItemCommentPage(@RequestBody FoodItemComment foodItemComment) {
        Page<FoodItemComment> page = new Page<>(foodItemComment.getPageNumber(),foodItemComment.getPageSize());
        QueryWrapper<FoodItemComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(foodItemComment.getStar() != null,FoodItemComment::getStar,foodItemComment.getStar())
                .like(StringUtils.isNotBlank(foodItemComment.getContent()),FoodItemComment::getContent,foodItemComment.getContent())
                .eq(StringUtils.isNotBlank(foodItemComment.getUserId()),FoodItemComment::getUserId,foodItemComment.getUserId())
                .eq(StringUtils.isNotBlank(foodItemComment.getFoodId()),FoodItemComment::getFoodId,foodItemComment.getFoodId())
                .like(StringUtils.isNotBlank(foodItemComment.getCreateBy()),FoodItemComment::getCreateBy,foodItemComment.getCreateBy())
                .eq(foodItemComment.getCreateTime() != null,FoodItemComment::getCreateTime,foodItemComment.getCreateTime())
                .like(StringUtils.isNotBlank(foodItemComment.getUpdateBy()),FoodItemComment::getUpdateBy,foodItemComment.getUpdateBy())
                .eq(foodItemComment.getUpdateTime() != null,FoodItemComment::getUpdateTime,foodItemComment.getUpdateTime())
                .orderByDesc(FoodItemComment::getCreateTime);
        Page<FoodItemComment> foodItemCommentPage = foodItemCommentService.page(page, queryWrapper);
        return Result.success(foodItemCommentPage);
    }

    @GetMapping("getFoodItemCommentByFood")
    public Result getFoodItemCommentByFood(@RequestParam("foodId")String foodId) {
        QueryWrapper<FoodItemComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FoodItemComment::getFoodId,foodId)
                .orderByDesc(FoodItemComment::getCreateTime);
        List<FoodItemComment> commentList = foodItemCommentService.list(queryWrapper);
        for (FoodItemComment foodItemComment : commentList) {
            User user = userService.getById(foodItemComment.getUserId());
            foodItemComment.setAvatar(user.getAvatar());
        }
        return Result.success(commentList);
    }

    /** 根据id获取美食评论 */
    @GetMapping("getFoodItemCommentById")
    public Result getFoodItemCommentById(@RequestParam("id")String id) {
        FoodItemComment foodItemComment = foodItemCommentService.getById(id);
        return Result.success(foodItemComment);
    }

    /** 保存美食评论 */
    @PostMapping("saveFoodItemComment")
    @Transactional(rollbackFor = Exception.class)
    public Result saveFoodItemComment(@RequestBody FoodItemComment foodItemComment) {
        FoodItem foodItem = foodItemService.getById(foodItemComment.getFoodId());
        QueryWrapper<FoodItemComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FoodItemComment::getFoodId,foodItem.getId());
        List<FoodItemComment> commentList = foodItemCommentService.list(queryWrapper);
        float rate = 0;
        for (FoodItemComment comment :commentList) {
            rate += comment.getStar();
        }
        rate += foodItemComment.getStar();
        foodItem.setRate(new BigDecimal(rate/(commentList.size() + 1)).setScale(BigDecimal.ROUND_FLOOR).floatValue());
        foodItemService.updateById(foodItem);
        User user = ShiroUtils.getUserInfo();
        foodItemComment.setUserId(user.getId());
        boolean save = foodItemCommentService.save(foodItemComment);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑美食评论 */
    @PostMapping("editFoodItemComment")
    public Result editFoodItemComment(@RequestBody FoodItemComment foodItemComment) {
        boolean save = foodItemCommentService.updateById(foodItemComment);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除美食评论 */
    @GetMapping("removeFoodItemComment")
    public Result removeFoodItemComment(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                foodItemCommentService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("美食评论id不能为空！");
        }
    }

}
