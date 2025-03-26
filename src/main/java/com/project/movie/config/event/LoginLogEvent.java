package com.project.movie.config.event;

import com.project.movie.domain.LoginLog;
import org.springframework.context.ApplicationEvent;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: 登陆日志监听类
 * @date 2023/9/23 10:06
 */
public class LoginLogEvent extends ApplicationEvent {

    private LoginLog source;

    public LoginLogEvent(LoginLog source) {
        super(source);
        this.source = source;
    }

    @Override
    public LoginLog getSource() {
        return source;
    }

    public void setSource(LoginLog source) {
        this.source = source;
    }
}
