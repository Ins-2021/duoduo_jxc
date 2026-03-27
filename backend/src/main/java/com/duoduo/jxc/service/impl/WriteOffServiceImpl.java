package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.FinanceConverter;
import com.duoduo.jxc.dto.finance.WriteOffDTO;
import com.duoduo.jxc.entity.WriteOff;
import com.duoduo.jxc.mapper.WriteOffMapper;
import com.duoduo.jxc.service.WriteOffService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 核销单Service实现类
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Service
@RequiredArgsConstructor
public class WriteOffServiceImpl extends ServiceImpl<WriteOffMapper, WriteOff> implements WriteOffService {

    private final FinanceConverter converter;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<WriteOffDTO> pageList(PageQuery query) {
        LambdaQueryWrapper<WriteOff> wrapper = new LambdaQueryWrapper<>();
        String keyword = query.getParam("keyword") != null ? query.getParam("keyword").toString() : null;
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(WriteOff::getWriteOffNo, keyword)
                    .or().like(WriteOff::getBillNo, keyword));
        }
        String type = query.getParam("type") != null ? query.getParam("type").toString() : null;
        if (StringUtils.hasText(type)) {
            wrapper.eq(WriteOff::getType, type);
        }
        wrapper.orderByDesc(WriteOff::getCreateTime);

        Page<WriteOff> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<WriteOffDTO> dtoList = page.getRecords().stream()
                .map(converter::toDTO)
                .collect(java.util.stream.Collectors.toList());

        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public WriteOffDTO getById(Long id) {
        WriteOff entity = super.getById(id);
        if (entity == null) {
            return null;
        }
        return converter.toDTO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(WriteOffDTO dto) {
        WriteOff entity = new WriteOff();
        BeanUtils.copyProperties(dto, entity);
        entity.setWriteOffNo(generateWriteOffNo());
        entity.setCreateTime(LocalDateTime.now());
        save(entity);
        return entity.getWriteOffId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    private String generateWriteOffNo() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "HX" + dateStr + randomStr;
    }
}
