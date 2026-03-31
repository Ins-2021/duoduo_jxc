package com.duoduo.jxc.exception;

import lombok.Getter;

import java.io.Serializable;

/**
 * 业务异常基类
 * <p>
 * 所有业务异常必须继承此类。
 * 符合阿里巴巴Java开发手册 3.1-3.3 异常处理规约：
 * - 使用自定义异常而非通用RuntimeException
 * - 异常包含错误码，便于快速定位问题
 * - 异常消息使用中文，便于业务人员理解
 * - 支持参数化消息（国际化支持）
 * </p>
 *
 * @author duoduo
 * @version 1.0.0
 * @since 2026-04-01
 */
@Getter
public class BizException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     * <p>用于快速定位问题类型</p>
     */
    private final String errorCode;

    /**
     * 错误参数
     * <p>用于国际化消息替换，如：{"name": "张三"} -> "用户[张三]不存在"</p>
     */
    private final transient Object[] params;

    /**
     * 构造业务异常（仅错误码）
     *
     * @param errorCode 错误码
     */
    public BizException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
        this.params = null;
    }

    /**
     * 构造业务异常（错误码+消息）
     *
     * @param errorCode 错误码
     * @param message   错误消息
     */
    public BizException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.params = null;
    }

    /**
     * 构造业务异常（错误码+消息+原始异常）
     *
     * @param errorCode 错误码
     * @param message   错误消息
     * @param cause     原始异常
     */
    public BizException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.params = null;
    }

    /**
     * 构造业务异常（带参数，用于国际化）
     *
     * @param errorCode 错误码
     * @param params    消息参数
     */
    public BizException(String errorCode, Object... params) {
        super(errorCode);
        this.errorCode = errorCode;
        this.params = params;
    }

    /**
     * 构造业务异常（完整参数）
     *
     * @param errorCode 错误码
     * @param message   错误消息
     * @param cause     原始异常
     * @param params    消息参数
     */
    public BizException(String errorCode, String message, Throwable cause, Object... params) {
        super(message, cause);
        this.errorCode = errorCode;
        this.params = params;
    }

    /**
     * 获取格式化后的错误消息
     * <p>如果有参数，使用String.format格式化</p>
     *
     * @return 格式化后的消息
     */
    public String getFormattedMessage() {
        String message = getMessage();
        if (params != null && params.length > 0 && message != null) {
            try {
                return String.format(message, params);
            } catch (Exception e) {
                // 格式化失败，返回原始消息
                return message;
            }
        }
        return message;
    }

    @Override
    public String toString() {
        return "BizException{" +
                "errorCode='" + errorCode + '\'' +
                ", message='" + getMessage() + '\'' +
                ", params=" + java.util.Arrays.toString(params) +
                '}';
    }
}
