package com.duoduo.jxc.security;

import com.duoduo.jxc.annotation.DataScope;
import com.duoduo.jxc.entity.SysUser;
import com.duoduo.jxc.entity.rbac.SysRole;
import com.duoduo.jxc.mapper.SysUserMapper;
import com.duoduo.jxc.mapper.SysRoleMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Aspect
@Component
public class DataScopeAspect {

    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Autowired
    private SysRoleMapper sysRoleMapper;

    // 数据权限过滤关键字
    public static final String DATA_SCOPE_ALL = "1";
    public static final String DATA_SCOPE_CUSTOM = "2";
    public static final String DATA_SCOPE_DEPT = "3";
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";
    public static final String DATA_SCOPE_SELF = "5";

    @Before("@annotation(dataScope)")
    public void doBefore(JoinPoint point, DataScope dataScope) throws Throwable {
        clearDataScope();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return;
        }

        Long userId = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            userId = jwt.getClaim("userId");
        }
        if (userId == null) {
            return;
        }

        // admin has all permissions
        if (userId == 1L) {
            return;
        }

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return;
        }

        List<SysRole> roles = sysRoleMapper.selectRolesByUserId(userId);
        StringBuilder sqlString = new StringBuilder();

        for (SysRole role : roles) {
            if (role.getStatus() != null && role.getStatus() != 1) {
                    continue;
                }
            String ds = role.getDataScope();
            if (DATA_SCOPE_ALL.equals(ds)) {
                sqlString = new StringBuilder();
                break;
            } else if (DATA_SCOPE_CUSTOM.equals(ds)) {
                sqlString.append(String.format(
                        " OR %s.dept_id IN ( SELECT dept_id FROM jxc_sys_role_dept WHERE role_id = %d ) ",
                        dataScope.deptAlias(), role.getRoleId()));
            } else if (DATA_SCOPE_DEPT.equals(ds)) {
                sqlString.append(String.format(" OR %s.dept_id = %d ",
                        dataScope.deptAlias(), user.getDeptId()));
            } else if (DATA_SCOPE_DEPT_AND_CHILD.equals(ds)) {
                sqlString.append(String.format(
                        " OR %s.dept_id IN ( SELECT dept_id FROM jxc_sys_dept WHERE dept_id = %d or ancestors LIKE '%%,%d,%%' ) ",
                        dataScope.deptAlias(), user.getDeptId(), user.getDeptId()));
            } else if (DATA_SCOPE_SELF.equals(ds)) {
                if (StringUtils.hasText(dataScope.userAlias())) {
                    sqlString.append(String.format(" OR %s.user_id = %d ",
                            dataScope.userAlias(), user.getUserId()));
                } else {
                    // default user id field
                    sqlString.append(String.format(" OR %s.dept_id IS NULL ",
                            dataScope.deptAlias()));
                }
            }
        }

        if (StringUtils.hasText(sqlString.toString())) {
            DataScopeContextHolder.setSql("(" + sqlString.substring(4) + ")");
        } else {
            // No roles or all permissions? Wait, if sqlString is empty and it wasn't broken by DATA_SCOPE_ALL, then what?
            // Actually, if sqlString is empty after loop and not admin, we might restrict completely if no roles.
            if (roles.isEmpty() || sqlString.length() == 0) {
                 // if DATA_SCOPE_ALL matched, sqlString is empty but loop breaks.
                 // let's just leave it empty if DATA_SCOPE_ALL.
                 boolean hasAll = roles.stream().anyMatch(r -> DATA_SCOPE_ALL.equals(r.getDataScope()));
                 if (!hasAll) {
                     DataScopeContextHolder.setSql("1 = 0");
                 }
            }
        }
    }

    @After("@annotation(dataScope)")
    public void doAfter(JoinPoint point, DataScope dataScope) {
        clearDataScope();
    }

    private void clearDataScope() {
        DataScopeContextHolder.clear();
    }
}