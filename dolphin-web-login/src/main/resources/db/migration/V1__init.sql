/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50619
Source Host           : localhost:3306
Source Database       : dolphin-login

Target Server Type    : MYSQL
Target Server Version : 50619
File Encoding         : 65001

Date: 2015-05-14 14:58:29
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'admin@test.com', 'e00cf25ad42683b3df678c61f42c6bda');
INSERT INTO `user` VALUES ('2', 'user@test.com', '7e58d63b60197ceb55a1c487989a3720');
