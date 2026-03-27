package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.printtemplate.PrintTemplateDTO;
import com.duoduo.jxc.dto.printtemplate.PrintTemplateQuery;
import com.duoduo.jxc.dto.printtemplate.PrintTemplateSettingsDTO;
import com.duoduo.jxc.entity.PrintTemplate;
import com.duoduo.jxc.entity.SysConfig;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.PrintTemplateMapper;
import com.duoduo.jxc.mapper.SysConfigMapper;
import com.duoduo.jxc.service.PrintTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrintTemplateServiceImpl extends ServiceImpl<PrintTemplateMapper, PrintTemplate> implements PrintTemplateService {

    private static final String KEY_PRINT_NO_DIALOG = "printNoDialog";

    private final SysConfigMapper sysConfigMapper;

    @Override
    public PageResult<PrintTemplateDTO> pageQuery(PrintTemplateQuery query) {
        Page<PrintTemplate> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<PrintTemplate> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getTemplateName())) {
            wrapper.like(PrintTemplate::getTemplateName, query.getTemplateName());
        }
        if (StringUtils.hasText(query.getBizType())) {
            wrapper.eq(PrintTemplate::getBizType, query.getBizType());
        }
        if (query.getShowDisabled() == null || !query.getShowDisabled()) {
            wrapper.eq(PrintTemplate::getEnabled, 1);
        }
        wrapper.orderByDesc(PrintTemplate::getCreateTime);

        Page<PrintTemplate> result = page(page, wrapper);

        return new PageResult<>(
                result.getTotal(),
                result.getRecords().stream().map(entity -> {
                    PrintTemplateDTO dto = new PrintTemplateDTO();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).collect(Collectors.toList())
        );
    }

    @Override
    public PrintTemplateDTO getDetail(Long id) {
        PrintTemplate entity = getById(id);
        if (entity == null) {
            throw new BusinessException(BizCode.PRINT_TEMPLATE_NOT_FOUND);
        }
        PrintTemplateDTO dto = new PrintTemplateDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public PrintTemplateDTO saveTemplate(PrintTemplateDTO dto) {
        if (dto == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "参数错误");
        }
        PrintTemplate entity = new PrintTemplate();
        BeanUtils.copyProperties(dto, entity);

        if (dto.getTemplateId() == null) {
            if (!StringUtils.hasText(dto.getTemplateName())) {
                entity.setTemplateName("未命名模板");
            }
            if (entity.getEnabled() == null) {
                entity.setEnabled(1);
            }
            if (entity.getIsDefault() == null) {
                entity.setIsDefault(0);
            }
            save(entity);
        } else {
            PrintTemplate existing = getById(dto.getTemplateId());
            if (existing == null) {
                throw new BusinessException(BizCode.PRINT_TEMPLATE_NOT_FOUND);
            }
            updateById(entity);
        }

        PrintTemplateDTO saved = new PrintTemplateDTO();
        BeanUtils.copyProperties(entity, saved);
        return saved;
    }

    @Override
    public Long copyTemplate(Long id) {
        PrintTemplate existing = getById(id);
        if (existing == null) {
            throw new BusinessException(BizCode.PRINT_TEMPLATE_NOT_FOUND);
        }
        PrintTemplate copy = new PrintTemplate();
        BeanUtils.copyProperties(existing, copy);
        copy.setTemplateId(null);
        copy.setIsDefault(0);
        copy.setTemplateName((existing.getTemplateName() == null ? "未命名模板" : existing.getTemplateName()) + "-副本");
        save(copy);
        return copy.getTemplateId();
    }

    @Override
    public void setDefault(Long id) {
        PrintTemplate existing = getById(id);
        if (existing == null) {
            throw new BusinessException(BizCode.PRINT_TEMPLATE_NOT_FOUND);
        }
        update(new LambdaUpdateWrapper<PrintTemplate>()
                .eq(PrintTemplate::getBizType, existing.getBizType())
                .set(PrintTemplate::getIsDefault, 0));
        existing.setIsDefault(1);
        updateById(existing);
    }

    @Override
    public void setEnabled(Long id, Integer enabled) {
        PrintTemplate existing = getById(id);
        if (existing == null) {
            throw new BusinessException(BizCode.PRINT_TEMPLATE_NOT_FOUND);
        }
        if (enabled == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "参数错误");
        }
        existing.setEnabled(enabled);
        updateById(existing);
    }

    @Override
    public PrintTemplateSettingsDTO getSettings() {
        PrintTemplateSettingsDTO dto = new PrintTemplateSettingsDTO();
        SysConfig config = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, KEY_PRINT_NO_DIALOG).last("limit 1"));
        if (config == null || !StringUtils.hasText(config.getConfigValue())) {
            dto.setPrintNoDialog(false);
            return dto;
        }
        dto.setPrintNoDialog("true".equalsIgnoreCase(config.getConfigValue().trim()));
        return dto;
    }

    @Override
    public void saveSettings(PrintTemplateSettingsDTO dto) {
        if (dto == null) {
            return;
        }
        String value = dto.getPrintNoDialog() != null && dto.getPrintNoDialog() ? "true" : "false";
        upsertSysConfig(KEY_PRINT_NO_DIALOG, value, "打印设置:打印不显示弹窗");
    }

    @Override
    public List<String> listBizTypes() {
        QueryWrapper<PrintTemplate> wrapper = new QueryWrapper<>();
        wrapper.select("distinct biz_type as bizType")
                .eq("deleted", 0)
                .orderByAsc("biz_type");
        List<Map<String, Object>> rows = baseMapper.selectMaps(wrapper);
        return rows.stream()
                .map(r -> r.get("bizType") == null ? null : String.valueOf(r.get("bizType")))
                .filter(StringUtils::hasText)
                .toList();
    }

    private void upsertSysConfig(String key, String value, String remark) {
        SysConfig existing = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key).last("limit 1"));
        if (existing == null) {
            SysConfig config = new SysConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            config.setRemark(remark);
            sysConfigMapper.insert(config);
            return;
        }
        existing.setConfigValue(value);
        existing.setRemark(remark);
        sysConfigMapper.updateById(existing);
    }
}
