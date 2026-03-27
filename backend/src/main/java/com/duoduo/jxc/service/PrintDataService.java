package com.duoduo.jxc.service;

import com.duoduo.jxc.dto.print.PrintDataDTO;

public interface PrintDataService {
    PrintDataDTO previewData(Long templateId, String bizId);
}

