package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.BundleDTO;
import com.duoduo.jxc.dto.BundleQuery;
import com.duoduo.jxc.entity.Bundle;

public interface BundleService extends IService<Bundle> {
    PageResult<BundleDTO> pageQuery(BundleQuery query);
    BundleDTO getDetail(Long id);
    Long create(BundleDTO dto);
    void update(BundleDTO dto);
    void delete(Long id);
}
