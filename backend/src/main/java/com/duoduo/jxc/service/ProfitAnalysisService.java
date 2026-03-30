package com.duoduo.jxc.service;

import com.duoduo.jxc.dto.report.*;

import java.util.List;

/**
 * 利润分析服务
 */
public interface ProfitAnalysisService {

    /**
     * 获取款式利润排行
     *
     * @param query 查询条件
     * @return 款式利润列表
     */
    List<StyleProfitDTO> getStyleProfitRanking(ProfitQueryDTO query);

    /**
     * 获取客户利润排行
     *
     * @param query 查询条件
     * @return 客户利润列表
     */
    List<CustomerProfitDTO> getCustomerProfitRanking(ProfitQueryDTO query);

    /**
     * 获取利润趋势
     *
     * @param query 查询条件
     * @return 利润趋势数据
     */
    ProfitTrendDTO getProfitTrend(ProfitQueryDTO query);

    /**
     * 获取利润汇总
     *
     * @param query 查询条件
     * @return 利润汇总
     */
    ProfitSummaryDTO getProfitSummary(ProfitQueryDTO query);

    /**
     * 获取款式利润详情
     *
     * @param styleId 款式ID
     * @param query   查询条件
     * @return 款式利润详情
     */
    StyleProfitDetailDTO getStyleProfitDetail(Long styleId, ProfitQueryDTO query);
}
