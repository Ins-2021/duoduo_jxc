package com.duoduo.jxc.dto.workflow;

import lombok.Data;

import java.util.List;

@Data
public class WfDiagramStateResponse {

    private List<String> activeNodeKeys;

    private List<String> finishedNodeKeys;
}

