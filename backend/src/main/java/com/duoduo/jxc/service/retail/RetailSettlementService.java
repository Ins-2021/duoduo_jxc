package com.duoduo.jxc.service.retail;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.retail.RetailSettlementDTO;
import com.duoduo.jxc.dto.retail.RetailSettlementQuery;

public interface RetailSettlementService {
    
    /**
     * 生成日结单
     * @param dto 日结参数，需包含 storeId 和 settlementDate
     * @return 日结单ID
     */
    Long createDailySettlement(RetailSettlementDTO dto);

    /**
     * 分页查询日结单
     */
    PageResult<RetailSettlementDTO> pageQuery(RetailSettlementQuery query);
}