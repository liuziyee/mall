drop table if exists `user`;
CREATE TABLE IF NOT EXISTS `mall`.`user` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `username` varchar(64) NOT NULL DEFAULT '' COMMENT '用户名',
    `password` varchar(256) NOT NULL DEFAULT '' COMMENT '密码(MD5)',
    `extra_info` varchar(1024) NOT NULL DEFAULT '' COMMENT '额外信息',
    `create_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '更新时间',
    `deleted` tinyint(1) default 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`)
    ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

drop table if exists `address`;
CREATE TABLE IF NOT EXISTS `mall`.`address` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '用户id',
    `receiver_name` varchar(64) NOT NULL DEFAULT '' COMMENT '收件人姓名',
    `phone` varchar(64) NOT NULL DEFAULT '' COMMENT '收件人电话',
    `province` varchar(64) NOT NULL DEFAULT '' COMMENT '省',
    `city` varchar(64) NOT NULL DEFAULT '' COMMENT '市',
    `district` varchar(64) NOT NULL DEFAULT '' COMMENT '区',
    `detail` varchar(256) NOT NULL DEFAULT '' COMMENT '详细地址',
    `create_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '更新时间',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

drop table if exists `wallet`;
CREATE TABLE IF NOT EXISTS `mall`.`wallet` (
    `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '用户id',
    `balance` bigint(20) NOT NULL DEFAULT 0 COMMENT '余额',
    `create_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '更新时间',
    PRIMARY KEY (`uid`)
    ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='用户钱包表';