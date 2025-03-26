package com.project.movie.controller.log;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.movie.domain.OperateLog;
import com.project.movie.domain.Result;
import com.project.movie.service.OperateLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: 操作日志controller
 * @date 2023/9/25 8:43
 */
@Controller
@ResponseBody
@RequestMapping("operLog")
public class ApeOperLogController {

    @Autowired
    private OperateLogService apeOperateLogService;

    /** 查询 */
    @PostMapping("getLogPage")
    public Result getLogPage(@RequestBody OperateLog apeOperateLog) {
        Page<OperateLog> page = new Page<>(apeOperateLog.getPageNumber(),apeOperateLog.getPageSize());
        QueryWrapper<OperateLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(apeOperateLog.getType()!=null,OperateLog::getType,apeOperateLog.getType())
                .like(StringUtils.isNotBlank(apeOperateLog.getOperUserAccount()),OperateLog::getOperUserAccount,apeOperateLog.getOperUserAccount())
                .ge(apeOperateLog.getStartTime() != null,OperateLog::getOperTime,apeOperateLog.getStartTime())
                .le(apeOperateLog.getEndTime() != null,OperateLog::getOperTime,apeOperateLog.getEndTime())
                .orderByDesc(OperateLog::getOperTime);
        Page<OperateLog> logPage = apeOperateLogService.page(page, queryWrapper);
        return Result.success(logPage);
    }

    /** 删除 */
    @GetMapping("removeLog")
    public Result removeLog(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeOperateLogService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("日志id不能为空！");
        }
    }

    /** 清空 */
    @GetMapping("clearLog")
    public Result clearLog() {
        boolean remove = apeOperateLogService.remove(null);
        if (remove) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }

}
