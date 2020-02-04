package com.qunchuang.modeler.config.security;

import org.flowable.common.engine.impl.runtime.Clock;
import org.flowable.idm.engine.IdmEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
        basePackages = {"org.flowable.idm.extension.conf", "org.flowable.idm.extension.bean"}
)
public class FlowableIdmEngineConfiguration {
    public FlowableIdmEngineConfiguration() {
    }

    @Bean("clock")
    public Clock getClock(IdmEngine idmEngine) {
        return idmEngine.getIdmEngineConfiguration().getClock();
    }
}
