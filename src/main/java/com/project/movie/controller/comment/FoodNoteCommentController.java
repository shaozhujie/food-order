package com.project.movie.controller.comment;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.movie.common.enums.BusinessType;
import com.project.movie.common.enums.ResultCode;
import com.project.movie.config.utils.ShiroUtils;
import com.project.movie.domain.FoodItemComment;
import com.project.movie.domain.FoodNoteComment;
import com.project.movie.domain.Result;
import com.project.movie.domain.User;
import com.project.movie.service.FoodNoteCommentService;
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
 * @description: 笔记评论controller
 * @date 2025/03/19 11:14
 */
@Controller
@ResponseBody
@RequestMapping("comment")
public class FoodNoteCommentController {

    @Autowired
    private FoodNoteCommentService foodNoteCommentService;
    @Autowired
    private UserService userService;

    /** 分页获取笔记评论 */
    @PostMapping("getFoodNoteCommentPage")
    public Result getFoodNoteCommentPage(@RequestBody FoodNoteComment foodNoteComment) {
        Page<FoodNoteComment> page = new Page<>(foodNoteComment.getPageNumber(),foodNoteComment.getPageSize());
        QueryWrapper<FoodNoteComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(foodNoteComment.getContent()),FoodNoteComment::getContent,foodNoteComment.getContent())
                .eq(StringUtils.isNotBlank(foodNoteComment.getUserId()),FoodNoteComment::getUserId,foodNoteComment.getUserId())
                .eq(StringUtils.isNotBlank(foodNoteComment.getNoteId()),FoodNoteComment::getNoteId,foodNoteComment.getNoteId())
                .like(StringUtils.isNotBlank(foodNoteComment.getCreateBy()),FoodNoteComment::getCreateBy,foodNoteComment.getCreateBy());
        Page<FoodNoteComment> foodNoteCommentPage = foodNoteCommentService.page(page, queryWrapper);
        return Result.success(foodNoteCommentPage);
    }

    @GetMapping("getFoodNoteCommentList")
    public Result getFoodNoteCommentList(@RequestParam("noteId") String noteId) {
        QueryWrapper<FoodNoteComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FoodNoteComment::getNoteId,noteId)
                .orderByDesc(FoodNoteComment::getCreateTime);
        List<FoodNoteComment> commentList = foodNoteCommentService.list(queryWrapper);
        for (FoodNoteComment foodNoteComment : commentList) {
            User user = userService.getById(foodNoteComment.getUserId());
            foodNoteComment.setAvatar(user.getAvatar());
        }
        return Result.success(commentList);
    }

    /** 根据id获取笔记评论 */
    @GetMapping("getFoodNoteCommentById")
    public Result getFoodNoteCommentById(@RequestParam("id")String id) {
        FoodNoteComment foodNoteComment = foodNoteCommentService.getById(id);
        return Result.success(foodNoteComment);
    }

    /** 保存笔记评论 */
    @PostMapping("saveFoodNoteComment")
    public Result saveFoodNoteComment(@RequestBody FoodNoteComment foodNoteComment) {
        User user = ShiroUtils.getUserInfo();
        foodNoteComment.setUserId(user.getId());
        boolean save = foodNoteCommentService.save(foodNoteComment);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑笔记评论 */
    @PostMapping("editFoodNoteComment")
    public Result editFoodNoteComment(@RequestBody FoodNoteComment foodNoteComment) {
        boolean save = foodNoteCommentService.updateById(foodNoteComment);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除笔记评论 */
    @GetMapping("removeFoodNoteComment")
    public Result removeFoodNoteComment(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                foodNoteCommentService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("笔记评论id不能为空！");
        }
    }

}
