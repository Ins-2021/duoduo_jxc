package com.duoduo.jxc.converter;

import com.duoduo.jxc.dto.system.DocNoRuleDTO;
import com.duoduo.jxc.entity.DocNoRule;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 单号规则转换器
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Mapper(componentModel = "spring")
public interface DocNoRuleConverter {
    DocNoRuleConverter INSTANCE = Mappers.getMapper(DocNoRuleConverter.class);

    DocNoRule toEntity(DocNoRuleDTO dto);
    DocNoRuleDTO toDTO(DocNoRule entity);
}
