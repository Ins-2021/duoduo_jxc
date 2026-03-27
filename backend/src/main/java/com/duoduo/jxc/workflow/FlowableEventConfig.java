package com.duoduo.jxc.workflow;

import lombok.RequiredArgsConstructor;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class FlowableEventConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    private final WorkflowFlowableEventListener workflowFlowableEventListener;

    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
        engineConfiguration.setEnableEventDispatcher(true);
        List<FlowableEventListener> listeners = engineConfiguration.getEventListeners();
        if (listeners == null) {
            listeners = new ArrayList<>();
        } else {
            listeners = new ArrayList<>(listeners);
        }
        listeners.add(workflowFlowableEventListener);
        engineConfiguration.setEventListeners(listeners);
    }
}
