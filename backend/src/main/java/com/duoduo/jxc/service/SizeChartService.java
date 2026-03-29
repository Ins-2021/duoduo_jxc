package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.SizeChartDTO;
import com.duoduo.jxc.entity.SizeChart;

public interface SizeChartService extends IService<SizeChart> {
    
    PageResult<SizeChartDTO> pageQuery(PageQuery query);
    
    SizeChartDTO getDetail(Long id);
    
    Long create(SizeChartDTO dto);
    
    void update(SizeChartDTO dto);
    
    void delete(Long id);
}
