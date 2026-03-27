package com.duoduo.jxc.security;

public class DataScopeContextHolder {
    private static final ThreadLocal<String> DATA_SCOPE_SQL = new ThreadLocal<>();

    public static void setSql(String sql) {
        DATA_SCOPE_SQL.set(sql);
    }

    public static String getSql() {
        return DATA_SCOPE_SQL.get();
    }

    public static void clear() {
        DATA_SCOPE_SQL.remove();
    }
}