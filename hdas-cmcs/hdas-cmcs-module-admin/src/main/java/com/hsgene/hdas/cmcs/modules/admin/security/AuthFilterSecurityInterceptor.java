package com.hsgene.hdas.cmcs.modules.admin.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Service;

import javax.servlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 拦截器
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.security
 * @author: maodi
 * @createDate: 2018/6/22 11:07
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Service
public class AuthFilterSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {

    @Autowired
    private FilterInvocationSecurityMetadataSource securityMetadataSource;

    @Bean
    public AffirmativeBased setAffirmativeBased() {
        List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<>();
        AccessDecisionVoter<? extends Object> voter = new AuthVoter();
        decisionVoters.add(voter);
        AffirmativeBased based = new AffirmativeBased(decisionVoters);
        return based;
    }

    /**
     * 使用投票器进行权限决策
     *
     * @param based
     */
    @Autowired
    public void setAccessDecisionManager(AffirmativeBased based) {
        super.setAccessDecisionManager(based);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        FilterInvocation fi = new FilterInvocation(request, response, chain);
        invoke(fi);
    }

    public void invoke(FilterInvocation fi) throws IOException, ServletException {
        /**
         * fi里面有一个被拦截的url
         * 里面调用AuthMetadataSourceService的getAttributes(Object object)这个方法获取fi对应的所有权限
         * 再调用AuthAccessDecisionManager的decide方法来校验用户的权限是否足够
         */
        InterceptorStatusToken token = super.beforeInvocation(fi);
        try {
            //执行下一个拦截器
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        } finally {
            super.afterInvocation(token, null);
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

}
