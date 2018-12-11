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