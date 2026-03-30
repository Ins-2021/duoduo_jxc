package com.duoduo.jxc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WechatMiniAppProperties {

    private boolean enabled = false;

    private String appId;

    private String secret;

    private String code2sessionUrl = "https://api.weixin.qq.com/sns/jscode2session";

    private Map<String, String> openidUserMap = new HashMap<>();

    private String defaultUsername;
}

