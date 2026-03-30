package com.duoduo.jxc.dto.report;

import lombok.Data;
import java.util.List;

/**
 * 款式利润详情DTO
 */
@Data
public class StyleProfitDetailDTO {

    private StyleProfitDTO summary;
    private List<ColorProfitDTO> byColor;   // 按颜色分析
    private List<SizeProfitDTO> bySize;     // 按尺码分析
    private List<MonthlyProfitDTO> byMonth; // 按月度趋势
}
