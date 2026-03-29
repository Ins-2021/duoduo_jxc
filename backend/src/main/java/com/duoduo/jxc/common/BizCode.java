package com.duoduo.jxc.common;

import lombok.Getter;

/**
 * 统一业务响应码枚举
 * <p>
 * 前端可根据 code 值做差异化处理（如 401 跳转登录、403 提示无权限等）。
 * 使用方式: throw new BusinessException(BizCode.PRODUCT_NOT_FOUND);
 */
@Getter
public enum BizCode {

    // ---- 通用 10xxx ----
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    DUPLICATE(409, "数据重复"),
    SYSTEM_ERROR(500, "系统异常，请稍后重试"),

    // ---- 认证 20xxx ----
    USER_NOT_FOUND(20001, "用户不存在"),
    USER_ALREADY_EXISTS(20007, "账号已存在"),
    USER_DISABLED(20002, "用户已停用"),
    TOKEN_INVALID(20003, "Token无效或已过期"),
    TOKEN_EMPTY(20004, "Token不能为空"),
    PASSWORD_ERROR(20005, "密码错误"),
    OLD_PASSWORD_ERROR(20006, "原密码错误"),

    // ---- 商品 30xxx ----
    CATEGORY_NOT_FOUND(30001, "分类不存在"),
    CATEGORY_HAS_CHILDREN(30002, "存在子分类，不能删除"),
    CATEGORY_HAS_PRODUCTS(30003, "该分类下存在商品，无法删除"),
    CATEGORY_NAME_DUPLICATE(30004, "同级分类下已存在该名称"),
    SPU_NOT_FOUND(30005, "商品不存在"),
    SPU_NAME_SPEC_DUPLICATE(30010, "商品名称及规格已存在"),
    SKU_CODE_DUPLICATE(30006, "条形码已存在"),
    SKU_CODE_IN_PRODUCT_DUPLICATE(30009, "条形码在当前商品中存在重复"),
    ATTRIBUTE_NOT_FOUND(30007, "属性不存在"),
    ATTRIBUTE_OPTION_NOT_FOUND(30013, "选项不存在"),
    ATTRIBUTE_OPTION_DUPLICATE(30008, "该选项已存在"),
    SPEC_IN_PRODUCT_DUPLICATE(30011, "规格在当前商品中存在重复"),

    // ---- 销售 40xxx ----
    SALES_ORDER_NOT_FOUND(40001, "销售单不存在"),
    SALES_ORDER_CANNOT_MODIFY(40002, "已审核的销售单不允许修改"),
    SALES_ORDER_CANNOT_DELETE(40003, "已审核的销售单不允许删除"),
    SALES_ORDER_CANNOT_RETURN(40004, "销售预订单不支持直接退货"),
    SALES_RETURN_ORDER_NOT_FOUND(40005, "销售退货单不存在"),
    SALES_RETURN_CANNOT_MODIFY(40006, "已审核的销售退货单不允许修改"),
    SALES_RETURN_CANNOT_DELETE(40007, "已审核的销售退货单不允许删除"),
    SALES_RETURN_CUSTOMER_MISMATCH(40008, "退货客户必须与原销售单一致"),
    SALES_RETURN_ITEM_DUPLICATE(40009, "同一销售明细不能重复退货"),
    SALES_RETURN_QTY_EXCEEDED(40010, "退货数量超过可退数量"),
    SALES_RETURN_NO_SOURCE(40011, "请选择原销售单"),
    SALES_RETURN_NO_ITEMS(40012, "请至少选择一条退货明细"),
    SALES_ORDER_NOT_AUDITED(40013, "只有已审核的单据才能反审核"),

    // ---- 采购 50xxx ----
    PURCHASE_ORDER_NOT_FOUND(50001, "采购单不存在"),
    ORDER_NOT_DRAFT(50002, "只能提交草稿状态的订单"),
    PURCHASE_ORDER_CANNOT_MODIFY(50003, "已审核的采购单不允许修改"),
    PURCHASE_ORDER_CANNOT_DELETE(50004, "已审核的采购单不允许删除"),

    // ---- 销售补充 45xxx ----
    BOOKING_ORDER_INVALID(45001, "无效的预订单"),
    BOOKING_DELIVERY_QTY_EXCEED(45002, "发货数量超过未转数量"),
    BOOKING_ORDER_STATUS_ERROR(45003, "预订单状态不允许此操作"),
    INVENTORY_LOCK_FAILED(45004, "库存锁定失败，可用库存不足"),
    INVENTORY_DEDUCT_FAILED(45005, "库存扣减失败，可用库存不足"),
    INVENTORY_UNLOCK_FAILED(45006, "库存解锁失败，锁定数量不足"),
    BOOKING_DETAIL_NOT_FOUND(45007, "预订单明细不存在"),
    BOOKING_DELIVERY_EMPTY(45008, "发货明细不能为空"),
    BOOKING_DELIVERY_QTY_INVALID(45009, "发货数量必须大于0"),

