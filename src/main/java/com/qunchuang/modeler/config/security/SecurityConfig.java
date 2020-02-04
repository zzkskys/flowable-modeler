package com.qunchuang.modeler.config.security;

import org.flowable.idm.api.IdmIdentityService;
import org.flowable.ui.common.security.ClearFlowableCookieLogoutHandler;
import org.flowable.ui.idm.properties.FlowableIdmAppProperties;
import org.flowable.ui.idm.security.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class SecurityConfig {
    protected final IdmIdentityService identityService;
    protected final FlowableIdmAppProperties idmAppProperties;

    public SecurityConfig(IdmIdentityService identityService, FlowableIdmAppProperties idmAppProperties) {
        this.identityService = identityService;
        this.idmAppProperties = idmAppProperties;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        org.flowable.ui.idm.security.UserDetailsService userDetailsService = new org.flowable.ui.idm.security.UserDetailsService();
        userDetailsService.setUserValidityPeriod(this.idmAppProperties.getSecurity().getUserValidityPeriod());
        return userDetailsService;
    }

    @Bean("dbAuthenticationProvider")
    @ConditionalOnMissingBean(AuthenticationProvider.class)
    @ConditionalOnProperty(
            prefix = "flowable.idm.ldap",
            name = "enabled",
            havingValue = "false",
            matchIfMissing = true
    )
    public AuthenticationProvider dbAuthenticationProvider(PasswordEncoder passwordEncoder) {
        CustomDaoAuthenticationProvider daoAuthenticationProvider = new CustomDaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(this.userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

    @Bean("ldapAuthenticationProvider")
    @ConditionalOnProperty(prefix = "flowable.idm.ldap", name = "enabled", havingValue = "true")
    public AuthenticationProvider ldapAuthenticationProvider() {
        return new CustomLdapAuthenticationProvider(this.userDetailsService(), this.identityService);
    }

    @Bean
    public CustomPersistentRememberMeServices rememberMeServices() {
        return new CustomPersistentRememberMeServices(this.idmAppProperties, this.userDetailsService());
    }

    @Configuration
    @Order(10)
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        private final FlowableIdmAppProperties idmAppProperties;
        private final AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;
        private final AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;
        private final AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;
        private final Http401UnauthorizedEntryPoint authenticationEntryPoint;
        private final RememberMeServices rememberMeServices;

        public FormLoginWebSecurityConfigurerAdapter(FlowableIdmAppProperties idmAppProperties,
                                                     AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler,
                                                     AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler,
                                                     AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler,
                                                     Http401UnauthorizedEntryPoint authenticationEntryPoint,
                                                     RememberMeServices rememberMeServices) {
            this.idmAppProperties = idmAppProperties;
            this.ajaxAuthenticationSuccessHandler = ajaxAuthenticationSuccessHandler;
            this.ajaxAuthenticationFailureHandler = ajaxAuthenticationFailureHandler;
            this.ajaxLogoutSuccessHandler = ajaxLogoutSuccessHandler;
            this.authenticationEntryPoint = authenticationEntryPoint;
            this.rememberMeServices = rememberMeServices;
        }

        protected void configure(HttpSecurity http) throws Exception {
            http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .rememberMe().rememberMeServices(rememberMeServices).key(idmAppProperties.getSecurity().getRememberMeKey())
                    .and()
                    .formLogin().loginProcessingUrl("/app/authentication").successHandler(ajaxAuthenticationSuccessHandler).failureHandler(ajaxAuthenticationFailureHandler)
                    .usernameParameter("j_username").passwordParameter("j_password").permitAll()
                    .and()
                    .logout().logoutUrl("/app/logout").logoutSuccessHandler(ajaxLogoutSuccessHandler).addLogoutHandler(new ClearFlowableCookieLogoutHandler()).permitAll()
                    .and()
                    .csrf().disable().headers().frameOptions().sameOrigin().addHeaderWriter(new XXssProtectionHeaderWriter())
                    .and()
                    .authorizeRequests().antMatchers("/*", "/app/rest/authenticate").permitAll();
//                    .antMatchers("/app/**").hasAuthority("access-idm")
//                    .antMatchers("/modeler/**").hasAuthority("access-modeler");
        }
    }
}

