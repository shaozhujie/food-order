package com.project.movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.movie.domain.RoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: 角色菜单关系mapper
 * @date 2023/8/31 10:56
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    /**
    * @description: 根据角色获取权限
    * @param: role
    * @return:
    * @author shaozhujie
    * @date: 2023/9/8 8:56
    */
    Set<String> getRoleMenusSet(@Param("role") String role);
}
