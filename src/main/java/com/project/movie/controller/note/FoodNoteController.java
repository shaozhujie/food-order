package com.project.movie.controller.note;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.movie.common.enums.ResultCode;
import com.project.movie.config.utils.ShiroUtils;
import com.project.movie.domain.FoodNote;
import com.project.movie.domain.FoodNoteComment;
import com.project.movie.domain.Result;
import com.project.movie.domain.User;
import com.project.movie.service.FoodNoteCommentService;
import com.project.movie.service.FoodNoteService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 美食笔记controller
 * @date 2025/03/19 10:55
 */
@Controller
@ResponseBody
@RequestMapping("note")
public class FoodNoteController {

    @Autowired
    private FoodNoteService foodNoteService;
    @Autowired
    private FoodNoteCommentService foodNoteCommentService;

    /** 分页获取美食笔记 */
    @PostMapping("getFoodNotePage")
    public Result getFoodNotePage(@RequestBody FoodNote foodNote) {
        Page<FoodNote> page = new Page<>(foodNote.getPageNumber(),foodNote.getPageSize());
        QueryWrapper<FoodNote> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(foodNote.getUserId()),FoodNote::getUserId,foodNote.getUserId())
                .like(StringUtils.isNotBlank(foodNote.getTitle()),FoodNote::getTitle,foodNote.getTitle())
                .like(StringUtils.isNotBlank(foodNote.getIntroduce()),FoodNote::getIntroduce,foodNote.getIntroduce())
                .like(StringUtils.isNotBlank(foodNote.getCreateBy()),FoodNote::getCreateBy,foodNote.getCreateBy())
                .orderByDesc(FoodNote::getCreateTime);
        Page<FoodNote> foodNotePage = foodNoteService.page(page, queryWrapper);
        return Result.success(foodNotePage);
    }

    /** 根据id获取美食笔记 */
    @GetMapping("getFoodNoteById")
    public Result getFoodNoteById(@RequestParam("id")String id) {
        FoodNote foodNote = foodNoteService.getById(id);
        return Result.success(foodNote);
    }

    /** 保存美食笔记 */
    @PostMapping("saveFoodNote")
    public Result saveFoodNote(@RequestBody FoodNote foodNote) {
        User user = ShiroUtils.getUserInfo();
        foodNote.setUserId(user.getId());
        boolean save = foodNoteService.save(foodNote);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑美食笔记 */
    @PostMapping("editFoodNote")
    public Result editFoodNote(@RequestBody FoodNote foodNote) {
        boolean save = foodNoteService.updateById(foodNote);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除美食笔记 */
    @GetMapping("removeFoodNote")
    @Transactional(rollbackFor = Exception.class)
    public Result removeFoodNote(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                QueryWrapper<FoodNoteComment> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(FoodNoteComment::getNoteId,id);
                foodNoteCommentService.remove(queryWrapper);
                foodNoteService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("美食笔记id不能为空！");
        }
    }

}
