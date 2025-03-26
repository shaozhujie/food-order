package com.project.movie.config.event.listener;

import com.project.movie.config.config.AsyncPoolConfig;
import com.project.movie.config.event.LogEvent;
import com.project.movie.config.event.LoginLogEvent;
import com.project.movie.domain.LoginLog;
import com.project.movie.domain.OperateLog;
import com.project.movie.service.LoginLogService;
import com.project.movie.service.OperateLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: 监听事件
 * @date 2023/9/22 10:59
 */
@Slf4j
@Component
public class SystemEventListener {

    @Autowired
    private OperateLogService apeOperateLogService;
    @Autowired
    private LoginLogService apeLoginLogService;

    @Async(value = AsyncPoolConfig.TASK_EXECUTOR_NAME)
    @EventListener(classes = LogEvent.class)
    public void handleLogEvent(LogEvent event) {
        //处理日志业务逻辑
        OperateLog operateLog = event.getSource();
        apeOperateLogService.save(operateLog);
    }

    @Async(value = AsyncPoolConfig.TASK_EXECUTOR_NAME)
    @EventListener(classes = LoginLogEvent.class)
    public void handleLoginLogEvent(LoginLogEvent event) {
        //处理日志业务逻辑
        LoginLog loginLog = event.getSource();
        apeLoginLogService.save(loginLog);
    }


}
