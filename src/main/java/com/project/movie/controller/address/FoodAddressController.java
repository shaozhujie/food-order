package com.project.movie.controller.address;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.movie.common.enums.ResultCode;
import com.project.movie.config.utils.ShiroUtils;
import com.project.movie.domain.FoodAddress;
import com.project.movie.domain.Result;
import com.project.movie.domain.User;
import com.project.movie.service.FoodAddressService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 地址controller
 * @date 2025/03/13 10:19
 */
@Controller
@ResponseBody
@RequestMapping("address")
public class FoodAddressController {

    @Autowired
    private FoodAddressService foodAddressService;

    /** 分页获取地址 */
    @PostMapping("getFoodAddressPage")
    public Result getFoodAddressPage(@RequestBody FoodAddress foodAddress) {
        Page<FoodAddress> page = new Page<>(foodAddress.getPageNumber(),foodAddress.getPageSize());
        QueryWrapper<FoodAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(foodAddress.getName()),FoodAddress::getName,foodAddress.getName())
                .like(StringUtils.isNotBlank(foodAddress.getTel()),FoodAddress::getTel,foodAddress.getTel())
                .like(StringUtils.isNotBlank(foodAddress.getAddress()),FoodAddress::getAddress,foodAddress.getAddress())
                .eq(foodAddress.getIsDefault() != null,FoodAddress::getIsDefault,foodAddress.getIsDefault())
                .eq(StringUtils.isNotBlank(foodAddress.getUserId()),FoodAddress::getUserId,foodAddress.getUserId())
                .like(StringUtils.isNotBlank(foodAddress.getCreateBy()),FoodAddress::getCreateBy,foodAddress.getCreateBy())
                .eq(foodAddress.getCreateTime() != null,FoodAddress::getCreateTime,foodAddress.getCreateTime())
                .like(StringUtils.isNotBlank(foodAddress.getUpdateBy()),FoodAddress::getUpdateBy,foodAddress.getUpdateBy())
                .eq(foodAddress.getUpdateTime() != null,FoodAddress::getUpdateTime,foodAddress.getUpdateTime());
        Page<FoodAddress> foodAddressPage = foodAddressService.page(page, queryWrapper);
        return Result.success(foodAddressPage);
    }

    @GetMapping("getFoodAddressIsDefault")
    public Result getFoodAddressIsDefault() {
        User user = ShiroUtils.getUserInfo();
        QueryWrapper<FoodAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FoodAddress::getUserId,user.getId())
                .eq(FoodAddress::getIsDefault,1).last("limit 1");
        FoodAddress address = foodAddressService.getOne(queryWrapper);
        if (address != null) {
           return Result.success(address);
        } else {
            QueryWrapper<FoodAddress> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(FoodAddress::getUserId,user.getId()).orderByDesc(FoodAddress::getCreateTime).last("limit 1");
            FoodAddress foodAddress = foodAddressService.getOne(wrapper);
            return Result.success(foodAddress);
        }
    }

    @GetMapping("getFoodAddressList")
    public Result getFoodAddressList() {
        User user = ShiroUtils.getUserInfo();
        QueryWrapper<FoodAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(FoodAddress::getUserId,user.getId()).orderByDesc(FoodAddress::getCreateTime);
        List<FoodAddress> foodAddressPage = foodAddressService.list(queryWrapper);
        return Result.success(foodAddressPage);
    }

    /** 根据id获取地址 */
    @GetMapping("getFoodAddressById")
    public Result getFoodAddressById(@RequestParam("id")String id) {
        FoodAddress foodAddress = foodAddressService.getById(id);
        return Result.success(foodAddress);
    }

    /** 保存地址 */
    @PostMapping("saveFoodAddress")
    @Transactional(rollbackFor = Exception.class)
    public Result saveFoodAddress(@RequestBody FoodAddress foodAddress) {
        User userInfo = ShiroUtils.getUserInfo();
        if (foodAddress.getIsDefault() == 1) {
            UpdateWrapper<FoodAddress> wrapper = new UpdateWrapper<>();
            wrapper.lambda().eq(FoodAddress::getUserId,userInfo.getId()).set(FoodAddress::getIsDefault,0);
            foodAddressService.update(wrapper);
        }
        foodAddress.setUserId(userInfo.getId());
        boolean save = foodAddressService.save(foodAddress);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑地址 */
    @PostMapping("editFoodAddress")
    @Transactional(rollbackFor = Exception.class)
    public Result editFoodAddress(@RequestBody FoodAddress foodAddress) {
        User userInfo = ShiroUtils.getUserInfo();
        if (foodAddress.getIsDefault() == 1) {
            UpdateWrapper<FoodAddress> wrapper = new UpdateWrapper<>();
            wrapper.lambda().eq(FoodAddress::getUserId,userInfo.getId()).
                ne(FoodAddress::getId,foodAddress.getId())
                .set(FoodAddress::getIsDefault,0);
            foodAddressService.update(wrapper);
        }
        boolean save = foodAddressService.updateById(foodAddress);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除地址 */
    @GetMapping("removeFoodAddress")
    public Result removeFoodAddress(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                foodAddressService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("地址id不能为空！");
        }
    }

}
