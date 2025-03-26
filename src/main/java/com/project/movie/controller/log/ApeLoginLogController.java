package com.project.movie.controller.log;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.movie.domain.LoginLog;
import com.project.movie.domain.Result;
import com.project.movie.service.LoginLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: 登陆日志controller
 * @date 2023/9/25 8:35
 */
@Controller
@ResponseBody
@RequestMapping("loginLog")
public class ApeLoginLogController {

    @Autowired
    private LoginLogService apeLoginLogService;

    /** 查询 */
    @PostMapping("getLogPage")
    public Result getLogPage(@RequestBody LoginLog apeLoginLog) {
        Page<LoginLog> page = new Page<>(apeLoginLog.getPageNumber(),apeLoginLog.getPageSize());
        QueryWrapper<LoginLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(StringUtils.isNotBlank(apeLoginLog.getUserName()),LoginLog::getUserName,apeLoginLog.getUserName())
                .eq(apeLoginLog.getStatus() != null,LoginLog::getStatus,apeLoginLog.getStatus())
                .ge(apeLoginLog.getStartTime() != null,LoginLog::getLoginTime,apeLoginLog.getStartTime())
                .le(apeLoginLog.getEndTime() != null,LoginLog::getLoginTime,apeLoginLog.getEndTime())
                .orderByDesc(LoginLog::getLoginTime);
        Page<LoginLog> logPage = apeLoginLogService.page(page, queryWrapper);
        return Result.success(logPage);
    }

    /** 删除 */
    @GetMapping("removeLog")
    public Result removeLog(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeLoginLogService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("日志id不能为空！");
        }
    }

    /** 清空 */
    @GetMapping("clearLog")
    public Result clearLog() {
        boolean remove = apeLoginLogService.remove(null);
        if (remove) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }

}
