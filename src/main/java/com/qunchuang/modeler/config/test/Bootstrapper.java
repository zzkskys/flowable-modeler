package com.qunchuang.modeler.config.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.Privilege;
import org.flowable.idm.api.User;
import org.flowable.idm.api.UserQuery;
import org.flowable.spring.boot.ldap.FlowableLdapProperties;
import org.flowable.ui.idm.properties.FlowableIdmAppProperties;
import org.flowable.ui.idm.properties.FlowableIdmAppProperties.Admin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class Bootstrapper implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(org.flowable.ui.idm.conf.Bootstrapper.class);
    @Autowired
    private IdmIdentityService identityService;
    private FlowableLdapProperties ldapProperties;
    @Autowired
    private FlowableIdmAppProperties idmAppProperties;

    public Bootstrapper() {
    }

    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            if (this.ldapProperties != null && this.ldapProperties.isEnabled()) {
                if (this.identityService.createPrivilegeQuery().privilegeName("access-idm").count() == 0L) {
                    String adminUserId = this.idmAppProperties.getAdmin().getUserId();
                    if (StringUtils.isNotEmpty(adminUserId)) {
                        this.initializeDefaultPrivileges(adminUserId);
                    } else {
                        LOGGER.warn("No user found with IDM access. Set flowable.idp.app.admin.user-id to give at least one user access to the IDM application to configure privileges.");
                    }
                }
            } else if (this.idmAppProperties.isBootstrap()) {
                this.createDefaultAdminUserAndPrivileges();
            }
        }

    }

    protected void createDefaultAdminUserAndPrivileges() {
        String adminUserId = this.idmAppProperties.getAdmin().getUserId();
        if (StringUtils.isNotEmpty(adminUserId)) {
            User adminUser = (User)((UserQuery)this.identityService.createUserQuery().userId(adminUserId)).singleResult();
            if (adminUser == null) {
                LOGGER.info("No admin user found, initializing default entities");
                adminUser = this.initializeAdminUser();
            }

            this.initializeDefaultPrivileges(adminUser.getId());
        }

    }

    protected User initializeAdminUser() {
        Admin adminConfig = this.idmAppProperties.getAdmin();
        String adminUserId = adminConfig.getUserId();
        Assert.notNull(adminUserId, "flowable.idm.app.admin.user-id property must be set");
        String adminPassword = adminConfig.getPassword();
        Assert.notNull(adminPassword, "flowable.idm.app.admin.password property must be set");
        String adminFirstname = adminConfig.getFirstName();
        Assert.notNull(adminFirstname, "flowable.idm.app.admin.first-name property must be set");
        String adminLastname = adminConfig.getLastName();
        Assert.notNull(adminLastname, "flowable.idm.app.admin.last-name property must be set");
        String adminEmail = adminConfig.getEmail();
        User admin = this.identityService.newUser(adminUserId);
        admin.setFirstName(adminFirstname);
        admin.setLastName(adminLastname);
        admin.setEmail(adminEmail);
        admin.setPassword(adminPassword);
        this.identityService.saveUser(admin);
        return admin;
    }

    protected void initializeDefaultPrivileges(String adminId) {
        List<Privilege> privileges = this.identityService.createPrivilegeQuery().list();
        Map<String, Privilege> privilegeMap = new HashMap();
        Iterator var4 = privileges.iterator();

        Privilege adminAppPrivilege;
        while(var4.hasNext()) {
            adminAppPrivilege = (Privilege)var4.next();
            privilegeMap.put(adminAppPrivilege.getName(), adminAppPrivilege);
        }

        Privilege idmAppPrivilege = this.findOrCreatePrivilege("access-idm", privilegeMap);
        if (!this.privilegeMappingExists(adminId, idmAppPrivilege)) {
            this.identityService.addUserPrivilegeMapping(idmAppPrivilege.getId(), adminId);
        }

        adminAppPrivilege = this.findOrCreatePrivilege("access-admin", privilegeMap);
        if (!this.privilegeMappingExists(adminId, adminAppPrivilege)) {
            this.identityService.addUserPrivilegeMapping(adminAppPrivilege.getId(), adminId);
        }

        Privilege modelerAppPrivilege = this.findOrCreatePrivilege("access-modeler", privilegeMap);
        if (!this.privilegeMappingExists(adminId, modelerAppPrivilege)) {
            this.identityService.addUserPrivilegeMapping(modelerAppPrivilege.getId(), adminId);
        }

        Privilege taskAppPrivilege = this.findOrCreatePrivilege("access-task", privilegeMap);
        if (!this.privilegeMappingExists(adminId, taskAppPrivilege)) {
            this.identityService.addUserPrivilegeMapping(taskAppPrivilege.getId(), adminId);
        }

        Privilege restApiAccessPrivilege = this.findOrCreatePrivilege("access-rest-api", privilegeMap);
        if (!this.privilegeMappingExists(adminId, restApiAccessPrivilege)) {
            this.identityService.addUserPrivilegeMapping(restApiAccessPrivilege.getId(), adminId);
        }

    }

    protected Privilege findOrCreatePrivilege(String privilegeName, Map<String, Privilege> privilegeMap) {
        Privilege privilege = null;
        if (privilegeMap.containsKey(privilegeName)) {
            privilege = (Privilege)privilegeMap.get(privilegeName);
        } else {
            try {
                privilege = this.identityService.createPrivilege(privilegeName);
            } catch (Exception var5) {
                privilege = (Privilege)this.identityService.createPrivilegeQuery().privilegeName(privilegeName).singleResult();
            }
        }

        if (privilege == null) {
            throw new FlowableException("Could not find or create access-rest-api privilege");
        } else {
            return privilege;
        }
    }

    protected boolean privilegeMappingExists(String restAdminId, Privilege privilege) {
        return this.identityService.createPrivilegeQuery().userId(restAdminId).privilegeId(privilege.getId()).singleResult() != null;
    }

    @Autowired(
            required = false
    )
    public void setLdapProperties(FlowableLdapProperties ldapProperties) {
        this.ldapProperties = ldapProperties;
    }
}
