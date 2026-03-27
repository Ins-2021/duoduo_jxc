package com.duoduo.jxc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.duoduo.jxc.entity.rbac.SysMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    @Select("""
            SELECT DISTINCT m.perms
            FROM jxc_sys_user_role ur
            JOIN jxc_sys_role_menu rm ON ur.role_id = rm.role_id
            JOIN jxc_sys_menu m ON rm.menu_id = m.menu_id
            WHERE ur.user_id = #{userId}
              AND m.deleted = 0
              AND m.status = 1
              AND m.perms IS NOT NULL
              AND m.perms <> ''
            """)
    List<String> selectPermsByUserId(@Param("userId") Long userId);

    @Select("""
            SELECT DISTINCT m.*
            FROM jxc_sys_user_role ur
            JOIN jxc_sys_role_menu rm ON ur.role_id = rm.role_id
            JOIN jxc_sys_menu m ON rm.menu_id = m.menu_id
            WHERE ur.user_id = #{userId}
              AND m.deleted = 0
              AND m.status = 1
              AND m.visible = 1
            ORDER BY m.parent_id ASC, m.order_num ASC
            """)
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);
}