    // ---- 库存 60xxx ----
    TRANSFER_ORDER_NOT_FOUND(60001, "调拨单不存在"),
    TRANSFER_ORDER_CANNOT_APPROVE(60002, "只能审核待审核状态的调拨单"),
    TRANSFER_ORDER_CANNOT_REJECT(60003, "只能驳回待审核状态的调拨单"),
    ASSEMBLY_ORDER_NOT_FOUND(60004, "组装拆卸单不存在"),
    ASSEMBLY_ORDER_CANNOT_APPROVE(60005, "只能审核待审核状态的组装拆卸单"),
    STOCK_INSUFFICIENT(60006, "库存不足"),
    STOCK_IN_OUT_NOT_FOUND(60007, "出入库单不存在"),
    STOCK_IN_OUT_CANNOT_APPROVE(60008, "只能审核待审核状态的出入库单"),
    INVENTORY_CHECK_NOT_FOUND(60009, "盘点单不存在"),
    TRANSFER_ORDER_WAREHOUSE_CONFLICT(60010, "调出仓与调入仓不能相同"),
    TRANSFER_ORDER_CANNOT_MODIFY(60011, "已审核的调拨单不允许修改"),
    TRANSFER_ORDER_CANNOT_DELETE(60012, "已审核的调拨单不允许删除"),
    ASSEMBLY_ORDER_CANNOT_MODIFY(60013, "已审核的组装拆卸单不允许修改"),
    ASSEMBLY_ORDER_CANNOT_DELETE(60014, "已审核的组装拆卸单不允许删除"),
    STOCK_IN_OUT_CANNOT_MODIFY(60015, "已审核的出入库单不允许修改"),
    STOCK_IN_OUT_CANNOT_DELETE(60016, "已审核的出入库单不允许删除"),
    INVENTORY_CHECK_CANNOT_MODIFY(60017, "已完成的盘点单不允许修改"),
    INVENTORY_CHECK_CANNOT_DELETE(60018, "已完成的盘点单不允许删除"),

    // ---- 财务 70xxx ----
    PAYMENT_ORDER_NOT_FOUND(70001, "付款单不存在"),
    RECEIPT_ORDER_NOT_FOUND(70002, "收款单不存在"),
    INCOME_EXPENSE_NOT_FOUND(70003, "收支单不存在"),

    // ---- 财务补充 72xxx ----
    RECEIVABLE_NOT_FOUND(72001, "应收账款不存在"),
    RECEIVABLE_AMOUNT_MISMATCH(72003, "核销金额不能大于剩余金额"),
    RECEIVABLE_SOURCE_SAVE_FAILED(72005, "应收来源明细保存失败"),
    BOOKING_LOCK_STOCK_FAILED(72006, "预订单库存锁定失败"),
    SETTLEMENT_DATE_EMPTY(72007, "结算日期不能为空"),
    SETTLEMENT_ALREADY_EXISTS(72008, "该日期已生成日结单"),
    SETTLEMENT_NO_ORDERS(72009, "该日期没有可结算的零售单"),

    // ---- 系统 80xxx ----
    ROLE_NOT_FOUND(80001, "角色不存在"),
    ROLE_KEY_DUPLICATE(80002, "角色标识已存在"),
    MENU_NOT_FOUND(80003, "菜单不存在"),

    // ---- 工作流 85xxx ----
    WORKFLOW_KEY_DUPLICATE(85001, "流程Key已存在"),
    WORKFLOW_MODEL_NOT_FOUND(85002, "流程模型不存在"),
    WORKFLOW_NO_APPROVAL_NODE(85003, "流程中未找到审批节点"),
    WORKFLOW_NO_BINDING(85004, "该业务类型未绑定可用流程"),
    WORKFLOW_CANNOT_REJECT_TO_PREV(85005, "无法驳回到上一节点"),
    WORKFLOW_CANNOT_FIND_FIRST_NODE(85006, "无法定位首个审批节点"),
    WORKFLOW_INSTANCE_NOT_FOUND(85007, "流程实例不存在"),
    WORKFLOW_DIAGRAM_NOT_FOUND(85008, "流程图不存在"),
    WORKFLOW_NO_APPROVER(85009, "审批节点未配置审批人"),
    WORKFLOW_TASK_NOT_FOUND(85010, "任务不存在或无权限处理"),
    WORKFLOW_TASK_ALREADY_CLAIMED(85011, "任务已被他人签收"),

    // ---- 文件 90xxx ----
    FILE_EMPTY(90001, "文件不能为空"),
    FILE_SIZE_EXCEEDED(90002, "文件大小超限"),
    FILE_PATH_INVALID(90003, "非法文件路径"),
    FILE_SAVE_FAILED(90004, "文件保存失败"),
    FILE_ID_EMPTY(90005, "fileId 不能为空"),
    FILE_NOT_FOUND(90006, "文件不存在"),

    // ---- 打印 95xxx ----
    PRINT_TEMPLATE_NOT_FOUND(95001, "模板不存在"),
    PRINT_TEMPLATE_ID_EMPTY(95002, "templateId 不能为空"),

    // ---- BOM 46xxx ----
    BOM_NOT_FOUND(46001, "BOM不存在"),
    BOM_ITEM_NOT_FOUND(46002, "BOM明细不存在"),
    BOM_PROCESS_NOT_FOUND(46003, "BOM工序不存在"),
    BOM_HAS_ITEMS(46004, "该BOM存在明细，不能删除"),
    BOM_HAS_PROCESSES(46005, "该BOM存在工序，不能删除"),
    ;

    private final int code;
    private final String msg;

    BizCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
