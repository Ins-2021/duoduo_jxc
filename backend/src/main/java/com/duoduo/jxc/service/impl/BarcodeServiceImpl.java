package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.dto.inventory.BarcodeDTO;
import com.duoduo.jxc.dto.inventory.BarcodeGenerateRequest;
import com.duoduo.jxc.entity.Barcode;
import com.duoduo.jxc.entity.BarcodeRule;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.BarcodeMapper;
import com.duoduo.jxc.service.BarcodeService;
import com.duoduo.jxc.service.BarcodeRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class BarcodeServiceImpl extends ServiceImpl<BarcodeMapper, Barcode> implements BarcodeService {

    private final BarcodeRuleService barcodeRuleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BarcodeDTO> generateBarcode(BarcodeGenerateRequest request) {
        if (request == null || !StringUtils.hasText(request.getBarcodeType()) || request.getRefId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST);
        }
        int count = request.getCount() != null && request.getCount() > 0 ? request.getCount() : 1;
        if (count > 100) {
            count = 100;
        }

        // 获取规则
        BarcodeRule rule = getRule(request.getBarcodeType(), request.getRuleId());

        List<BarcodeDTO> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String barcodeContent = buildBarcodeContent(rule);
            Barcode barcode = new Barcode();
            barcode.setBarcodeContent(barcodeContent);
            barcode.setRefType(request.getRefType() != null ? request.getRefType() : request.getBarcodeType());
            barcode.setRefId(request.getRefId());
            barcode.setRuleId(rule != null ? rule.getRuleId() : null);
            barcode.setBarcodeType(request.getBarcodeType());
            barcode.setPrinted(0);
            barcode.setCreateTime(LocalDateTime.now());
            barcode.setUpdateTime(LocalDateTime.now());
            save(barcode);
            result.add(toDTO(barcode));
        }

        log.info("生成条码: type={}, refType={}, refId={}, count={}",
                request.getBarcodeType(), request.getRefType(), request.getRefId(), count);
        return result;
    }

    @Override
    public BarcodeDTO parseBarcode(String barcodeContent) {
        if (!StringUtils.hasText(barcodeContent)) {
            throw new BusinessException(BizCode.BAD_REQUEST);
        }

        LambdaQueryWrapper<Barcode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Barcode::getBarcodeContent, barcodeContent)
               .orderByDesc(Barcode::getCreateTime)
               .last("LIMIT 1");
        Barcode barcode = getOne(wrapper);

        BarcodeDTO dto = new BarcodeDTO();
        if (barcode != null) {
            BeanUtils.copyProperties(barcode, dto);
        } else {
            dto.setBarcodeContent(barcodeContent);
            // 尝试从内容解析类型
            dto.setBarcodeType(guessBarcodeType(barcodeContent));
        }
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void printBarcode(Long barcodeId) {
        Barcode barcode = getById(barcodeId);
        if (barcode == null) {
            throw new BusinessException(BizCode.NOT_FOUND, "条码不存在");
        }
        LambdaUpdateWrapper<Barcode> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Barcode::getBarcodeId, barcodeId)
               .set(Barcode::getPrinted, 1)
               .set(Barcode::getPrintTime, LocalDateTime.now());
        update(wrapper);
        log.info("条码已打印: barcodeId={}, content={}", barcodeId, barcode.getBarcodeContent());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void printBarcodeBatch(List<Long> barcodeIds) {
        if (barcodeIds == null || barcodeIds.isEmpty()) {
            return;
        }
        LambdaUpdateWrapper<Barcode> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Barcode::getBarcodeId, barcodeIds)
               .set(Barcode::getPrinted, 1)
               .set(Barcode::getPrintTime, LocalDateTime.now());
        update(wrapper);
        log.info("批量打印条码: count={}", barcodeIds.size());
    }

    private BarcodeRule getRule(String barcodeType, Long ruleId) {
        // 优先使用指定规则
        if (ruleId != null) {
            return barcodeRuleService.getById(ruleId);
        }
        // 查找该类型的默认规则
        LambdaQueryWrapper<BarcodeRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BarcodeRule::getRuleType, barcodeType)
               .eq(BarcodeRule::getIsDefault, 1)
               .eq(BarcodeRule::getIsActive, 1);
        BarcodeRule rule = barcodeRuleService.getOne(wrapper);
        if (rule != null) {
            return rule;
        }
        // 没有默认规则，查找该类型任意启用的规则
        LambdaQueryWrapper<BarcodeRule> anyWrapper = new LambdaQueryWrapper<>();
        anyWrapper.eq(BarcodeRule::getRuleType, barcodeType)
                  .eq(BarcodeRule::getIsActive, 1)
                  .orderByDesc(BarcodeRule::getCreateTime)
                  .last("LIMIT 1");
        return barcodeRuleService.getOne(anyWrapper);
    }

    private String buildBarcodeContent(BarcodeRule rule) {
        if (rule == null) {
            return "BC" + System.currentTimeMillis() + randomDigits(4);
        }

        String content = rule.getPrefix() != null ? rule.getPrefix() : "";
        if (StringUtils.hasText(rule.getDateFormat())) {
            try {
                content += LocalDate.now().format(DateTimeFormatter.ofPattern(rule.getDateFormat()));
            } catch (Exception e) {
                content += LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
        }
        int seqLen = rule.getSeqLength() != null ? rule.getSeqLength() : 4;
        content += randomDigits(seqLen);
        return content;
    }

    private String randomDigits(int length) {
        StringBuilder sb = new StringBuilder();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String guessBarcodeType(String content) {
        if (!StringUtils.hasText(content) || content.length() < 2) {
            return "UNKNOWN";
        }
        // 通过前缀推测类型
        if (content.startsWith("SKU") || content.startsWith("SP")) return "SKU";
        if (content.startsWith("BOX") || content.startsWith("X")) return "BOX";
        if (content.startsWith("PC") || content.startsWith("BATCH")) return "BATCH";
        return "UNKNOWN";
    }

    private BarcodeDTO toDTO(Barcode entity) {
        BarcodeDTO dto = new BarcodeDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
