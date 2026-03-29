package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.FirstArticleConfirmationDTO;
import com.duoduo.jxc.dto.FirstArticleConfirmationQuery;
import com.duoduo.jxc.entity.FirstArticleConfirmation;

public interface FirstArticleConfirmationService extends IService<FirstArticleConfirmation> {
    PageResult<FirstArticleConfirmationDTO> pageQuery(FirstArticleConfirmationQuery query);
    FirstArticleConfirmationDTO getDetail(Long id);
    Long create(FirstArticleConfirmationDTO dto);
    void update(FirstArticleConfirmationDTO dto);
    void delete(Long id);
}
