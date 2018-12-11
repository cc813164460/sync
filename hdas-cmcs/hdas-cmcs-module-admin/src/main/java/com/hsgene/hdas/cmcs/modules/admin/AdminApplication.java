package com.hsgene.hdas.cmcs.modules.admin;

import com.hsgene.hdas.cmcs.modules.common.datasource.DynamicDataSourceRegister;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @description: 启动配置中心web入口
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin
 * @author: maodi
 * @createDate: 2018/5/25 16:20
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
//引入动态切换数据注册类
//启动redis的session
//自动配置数据
//开启数据库事务
//扫描mapper的地址
@Import({DynamicDataSourceRegister.class})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableRedisHttpSession
@EnableTransactionManagement
@MapperScan(basePackages = "com.hsgene.hdas.cmcs.modules.admin.mapper")
public class AdminApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AdminApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(AdminApplication.class);
        application.run(args);
    }

}
