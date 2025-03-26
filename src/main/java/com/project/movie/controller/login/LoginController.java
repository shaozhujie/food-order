package com.project.movie.controller.login;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.project.movie.common.utils.JwtUtil;
import com.project.movie.common.utils.PasswordUtils;
import com.project.movie.config.utils.RedisUtils;
import com.project.movie.config.utils.ShiroUtils;
import com.project.movie.domain.*;
import com.project.movie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @description: 登陆
 * @date 2024/2/26 21:20
 */
@Controller
@ResponseBody
@RequestMapping("login")
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private FoodNoteService foodNoteService;
    @Autowired
    private FoodItemService foodItemService;
    @Autowired
    private FoodItemCommentService foodItemCommentService;
    @Autowired
    private FoodOrderService foodOrderService;

    @Autowired
    private RedisUtils redisUtils;

    @PostMapping()
    public Result login(HttpServletRequest request, @RequestBody JSONObject jsonObject) {
        String username = jsonObject.getString("loginAccount");
        String password = jsonObject.getString("password");
        Integer type = jsonObject.getInteger("type");
        QueryWrapper<User> query = new QueryWrapper<>();
        query.lambda().eq(User::getLoginAccount,username);
        if (type == 0) {
            query.lambda().eq(User::getUserType,1);
        } else {
            query.lambda().ne(User::getUserType,1);
        }
        User user = userService.getOne(query);
        if (user == null) {
            return Result.fail("用户名不存在！");
        }
        //比较加密后得密码
        boolean decrypt = PasswordUtils.decrypt(password, user.getPassword() + "$" + user.getSalt());
        if (!decrypt) {
            return Result.fail("用户名或密码错误！");
        }
        if (user.getStatus() == 1) {
            return Result.fail("用户被禁用！");
        }
        //密码正确生成token返回
        String token = JwtUtil.sign(user.getId(), user.getPassword());
        JSONObject json = new JSONObject();
        json.put("token", token);
        return Result.success(json);
    }

    @GetMapping("logout")
    public Result logout() {
        return Result.success();
    }

    @GetMapping("getUserIndex")
    public Result getUserIndex() {
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUserType,1);
        int user = userService.count(queryWrapper);
        jsonObject.put("user",user);
        QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.lambda().eq(User::getUserType,2);
        int shop = userService.count(queryWrapper1);
        jsonObject.put("shop",shop);
        int note = foodNoteService.count();
        jsonObject.put("note",note);
        int food = foodItemService.count();
        jsonObject.put("food",food);
        return Result.success(jsonObject);
    }

    @GetMapping("getIndexData")
    public Result getIndexData() {
        User userInfo = ShiroUtils.getUserInfo();
        JSONObject jsonObject = new JSONObject();
        //获取用户数量
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUserType,1);
        int user = userService.count(queryWrapper);
        jsonObject.put("user",user);
        //获取商家数量
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getUserType,2);
        int shop = userService.count(wrapper);
        jsonObject.put("shop",shop);
        //获取美食数量
        int food = foodItemService.count();
        jsonObject.put("food",food);
        int comment = foodItemCommentService.count();
        jsonObject.put("comment",comment);
        int order = foodOrderService.count();
        jsonObject.put("order",order);
        int note = foodNoteService.count();
        jsonObject.put("note",note);
        QueryWrapper<User> wrapper1 = new QueryWrapper<>();
        wrapper1.lambda().eq(User::getUserType,1).eq(User::getSex,0);
        int nan = userService.count(wrapper1);
        jsonObject.put("nan",nan);
        QueryWrapper<User> wrapper2 = new QueryWrapper<>();
        wrapper2.lambda().eq(User::getUserType,1).eq(User::getSex,1);
        int nv = userService.count(wrapper2);
        jsonObject.put("nan",nan);
        if (userInfo.getUserType() == 0) {
            QueryWrapper<User> wrapper3 = new QueryWrapper<>();
            wrapper3.lambda().eq(User::getUserType,2);
            List<User> userList = userService.list(wrapper3);
            List<String> nameList = new ArrayList<>();
            List<Float> moneyList = new ArrayList<>();
            for (User user1 : userList) {
                nameList.add(user1.getUserName());
                QueryWrapper<FoodOrder> wrapper4 = new QueryWrapper<>();
                wrapper4.lambda().eq(FoodOrder::getShopId,user1.getId());
                List<FoodOrder> orderList = foodOrderService.list(wrapper4);
                float price = 0;
                for (FoodOrder foodOrder : orderList) {
                    price += foodOrder.getTotalPrice();
                }
                moneyList.add(price);
            }
            jsonObject.put("name",nameList);
            jsonObject.put("sale",moneyList);
        } else {
            QueryWrapper<FoodItem> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.lambda().eq(FoodItem::getUserId,userInfo.getId());
            List<FoodItem> itemList = foodItemService.list(queryWrapper1);
            List<String> nameList = new ArrayList<>();
            List<Float> moneyList = new ArrayList<>();
            for (FoodItem foodItem : itemList) {
                nameList.add(foodItem.getName());
                QueryWrapper<FoodOrder> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.lambda().eq(FoodOrder::getFoodId,foodItem.getId());
                List<FoodOrder> orderList = foodOrderService.list(queryWrapper2);
                float price = 0;
                for (FoodOrder foodOrder : orderList) {
                    price += foodOrder.getTotalPrice();
                }
                moneyList.add(price);
            }
            jsonObject.put("name",nameList);
            jsonObject.put("sale",moneyList);
        }
        return Result.success(jsonObject);
    }

}
