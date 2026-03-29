package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.inventory.BarcodeRuleDTO;
import com.duoduo.jxc.entity.BarcodeRule;
import com.duoduo.jxc.mapper.BarcodeRuleMapper;
import com.duoduo.jxc.service.BarcodeRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BarcodeRuleServiceImpl extends ServiceImpl<BarcodeRuleMapper, BarcodeRule> implements BarcodeRuleService {

    @Override
    public PageResult<BarcodeRuleDTO> pageQuery(int pageNum, int pageSize, String keyword, String ruleType) {
        Page<BarcodeRule> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BarcodeRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(ruleType), BarcodeRule::getRuleType, ruleType)
               .and(StringUtils.hasText(keyword), w -> w.like(BarcodeRule::getRuleName, keyword)
                       .or().like(BarcodeRule::getPrefix, keyword))
               .orderByDesc(BarcodeRule::getCreateTime);
        page(page, wrapper);
        List<BarcodeRuleDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public BarcodeRuleDTO getDetail(Long id) {
        BarcodeRule entity = getById(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(BarcodeRuleDTO dto) {
        BarcodeRule entity = new BarcodeRule();
        BeanUtils.copyProperties(dto, entity);
        if (entity.getSeqLength() == null) entity.setSeqLength(4);
        if (entity.getIsActive() == null) entity.setIsActive(1);
        if (entity.getIsDefault() == null) entity.setIsDefault(0);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        // 如果设为默认规则，取消同类型的其他默认
        if (Integer.valueOf(1).equals(entity.getIsDefault()) && StringUtils.hasText(entity.getRuleType())) {
            clearDefault(entity.getRuleType());
        }
        save(entity);
        return entity.getRuleId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BarcodeRuleDTO dto) {
        BarcodeRule entity = new BarcodeRule();
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        if (Integer.valueOf(1).equals(entity.getIsDefault()) && StringUtils.hasText(entity.getRuleType())) {
            clearDefault(entity.getRuleType());
        }
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    private void clearDefault(String ruleType) {
        LambdaUpdateWrapper<BarcodeRule> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(BarcodeRule::getRuleType, ruleType)
               .eq(BarcodeRule::getIsDefault, 1)
               .set(BarcodeRule::getIsDefault, 0);
        update(wrapper);
    }

    private BarcodeRuleDTO toDTO(BarcodeRule entity) {
        BarcodeRuleDTO dto = new BarcodeRuleDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
