package com.hsgene.hdas.cmcs.modules.admin.security;

import com.hsgene.hdas.cmcs.modules.admin.domain.Constant;
import com.hsgene.hdas.cmcs.modules.admin.dto.AuthGrantedAuthority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;

/**
 * @description: 投票器
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.security
 * @author: maodi
 * @createDate: 2018/6/22 11:07
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
public class AuthVoter implements AccessDecisionVoter<Object> {

    public static final String[] LOGIN_PERMIT_URLS = {
            "/scripts/**",
            "/styles/**",
            "/img/**",
            "/views/common/footer.html",
            "/views/common/left_nav.html",
            "/views/common/main.html",
            "/login",
            "/logout",
            "/",
            "/get_login_info",
            "/role_auth_resource/get_user_auth_resource",
            "/role_auth_resource/get_main_version_auth",
            "/role_auth_resource/get_release_status",
            "/user/get_max_classId_by_username",
            "/history_log_manage/get_current_use_item",
            "/user/update_password",
            "/user/get_password_by_username",
            "/password_update",
            "/role/get_classId_by_roleId",
            "/app/app_data",
            "/item/get_classId_by_id"
    };

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return configAttribute.getAttribute() != null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> collection) {
        HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
        int result;
        Object username = request.getSession().getAttribute(Constant.SESSION_KEY);
        if (authentication == null) {
            result = ACCESS_DENIED;
        } else {
            result = ACCESS_GRANTED;
            boolean adminFlag = "admin".equals(username);
            boolean notNeedAuth = adminFlag || "anonymousUser".equals(authentication.getPrincipal()) || Arrays.stream
                    (LOGIN_PERMIT_URLS).anyMatch(pUrl -> matches(pUrl, request));
            if (!notNeedAuth) {
                boolean access = authentication.getAuthorities().stream().anyMatch(ga -> {
                    if (ga instanceof AuthGrantedAuthority) {
                        AuthGrantedAuthority authority = (AuthGrantedAuthority) ga;
                        String urlsStr = authority.getPermissionUrl();
                        String[] urls = urlsStr.split(",");
                        for (String url : urls) {
                            url = url.trim();
                            boolean flag = false;
                            String method = authority.getHttpMethod();
                            if (matches(url, request)) {
                                flag = method.equals(request.getMethod()) || "ALL".equals(method);
                            }
                            //有一个满足，整个urls都通过
                            if (flag) {
                                return flag;
                            }
                        }
                    }
                    return false;
                });
                if (!access) {
                    result = ACCESS_DENIED;
                }
            }
        }
        return result;
    }

    private boolean matches(String url, HttpServletRequest request) {
        boolean isMatch;
        String uri = request.getRequestURI();
        //验证公共配置
        if (uri.startsWith("/public_config/")) {
            Object versionId = request.getParameter("versionId");
            String requestUri = uri;
            if (versionId != null) {
                requestUri += "?versionId=" + versionId;
            }
            isMatch = url.startsWith(requestUri);
        }
        //验证历史记录管理
        else if (uri.startsWith("/history_log_manage/")) {
            Object versionId = request.getParameter("versionId");
            Object releaseStatus = request.getParameter("releaseStatus");
            String requestUri = uri;
            if (versionId != null) {
                requestUri += "?versionId=" + versionId;
            }
            if (releaseStatus != null) {
                if (requestUri.indexOf("?") != -1) {
                    requestUri += "&releaseStatus=" + releaseStatus;
                } else {
                    requestUri += "?releaseStatus=" + releaseStatus;
                }
            }
            isMatch = url.startsWith(requestUri);
        }
        //验证动态页
        else if (uri.startsWith("/instance/")) {
            Object productModuleEnvId = request.getParameter("productModuleEnvId");
            Object versionId = request.getParameter("versionId");
            String requestUri = uri;
            if (productModuleEnvId != null) {
                requestUri += "?productModuleEnvId=" + productModuleEnvId;
            }
            if (versionId != null) {
                if (requestUri.indexOf("?") != -1) {
                    requestUri += "&versionId=" + versionId;
                } else {
                    requestUri += "?versionId=" + versionId;
                }
            }
            isMatch = url.startsWith(requestUri);
        } else {
            AntPathRequestMatcher matcher = new AntPathRequestMatcher(url);
            isMatch = matcher.matches(request);
        }
        return isMatch;
    }

}