package com.duoduo.jxc.constant;

/**
 * 业务异常码常量
 * <p>
 * 错误码格式：MODULE_ERROR_SEQ
 * 示例：QR001 - 二维码模块第1个错误
 * </p>
 * <p>
 * 符合阿里巴巴Java开发手册：
 * - 错误码便于快速定位问题
 * - 分类清晰，便于维护
 * - 中英文消息分离（实际项目中可结合i18n）
 * </p>
 *
 * @author duoduo
 * @version 1.0.0
 * @since 2026-04-01
 */
public final class BizCodeConstant {

    private BizCodeConstant() {
        throw new AssertionError("常量类不应被实例化");
    }

    // ==================== 系统级错误 1xxx ====================

    /**
     * 系统内部错误
     */
    public static final String SYSTEM_ERROR = "SYS001";

    /**
     * 参数校验失败
     */
    public static final String PARAM_INVALID = "SYS002";

    /**
     * 数据不存在
     */
    public static final String DATA_NOT_FOUND = "SYS003";

    /**
     * 数据库操作失败
     */
    public static final String DB_ERROR = "SYS004";

    /**
     * 网络请求失败
     */
    public static final String NETWORK_ERROR = "SYS005";

    /**
     * 服务暂时不可用
     */
    public static final String SERVICE_UNAVAILABLE = "SYS006";

    /**
     * 请求超时
     */
    public static final String REQUEST_TIMEOUT = "SYS007";

    /**
     * 非法操作
     */
    public static final String ILLEGAL_OPERATION = "SYS008";

    // ==================== 二维码模块错误 2xxx ====================

    /**
     * 二维码生成失败
     */
    public static final String QR_GENERATE_ERROR = "QR001";

    /**
     * 二维码解析失败
     */
    public static final String QR_PARSE_ERROR = "QR002";

    /**
     * 二维码已过期
     */
    public static final String QR_EXPIRED = "QR003";

    /**
     * 二维码签名验证失败
     */
    public static final String QR_SIGNATURE_INVALID = "QR004";

    /**
     * 二维码内容无效
     */
    public static final String QR_CONTENT_INVALID = "QR005";

    /**
     * 二维码已被禁用
     */
    public static final String QR_DISABLED = "QR006";

    /**
     * 二维码类型不支持
     */
    public static final String QR_TYPE_NOT_SUPPORTED = "QR007";

    /**
     * 二维码打印失败
     */
    public static final String QR_PRINT_ERROR = "QR008";

    // ==================== 扫码模块错误 3xxx ====================

    /**
     * 重复扫码
     */
    public static final String SCAN_DUPLICATE = "SC001";

    /**
     * 操作过于频繁
     */
    public static final String SCAN_TOO_FREQUENT = "SC002";

    /**
     * 扎包不存在
     */
    public static final String BUNDLE_NOT_FOUND = "SC003";

    /**
     * 扎包状态异常
     */
    public static final String BUNDLE_STATUS_ERROR = "SC004";

    /**
     * 工序不存在
     */
    public static final String PROCESS_NOT_FOUND = "SC005";

    /**
     * 工人不存在
     */
    public static final String WORKER_NOT_FOUND = "SC006";

    /**
     * 并发操作冲突
     */
    public static final String CONCURRENT_CONFLICT = "SC007";

    /**
     * 扫码数量超过扎包数量
     */
    public static final String SCAN_QUANTITY_EXCEED = "SC008";

    /**
     * 工序顺序错误
     */
    public static final String PROCESS_SEQUENCE_ERROR = "SC009";

    /**
     * 当前工序已完成
     */
    public static final String PROCESS_ALREADY_COMPLETED = "SC010";

    /**
     * 无权限扫码
     */
    public static final String SCAN_NO_PERMISSION = "SC011";

    /**
     * 设备信息无效
     */
    public static final String DEVICE_INFO_INVALID = "SC012";

    // ==================== 扎包流转模块错误 4xxx ====================

    /**
     * 流转记录不存在
     */
    public static final String FLOW_NOT_FOUND = "FL001";

    /**
     * 流转状态异常
     */
    public static final String FLOW_STATUS_ERROR = "FL002";

    /**
     * 交接失败
     */
    public static final String TRANSFER_FAILED = "FL003";

    /**
     * 无权限交接
     */
    public static final String TRANSFER_NO_PERMISSION = "FL004";

    /**
     * 扎包已被其他人接收
     */
    public static final String BUNDLE_ALREADY_RECEIVED = "FL005";

    /**
     * 扎包交接超时
     */
    public static final String TRANSFER_TIMEOUT = "FL006";

    /**
     * 扎包流转记录已满
     */
    public static final String FLOW_RECORD_FULL = "FL007";

    // ==================== 打印模块错误 5xxx ====================

    /**
     * 打印失败
     */
    public static final String PRINT_FAILED = "PR001";

    /**
     * 打印机不可用
     */
    public static final String PRINTER_UNAVAILABLE = "PR002";

    /**
     * 打印模板不存在
     */
    public static final String PRINT_TEMPLATE_NOT_FOUND = "PR003";

    /**
     * 标签尺寸不支持
     */
    public static final String LABEL_SIZE_NOT_SUPPORTED = "PR004";

    // ==================== 生产订单模块错误 6xxx ====================

    /**
     * 生产订单不存在
     */
    public static final String ORDER_NOT_FOUND = "OR001";

    /**
     * 生产订单状态异常
     */
    public static final String ORDER_STATUS_ERROR = "OR002";

    /**
     * 生产订单已完成
     */
    public static final String ORDER_ALREADY_COMPLETED = "OR003";

    /**
     * 生产订单已取消
     */
    public static final String ORDER_ALREADY_CANCELLED = "OR004";

    // ==================== 计件工资模块错误 7xxx ====================

    /**
     * 工资计算失败
     */
    public static final String WAGE_CALCULATE_ERROR = "WG001";

    /**
     * 单价不存在
     */
    public static final String PRICE_NOT_FOUND = "WG002";

    /**
     * 工资记录已结算
     */
    public static final String WAGE_ALREADY_SETTLED = "WG003";

    /**
     * 工资记录不存在
     */
    public static final String WAGE_RECORD_NOT_FOUND = "WG004";

    // ==================== 缓存模块错误 8xxx ====================

    /**
     * 缓存操作失败
     */
    public static final String CACHE_ERROR = "CA001";

    /**
     * 获取锁失败
     */
    public static final String LOCK_ACQUIRE_FAILED = "CA002";

    /**
     * 释放锁失败
     */
    public static final String LOCK_RELEASE_FAILED = "CA003";

    /**
     * 缓存键不存在
     */
    public static final String CACHE_KEY_NOT_FOUND = "CA004";

    // ==================== 文件模块错误 9xxx ====================

    /**
     * 文件上传失败
     */
    public static final String FILE_UPLOAD_ERROR = "FI001";

    /**
     * 文件下载失败
     */
    public static final String FILE_DOWNLOAD_ERROR = "FI002";

    /**
     * 文件不存在
     */
    public static final String FILE_NOT_FOUND = "FI003";

    /**
     * 文件格式不支持
     */
    public static final String FILE_FORMAT_NOT_SUPPORTED = "FI004";

    /**
     * 文件大小超过限制
     */
    public static final String FILE_SIZE_EXCEED = "FI005";
}
