package com.qunchuang.modeler.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.ui.common.model.UserRepresentation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Create Time : 2020/01/10
 *
 * @author zzk
 */
@RestController
@RequestMapping("/app")
public class FlowableAuthController {

    private final ObjectMapper objectMapper;

    public FlowableAuthController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    //认证成功后的接口
    @GetMapping("/rest/authenticate")
    public ObjectNode isAuthenticated() {
        String user = "admin";
        ObjectNode result = this.objectMapper.createObjectNode();
        result.put("login", user);
        return result;
    }


    //获得认证后的 User
    @GetMapping(value = "/rest/account")
    public UserRepresentation getAccount() {
        UserRepresentation userRepresentation = new UserRepresentation();

        userRepresentation.setFirstName("adminss");
        userRepresentation.setLastName("admin");
        userRepresentation.setId("admin");
        List<String> privileges = new ArrayList<>();
        privileges.add("access-admin");
        privileges.add("access-idm");
        privileges.add("access-modeler");
        privileges.add("access-rest-api");
        privileges.add("access-task");
        userRepresentation.setPrivileges(privileges);

        return userRepresentation;
    }
}
