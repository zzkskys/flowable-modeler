package com.qunchuang.modeler.config;

import org.flowable.idm.api.IdmIdentityService;
import org.flowable.ui.common.properties.FlowableRestAppProperties;
import org.flowable.ui.common.security.ClearFlowableCookieLogoutHandler;
import org.flowable.ui.idm.properties.FlowableIdmAppProperties;
import org.flowable.ui.idm.security.*;
import org.flowable.ui.idm.web.CustomFormLoginConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.AuthorizedUrl;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        jsr250Enabled = true
)
public class BosSecurityConfiguration {
    @Autowired
    protected IdmIdentityService identityService;
    @Autowired
    protected FlowableIdmAppProperties idmAppProperties;

    public BosSecurityConfiguration() {
    }

    @Bean
    public UserDetailsService userDetailsService() {
        org.flowable.ui.idm.security.UserDetailsService userDetailsService = new org.flowable.ui.idm.security.UserDetailsService();
        userDetailsService.setUserValidityPeriod(this.idmAppProperties.getSecurity().getUserValidityPeriod());
        return userDetailsService;
    }

    @Bean("dbAuthenticationProvider")
    @ConditionalOnMissingBean({AuthenticationProvider.class})
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
    @ConditionalOnProperty(
            prefix = "flowable.idm.ldap",
            name = "enabled",
            havingValue = "true"
    )
    public AuthenticationProvider ldapAuthenticationProvider() {
        return new CustomLdapAuthenticationProvider(this.userDetailsService(), this.identityService);
    }

    @Bean
    public CustomPersistentRememberMeServices rememberMeServices() {
        return new CustomPersistentRememberMeServices(this.idmAppProperties, this.userDetailsService());
    }

    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        protected final FlowableRestAppProperties restAppProperties;
        protected final FlowableIdmAppProperties idmAppProperties;

        public ApiWebSecurityConfigurationAdapter(FlowableRestAppProperties restAppProperties, FlowableIdmAppProperties idmAppProperties) {
            this.restAppProperties = restAppProperties;
            this.idmAppProperties = idmAppProperties;
        }

        protected void configure(HttpSecurity http) throws Exception {
            (http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()).csrf().disable();
            if (this.idmAppProperties.isRestEnabled()) {
                if (this.restAppProperties.isVerifyRestApiPrivilege()) {
                    ((HttpSecurity) ((AuthorizedUrl) http.antMatcher("/api/**").authorizeRequests().antMatchers(new String[]{"/api/**"})).hasAuthority("access-rest-api").and()).httpBasic();
                } else {
                    ((HttpSecurity) ((AuthorizedUrl) http.antMatcher("/api/**").authorizeRequests().antMatchers(new String[]{"/api/**"})).authenticated().and()).httpBasic();
                }
            } else {
                ((AuthorizedUrl) http.antMatcher("/api/**").authorizeRequests().antMatchers(new String[]{"/api/**"})).denyAll();
            }

        }
    }

    @Configuration
    @Order(10)
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        @Autowired
        private FlowableIdmAppProperties idmAppProperties;
        @Autowired
        private AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;
        @Autowired
        private AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;
        @Autowired
        private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;
        @Autowired
        private Http401UnauthorizedEntryPoint authenticationEntryPoint;
        @Autowired
        private RememberMeServices rememberMeServices;

        public FormLoginWebSecurityConfigurerAdapter() {
        }

        protected void configure(HttpSecurity http) throws Exception {
            ((AuthorizedUrl) ((AuthorizedUrl) ((AuthorizedUrl) ((((((http.exceptionHandling().authenticationEntryPoint(this.authenticationEntryPoint).and()).sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()).rememberMe().rememberMeServices(this.rememberMeServices).key(this.idmAppProperties.getSecurity().getRememberMeKey()).and()).logout().logoutUrl("/app/logout").logoutSuccessHandler(this.ajaxLogoutSuccessHandler).addLogoutHandler(new ClearFlowableCookieLogoutHandler()).permitAll().and()).csrf().disable()).headers().frameOptions().sameOrigin().addHeaderWriter(new XXssProtectionHeaderWriter()).and()).authorizeRequests().antMatchers(new String[]{"/*"})).permitAll().antMatchers(new String[]{"/app/rest/authenticate"})).permitAll().antMatchers(new String[]{"/app/**"})).hasAuthority("access-idm");
            CustomFormLoginConfig<HttpSecurity> loginConfig = new CustomFormLoginConfig();
            ((CustomFormLoginConfig) (((CustomFormLoginConfig) loginConfig.loginProcessingUrl("/app/authentication")).successHandler(this.ajaxAuthenticationSuccessHandler)).failureHandler(this.ajaxAuthenticationFailureHandler)).usernameParameter("j_username").passwordParameter("j_password").permitAll();
            http.apply(loginConfig);
        }
    }
}

