package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.wage.PiecePriceDTO;
import com.duoduo.jxc.dto.wage.PiecePriceQuery;
import com.duoduo.jxc.entity.wage.PiecePrice;

public interface PiecePriceService extends IService<PiecePrice> {
    PageResult<PiecePriceDTO> pageQuery(PiecePriceQuery query);
    PiecePriceDTO getDetail(Long id);
    Long create(PiecePriceDTO dto);
    void update(PiecePriceDTO dto);
    void delete(Long id);
}
