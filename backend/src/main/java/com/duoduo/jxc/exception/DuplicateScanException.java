package com.duoduo.jxc.exception;

import com.duoduo.jxc.constant.BizCodeConstant;

/**
 * 重复扫码异常
 * <p>
 * 用于处理扫码防重机制触发的业务异常
 * 包括：重复扫码、操作过于频繁、并发冲突等场景
 * </p>
 *
 * @author duoduo
 * @version 1.0.0
 * @since 2026-04-01
 */
public class DuplicateScanException extends BizException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造重复扫码异常（错误码+消息）
     *
     * @param errorCode 错误码
     * @param message   错误消息
     */
    public DuplicateScanException(String errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * 构造重复扫码异常（错误码+消息+原始异常）
     *
     * @param errorCode 错误码
     * @param message   错误消息
     * @param cause     原始异常
     */
    public DuplicateScanException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * 构造重复扫码异常（带参数）
     *
     * @param errorCode 错误码
     * @param params    消息参数
     */
    public DuplicateScanException(String errorCode, Object... params) {
        super(errorCode, params);
    }

    // ==================== 便捷工厂方法 ====================

    /**
     * 重复扫码
     *
     * @param bundleNo  扎包号
     * @param processId 工序ID
     * @return DuplicateScanException
     */
    public static DuplicateScanException duplicate(String bundleNo, Long processId) {
        return new DuplicateScanException(
            BizCodeConstant.SCAN_DUPLICATE,
            String.format("扎包[%s]的工序[%d]今日已完成计件，请勿重复扫码", bundleNo, processId)
        );
    }

    /**
     * 操作过于频繁
     *
     * @return DuplicateScanException
     */
    public static DuplicateScanException tooFrequent() {
        return new DuplicateScanException(
            BizCodeConstant.SCAN_TOO_FREQUENT,
            "操作过于频繁，请稍后再试"
        );
    }

    /**
     * 并发操作冲突
     *
     * @param resource 资源名称
     * @return DuplicateScanException
     */
    public static DuplicateScanException concurrentConflict(String resource) {
        return new DuplicateScanException(
            BizCodeConstant.CONCURRENT_CONFLICT,
            String.format("[%s]正在被其他用户操作，请稍后再试", resource)
        );
    }

    /**
     * 扎包不存在
     *
     * @param bundleNo 扎包号
     * @return DuplicateScanException
     */
    public static DuplicateScanException bundleNotFound(String bundleNo) {
        return new DuplicateScanException(
            BizCodeConstant.BUNDLE_NOT_FOUND,
            String.format("扎包[%s]不存在", bundleNo)
        );
    }

    /**
     * 工序不存在
     *
     * @param processId 工序ID
     * @return DuplicateScanException
     */
    public static DuplicateScanException processNotFound(Long processId) {
        return new DuplicateScanException(
            BizCodeConstant.PROCESS_NOT_FOUND,
            String.format("工序[%d]不存在", processId)
        );
    }

    /**
     * 工人不存在
     *
     * @param workerId 工人ID
     * @return DuplicateScanException
     */
    public static DuplicateScanException workerNotFound(Long workerId) {
        return new DuplicateScanException(
            BizCodeConstant.WORKER_NOT_FOUND,
            String.format("工人[%d]不存在", workerId)
        );
    }

    /**
     * 扫码数量超过扎包数量
     *
     * @param bundleNo       扎包号
     * @param bundleQuantity 扎包数量
     * @param scanQuantity   扫码数量
     * @return DuplicateScanException
     */
    public static DuplicateScanException quantityExceed(
            String bundleNo, 
            Integer bundleQuantity, 
            Integer scanQuantity) {
        return new DuplicateScanException(
            BizCodeConstant.SCAN_QUANTITY_EXCEED,
            String.format("扎包[%s]扫码数量[%d]超过扎包数量[%d]", 
                bundleNo, scanQuantity, bundleQuantity)
        );
    }

    /**
     * 工序顺序错误
     *
     * @param currentSeq 当前工序顺序
     * @param requiredSeq 要求的工序顺序
     * @return DuplicateScanException
     */
    public static DuplicateScanException processSequenceError(
            Integer currentSeq, 
            Integer requiredSeq) {
        return new DuplicateScanException(
            BizCodeConstant.PROCESS_SEQUENCE_ERROR,
            String.format("工序顺序错误，当前[%d]，要求[%d]", currentSeq, requiredSeq)
        );
    }

    /**
     * 当前工序已完成
     *
     * @param processName 工序名称
     * @return DuplicateScanException
     */
    public static DuplicateScanException processAlreadyCompleted(String processName) {
        return new DuplicateScanException(
            BizCodeConstant.PROCESS_ALREADY_COMPLETED,
            String.format("工序[%s]已完成", processName)
        );
    }

    /**
     * 无权限扫码
     *
     * @param reason 原因
     * @return DuplicateScanException
     */
    public static DuplicateScanException noPermission(String reason) {
        return new DuplicateScanException(
            BizCodeConstant.SCAN_NO_PERMISSION,
            String.format("无权限扫码：%s", reason)
        );
    }
}
