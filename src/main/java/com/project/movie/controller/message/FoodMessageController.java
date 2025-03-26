package com.project.movie.controller.message;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.movie.common.enums.ResultCode;
import com.project.movie.domain.FoodMessage;
import com.project.movie.domain.Result;
import com.project.movie.service.FoodMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 资讯controller
 * @date 2025/03/19 11:28
 */
@Controller
@ResponseBody
@RequestMapping("message")
public class FoodMessageController {

    @Autowired
    private FoodMessageService foodMessageService;

    /** 分页获取资讯 */
    @PostMapping("getFoodMessagePage")
    public Result getFoodMessagePage(@RequestBody FoodMessage foodMessage) {
        Page<FoodMessage> page = new Page<>(foodMessage.getPageNumber(),foodMessage.getPageSize());
        QueryWrapper<FoodMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(foodMessage.getTitle()),FoodMessage::getTitle,foodMessage.getTitle())
                .like(StringUtils.isNotBlank(foodMessage.getIntroduce()),FoodMessage::getIntroduce,foodMessage.getIntroduce())
                .orderByDesc(FoodMessage::getCreateTime);
        Page<FoodMessage> foodMessagePage = foodMessageService.page(page, queryWrapper);
        return Result.success(foodMessagePage);
    }

    /** 根据id获取资讯 */
    @GetMapping("getFoodMessageById")
    public Result getFoodMessageById(@RequestParam("id")String id) {
        FoodMessage foodMessage = foodMessageService.getById(id);
        return Result.success(foodMessage);
    }

    /** 保存资讯 */
    @PostMapping("saveFoodMessage")
    public Result saveFoodMessage(@RequestBody FoodMessage foodMessage) {
        boolean save = foodMessageService.save(foodMessage);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑资讯 */
    @PostMapping("editFoodMessage")
    public Result editFoodMessage(@RequestBody FoodMessage foodMessage) {
        boolean save = foodMessageService.updateById(foodMessage);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除资讯 */
    @GetMapping("removeFoodMessage")
    public Result removeFoodMessage(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                foodMessageService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("资讯id不能为空！");
        }
    }

}
