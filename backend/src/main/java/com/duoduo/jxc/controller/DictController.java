package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.enums.CommonStatusEnum;
import com.duoduo.jxc.enums.DictEnum;
import com.duoduo.jxc.dto.common.DictItemDTO;
import com.duoduo.jxc.enums.FinanceAccountStatusEnum;
import com.duoduo.jxc.enums.OutStockStatusEnum;
import com.duoduo.jxc.enums.ProductStatusEnum;
import com.duoduo.jxc.enums.PurchaseOrderStatusEnum;
import com.duoduo.jxc.enums.PurchaseOrderTypeEnum;
import com.duoduo.jxc.enums.SalesOrderStatusEnum;
import com.duoduo.jxc.enums.SalesOrderTypeEnum;
import com.duoduo.jxc.enums.SettleStatusEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/dict")
public class DictController {

    @GetMapping("/{key}")
    public Result<List<DictItemDTO>> get(@PathVariable String key) {
        return Result.success(switch (key) {
            case "common_status" -> toOptions(CommonStatusEnum.values());
            case "product_status" -> toOptions(ProductStatusEnum.values());
            case "finance_account_status" -> toOptions(FinanceAccountStatusEnum.values());
            case "sales_order_status" -> toOptions(SalesOrderStatusEnum.values());
            case "sales_order_type" -> toOptions(SalesOrderTypeEnum.values());
            case "purchase_order_status" -> toOptions(PurchaseOrderStatusEnum.values());
            case "purchase_order_type" -> toOptions(PurchaseOrderTypeEnum.values());
            case "settle_status" -> toOptions(SettleStatusEnum.values());
            case "out_stock_status" -> toOptions(OutStockStatusEnum.values());
            case "wf_model_status" -> List.of(
                    new DictItemDTO("draft", "草稿"),
                    new DictItemDTO("published", "已发布")
            );
            case "wf_instance_status" -> List.of(
                    new DictItemDTO("running", "运行中"),
                    new DictItemDTO("finished", "已结束")
            );
            case "wf_task_status" -> List.of(
                    new DictItemDTO("todo", "待办")
            );
            default -> List.of();
        });
    }

    private static <E extends Enum<E> & DictEnum> List<DictItemDTO> toOptions(E[] values) {
        return Arrays.stream(values).map(v -> new DictItemDTO(String.valueOf(v.getCode()), v.getLabel())).toList();
    }
}
