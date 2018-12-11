/*
Navicat MySQL Data Transfer

Source Server         : 192.168.10.105
Source Server Version : 50710
Source Host           : 192.168.10.105:3306
Source Database       : configcenterdb

Target Server Type    : MYSQL
Target Server Version : 50710
File Encoding         : 65001

Date: 2018-09-18 16:47:21
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for app
-- ----------------------------
DROP TABLE IF EXISTS `app`;
CREATE TABLE `app` (
  `id` bigint(11) NOT NULL COMMENT '主键',
  `ip` varchar(255) DEFAULT NULL,
  `hostname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='实例表';

-- ----------------------------
-- Records of app
-- ----------------------------
INSERT INTO `app` VALUES ('0', '192.168.10.156', 'DESKTOP-8RT6NG5');
INSERT INTO `app` VALUES ('1', '192.168.10.155', 'DESKTOP-155');
INSERT INTO `app` VALUES ('2', '192.168.10.154', 'DESKTOP-154');
INSERT INTO `app` VALUES ('3', '192.168.10.153', 'DESKTOP-153');

-- ----------------------------
-- Table structure for area
-- ----------------------------
DROP TABLE IF EXISTS `area`;
CREATE TABLE `area` (
  `id` bigint(11) NOT NULL COMMENT '主键',
  `name` varchar(255) DEFAULT NULL COMMENT '区域名称',
  `create_date_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分布表';

-- ----------------------------
-- Records of area
-- ----------------------------

-- ----------------------------
-- Table structure for auth
-- ----------------------------
DROP TABLE IF EXISTS `auth`;
CREATE TABLE `auth` (
  `id` bigint(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` text,
  `is_delete` int(1) NOT NULL DEFAULT '0' COMMENT '是否可用 0代表可用，1代表已经禁止',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='权限表';

-- ----------------------------
-- Records of auth
-- ----------------------------
INSERT INTO `auth` VALUES ('0', '查看', '查看权限', '0');
INSERT INTO `auth` VALUES ('1', '添加', '添加权限', '0');
INSERT INTO `auth` VALUES ('2', '编辑', '编辑权限', '0');
INSERT INTO `auth` VALUES ('3', '删除', '删除权限', '0');
INSERT INTO `auth` VALUES ('4', '发布', '发布权限', '0');
INSERT INTO `auth` VALUES ('5', '下线', '下线权限', '0');

-- ----------------------------
-- Table structure for auth_resource
-- ----------------------------
DROP TABLE IF EXISTS `auth_resource`;
CREATE TABLE `auth_resource` (
  `id` bigint(11) NOT NULL,
  `auth_id` bigint(11) DEFAULT NULL COMMENT '权限ID',
  `resource_id` bigint(11) DEFAULT NULL COMMENT '资源ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='权限资源关系表';

-- ----------------------------
-- Records of auth_resource
-- ----------------------------

-- ----------------------------
-- Table structure for class
-- ----------------------------
DROP TABLE IF EXISTS `class`;
CREATE TABLE `class` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of class
-- ----------------------------
INSERT INTO `class` VALUES ('0', '一般');
INSERT INTO `class` VALUES ('1', '保密');
INSERT INTO `class` VALUES ('2', '机密');

-- ----------------------------
-- Table structure for env
-- ----------------------------
DROP TABLE IF EXISTS `env`;
CREATE TABLE `env` (
  `id` bigint(11) NOT NULL COMMENT '主键',
  `name` varchar(255) DEFAULT NULL COMMENT '环境名称',
  `create_date_time` datetime DEFAULT NULL COMMENT '创建日期',
  `description` text COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='环境表';

-- ----------------------------
-- Records of env
-- ----------------------------

-- ----------------------------
-- Table structure for env_module_product
-- ----------------------------
DROP TABLE IF EXISTS `env_module_product`;
CREATE TABLE `env_module_product` (
  `id` bigint(20) NOT NULL,
  `env_id` bigint(20) DEFAULT NULL,
  `module_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of env_module_product
-- ----------------------------

-- ----------------------------
-- Table structure for env_product
-- ----------------------------
DROP TABLE IF EXISTS `env_product`;
CREATE TABLE `env_product` (
  `id` bigint(11) NOT NULL,
  `env_id` bigint(11) DEFAULT NULL,
  `product_id` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='环境项目关系表';

-- ----------------------------
-- Records of env_product
-- ----------------------------

-- ----------------------------
-- Table structure for instance
-- ----------------------------
DROP TABLE IF EXISTS `instance`;
CREATE TABLE `instance` (
  `id` bigint(32) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `resource_id` bigint(32) DEFAULT NULL,
  `is_delete` int(11) DEFAULT NULL,
  `instance_type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='配置表';

-- ----------------------------
-- Records of instance
-- ----------------------------

-- ----------------------------
-- Table structure for instance_type
-- ----------------------------
DROP TABLE IF EXISTS `instance_type`;
CREATE TABLE `instance_type` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='配置类型表';

-- ----------------------------
-- Records of instance_type
-- ----------------------------
INSERT INTO `instance_type` VALUES ('0', 'properties');
INSERT INTO `instance_type` VALUES ('1', 'json');
INSERT INTO `instance_type` VALUES ('2', 'xml');
INSERT INTO `instance_type` VALUES ('3', 'yml');

-- ----------------------------
-- Table structure for item
-- ----------------------------
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item` (
  `id` bigint(20) NOT NULL,
  `version_num` varchar(32) DEFAULT NULL,
  `instance_id` bigint(32) DEFAULT NULL,
  `key` varchar(255) DEFAULT NULL,
  `value` longtext,
  `comment` text,
  `line_num` int(11) DEFAULT NULL,
  `is_delete` int(11) DEFAULT NULL,
  `release_status` int(11) DEFAULT NULL,
  `last_update_by` bigint(11) DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `version_id` int(11) DEFAULT NULL,
  `is_public` int(11) DEFAULT '0',
  `instance_type_id` bigint(11) DEFAULT NULL,
  `class_id` bigint(11) DEFAULT NULL,
  `env_id` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `must_change` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='条目表';

-- ----------------------------
-- Records of item
-- ----------------------------

-- ----------------------------
-- Table structure for item_app
-- ----------------------------
DROP TABLE IF EXISTS `item_app`;
CREATE TABLE `item_app` (
  `id` bigint(11) NOT NULL,
  `app_id` bigint(11) DEFAULT NULL,
  `item_id` bigint(11) DEFAULT NULL,
  `is_delete` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='条目实例关系表';

-- ----------------------------
-- Records of item_app
-- ----------------------------

-- ----------------------------
-- Table structure for item_resource
-- ----------------------------
DROP TABLE IF EXISTS `item_resource`;
CREATE TABLE `item_resource` (
  `id` bigint(32) NOT NULL,
  `item_id` bigint(32) DEFAULT NULL,
  `resource_id` bigint(32) DEFAULT NULL,
  `is_delete` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='条目资源关系表';

-- ----------------------------
-- Records of item_resource
-- ----------------------------

-- ----------------------------
-- Table structure for module
-- ----------------------------
DROP TABLE IF EXISTS `module`;
CREATE TABLE `module` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `create_date_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `update_date_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of module
-- ----------------------------

-- ----------------------------
-- Table structure for module_product
-- ----------------------------
DROP TABLE IF EXISTS `module_product`;
CREATE TABLE `module_product` (
  `id` bigint(20) NOT NULL,
  `module_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of module_product
-- ----------------------------

-- ----------------------------
-- Table structure for organ
-- ----------------------------
DROP TABLE IF EXISTS `organ`;
CREATE TABLE `organ` (
  `id` bigint(11) NOT NULL COMMENT '主键',
  `name` varchar(255) DEFAULT NULL COMMENT '组织名称',
  `create_date_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='部门表';

-- ----------------------------
-- Records of organ
-- ----------------------------

-- ----------------------------
-- Table structure for organ_area
-- ----------------------------
DROP TABLE IF EXISTS `organ_area`;
CREATE TABLE `organ_area` (
  `id` bigint(11) NOT NULL,
  `organ_id` bigint(11) DEFAULT NULL,
  `area_id` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='部门分布关系表';

-- ----------------------------
-- Records of organ_area
-- ----------------------------

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` bigint(11) NOT NULL COMMENT '主键',
  `name` varchar(255) DEFAULT NULL COMMENT '业务系统名称',
  `user_id` bigint(11) DEFAULT NULL COMMENT '用户ID',
  `area_id` bigint(11) DEFAULT NULL COMMENT '区域ID',
  `organ_id` bigint(11) DEFAULT NULL COMMENT '组织ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目表';

-- ----------------------------
-- Records of product
-- ----------------------------

-- ----------------------------
-- Table structure for resource
-- ----------------------------
DROP TABLE IF EXISTS `resource`;
CREATE TABLE `resource` (
  `id` bigint(20) NOT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `module_id` bigint(20) DEFAULT NULL,
  `env_id` bigint(20) DEFAULT NULL,
  `version_id` bigint(20) DEFAULT NULL,
  `is_delete` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='资源表';

-- ----------------------------
-- Records of resource
-- ----------------------------

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` bigint(11) NOT NULL COMMENT '主键',
  `name` varchar(255) DEFAULT NULL COMMENT '角色名称',
  `description` text,
  `is_delete` int(1) DEFAULT '0' COMMENT '0代表可用，1代表不可用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
-- Records of role
-- ----------------------------

-- ----------------------------
-- Table structure for role_auth_resource
-- ----------------------------
DROP TABLE IF EXISTS `role_auth_resource`;
CREATE TABLE `role_auth_resource` (
  `id` bigint(11) NOT NULL,
  `ar_id` bigint(11) DEFAULT NULL COMMENT '权限ID',
  `role_id` bigint(11) DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色权限资源关系表';

-- ----------------------------
-- Records of role_auth_resource
-- ----------------------------

-- ----------------------------
-- Table structure for role_class
-- ----------------------------
DROP TABLE IF EXISTS `role_class`;
CREATE TABLE `role_class` (
  `id` bigint(20) DEFAULT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  `class_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role_class
-- ----------------------------

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` bigint(20) NOT NULL,
  `role_id` bigint(11) DEFAULT NULL,
  `url` longtext,
  `method` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统权限表';

-- ----------------------------
-- Records of sys_permission
-- ----------------------------

-- ----------------------------
-- Table structure for sys_resource
-- ----------------------------
DROP TABLE IF EXISTS `sys_resource`;
CREATE TABLE `sys_resource` (
  `id` bigint(11) NOT NULL,
  `one_name` varchar(255) DEFAULT NULL,
  `two_name` varchar(255) DEFAULT NULL,
  `three_name` varchar(255) DEFAULT NULL,
  `four_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统固定资源表';

-- ----------------------------
-- Records of sys_resource
-- ----------------------------
INSERT INTO `sys_resource` VALUES ('9', '公共配置', '无', '无', '无');
INSERT INTO `sys_resource` VALUES ('10', '公共配置', '灰度版本', '无', '无');
INSERT INTO `sys_resource` VALUES ('11', '历史记录管理', '无', '已发布', '无');
INSERT INTO `sys_resource` VALUES ('12', '历史记录管理', '无', '未发布', '无');
INSERT INTO `sys_resource` VALUES ('13', '历史记录管理', '灰度版本', '已发布', '无');
INSERT INTO `sys_resource` VALUES ('14', '历史记录管理', '灰度版本', '未发布', '无');
INSERT INTO `sys_resource` VALUES ('15', '设置', '项目管理', '无', '无');
INSERT INTO `sys_resource` VALUES ('16', '设置', '模块管理', '无', '无');
INSERT INTO `sys_resource` VALUES ('17', '设置', '环境管理', '无', '无');
INSERT INTO `sys_resource` VALUES ('18', '设置', '分布管理', '无', '无');
INSERT INTO `sys_resource` VALUES ('19', '设置', '部门管理', '无', '无');
INSERT INTO `sys_resource` VALUES ('20', '设置', '角色管理', '无', '无');
INSERT INTO `sys_resource` VALUES ('21', '设置', '人员管理', '无', '无');
INSERT INTO `sys_resource` VALUES ('22', '设置', '使用权限管理', '无', '无');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(11) NOT NULL COMMENT '主键',
  `user_name` varchar(255) DEFAULT NULL COMMENT '用户名字',
  `nick_name` varchar(255) DEFAULT NULL COMMENT '昵称',
  `mobile` varchar(255) DEFAULT NULL COMMENT '电话',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `create_date_time` datetime DEFAULT NULL COMMENT '创建时间',
  `is_delete` int(1) DEFAULT NULL COMMENT '0代表用户未被禁止；1代表用户禁止',
  `password` varchar(255) DEFAULT NULL,
  `last_date_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('0', 'admin', 'admin', '13245678900', '123456@qq.com', '2018-09-04 16:28:47', '0', 'PxSIisW/nmHjR4/ExiMtMg==', '2018-09-18 15:26:40');

-- ----------------------------
-- Table structure for user_area
-- ----------------------------
DROP TABLE IF EXISTS `user_area`;
CREATE TABLE `user_area` (
  `id` bigint(11) NOT NULL,
  `user_id` bigint(11) DEFAULT NULL,
  `area_id` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户分布关系表';

-- ----------------------------
-- Records of user_area
-- ----------------------------

-- ----------------------------
-- Table structure for user_organ
-- ----------------------------
DROP TABLE IF EXISTS `user_organ`;
CREATE TABLE `user_organ` (
  `id` bigint(11) NOT NULL,
  `user_id` bigint(11) DEFAULT NULL,
  `organ_id` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户部门关系表';

-- ----------------------------
-- Records of user_organ
-- ----------------------------

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `id` bigint(11) NOT NULL COMMENT '主键',
  `user_id` varchar(255) DEFAULT NULL COMMENT '用户ID',
  `role_id` varchar(255) DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户角色关系表';

-- ----------------------------
-- Records of user_role
-- ----------------------------

-- ----------------------------
-- Table structure for version
-- ----------------------------
DROP TABLE IF EXISTS `version`;
CREATE TABLE `version` (
  `id` bigint(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='版本表';

-- ----------------------------
-- Records of version
-- ----------------------------
INSERT INTO `version` VALUES ('0', '无');
INSERT INTO `version` VALUES ('1', '灰度版本');
