package com.duoduo.jxc.service.wechat;

import com.alibaba.fastjson2.JSON;
import com.duoduo.jxc.config.WechatMiniAppProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class WechatMiniAppService {

    private final WechatMiniAppProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    public String code2sessionOpenid(String code) {
        if (!StringUtils.hasText(code)) {
            throw new IllegalArgumentException("code 不能为空");
        }
        if (!StringUtils.hasText(properties.getAppId()) || !StringUtils.hasText(properties.getSecret())) {
            throw new IllegalStateException("未配置微信小程序 appId/secret");
        }
        String url = UriComponentsBuilder.fromHttpUrl(properties.getCode2sessionUrl())
                .queryParam("appid", properties.getAppId())
                .queryParam("secret", properties.getSecret())
                .queryParam("js_code", code)
                .queryParam("grant_type", "authorization_code")
                .build(true)
                .toUriString();
        String raw = restTemplate.getForObject(url, String.class);
        Code2SessionResponse resp = JSON.parseObject(raw, Code2SessionResponse.class);
        if (resp == null) {
            throw new IllegalStateException("微信响应为空");
        }
        if (resp.getErrcode() != null && resp.getErrcode() != 0) {
            throw new IllegalStateException(resp.getErrmsg() != null ? resp.getErrmsg() : "微信登录失败");
        }
        if (!StringUtils.hasText(resp.getOpenid())) {
            throw new IllegalStateException("微信响应缺少 openid");
        }
        return resp.getOpenid();
    }

    @Data
    public static class Code2SessionResponse {
        private String openid;
        private String session_key;
        private String unionid;
        private Integer errcode;
        private String errmsg;
    }
}

