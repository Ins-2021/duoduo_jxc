package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.bom.BomDTO;
import com.duoduo.jxc.dto.bom.BomItemDTO;
import com.duoduo.jxc.dto.bom.BomProcessDTO;
import com.duoduo.jxc.dto.bom.BomQuery;
import com.duoduo.jxc.entity.Bom;
import com.duoduo.jxc.entity.BomItem;
import com.duoduo.jxc.entity.BomProcess;
import com.duoduo.jxc.entity.Style;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.BomItemMapper;
import com.duoduo.jxc.mapper.BomMapper;
import com.duoduo.jxc.mapper.BomProcessMapper;
import com.duoduo.jxc.mapper.StyleMapper;
import com.duoduo.jxc.service.BomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BomServiceImpl extends ServiceImpl<BomMapper, Bom> implements BomService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final BomItemMapper bomItemMapper;
    private final BomProcessMapper bomProcessMapper;
    private final StyleMapper styleMapper;

    @Override
    public PageResult<BomDTO> pageQuery(BomQuery query) {
        Page<Bom> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Bom> wrapper = new LambdaQueryWrapper<>();
        Set<Long> matchedStyleIds = resolveMatchedStyleIds(query);
        wrapper.like(StringUtils.hasText(query.getBomNo()), Bom::getBomNo, query.getBomNo())
               .eq(query.getStyleId() != null, Bom::getStyleId, query.getStyleId())
               .eq(query.getStatus() != null, Bom::getStatus, query.getStatus())
               .and(StringUtils.hasText(query.getKeyword()), w ->
                   w.like(Bom::getBomNo, query.getKeyword())
                    .or(matchedStyleIds != null && !matchedStyleIds.isEmpty())
                    .in(matchedStyleIds != null && !matchedStyleIds.isEmpty(), Bom::getStyleId, matchedStyleIds))
               .orderByDesc(Bom::getCreateTime);
        if (query.getStyleId() == null && matchedStyleIds != null) {
            if (matchedStyleIds.isEmpty()) {
                return new PageResult<>(0L, List.of());
            }
            wrapper.in(Bom::getStyleId, matchedStyleIds);
        }
        page(page, wrapper);
        List<BomDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        fillStyleInfo(dtoList);
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public BomDTO getDetail(Long id) {
        Bom entity = getById(id);
        if (entity == null) {
            return null;
        }
        BomDTO dto = toDTO(entity);
        fillStyleInfo(List.of(dto));
        dto.setItems(getItems(id));
        dto.setProcesses(getProcesses(id));
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(BomDTO dto) {
        Bom entity = new Bom();
        BeanUtils.copyProperties(dto, entity);
        entity.setBomNo(generateBomNo());
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        save(entity);
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            saveItems(entity.getBomId(), dto.getItems());
        }
        if (dto.getProcesses() != null && !dto.getProcesses().isEmpty()) {
            saveProcesses(entity.getBomId(), dto.getProcesses());
        }
        return entity.getBomId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BomDTO dto) {
        Bom existing = getById(dto.getBomId());
        if (existing == null) {
            throw new BusinessException(BizCode.BOM_NOT_FOUND);
        }
        Bom entity = new Bom();
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Bom existing = getById(id);
        if (existing == null) {
            throw new BusinessException(BizCode.BOM_NOT_FOUND);
        }
        LambdaQueryWrapper<BomItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(BomItem::getBomId, id);
        bomItemMapper.delete(itemWrapper);
        LambdaQueryWrapper<BomProcess> processWrapper = new LambdaQueryWrapper<>();
        processWrapper.eq(BomProcess::getBomId, id);
        bomProcessMapper.delete(processWrapper);
        removeById(id);
    }

    @Override
    public List<BomItemDTO> getItems(Long bomId) {
        LambdaQueryWrapper<BomItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BomItem::getBomId, bomId)
               .orderByAsc(BomItem::getSortOrder);
        List<BomItem> items = bomItemMapper.selectList(wrapper);
        return items.stream().map(this::toItemDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveItems(Long bomId, List<BomItemDTO> items) {
        LambdaQueryWrapper<BomItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BomItem::getBomId, bomId);
        bomItemMapper.delete(wrapper);
        if (items != null && !items.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                BomItemDTO dto = items.get(i);
                BomItem entity = new BomItem();
                BeanUtils.copyProperties(dto, entity);
                entity.setItemId(null);
                entity.setBomId(bomId);
                if (entity.getSortOrder() == null) {
                    entity.setSortOrder(i + 1);
                }
                bomItemMapper.insert(entity);
            }
        }
    }

    @Override
    public List<BomProcessDTO> getProcesses(Long bomId) {
        LambdaQueryWrapper<BomProcess> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BomProcess::getBomId, bomId)
               .orderByAsc(BomProcess::getSequence);
        List<BomProcess> processes = bomProcessMapper.selectList(wrapper);
        return processes.stream().map(this::toProcessDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveProcesses(Long bomId, List<BomProcessDTO> processes) {
        LambdaQueryWrapper<BomProcess> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BomProcess::getBomId, bomId);
        bomProcessMapper.delete(wrapper);
        if (processes != null && !processes.isEmpty()) {
            for (int i = 0; i < processes.size(); i++) {
                BomProcessDTO dto = processes.get(i);
                BomProcess entity = new BomProcess();
                BeanUtils.copyProperties(dto, entity);
                entity.setId(null);
                entity.setBomId(bomId);
                if (entity.getSequence() == null) {
                    entity.setSequence(i + 1);
                }
                bomProcessMapper.insert(entity);
            }
        }
    }

    @Override
    public List<BomDTO> listByStyleId(Long styleId) {
        LambdaQueryWrapper<Bom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bom::getStyleId, styleId)
               .orderByDesc(Bom::getCreateTime);
        List<Bom> list = list(wrapper);
        List<BomDTO> dtoList = list.stream().map(this::toDTO).collect(Collectors.toList());
        fillStyleInfo(dtoList);
        return dtoList;
    }

    private BomDTO toDTO(Bom entity) {
        BomDTO dto = new BomDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private Set<Long> resolveMatchedStyleIds(BomQuery query) {
        if (query.getStyleId() != null) {
            return null;
        }
        boolean hasStyleCode = StringUtils.hasText(query.getStyleCode());
        boolean hasKeyword = StringUtils.hasText(query.getKeyword());
        if (!hasStyleCode && !hasKeyword) {
            return null;
        }
        LambdaQueryWrapper<Style> styleWrapper = new LambdaQueryWrapper<>();
        if (hasStyleCode) {
            styleWrapper.like(Style::getStyleNo, query.getStyleCode());
        }
        if (hasKeyword) {
            styleWrapper.and(w -> w.like(Style::getStyleNo, query.getKeyword())
                .or()
                .like(Style::getStyleName, query.getKeyword()));
        }
        return styleMapper.selectList(styleWrapper).stream()
            .map(Style::getStyleId)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void fillStyleInfo(List<BomDTO> dtoList) {
        Set<Long> styleIds = dtoList.stream()
            .map(BomDTO::getStyleId)
            .filter(id -> id != null)
            .collect(Collectors.toSet());
        if (styleIds.isEmpty()) {
            return;
        }
        Map<Long, Style> styleMap = styleMapper.selectBatchIds(styleIds).stream()
            .collect(Collectors.toMap(Style::getStyleId, style -> style));
        dtoList.forEach(dto -> {
            Style style = styleMap.get(dto.getStyleId());
            if (style != null) {
                dto.setStyleCode(style.getStyleNo());
                dto.setStyleName(style.getStyleName());
            }
        });
    }

    private BomItemDTO toItemDTO(BomItem entity) {
        BomItemDTO dto = new BomItemDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private BomProcessDTO toProcessDTO(BomProcess entity) {
        BomProcessDTO dto = new BomProcessDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateBomNo() {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "BOM" + dateStr + randomStr;
    }
}
