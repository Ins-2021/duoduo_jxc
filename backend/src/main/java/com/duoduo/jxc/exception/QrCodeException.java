package com.duoduo.jxc.exception;

import com.duoduo.jxc.constant.BizCodeConstant;

/**
 * 二维码相关异常
 * <p>
 * 用于处理二维码生成、解析、验证过程中出现的业务异常
 * </p>
 *
 * @author duoduo
 * @version 1.0.0
 * @since 2026-04-01
 */
public class QrCodeException extends BizException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造二维码异常（错误码+消息）
     *
     * @param errorCode 错误码
     * @param message   错误消息
     */
    public QrCodeException(String errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * 构造二维码异常（错误码+消息+原始异常）
     *
     * @param errorCode 错误码
     * @param message   错误消息
     * @param cause     原始异常
     */
    public QrCodeException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * 构造二维码异常（带参数）
     *
     * @param errorCode 错误码
     * @param params    消息参数
     */
    public QrCodeException(String errorCode, Object... params) {
        super(errorCode, params);
    }

    // ==================== 便捷工厂方法 ====================

    /**
     * 二维码生成失败
     *
     * @param cause 原始异常
     * @return QrCodeException
     */
    public static QrCodeException generateFailed(Throwable cause) {
        return new QrCodeException(
            BizCodeConstant.QR_GENERATE_ERROR,
            "二维码生成失败",
            cause
        );
    }

    /**
     * 二维码解析失败
     *
     * @param content 二维码内容
     * @return QrCodeException
     */
    public static QrCodeException parseFailed(String content) {
        return new QrCodeException(
            BizCodeConstant.QR_PARSE_ERROR,
            "二维码解析失败，内容无效",
            content
        );
    }

    /**
     * 二维码已过期
     *
     * @param qrCodeNo 二维码编号
     * @return QrCodeException
     */
    public static QrCodeException expired(String qrCodeNo) {
        return new QrCodeException(
            BizCodeConstant.QR_EXPIRED,
            "二维码已过期：" + qrCodeNo
        );
    }

    /**
     * 二维码签名验证失败
     *
     * @return QrCodeException
     */
    public static QrCodeException signatureInvalid() {
        return new QrCodeException(
            BizCodeConstant.QR_SIGNATURE_INVALID,
            "二维码签名验证失败，可能已被篡改"
        );
    }

    /**
     * 二维码内容无效
     *
     * @param reason 原因
     * @return QrCodeException
     */
    public static QrCodeException contentInvalid(String reason) {
        return new QrCodeException(
            BizCodeConstant.QR_CONTENT_INVALID,
            "二维码内容无效：" + reason
        );
    }

    /**
     * 二维码已被禁用
     *
     * @param qrCodeNo 二维码编号
     * @return QrCodeException
     */
    public static QrCodeException disabled(String qrCodeNo) {
        return new QrCodeException(
            BizCodeConstant.QR_DISABLED,
            "二维码已被禁用：" + qrCodeNo
        );
    }

    /**
     * 二维码类型不支持
     *
     * @param type 类型
     * @return QrCodeException
     */
    public static QrCodeException typeNotSupported(String type) {
        return new QrCodeException(
            BizCodeConstant.QR_TYPE_NOT_SUPPORTED,
            "不支持的二维码类型：" + type
        );
    }

    /**
     * 二维码打印失败
     *
     * @param cause 原始异常
     * @return QrCodeException
     */
    public static QrCodeException printFailed(Throwable cause) {
        return new QrCodeException(
            BizCodeConstant.QR_PRINT_ERROR,
            "二维码打印失败",
            cause
        );
    }
}
