package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.inventory.BarcodeRuleDTO;
import com.duoduo.jxc.entity.BarcodeRule;

public interface BarcodeRuleService extends IService<BarcodeRule> {
    PageResult<BarcodeRuleDTO> pageQuery(int pageNum, int pageSize, String keyword, String ruleType);
    BarcodeRuleDTO getDetail(Long id);
    Long create(BarcodeRuleDTO dto);
    void update(BarcodeRuleDTO dto);
    void delete(Long id);
}
