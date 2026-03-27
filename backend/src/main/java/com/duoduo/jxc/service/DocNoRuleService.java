package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.dto.system.DocNoRuleDTO;
import com.duoduo.jxc.entity.DocNoRule;

/**
 * 单号规则Service接口
 *
 * @author duoduo
 * @date 2026-03-25
 */
public interface DocNoRuleService extends IService<DocNoRule> {
    /**
     * 生成单据编号
     *
     * @param docType 单据类型
     * @return 单据编号
     */
    String generateDocNo(String docType);
}
