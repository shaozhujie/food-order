package com.project.movie.controller.item;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.movie.common.enums.BusinessType;
import com.project.movie.common.enums.ResultCode;
import com.project.movie.common.utils.ItemCF;
import com.project.movie.config.utils.ShiroUtils;
import com.project.movie.domain.*;
import com.project.movie.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 美食controller
 * @date 2025/03/18 09:23
 */
@Controller
@ResponseBody
@RequestMapping("item")
public class FoodItemController {

    @Autowired
    private FoodItemService foodItemService;
    @Autowired
    private UserService userService;
    @Autowired
    private FoodLikeService foodLikeService;
    @Autowired
    private FoodOrderService foodOrderService;
    @Autowired
    private FoodItemCommentService foodItemCommentService;

    /** 分页获取美食 */
    @PostMapping("getFoodItemPage")
    public Result getFoodItemPage(@RequestBody FoodItem foodItem) {
        Page<FoodItem> page = new Page<>(foodItem.getPageNumber(),foodItem.getPageSize());
        QueryWrapper<FoodItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(foodItem.getCityId()),FoodItem::getCityId,foodItem.getCityId())
                .eq(StringUtils.isNotBlank(foodItem.getTypeId()),FoodItem::getTypeId,foodItem.getTypeId())
                .eq(StringUtils.isNotBlank(foodItem.getUserId()),FoodItem::getUserId,foodItem.getUserId())
                .like(StringUtils.isNotBlank(foodItem.getName()),FoodItem::getName,foodItem.getName())
                .like(StringUtils.isNotBlank(foodItem.getContent()),FoodItem::getContent,foodItem.getContent())
                .like(StringUtils.isNotBlank(foodItem.getIntroduce()),FoodItem::getIntroduce,foodItem.getIntroduce())
                .orderByDesc(FoodItem::getCreateTime);
        Page<FoodItem> foodItemPage = foodItemService.page(page, queryWrapper);
        for (FoodItem item : foodItemPage.getRecords()) {
            User user = userService.getById(item.getUserId());
            item.setUserName(user.getUserName());
            item.setOpen(user.getOpen());
            item.setLogo(user.getLogo());
        }
        return Result.success(foodItemPage);
    }

    @GetMapping("getFoodItemRecommend")
    public Result getFoodItemRecommend() {
        User user = ShiroUtils.getUserInfo();
        //先获取用户喜欢的电影的类型
        List<RelateDTO> list = new ArrayList<>();
        Map<String,Double> map = new HashMap<>();
        //获取收藏
        QueryWrapper<FoodLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FoodLike::getUserId,user.getId());
        List<FoodLike> favorList = foodLikeService.list(queryWrapper);
        for (FoodLike like : favorList) {
            FoodItem foodItem = foodItemService.getById(like.getItemId());
            if (foodItem != null) {
                String[] strings = foodItem.getTypeId().split(",");
                for (String type : strings) {
                    if (map.containsKey(type)) {
                        Double aDouble = map.get(type);
                        map.put(type,aDouble + 1);
                    } else {
                        map.put(type,1d);
                    }
                }
            }
        }
        //获取已购订单
        QueryWrapper<FoodOrder> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.lambda().eq(FoodOrder::getUserId,user.getId());
        List<FoodOrder> orderList = foodOrderService.list(queryWrapper2);
        for (FoodOrder order : orderList) {
            FoodItem movieItem = foodItemService.getById(order.getFoodId());
            if (movieItem != null) {
                String[] strings = movieItem.getTypeId().split(",");
                for (String type : strings) {
                    if (map.containsKey(type)) {
                        Double aDouble = map.get(type);
                        map.put(type,aDouble + 1);
                    } else {
                        map.put(type,1d);
                    }
                }
            }
        }
        //完事之后循环map
        for (String key : map.keySet()) {
            RelateDTO relateDTO = new RelateDTO();
            relateDTO.setItemId(key);
            relateDTO.setIndex(map.get(key));
            list.add(relateDTO);
        }
        String type = "";
        Double height = 0d;
        for (RelateDTO relateDTO : list) {
            if (relateDTO.getIndex() > height) {
                height = relateDTO.getIndex();
                type = relateDTO.getItemId();
            }
        }
        if (list.size() <= 1) {
            List<FoodItem> movieItemList = foodItemService.getRecommandItem(new ArrayList<>());
            for (FoodItem item : movieItemList) {
                User userServiceById = userService.getById(item.getUserId());
                item.setUserName(userServiceById.getUserName());
                item.setOpen(userServiceById.getOpen());
                item.setLogo(userServiceById.getLogo());
            }
            return Result.success(movieItemList);
        } else {
            List<String> recommendations = ItemCF.recommend(type, list);
            //查询爱看的电影
            List<FoodItem> movieItemList = foodItemService.getRecommandItem(recommendations);
            for (FoodItem item : movieItemList) {
                User userServiceById = userService.getById(item.getUserId());
                item.setUserName(userServiceById.getUserName());
                item.setOpen(userServiceById.getOpen());
                item.setLogo(userServiceById.getLogo());
            }
            return Result.success(movieItemList);
        }
    }

    /** 根据id获取美食 */
    @GetMapping("getFoodItemById")
    public Result getFoodItemById(@RequestParam("id")String id) {
        FoodItem foodItem = foodItemService.getById(id);
        User user = userService.getById(foodItem.getUserId());
        foodItem.setUserName(user.getUserName());
        foodItem.setOpen(user.getOpen());
        foodItem.setLogo(user.getLogo());
        return Result.success(foodItem);
    }

    /** 保存美食 */
    @PostMapping("saveFoodItem")
    public Result saveFoodItem(@RequestBody FoodItem foodItem) {
        String userId = foodItem.getUserId();
        User userInfo = userService.getById(userId);
        foodItem.setCityId(userInfo.getCity());
        boolean save = foodItemService.save(foodItem);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑美食 */
    @PostMapping("editFoodItem")
    public Result editFoodItem(@RequestBody FoodItem foodItem) {
        boolean save = foodItemService.updateById(foodItem);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除美食 */
    @GetMapping("removeFoodItem")
    @Transactional(rollbackFor = Exception.class)
    public Result removeFoodItem(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                //删除订单
                QueryWrapper<FoodOrder> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(FoodOrder::getFoodId,id);
                foodOrderService.remove(queryWrapper);
                //删除评论
                QueryWrapper<FoodItemComment> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(FoodItemComment::getFoodId,id);
                foodItemCommentService.remove(wrapper);
                //删除收藏
                QueryWrapper<FoodLike> wrapper1 = new QueryWrapper<>();
                wrapper1.lambda().eq(FoodLike::getItemId,id);
                foodLikeService.remove(wrapper1);
                foodItemService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("美食id不能为空！");
        }
    }

    @PostMapping("getFoodIds")
    public Result getFoodIds(@RequestBody JSONObject jsonObject) {
        String ids = jsonObject.getString("ids");
        String[] split = ids.split(",");
        QueryWrapper<FoodItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(FoodItem::getId,split);
        List<FoodItem> itemList = foodItemService.list(queryWrapper);
        return Result.success(itemList);
    }

}
