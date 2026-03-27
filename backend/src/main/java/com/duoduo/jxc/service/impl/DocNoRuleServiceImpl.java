package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.converter.DocNoRuleConverter;
import com.duoduo.jxc.entity.DocNoRule;
import com.duoduo.jxc.mapper.DocNoRuleMapper;
import com.duoduo.jxc.service.DocNoRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 单号规则Service实现类
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocNoRuleServiceImpl extends ServiceImpl<DocNoRuleMapper, DocNoRule> implements DocNoRuleService {

    private final DocNoRuleConverter converter;

    private static final ConcurrentHashMap<String, AtomicLong> SEQUENCE_MAP = new ConcurrentHashMap<>();

    @Override
    public String generateDocNo(String docType) {
        DocNoRule rule = lambdaQuery()
                .eq(DocNoRule::getDocType, docType)
                .eq(DocNoRule::getStatus, 1)
                .one();

        if (rule == null) {
            log.warn("未找到单据类型 {} 的编号规则，使用默认规则", docType);
            return generateDefaultNo(docType);
        }

        return generateByRule(rule);
    }

    /**
     * 根据规则生成单号
     */
    private String generateByRule(DocNoRule rule) {
        StringBuilder sb = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();

        // 添加前缀
        if (rule.getPrefix() != null && !rule.getPrefix().isEmpty()) {
            sb.append(rule.getPrefix());
        }

        // 添加年份
        if (rule.getIncludeYear() != null && rule.getIncludeYear() == 1) {
            sb.append(now.format(DateTimeFormatter.ofPattern("yyyy")));
        }

        // 添加月份
        if (rule.getIncludeMonth() != null && rule.getIncludeMonth() == 1) {
            sb.append(now.format(DateTimeFormatter.ofPattern("MM")));
        }

        // 添加日期
        if (rule.getIncludeDay() != null && rule.getIncludeDay() == 1) {
            sb.append(now.format(DateTimeFormatter.ofPattern("dd")));
        }

        // 添加随机数
        if (rule.getUseRandom() != null && rule.getUseRandom() == 1) {
            int randomLen = rule.getRandomLength() != null ? rule.getRandomLength() : 6;
            String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, randomLen).toUpperCase();
            sb.append(randomStr);
        } else {
            // 添加流水号
            int seqLen = rule.getSeqLength() != null ? rule.getSeqLength() : 4;
            String seq = getSequence(rule.getDocType(), seqLen);
            sb.append(seq);
        }

        return sb.toString();
    }

    /**
     * 生成默认单号
     */
    private String generateDefaultNo(String docType) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return docType.toUpperCase() + dateStr + randomStr;
    }

    /**
     * 获取流水号
     */
    private String getSequence(String docType, int length) {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String key = docType + ":" + today;

        AtomicLong counter = SEQUENCE_MAP.computeIfAbsent(key, k -> new AtomicLong(1));
        long seq = counter.getAndIncrement();

        // 格式化流水号，补零
        return String.format("%0" + length + "d", seq);
    }
}
