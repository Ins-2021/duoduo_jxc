package com.duoduo.jxc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jxc.file")
public class FileStorageProperties {
    private String storageDir = "./data/uploads";
    private long maxSizeMb = 10;
}

