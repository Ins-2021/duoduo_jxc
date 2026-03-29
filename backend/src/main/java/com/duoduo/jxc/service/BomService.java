package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.bom.BomDTO;
import com.duoduo.jxc.dto.bom.BomItemDTO;
import com.duoduo.jxc.dto.bom.BomProcessDTO;
import com.duoduo.jxc.dto.bom.BomQuery;
import com.duoduo.jxc.entity.Bom;

import java.util.List;

public interface BomService extends IService<Bom> {
    PageResult<BomDTO> pageQuery(BomQuery query);
    BomDTO getDetail(Long id);
    Long create(BomDTO dto);
    void update(BomDTO dto);
    void delete(Long id);
    List<BomItemDTO> getItems(Long bomId);
    void saveItems(Long bomId, List<BomItemDTO> items);
    List<BomProcessDTO> getProcesses(Long bomId);
    void saveProcesses(Long bomId, List<BomProcessDTO> processes);
    List<BomDTO> listByStyleId(Long styleId);
}
