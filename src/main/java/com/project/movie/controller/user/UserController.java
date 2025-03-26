package com.project.movie.controller.user;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.movie.common.utils.PasswordUtils;
import com.project.movie.config.utils.RedisUtils;
import com.project.movie.config.utils.ShiroUtils;
import com.project.movie.domain.*;
import com.project.movie.enums.ResultCode;
import com.project.movie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @version 1.0
 * @description: 用户controller
 * @date 2024/2/26 21:00
 */
@Controller
@ResponseBody
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private FoodAddressService foodAddressService;
    @Autowired
    private FoodItemCommentService foodItemCommentService;
    @Autowired
    private FoodNoteCommentService foodNoteCommentService;
    @Autowired
    private FoodLikeService foodLikeService;
    @Autowired
    private FoodCarService foodCarService;
    @Autowired
    private FoodItemService foodItemService;

    /** 分页查询用户 */
    @PostMapping("getUserPage")
    public Result getUserPage(@RequestBody User user) {
        Page<User> page = userService.getUserPage(user);
        return Result.success(page);
    }

    @PostMapping("getUserPageTeam")
    public Result getUserPageTeam(@RequestBody User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ne(User::getUserType,1).ne(User::getUserType,0);
        Page<User> page = new Page<>(user.getPageNumber(),user.getPageSize());
        Page<User> userPage = userService.page(page,queryWrapper);
        return Result.success(userPage);
    }

    @GetMapping("getUserByType")
    public Result getUserByType(@RequestParam("type")Integer type) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUserType,type);
        List<User> userList = userService.list(queryWrapper);
        return Result.success(userList);
    }

    /** 根据id查询用户 */
    @GetMapping("getUserById")
    public Result getUserById(@RequestParam("id") String id) {
        User user = userService.getById(id);
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserRole::getUserId,user.getId());
        List<UserRole> list = userRoleService.list(queryWrapper);
        List<String> roles = new ArrayList<>();
        for (UserRole apeUserRole : list) {
            roles.add(apeUserRole.getRoleId());
        }
        user.setRoleIds(roles);
        return Result.success(user);
    }

    /** 新增用户 */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("saveUser")
    public Result saveUser(@RequestBody User user) {
        //先校验登陆账号是否重复
        boolean account = checkAccount(user);
        if (!account) {
            return Result.fail("登陆账号已存在不可重复！");
        }
        String uuid = IdWorker.get32UUID();
        //密码加盐加密
        String encrypt = PasswordUtils.encrypt(user.getPassword());
        String[] split = encrypt.split("\\$");
        user.setId(uuid);
        user.setPassword(split[0]);
        user.setSalt(split[1]);
        user.setAvatar("/img/avatar.jpeg");
        user.setPwdUpdateDate(new Date());
        //保存用户
        boolean save = userService.save(user);
        //再保存用户角色关系
        List<String> roleIds = user.getRoleIds();
        List<UserRole> apeUserRoles = new ArrayList<>();
        if (roleIds != null && roleIds.size() > 0) {
            for (String roleId : roleIds) {
                UserRole apeUserRole = new UserRole();
                apeUserRole.setUserId(uuid);
                apeUserRole.setRoleId(roleId);
                apeUserRoles.add(apeUserRole);
            }
        }
        userRoleService.saveBatch(apeUserRoles);
        return Result.success();
    }

    /** 编辑用户 */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("editUser")
    public Result editUser(@RequestBody User user) {
        User user1 = userService.getById(user.getId());
        if (!user1.getLoginAccount().equals(user.getLoginAccount())) {
            //先校验登陆账号是否重复
            boolean account = checkAccount(user);
            if (!account) {
                return Result.fail("登陆账号已存在不可重复！");
            }
        }
        //更新用户
        boolean edit = userService.updateById(user);
        //先删除用户角色关系
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserRole::getUserId,user.getId());
        userRoleService.remove(queryWrapper);
        //再次保存最新的关系
        List<String> roleIds = user.getRoleIds();
        List<UserRole> apeUserRoles = new ArrayList<>();
        if (roleIds != null && roleIds.size() > 0) {
            for (String roleId : roleIds) {
                UserRole apeUserRole = new UserRole();
                apeUserRole.setUserId(user.getId());
                apeUserRole.setRoleId(roleId);
                apeUserRoles.add(apeUserRole);
            }
        }
        userRoleService.saveBatch(apeUserRoles);
        return Result.success();
    }

    /** 校验用户 */
    public boolean checkAccount(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getLoginAccount,user.getLoginAccount());
        int count = userService.count(queryWrapper);
        return count <= 0;
    }

    /** 删除用户 */
    @Transactional(rollbackFor = Exception.class)
    @GetMapping("removeUser")
    public Result removeUser(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                User user = userService.getById(id);
                if (user.getUserType() == 2) {
                    QueryWrapper<FoodItem> wrapper = new QueryWrapper<>();
                    wrapper.lambda().eq(FoodItem::getUserId,id);
                    int count = foodItemService.count(wrapper);
                    if (count > 0) {
                        return Result.fail("存在发布的美食，无法删除");
                    }
                }
                boolean remove = userService.removeById(id);
                //删除收货地址
                QueryWrapper<FoodAddress> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(FoodAddress::getUserId,id);
                foodAddressService.remove(queryWrapper);
                //删除评论
                QueryWrapper<FoodItemComment> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(FoodItemComment::getUserId,id);
                foodItemCommentService.remove(wrapper);
                //删除笔记评论
                QueryWrapper<FoodNoteComment> wrapper1 = new QueryWrapper<>();
                wrapper1.lambda().eq(FoodNoteComment::getUserId,id);
                foodNoteCommentService.remove(wrapper1);
                //删除收藏
                QueryWrapper<FoodLike> wrapper2 = new QueryWrapper<>();
                wrapper2.lambda().eq(FoodLike::getUserId,id);
                foodLikeService.remove(wrapper2);
                //删除购物车
                QueryWrapper<FoodCar> wrapper3 = new QueryWrapper<>();
                wrapper3.lambda().eq(FoodCar::getUserId,id);
                foodCarService.remove(wrapper3);
            }
            return Result.success();
        } else {
            return Result.fail("角色id不能为空！");
        }
    }

    /** 重置密码 */
    @PostMapping("resetPassword")
    public Result resetPassword(@RequestBody JSONObject json) {
        String id = json.getString("id");
        String newPassword = json.getString("newPassword");
        String encrypt = PasswordUtils.encrypt(newPassword);
        String[] split = encrypt.split("\\$");
        User user = userService.getById(id);
        boolean decrypt = PasswordUtils.decrypt(newPassword, user.getPassword() + "$" + user.getSalt());
        if (decrypt) {
            return Result.fail("新密码不可和旧密码相同！");
        }
        user.setPassword(split[0]);
        user.setSalt(split[1]);
        user.setPwdUpdateDate(new Date());
        boolean update = userService.updateById(user);
        if (update) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 获取登陆用户信息 */
    @GetMapping("getUserInfo")
    public Result getUserInfo() {
        User user = ShiroUtils.getUserInfo();
        return Result.success(user);
    }

    /** 修改个人信息 */
    @PostMapping("setUserInfo")
    public Result setUserInfo(@RequestBody User user) {
        String id = ShiroUtils.getUserInfo().getId();
        user.setId(id);
        userService.updateById(user);
        return Result.success();
    }

    /** 修改个人头像 */
    @PostMapping("setUserAvatar/{id}")
    public Result setUserAvatar(@PathVariable("id") String id, @RequestParam("file") MultipartFile avatar) {
        if(StringUtils.isBlank(id)){
            return Result.fail("用户id为空!");
        }
        User apeUser = userService.getById(id);
        if(avatar.isEmpty()){
            return Result.fail("上传的头像不能为空!");
        }
        String coverType = avatar.getOriginalFilename().substring(avatar.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
        if ("jpeg".equals(coverType)  || "gif".equals(coverType) || "png".equals(coverType) || "bmp".equals(coverType)  || "jpg".equals(coverType)) {
            //文件路径
            String filePath = System.getProperty("user.dir")+System.getProperty("file.separator")+"img";
            //文件名=当前时间到毫秒+原来的文件名
            String fileName = id + "."+ coverType;
            //如果文件路径不存在，新增该路径
            File file1 = new File(filePath);
            if(!file1.exists()){
                boolean mkdir = file1.mkdir();
            }
            //现在的文件地址
            if (StringUtils.isNotBlank(apeUser.getAvatar())) {
                String s = apeUser.getAvatar().split("/")[2];
                File now = new File(filePath + System.getProperty("file.separator") + s);
                boolean delete = now.delete();
            }
            //实际的文件地址
            File dest = new File(filePath + System.getProperty("file.separator") + fileName);
            //存储到数据库里的相对文件地址
            String storeImgPath = "/img/"+fileName;
            try {
                avatar.transferTo(dest);
                //更新头像
                apeUser.setAvatar(storeImgPath);
                userService.updateById(apeUser);
                return Result.success(storeImgPath);
            } catch (IOException e) {
                return Result.fail("上传失败");
            }
        } else {
            return Result.fail("请选择正确的图片格式");
        }
    }

    @PostMapping("changePassword")
    public Result changePassword(@RequestBody JSONObject json) {
        String id = json.getString("id");
        String password = json.getString("password");
        User user = userService.getById(id);
        boolean decrypt = PasswordUtils.decrypt(password, user.getPassword() + "$" + user.getSalt());
        if (decrypt) {
            String newPassword = json.getString("newPassword");
            String encrypt = PasswordUtils.encrypt(newPassword);
            String[] split = encrypt.split("\\$");
            user.setSalt(split[1]);
            user.setPassword(split[0]);
            user.setPwdUpdateDate(new Date());
            boolean update = userService.updateById(user);
            if (update) {
                return Result.success();
            } else {
                return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
            }
        } else {
            return Result.fail("旧密码不正确");
        }
    }

    @GetMapping("getEmailReg")
    public Result getEmailReg(@RequestParam("email") String email) {
        String str="abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        Random r=new Random();
        String arr[]=new String [4];
        String reg="";
        for(int i=0;i<4;i++) {
            int n=r.nextInt(62);
            arr[i]=str.substring(n,n+1);
            reg+=arr[i];
        }
        try {
            redisUtils.set(email + "forget",reg.toLowerCase(),60L);
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setPort(465);
            sender.setHost("smtp.qq.com");
            sender.setUsername("1760272627@qq.com");
            sender.setPassword("moguqdleqpazecag");
            sender.setDefaultEncoding("utf-8");
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true"); // 使用TLS
            properties.put("mail.smtp.ssl.enable", "true"); // 启用SSL
            sender.setJavaMailProperties(properties);
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setFrom(sender.getUsername());
            helper.setTo(email);
            helper.setSubject("修改密码验证");
            helper.setText("您的邮箱验证码为："+reg,true);
            sender.send(msg);
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail("邮件发送失败");
        }
        return Result.success();
    }

    @PostMapping("forgetPassword")
    public Result forgetPassword(@RequestBody JSONObject jsonObject) {
        String loginAccount = jsonObject.getString("loginAccount");
        String email = jsonObject.getString("email");
        String password = jsonObject.getString("password");
        String code = jsonObject.getString("code").toLowerCase();
        String s = redisUtils.get(email + "forget");
        if (!code.equals(s)) {
            return Result.fail("验证码错误");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getLoginAccount,loginAccount).last("limit 1");
        User user = userService.getOne(queryWrapper);
        //密码加盐加密
        String encrypt = PasswordUtils.encrypt(password);
        String[] split = encrypt.split("\\$");
        user.setPassword(split[0]);
        user.setSalt(split[1]);
        boolean update = userService.updateById(user);
        if (update) {
            return Result.success();
        } else {
            return Result.fail("密码修改失败");
        }
    }

}
