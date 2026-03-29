package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.StyleColorwayDTO;
import com.duoduo.jxc.entity.StyleColorway;

public interface StyleColorwayService extends IService<StyleColorway> {
    PageResult<StyleColorwayDTO> pageQuery(StyleColorwayDTO query);
    StyleColorwayDTO getDetail(Long id);
    Long create(StyleColorwayDTO dto);
    void update(StyleColorwayDTO dto);
    void delete(Long id);
}
