package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.StyleDTO;
import com.duoduo.jxc.entity.Style;

public interface StyleService extends IService<Style> {
    PageResult<StyleDTO> pageQuery(StyleDTO query);
    StyleDTO getDetail(Long id);
    Long create(StyleDTO dto);
    void update(StyleDTO dto);
    void delete(Long id);
}
