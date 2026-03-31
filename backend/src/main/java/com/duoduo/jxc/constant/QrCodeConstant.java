package com.duoduo.jxc.constant;

/**
 * 二维码相关常量
 * <p>
 * 集中管理所有二维码相关的常量定义，避免魔法数字散落代码中。
 * 符合阿里巴巴Java开发手册 5.1 常量定义规约。
 * </p>
 *
 * @author duoduo
 * @version 1.0.0
 * @since 2026-04-01
 */
public final class QrCodeConstant {

    /**
     * 私有构造函数，防止实例化
     * 符合阿里巴巴规范：常量类不应被实例化
     */
    private QrCodeConstant() {
        throw new AssertionError("常量类不应被实例化");
    }

    // ==================== 二维码配置 ====================

    /**
     * 默认二维码尺寸（像素）
     */
    public static final int DEFAULT_QR_SIZE = 300;

    /**
     * 二维码过期时间（小时）- 默认30天
     */
    public static final int QR_EXPIRE_HOURS = 720;

    /**
     * 二维码版本号
     */
    public static final String QR_VERSION = "1.0";

    /**
     * 二维码类型：扎包
     */
    public static final String QR_TYPE_BUNDLE = "BUNDLE";

    /**
     * 二维码类型：SKU
     */
    public static final String QR_TYPE_SKU = "SKU";

    /**
     * 二维码类型：库位
     */
    public static final String QR_TYPE_LOCATION = "LOCATION";

    /**
     * 二维码类型：订单
     */
    public static final String QR_TYPE_ORDER = "ORDER";

    // ==================== 并发控制配置 ====================

    /**
     * 分布式锁过期时间（秒）
     * <p>
     * 锁在30秒后自动释放，防止死锁
     * </p>
     */
    public static final int LOCK_EXPIRE_SECONDS = 30;

    /**
     * 锁等待超时时间（秒）
     * <p>
     * 最多等待10秒获取锁
     * </p>
     */
    public static final int LOCK_WAIT_TIMEOUT_SECONDS = 10;

    /**
     * 扫码标记缓存时间（秒）- 24小时
     * <p>
     * 用于防重检查，缓存24小时
     * </p>
     */
    public static final int SCAN_MARK_EXPIRE_SECONDS = 86_400;

    /**
     * 扎包状态锁过期时间（秒）
     */
    public static final int BUNDLE_LOCK_EXPIRE_SECONDS = 30;

    // ==================== Redis Key 前缀 ====================

    /**
     * 扫码锁前缀
     * <p>
     * 格式：scan:lock:{bundleNo}:{workerId}
     * </p>
     */
    public static final String REDIS_KEY_SCAN_LOCK_PREFIX = "scan:lock:";

    /**
     * 扫码标记前缀
     * <p>
     * 格式：scan:mark:{date}:{bundleId}:{workerId}:{processId}
     * </p>
     */
    public static final String REDIS_KEY_SCAN_MARK_PREFIX = "scan:mark:";

    /**
     * 扎包状态锁前缀
     * <p>
     * 格式：bundle:lock:{bundleId}
     * </p>
     */
    public static final String REDIS_KEY_BUNDLE_LOCK_PREFIX = "bundle:lock:";

    // ==================== 业务限制 ====================

    /**
     * 最大扫码间隔（秒）- 防止重复扫码
     * <p>
     * 同一工人同一扎包同一工序，30秒内不能重复扫码
     * </p>
     */
    public static final int MAX_SCAN_INTERVAL_SECONDS = 30;

    /**
     * 最小计件数量
     */
    public static final int MIN_PIECE_QUANTITY = 1;

    /**
     * 最大计件数量
     */
    public static final int MAX_PIECE_QUANTITY = 9999;

    /**
     * 默认每页记录数
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 最大每页记录数
     */
    public static final int MAX_PAGE_SIZE = 100;

    // ==================== 打印配置 ====================

    /**
     * 默认标签尺寸
     */
    public static final String DEFAULT_LABEL_SIZE = "80x80";

    /**
     * 条形码高度（像素）
     */
    public static final int BARCODE_HEIGHT = 40;

    /**
     * 条形码最小宽度
     */
    public static final int BARCODE_MIN_WIDTH = 1;

    /**
     * 条形码最大宽度
     */
    public static final int BARCODE_MAX_WIDTH = 2;

    /**
     * 打印类型：二维码
     */
    public static final String PRINT_TYPE_QRCODE = "qrcode";

    /**
     * 打印类型：条形码
     */
    public static final String PRINT_TYPE_BARCODE = "barcode";

    /**
     * 打印类型：两者
     */
    public static final String PRINT_TYPE_BOTH = "both";

    // ==================== 二维码签名配置 ====================

    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5";

    /**
     * 签名长度
     */
    public static final int SIGNATURE_LENGTH = 16;

    /**
     * 是否启用签名验证
     */
    public static final boolean ENABLE_SIGNATURE = true;

    // ==================== 图片格式 ====================

    /**
     * 二维码图片格式
     */
    public static final String QR_IMAGE_FORMAT = "PNG";

    /**
     * 二维码内容字符集
     */
    public static final String QR_CHARSET = "UTF-8";

    /**
     * 二维码边距
     */
    public static final int QR_MARGIN = 1;
}
