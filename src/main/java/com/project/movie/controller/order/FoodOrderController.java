package com.project.movie.controller.order;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.movie.common.enums.BusinessType;
import com.project.movie.common.enums.ResultCode;
import com.project.movie.config.utils.ShiroUtils;
import com.project.movie.domain.*;
import com.project.movie.service.FoodCarService;
import com.project.movie.service.FoodItemService;
import com.project.movie.service.FoodOrderService;
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
 * @description: 订单controller
 * @date 2025/03/20 12:08
 */
@Controller
@ResponseBody
@RequestMapping("order")
public class FoodOrderController {

    @Autowired
    private FoodOrderService foodOrderService;
    @Autowired
    private FoodItemService foodItemService;
    @Autowired
    private FoodCarService foodCarService;

    /** 分页获取订单 */
    @PostMapping("getFoodOrderPage")
    public Result getFoodOrderPage(@RequestBody FoodOrder foodOrder) {
        Page<FoodOrder> page = new Page<>(foodOrder.getPageNumber(),foodOrder.getPageSize());
        QueryWrapper<FoodOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(foodOrder.getOrderNumber()),FoodOrder::getOrderNumber,foodOrder.getOrderNumber())
                .eq(StringUtils.isNotBlank(foodOrder.getUserId()),FoodOrder::getUserId,foodOrder.getUserId())
                .eq(StringUtils.isNotBlank(foodOrder.getShopId()),FoodOrder::getShopId,foodOrder.getShopId())
                .eq(StringUtils.isNotBlank(foodOrder.getFoodId()),FoodOrder::getFoodId,foodOrder.getFoodId())
                .like(StringUtils.isNotBlank(foodOrder.getFoodName()),FoodOrder::getFoodName,foodOrder.getFoodName())
                .like(StringUtils.isNotBlank(foodOrder.getName()),FoodOrder::getName,foodOrder.getName())
                .like(StringUtils.isNotBlank(foodOrder.getTel()),FoodOrder::getTel,foodOrder.getTel())
                .like(StringUtils.isNotBlank(foodOrder.getAddress()),FoodOrder::getAddress,foodOrder.getAddress())
                .eq(foodOrder.getType() != null,FoodOrder::getType,foodOrder.getType())
                .eq(foodOrder.getState() != null,FoodOrder::getState,foodOrder.getState())
                .eq(StringUtils.isNotBlank(foodOrder.getCreateBy()),FoodOrder::getCreateBy,foodOrder.getCreateBy())
                .orderByDesc(FoodOrder::getCreateTime);
        Page<FoodOrder> foodOrderPage = foodOrderService.page(page, queryWrapper);
        return Result.success(foodOrderPage);
    }

    /** 根据id获取订单 */
    @GetMapping("getFoodOrderById")
    public Result getFoodOrderById(@RequestParam("id")String id) {
        FoodOrder foodOrder = foodOrderService.getById(id);
        return Result.success(foodOrder);
    }

    /** 保存订单 */
    @PostMapping("saveFoodOrder")
    public Result saveFoodOrder(@RequestBody FoodOrder foodOrder) {
        boolean save = foodOrderService.save(foodOrder);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑订单 */
    @PostMapping("editFoodOrder")
    public Result editFoodOrder(@RequestBody FoodOrder foodOrder) {
        boolean save = foodOrderService.updateById(foodOrder);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除订单 */
    @GetMapping("removeFoodOrder")
    public Result removeFoodOrder(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                foodOrderService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("订单id不能为空！");
        }
    }

    @PostMapping("submitOrder")
    @Transactional(rollbackFor = Exception.class)
    public Result submitOrder(@RequestBody JSONObject jsonObject) throws Exception {
        User user = ShiroUtils.getUserInfo();
        Integer type = jsonObject.getInteger("type");
        List<FoodItem> food = jsonObject.getList("food", FoodItem.class);
        FoodAddress address = jsonObject.getObject("address", FoodAddress.class);
        String remark = jsonObject.getString("remark");
        for (FoodItem foodItem : food) {
            FoodItem item = foodItemService.getById(foodItem.getId());
            if ((item.getNum() - foodItem.getQuantity()) < 0) {
                throw new Exception(item.getName() + "剩余数量不足，下单失败");
            }
            item.setNum(item.getNum() - foodItem.getQuantity());
            item.setBuy(item.getBuy() + foodItem.getQuantity());
            foodItemService.updateById(item);
            FoodOrder foodOrder = new FoodOrder();
            foodOrder.setOrderNumber(String.valueOf(System.currentTimeMillis()));
            foodOrder.setUserId(user.getId());
            foodOrder.setShopId(foodItem.getUserId());
            foodOrder.setFoodId(foodItem.getId());
            foodOrder.setFoodName(foodItem.getName());
            foodOrder.setImage(foodItem.getImage());
            foodOrder.setName(address.getName());
            foodOrder.setTel(address.getTel());
            foodOrder.setAddress(address.getAddress());
            foodOrder.setType(type);
            foodOrder.setNum(foodItem.getQuantity());
            foodOrder.setPrice(foodItem.getPrice());
            foodOrder.setTotalPrice(new BigDecimal(foodItem.getPrice() * foodItem.getQuantity()).setScale(BigDecimal.ROUND_FLOOR).floatValue());
            foodOrder.setRemark(remark);
            foodOrderService.save(foodOrder);
            //删除购物车
            QueryWrapper<FoodCar> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(FoodCar::getUserId,user.getId())
                    .eq(FoodCar::getFoodId,foodItem.getId());
            foodCarService.remove(queryWrapper);
        }
        return Result.success();
    }

}
