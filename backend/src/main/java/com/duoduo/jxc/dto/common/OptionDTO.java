package com.duoduo.jxc.dto.common;

import lombok.Data;

@Data
public class OptionDTO {
    private Long value;
    private String label;

    public OptionDTO() {
    }

    public OptionDTO(Long value, String label) {
        this.value = value;
        this.label = label;
    }
}

