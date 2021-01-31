package com.huan.redisstat.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new MyPasswordEncoder();
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/**", "/h2/**");
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable(); // 关闭跨站检测
        http.authorizeRequests().anyRequest().fullyAuthenticated(); // 所有的请求全验证
        http.formLogin().loginPage("/login").loginProcessingUrl("/login_check")
                .failureUrl("/login").defaultSuccessUrl("/clusters").permitAll();

        http.logout().logoutUrl("/logout").logoutSuccessUrl("/").invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
        http.logout().permitAll();
    }
}
