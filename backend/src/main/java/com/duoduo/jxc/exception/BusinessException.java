package com.duoduo.jxc.exception;

import com.duoduo.jxc.common.BizCode;
import lombok.Getter;

/**
 * 业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(BizCode bizCode) {
        super(bizCode.getMsg());
        this.code = bizCode.getCode();
    }

    public BusinessException(BizCode bizCode, String message) {
        super(message);
        this.code = bizCode.getCode();
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    public BusinessException(BizCode bizCode, Throwable cause) {
        super(bizCode.getMsg(), cause);
        this.code = bizCode.getCode();
    }
}
