package com.duoduo.jxc.aspect;

import com.alibaba.fastjson2.JSON;
import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.entity.OperLog;
import com.duoduo.jxc.service.OperLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

/**
 * 操作日志切面处理
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogAspect {
    private static final String DEFAULT_OPERATOR_NAME = "管理员";
    private static final String DEFAULT_STORE_NAME = "衣多多";

    private final OperLogService operLogService;

    /**
     * 配置织入点
     */
    @Pointcut("@annotation(com.duoduo.jxc.annotation.Log)")
    public void logPointCut() {
    }

    /**
     * 环绕通知
     *
     * @param joinPoint 切点
     * @return 结果
     * @throws Throwable 异常
     */
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 执行方法
        Object result = null;
        Throwable error = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            long executeTime = System.currentTimeMillis() - startTime;
            handleLog(joinPoint, result, error, executeTime);
        }
    }

    private void handleLog(ProceedingJoinPoint joinPoint, Object result, Throwable error, long executeTime) {
        try {
            // 获取注解
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Log logAnnotation = method.getAnnotation(Log.class);
            
            if (logAnnotation == null) {
                return;
            }
            
            // 获取 Request
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
            
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = signature.getName();
            
            log.info("========================================= API LOG START =========================================");
            log.info("Module       : {} - {}", logAnnotation.title(), logAnnotation.action());
            log.info("Method       : {}.{}()", className, methodName);
            
            if (request != null) {
                log.info("URL          : {} {}", request.getMethod(), request.getRequestURI());
                log.info("Client IP    : {}", getClientIp(request));
            }
            
            if (logAnnotation.isSaveRequestData()) {
                log.info("Request Args : {}", getRequestParams(joinPoint.getArgs()));
            }
            
            if (error != null) {
                log.error("Exception    : {}", error.getMessage());
            } else if (logAnnotation.isSaveResponseData()) {
                log.info("Response     : {}", JSON.toJSONString(result));
            }
            
            log.info("Cost Time    : {} ms", executeTime);
            log.info("========================================== API LOG END ==========================================");

            saveOperLog(joinPoint, signature, logAnnotation, request, result, error, executeTime);
            
        } catch (Exception e) {
            log.error("==> Error recording API log", e);
        }
    }

    private void saveOperLog(ProceedingJoinPoint joinPoint,
                             MethodSignature signature,
                             Log logAnnotation,
                             HttpServletRequest request,
                             Object result,
                             Throwable error,
                             long executeTime) {
        try {
            OperLog operLog = new OperLog();
            operLog.setCreateTime(LocalDateTime.now());
            operLog.setUpdateTime(LocalDateTime.now());
            operLog.setTitle(logAnnotation.title());
            operLog.setAction(logAnnotation.action());
            operLog.setCostTime(executeTime);
            operLog.setStatus(error == null ? 1 : 0);
            if (error != null) {
                // 截断错误消息，避免超出数据库字段长度限制（500字符）
                String errorMsg = error.getMessage();
                if (errorMsg != null && errorMsg.length() > 500) {
                    errorMsg = errorMsg.substring(0, 497) + "...";
                }
                operLog.setErrorMsg(errorMsg);
            }

            if (request != null) {
                operLog.setRequestMethod(request.getMethod());
                operLog.setRequestUrl(request.getRequestURI());
                operLog.setRequestIp(getClientIp(request));
            }

            if (logAnnotation.isSaveRequestData()) {
                operLog.setRequestParams(getRequestParams(joinPoint.getArgs()));
            }

            if (error == null && logAnnotation.isSaveResponseData()) {
                operLog.setResponseData(JSON.toJSONString(result));
            }

            fillOperatorAndStore(operLog, request);
            operLog.setContent(buildContent(logAnnotation, operLog));

            operLogService.save(operLog);
        } catch (Exception e) {
            log.error("==> Error saving operation log", e);
        }
    }

    private void fillOperatorAndStore(OperLog operLog, HttpServletRequest request) {
        Long operatorId = com.duoduo.jxc.security.SecurityUtils.getUserId();
        String operatorName = com.duoduo.jxc.security.SecurityUtils.getUsername();
        Long storeId = null;
        String storeName = null;

        if (request != null) {
            if (operatorId == null) {
                operatorId = parseLong(request.getHeader("x-user-id"));
            }
            if (!StringUtils.hasText(operatorName)) {
                operatorName = safeText(request.getHeader("x-user-name"));
            }
            storeId = parseLong(request.getHeader("x-store-id"));
            storeName = safeText(request.getHeader("x-store-name"));
        }

        if (!StringUtils.hasText(operatorName)) {
            operatorName = DEFAULT_OPERATOR_NAME;
        }

        if (!StringUtils.hasText(storeName)) {
            storeName = DEFAULT_STORE_NAME;
        }

        operLog.setOperatorId(operatorId);
        operLog.setOperatorName(operatorName);
        operLog.setStoreId(storeId);
        operLog.setStoreName(storeName);
    }

    private String buildContent(Log logAnnotation, OperLog operLog) {
        String base = StringUtils.hasText(logAnnotation.action()) ? logAnnotation.action() : logAnnotation.title();
        String extra = extractBizHint(operLog.getRequestParams());
        String statusSuffix = operLog.getStatus() != null && operLog.getStatus() == 0 ? "（严重警告）" : "";
        if (StringUtils.hasText(extra)) {
            return base + " [" + extra + "]" + statusSuffix;
        }
        return base + statusSuffix;
    }

    private String extractBizHint(String json) {
        if (!StringUtils.hasText(json)) {
            return "";
        }
        try {
            Object obj = JSON.parse(json);
            if (!(obj instanceof Map<?, ?> map)) {
                return "";
            }
            String[] keys = new String[]{"docNo", "doc_no", "templateId", "template_id", "orderId", "order_id"};
            for (String k : keys) {
                Object v = map.get(k);
                if (v != null) {
                    String s = String.valueOf(v);
                    if (StringUtils.hasText(s)) {
                        return s;
                    }
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    private Long parseLong(String s) {
        if (!StringUtils.hasText(s)) {
            return null;
        }
        try {
            return Long.parseLong(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String safeText(String s) {
        return StringUtils.hasText(s) ? s.trim() : null;
    }

    /**
     * 获取请求参数
     */
    private String getRequestParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "";
        }
        
        try {
            for (Object arg : args) {
                if (arg instanceof HttpServletRequest || 
                    arg instanceof HttpServletResponse || 
                    arg instanceof MultipartFile) {
                    continue;
                }
                return JSON.toJSONString(arg);
            }
        } catch (Exception e) {
            log.warn("Failed to parse request args", e);
        }
        return "Cannot parse args";
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}
