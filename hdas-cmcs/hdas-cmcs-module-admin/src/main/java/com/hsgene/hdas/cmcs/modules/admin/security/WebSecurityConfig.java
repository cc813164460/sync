package com.hsgene.hdas.cmcs.modules.admin.security;

import com.hsgene.hdas.cmcs.modules.admin.handle.AjaxAuthFailHandler;
import com.hsgene.hdas.cmcs.modules.admin.handle.AjaxAuthSuccessHandler;
import com.hsgene.hdas.cmcs.modules.admin.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * @description:
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.security
 * @author: maodi
 * @createDate: 2018/6/21 15:04
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String[] PERMIT_URLS = {
            "/scripts/**",
            "/styles/**",
            "/img/**",
            "/login",
            "/timeout",
            "/error",
            "/logout",
            "/services/config",
            "/app/insert",
            "/app/update"
    };

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    SessionRegistry sessionRegistry;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(PERMIT_URLS).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .successHandler(new AjaxAuthSuccessHandler())
                .failureHandler(new AjaxAuthFailHandler())
                .permitAll()
                .and()
                .sessionManagement().maximumSessions(1).expiredUrl("/login").sessionRegistry(sessionRegistry)
                .and()
                .and()
                .logout()
                .permitAll();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        //替换成自己验证规则
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public SessionRegistry getSessionRegistry() {
        SessionRegistry sessionRegistry = new SessionRegistryImpl();
        return sessionRegistry;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        //是否隐藏用户没找到的异常
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {
        @Override
        protected boolean enableHttpSessionEventPublisher() {
            return true;
        }

    }

}
