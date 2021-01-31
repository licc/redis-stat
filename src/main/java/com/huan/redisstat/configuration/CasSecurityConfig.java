package com.huan.redisstat.configuration;

import com.huan.redisstat.service.UserService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorageImpl;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.cas.web.authentication.ServiceAuthenticationDetailsSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Map;

/**
 * Created by lihuan on 2021/2/1.
 */


@Setter
@Slf4j
@EnableWebSecurity
@ConfigurationProperties(prefix = "security")
@ConditionalOnProperty(prefix = "security", name = "cas-login", havingValue = "true")
public class CasSecurityConfig extends WebSecurityConfigurerAdapter {

    private String casServiceLogin;
    private String casServiceLogout;
    private String casUrlPrefix;
    private String appService;

    @Autowired
    private UserService userService;

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties sp = new ServiceProperties();
        sp.setService(appService);
        //这个值不配置会循环
        sp.setAuthenticateAllArtifacts(true);
        return sp;
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setAuthenticationUserDetailsService(customUserDetailsService());
        casAuthenticationProvider.setServiceProperties(serviceProperties());
        casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
        casAuthenticationProvider.setKey("an_id_for_this_auth_provider_only");
        return casAuthenticationProvider;
    }

    @Bean
    public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> customUserDetailsService() {
        return token -> {
            Map<String, Object> attributes = token.getAssertion().getPrincipal().getAttributes();

            String empNo = (String) attributes.get("employeeNumber");
            String ldapNo = empNo.toLowerCase();
            //获取权限访问控制
            log.info("Authenticating '{}'", attributes);
            return userService.getAccessableResource(token.getName(), ldapNo);
        };
    }

    @Bean
    public SessionAuthenticationStrategy sessionStrategy() {
        return new SessionFixationProtectionStrategy();
    }

    @Bean
    public Cas20ProxyTicketValidator cas20ServiceTicketValidator() {
        Cas20ProxyTicketValidator cas20ServiceTicketValidator = new Cas20ProxyTicketValidator(casUrlPrefix);
        cas20ServiceTicketValidator.setEncoding("UTF-8");
        cas20ServiceTicketValidator.setProxyGrantingTicketStorage(pgtStorage());

        return cas20ServiceTicketValidator;
    }

    @Bean
    public ProxyGrantingTicketStorageImpl pgtStorage() {
        return new ProxyGrantingTicketStorageImpl();
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        casAuthenticationFilter.setAuthenticationManager(authenticationManager());
        casAuthenticationFilter.setSessionAuthenticationStrategy(sessionStrategy());
        casAuthenticationFilter.setServiceProperties(serviceProperties());

        casAuthenticationFilter.setAuthenticationDetailsSource(new ServiceAuthenticationDetailsSource(serviceProperties()));

        casAuthenticationFilter.setProxyGrantingTicketStorage(pgtStorage());

        return casAuthenticationFilter;
    }

    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
        casAuthenticationEntryPoint.setLoginUrl(casServiceLogin);
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
        return casAuthenticationEntryPoint;
    }

    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setCasServerUrlPrefix(casUrlPrefix);
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        return singleSignOutFilter;
    }

    @Bean
    public LogoutFilter requestCasGlobalLogoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(casServiceLogout + "?service="
                + appService, new SecurityContextLogoutHandler());
        logoutFilter.setLogoutRequestMatcher(new AntPathRequestMatcher("/logout"));
        return logoutFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(casAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().exceptionHandling()
                .authenticationEntryPoint(casAuthenticationEntryPoint())
                .and().addFilterBefore(casAuthenticationFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class)
                .addFilterBefore(requestCasGlobalLogoutFilter(), LogoutFilter.class);

        http.authorizeRequests()
                .antMatchers("/HealthServlet/check").permitAll()
                .antMatchers("/**").authenticated();

        http.headers().cacheControl().disable()
                .xssProtection().disable()
                .frameOptions().disable()
                .contentTypeOptions().disable();

        /**
         * <logout invalidate-session="true" delete-cookies="JSESSIONID" />
         */
        http.logout().logoutUrl("/logout").logoutSuccessUrl("/").invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
    }
}
