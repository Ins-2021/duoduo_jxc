package com.duoduo.jxc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.duoduo.jxc.entity.rbac.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    @Select("""
            SELECT DISTINCT r.*
            FROM jxc_sys_user_role ur
            JOIN jxc_sys_role r ON ur.role_id = r.role_id
            WHERE ur.user_id = #{userId}
              AND r.deleted = 0
              AND r.status = 1
            ORDER BY r.role_id ASC
            """)
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);
}
