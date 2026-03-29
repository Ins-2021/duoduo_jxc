package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.production.CutBundleDTO;
import com.duoduo.jxc.dto.production.CutBundleQuery;
import com.duoduo.jxc.entity.CutBundle;

public interface CutBundleService extends IService<CutBundle> {
    PageResult<CutBundleDTO> pageQuery(CutBundleQuery query);
    CutBundleDTO getDetail(Long id);
    Long create(CutBundleDTO dto);
    void update(CutBundleDTO dto);
    void delete(Long id);
    void updateStatus(Long id, String status);
    void assignProcess(Long id, Long processId);
}
