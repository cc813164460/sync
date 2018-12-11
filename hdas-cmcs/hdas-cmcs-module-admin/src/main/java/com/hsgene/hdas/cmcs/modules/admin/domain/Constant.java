package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description:
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/6/19 10:36
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class Constant {


    public final static String SELECT_CN = "查看";
    public final static String INSERT_CN = "添加";
    public final static String UPDATE_CN = "编辑";
    public final static String DELETE_CN = "删除";
    public final static String RELEASE_CN = "发布";
    public final static String OFFLINE_CN = "下线";

    public final static String SELECT_EN = "select";
    public final static String INSERT_EN = "insert";
    public final static String UPDATE_EN = "update";
    public final static String DELETE_EN = "delete";
    public final static String RELEASE_EN = "release";
    public final static String OFFLINE_EN = "offline";

    public final static String SESSION_KEY = "user";

    public final static String UNDEFINED = "undefined";

    public final static List<ConfigAuthUrl> CONFIG_AUTH_URL_LIST = new ArrayList<>();

    static {
        for (int i = 9; i <= 22; i++) {
            for (int j = 0; j <= 5; j++) {
                ConfigAuthUrl cfau = new ConfigAuthUrl();
                cfau.setResourceId(i);
                cfau.setAuthId(j);
                Set<String> urlSet = new HashSet<>();
                switch (i) {
                    case 9:
                        urlSet.add("/public_config/query_properties?versionId=0");
                        urlSet.add("/public_config/query_exclude_properties?versionId=0");
                        urlSet.add("/public_config.html");
                        urlSet.add("/public_config/query_by_name?versionId=0");
                        urlSet.add("/public_config/get_product_module_list?versionId=0");
                        urlSet.add("/public_config/download?versionId=0");
                        urlSet.add("/env/env_data");
                        urlSet.add("/module/module_data");
                        urlSet.add("/public_config/get_all_key_and_value");
                        switch (j) {
                            case 1:
                                urlSet.add("/public_config_add.html");
                                urlSet.add("/public_config_copy_config.html");
                                urlSet.add("/public_config/insert?versionId=0");
                                urlSet.add("/public_config/upload?versionId=0");
                                urlSet.add("/public_config/insert_copy_config?version=0");
                                break;
                            case 2:
                                urlSet.add("/public_config_update.html");
                                urlSet.add("/public_config_copy_config.html");
                                urlSet.add("/public_config/update?versionId=0");
                                urlSet.add("/public_config/upload?versionId=0");
                                urlSet.add("/public_config/insert_copy_config?version=0");
                                break;
                            case 3:
                                urlSet.add("/public_config/delete_by_ids?versionId=0");
                                break;
                            case 4:
                                urlSet.add("/public_config/release?versionId=0");
                                break;
                            case 5:
                                urlSet.add("/public_config/offline?versionId=0");
                                break;
                            default:
                                break;
                        }
                        cfau.setUrlSet(urlSet);
                        CONFIG_AUTH_URL_LIST.add(cfau);
                        break;
                    case 10:
                        urlSet.add("/public_config/query_properties?versionId=1");
                        urlSet.add("/public_config/query_exclude_properties?versionId=1");
                        urlSet.add("/public_config.html");
                        urlSet.add("/public_config/query_by_name?versionId=1");
                        urlSet.add("/public_config/get_product_module_list?versionId=1");
                        urlSet.add("/public_config/download?versionId=1");
                        urlSet.add("/env/env_data");
                        urlSet.add("/module/module_data");
                        switch (j) {
                            case 1:
                                urlSet.add("/public_config_gray_add.html");
                                urlSet.add("/public_config_gray_copy_config.html");
                                urlSet.add("/public_config/insert?versionId=1");
                                urlSet.add("/public_config/upload?versionId=1");
                                urlSet.add("/public_config/insert_copy_config?version=1");
                                break;
                            case 2:
                                urlSet.add("/public_config_gray_update.html");
                                urlSet.add("/public_config_gray_copy_config.html");
                                urlSet.add("/public_config/update?versionId=1");
                                urlSet.add("/public_config/upload?versionId=1");
                                urlSet.add("/public_config/insert_copy_config?version=1");
                                break;
                            case 3:
                                urlSet.add("/public_config/delete_by_ids?versionId=1");
                                break;
                            case 4:
                                urlSet.add("/public_config/release?versionId=1");
                                break;
                            case 5:
                                urlSet.add("/public_config/offline?versionId=1");
                                break;
                            default:
                                break;
                        }
                        cfau.setUrlSet(urlSet);
                        CONFIG_AUTH_URL_LIST.add(cfau);
                        break;
                    case 11:
                        urlSet.add("/history_log_manage/query?versionId=0&releaseStatus=1");
                        urlSet.add("/history_log_manage_main.html");
                        urlSet.add("/history_log_manage/query_by_condition?versionId=0&releaseStatus=1");
                        urlSet.add("/user/user_data");
                        urlSet.add("/product/product_data");
                        switch (j) {
                            case 4:
                                urlSet.add("/history_log_manage/release?versionId=0");
                                break;
                            default:
                                break;
                        }
                        cfau.setUrlSet(urlSet);
                        CONFIG_AUTH_URL_LIST.add(cfau);
                        break;
                    case 12:
                        urlSet.add("/history_log_manage/query?versionId=0&releaseStatus=0");
                        urlSet.add("/history_log_manage_main.html");
                        urlSet.add("/history_log_manage/query_by_condition?versionId=0&releaseStatus=0");
                        urlSet.add("/user/user_data");
                        urlSet.add("/product/product_data");
                        switch (j) {
                            case 5:
                                urlSet.add("/history_log_manage/offline?versionId=0");
                                break;
                            default:
                                break;
                        }
                        cfau.setUrlSet(urlSet);
                        CONFIG_AUTH_URL_LIST.add(cfau);
                        break;
                    case 13:
                        urlSet.add("/history_log_manage/query?versionId=1&releaseStatus=1");
                        urlSet.add("/history_log_manage_gray.html");
                        urlSet.add("/history_log_manage/query_by_condition?versionId=1&releaseStatus=1");
                        urlSet.add("/user/user_data");
                        urlSet.add("/product/product_data");
                        switch (j) {
                            case 4:
                                urlSet.add("/history_log_manage/release?versionId=1");
                                break;
                            default:
                                break;
                        }
                        cfau.setUrlSet(urlSet);
                        CONFIG_AUTH_URL_LIST.add(cfau);
                        break;
                    case 14:
                        urlSet.add("/history_log_manage/query?versionId=1&releaseStatus=0");
                        urlSet.add("/history_log_manage_gray.html");
                        urlSet.add("/history_log_manage/query_by_condition?versionId=1&releaseStatus=0");
                        urlSet.add("/user/user_data");
                        urlSet.add("/product/product_data");
                        switch (j) {
                            case 5:
                                urlSet.add("/history_log_manage/offline?versionId=1");
                                break;
                            default:
                                break;
                        }
                        cfau.setUrlSet(urlSet);
                        CONFIG_AUTH_URL_LIST.add(cfau);
                        break;
                    case 15:
                        urlSet.add("/product/query");
                        urlSet.add("/product.html");
                        urlSet.add("/product/query_by_name");
                        switch (j) {
                            case 1:
                                urlSet.add("/product_add.html");
                                urlSet.add("/product/insert");
                                urlSet.add("/area/area_data");
                                urlSet.add("/organ/organ_data");
                                urlSet.add("/user/user_data");
                                break;
                            case 2:
                                urlSet.add("/product_update.html");
                                urlSet.add("/product/update");
                                urlSet.add("/area/area_data");
                                urlSet.add("/organ/organ_data");
                                urlSet.add("/user/user_data");
                                break;
                            case 3:
                                urlSet.add("/product/delete_by_ids");
                                break;
                            default:
                                break;
                        }
                        cfau.setUrlSet(urlSet);
                        CONFIG_AUTH_URL_LIST.add(cfau);
                        break;
                    case 16:
                        urlSet.add("/module_product/query");
                        urlSet.add("/module_product.html");
                        urlSet.add("/module_product/query_by_name");
                        switch (j) {
                            case 1:
                                urlSet.add("/module_product_add.html");
                                urlSet.add("/module_product/insert");
                                urlSet.add("/product/product_data");
                                break;
                            case 2:
                                urlSet.add("/module_product_update.html");
                                urlSet.add("/module_product/update");
                                urlSet.add("/product/product_data");
                                break;
                            case 3:
                                urlSet.add("/module_product/delete_by_ids");
                                break;
                            default:
                                break;
                        }
                        cfau.setUrlSet(urlSet);
                        CONFIG_AUTH_URL_LIST.add(cfau);
                        break;
                    case 17:
                        urlSet.add("/env_module_product/query");
                        urlSet.add("/env_module_product.html");
                        urlSet.add("/env_module_product/query_by_name");
                        switch (j) {
                            case 1:
                                urlSet.add("/env_module_product_add.html");
                                urlSet.add("/env_module_product/insert");
                                urlSet.add("/env_module_product/product_module_data");
                                break;
                            case 2:
                                urlSet.add("/env_module_product_update.html");
                                urlSet.add("/env_module_product/update");
                                urlSet.add("/env_module_product/product_module_data");
                                break;
                            case 3:
                                urlSet.add("/env_module_product/delete_by_ids");
                                break;
                            default:
                                break;
                        }
                        cfau.setUrlSet(urlSet);
                        CONFIG_AUTH_URL_LIST.add(cfau);
                        break;
                    case 18:
                        urlSet.add("/area/query");
                        urlSet.add("/area.html");
                        urlSet.add("/area/query_by_name");
                        switch (j) {
                            case 1:
                                urlSet.add("/area_add.html");
                                urlSet.add("/area/insert");
                                break;
                            case 2:
                                urlSet.add("/area_update.html");
                                urlSet.add("/area/update");
                                break;
                            case 3:
                                urlSet.add("/area/delete_by_ids");
                                break;
                            default:
                                break;
                        }
                        cfau.setUrlSet(urlSet);
                        CONFIG_AUTH_URL_LIST.add(cfau);
                        break;
                    case 19:
                        urlSet.add("/organ_area/query");
                        urlSet.add("/organ_area.html");
                        urlSet.add("/organ_area/query_by_name");
                        switch (j) {
                            case 1:
                                urlSet.add("/organ_area_add.html");
                                urlSet.add("/organ_area/insert");
                                urlSet.add("/area/area_data");
                                break;
                            case 2:
                                urlSet.add("/organ_area_update.html");
                                urlSet.add("/organ_area/update");
                                urlSet.add("/area/area_data");
                                break;
                            case 3:
                                urlSet.add("/organ_area/delete_by_ids");
                                break;
                            default:
                                break;
                        }
                        cfau.setUrlSet(urlSet);
                        CONFIG_AUTH_URL_LIST.add(cfau);
                        break;
                    case 20:
                        urlSet.add("/role/query");
                        urlSet.add("/role.html");
                        urlSet.add("/role/query_by_name");
                        switch (j) {
                            case 1:
                                urlSet.add("/role_add.html");
                                urlSet.add("/role/insert");
                                break;
                            case 2:
                                urlSet.add("/role_update.html");
                                urlSet.add("/role/update");
                                break;
                            case 3:
                                urlSet.add("/role/delete_by_ids");
                                break;
                            default:
                                break;
                        }
                        cfau.setUrlSet(urlSet);
                        CONFIG_AUTH_URL_LIST.add(cfau);
                        break;
                    case 21:
                        urlSet.add("/user/query");
                        urlSet.add("/user.html");
                        urlSet.add("/user/query_by_name");
                        switch (j) {
                            case 1:
                                urlSet.add("/user_add.html");
                                urlSet.add("/user/insert");
                                urlSet.add("/area/area_data");
                                urlSet.add("/organ/organ_data");
                                urlSet.add("/role/role_data");
                                break;
                            case 2:
                                urlSet.add("/user_update.html");
                                urlSet.add("/user/update");
                                urlSet.add("/area/area_data");
                                urlSet.add("/organ/organ_data");
                                urlSet.add("/role/role_data");
                                break;
                            case 3:
                                urlSet.add("/user/delete_by_ids");
                                break;
                            default:
                                break;
                        }
                        cfau.setUrlSet(urlSet);
                        CONFIG_AUTH_URL_LIST.add(cfau);
                        break;
                    case 22:
                        urlSet.add("/role_auth_resource/query");
                        urlSet.add("/role_auth_resource.html");
                        urlSet.add("/role_auth_resource/query_by_name");
                        switch (j) {
                            case 1:
                                urlSet.add("/role_auth_resource_add.html");
                                urlSet.add("/role_auth_resource/insert");
                                urlSet.add("/role/role_data");
                                urlSet.add("/role_auth_resource/query_action");
                                break;
                            case 2:
                                urlSet.add("/role_auth_resource_update.html");
                                urlSet.add("/role_auth_resource/update");
                                urlSet.add("/role/role_data");
                                urlSet.add("/role_auth_resource/query_action");
                                break;
                            case 3:
                                urlSet.add("/role_auth_resource/delete_by_ids");
                                break;
                            default:
                                break;
                        }
                        cfau.setUrlSet(urlSet);
                        CONFIG_AUTH_URL_LIST.add(cfau);
                        break;
                    default:
                        break;
                }
            }
        }
    }

}
