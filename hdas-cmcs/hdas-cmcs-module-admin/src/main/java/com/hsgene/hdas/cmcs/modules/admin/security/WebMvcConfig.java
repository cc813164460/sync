package com.hsgene.hdas.cmcs.modules.admin.security;

import com.hsgene.hdas.cmcs.modules.admin.domain.Constant;
import com.hsgene.hdas.cmcs.modules.admin.domain.SysPermission;
import com.hsgene.hdas.cmcs.modules.admin.dto.AuthGrantedAuthority;
import com.hsgene.hdas.cmcs.modules.admin.service.ISysPermissionService;
import com.hsgene.hdas.cmcs.modules.admin.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.security
 * @author: maodi
 * @createDate: 2018/8/1 14:19
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Bean
    public SecurityInterceptor getSecurityInterceptor() {
        return new SecurityInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration addInterceptor = registry.addInterceptor(getSecurityInterceptor());
        // 排除配置
        addInterceptor.excludePathPatterns("/error");
        addInterceptor.excludePathPatterns("/login**");
        addInterceptor.excludePathPatterns("/register**");
        // 拦截配置
        addInterceptor.addPathPatterns("/**");
    }

    private class SecurityInterceptor extends HandlerInterceptorAdapter {

        @Value("${server.session.timeout}")
        private int sessionTimeout = 1800;

        @Autowired
        private IUserService userService;

        @Autowired
        private ISysPermissionService sysPermissionService;

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
                Exception {
            HttpSession session = request.getSession();
            //设置session超时时间
            session.setMaxInactiveInterval(sessionTimeout);
            Object username = session.getAttribute(Constant.SESSION_KEY);
            SecurityContext securityContext = (SecurityContext) session.getAttribute
                    (HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
            if (securityContext != null) {
                Authentication authentication = securityContext.getAuthentication();
                List<AuthGrantedAuthority> authorityList = new ArrayList();
                List<SysPermission> sysPermissions = sysPermissionService.getByUserId(userService.getIdByName(username
                        .toString()));
                for (SysPermission sysPermission : sysPermissions) {
                    String url = sysPermission.getUrl();
                    String httpMethod = sysPermission.getMethod();
                    authorityList.add(new AuthGrantedAuthority(url, httpMethod));
                }
                UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(authentication
                        .getPrincipal(), authentication.getCredentials(), authorityList);
                result.setDetails(authentication.getDetails());
                securityContext.setAuthentication(result);
            }
            return true;
        }
    }

}
