package com.duoduo.jxc.security;

import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import com.duoduo.jxc.annotation.DataScope;
import com.duoduo.jxc.dto.rbac.CurrentUserResponse;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class CustomDataPermissionHandler implements DataPermissionHandler {

    // 缓存方法注解
    private final ConcurrentMap<String, DataScope> dataScopeCache = new ConcurrentHashMap<>();

    @Override
    public Expression getSqlSegment(Expression where, String mappedStatementId) {
        try {
            String sqlString = DataScopeContextHolder.getSql();
            if (!StringUtils.hasText(sqlString)) {
                return where;
            }

            Expression dataScopeExpression = CCJSqlParserUtil.parseCondExpression(sqlString);
            if (where == null) {
                return dataScopeExpression;
            }
            return new AndExpression(where, new net.sf.jsqlparser.expression.Parenthesis(dataScopeExpression));
        } catch (Exception e) {
            log.error("数据权限处理失败", e);
            return where;
        }
    }
}