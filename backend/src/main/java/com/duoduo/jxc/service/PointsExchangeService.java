package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.sales.PointsExchangeDTO;
import com.duoduo.jxc.entity.PointsExchange;

public interface PointsExchangeService extends IService<PointsExchange> {

    PageResult<PointsExchangeDTO> pageQuery(int pageNum, int pageSize, String keyword, String startDate, String endDate);

    Long create(PointsExchangeDTO dto);

    void confirm(Long exchangeId);

    void delete(Long exchangeId);

    PointsExchangeDTO getDetail(Long exchangeId);
}
