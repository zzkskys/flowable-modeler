package com.qunchuang.modeler.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.ui.common.service.exception.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 扩展 flowable 默认的流程设计器 流程图在线设计器,资源文件在 stencilset
 */
@RestController
@RequestMapping("/app")
public class FlowStencilSetResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(org.flowable.ui.modeler.rest.app.StencilSetResource.class);

    @Autowired
    protected ObjectMapper objectMapper;

    @RequestMapping(value = "/rest/stencil-sets/editor", method = RequestMethod.GET, produces = "application/json")
    public JsonNode getStencilSetForEditor() {
        try {
            return objectMapper.readTree(this.getClass().getClassLoader().getResourceAsStream("stencilset/stencilset_bpmn.json"));
        } catch (Exception e) {
            LOGGER.error("Error reading bpmn stencil set json", e);
            throw new InternalServerErrorException("Error reading bpmn stencil set json");
        }
    }

    @RequestMapping(value = "/rest/stencil-sets/cmmneditor", method = RequestMethod.GET, produces = "application/json")
    public JsonNode getCmmnStencilSetForEditor() {
        try {
            return objectMapper.readTree(this.getClass().getClassLoader().getResourceAsStream("stencilset/stencilset_cmmn.json"));
        } catch (Exception e) {
            LOGGER.error("Error reading bpmn stencil set json", e);
            throw new InternalServerErrorException("Error reading bpmn stencil set json");
        }
    }
}
