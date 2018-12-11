package com.hsgene.hdas.api.auth.common;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.FilterInvocation;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @description:
 * @projectName: hdas-api-auth
 * @package: com.hsgene.hdas.api.auth.common
 * @author: maodi
 * @createDate: 2018/10/8 11:43
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ApiAuthConfigurationProperties.class)
public class ApiInterceptor implements HandlerInterceptor, Filter {

    private DruidDataSource dataSource = null;

    @Resource
    ApiAuthConfigurationProperties properties;

    public ApiInterceptor() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Bean
    @ConditionalOnMissingBean
    public MysqlUtil initMysqlUtil() throws Exception {
        MysqlUtil mysqlUtil = new MysqlUtil(properties.getMysqlProductTag(), properties.getMysqlModuleTag(),
                properties.getMysqlHost(), properties.getMysqlPort(), properties.getMysqlDatabase(), properties
                .getMysqlUser(), properties.getMysqlPassword(), properties.getMysqlTable(), properties.getAccessKey()
                , properties.getSecretKey());
        return mysqlUtil;
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisUtil initRedisUtil() throws Exception {
        RedisUtil redisUtil = new RedisUtil(properties.getRedisProductTag(), properties.getRedisModuleTag(),
                properties.getRedisHost(), properties.getRedisPort(), properties.getRedisIndex(), properties
                .getRedisPassword(), properties.getAccessKey(), properties.getSecretKey());
        return redisUtil;
    }

    public String getPassword(String username) {
        String password = null;
        try {
            if (dataSource == null || dataSource.isClosed()) {
                dataSource = new DruidDataSource();
                dataSource.setUrl(properties.getUserConnection());
            }
            Connection con = dataSource.getConnection();
            ResultSet rs = con.prepareStatement("select " + properties.getPasswordColumn() + " from " + properties
                    .getUserTable() + " where " + properties.getUsernameColumn() + " = '" + username + "'")
                    .executeQuery();
            if (rs.next()) {
                password = rs.getString(1);
            }
            rs.close();
            con.close();
        } catch (Exception e) {
            log.error("获取当前用户密码出错", e);
        }
        return password;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            IDatabaseUtil idu;
            if (StringUtils.isNotBlank(properties.getMysqlHost())) {
                idu = initMysqlUtil();
            } else {
                idu = initRedisUtil();
            }
            //强制类型转换request
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            HttpSession session = httpServletRequest.getSession();
            String username = String.valueOf(session.getAttribute(properties.getSessionUserTag()));
            String password = getPassword(username);
            //将request转换为requestWrapper，可以多次使用requestWrapper.getInputStream()
            BodyReaderHttpServletRequestWrapper requestWrapper = new BodyReaderHttpServletRequestWrapper
                    (httpServletRequest);
            String containsAccessKey = properties.getContainsAccessKey();
            String tokenUrl = properties.getTokenUrl();
            String token = HttpUtil.getValueByHttpGet(tokenUrl);
            List<String> permitUrls = new ArrayList<>();
            if (StringUtils.isNotBlank(properties.getPermitUrlStr())) {
                permitUrls = Arrays.asList(properties.getPermitUrlStr().split(Constant.COMMA));
            }
            if (StringUtils.isNotBlank(containsAccessKey) && Constant.FALSE.equals(containsAccessKey)) {
                ApiAuthUse.judgeRequestNoAccessKey(requestWrapper, token, password, permitUrls);
            } else {
                ApiAuthUse.judgeRequestContainsAccessKey(requestWrapper, token, permitUrls, idu);
            }
            //这里将处理过的requestWrapper传入后续操作，可以多次使用requestWrapper.getInputStream()
            FilterInvocation fi = new FilterInvocation(requestWrapper, servletResponse, filterChain);
            invoke(fi);
        } catch (Exception e) {
            log.warn("鉴权失败", e);
            return;
        }
    }

    public void invoke(FilterInvocation fi) throws IOException, ServletException {
        /**
         * fi里面有一个被拦截的url
         * 里面调用AuthMetadataSourceService的getAttributes(Object object)这个方法获取fi对应的所有权限
         * 再调用AuthAccessDecisionManager的decide方法来校验用户的权限是否足够
         */
        //执行下一个拦截器
        fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object
            o) throws Exception {
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {

    }
}
