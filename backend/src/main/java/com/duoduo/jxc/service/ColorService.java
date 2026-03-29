package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.ColorDTO;
import com.duoduo.jxc.entity.Color;

public interface ColorService extends IService<Color> {
    
    PageResult<ColorDTO> pageQuery(PageQuery query);
    
    ColorDTO getDetail(Long id);
    
    Long create(ColorDTO dto);
    
    void update(ColorDTO dto);
    
    void delete(Long id);
}
