DROP TABLE IF EXISTS `user`;
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

DROP TABLE IF EXISTS `address`;
CREATE TABLE IF NOT EXISTS `mall`.`address` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '用户id',
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

DROP TABLE IF EXISTS `wallet`;
CREATE TABLE IF NOT EXISTS `mall`.`wallet` (
    `user_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '用户id',
    `balance` bigint(20) NOT NULL DEFAULT 0 COMMENT '余额',
    `create_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '更新时间',
    PRIMARY KEY (`user_id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='用户钱包表';

DROP TABLE IF EXISTS `deliveryman`;
CREATE TABLE IF NOT EXISTS `mall`.`deliveryman` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '骑手id',
    `name` varchar(64) NOT NULL DEFAULT '' COMMENT '骑手名',
    `status` varchar(64) NOT NULL DEFAULT '' COMMENT '状态',
    `create_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='骑手表';

INSERT INTO `deliveryman` VALUES (10,'张全蛋','AVAILABLE','2022-01-01 00:00:00','2022-01-01 00:00:00');

DROP TABLE IF EXISTS `order`;
CREATE TABLE IF NOT EXISTS `mall`.``  (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单id',
    `user_id` bigint(20) NOT NULL COMMENT '用户id',
    `goods_id` bigint(20) NOT NULL COMMENT '商品id',
    `address_id` bigint(20) NOT NULL COMMENT '地址id',
    `deliveryman_id` bigint(20) NOT NULL COMMENT '骑手id',
    `settlement_id` bigint(20) NOT NULL COMMENT '结算id',
    `reward_record_id` bigint(20) NOT NULL COMMENT '积分记录id',
    `pay_amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
    `status` varchar(64) NOT NULL DEFAULT '' COMMENT '状态',
    `create_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

DROP TABLE IF EXISTS `goods`;
CREATE TABLE IF NOT EXISTS `mall`.`goods`  (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品id',
    `name` varchar(64) NOT NULL DEFAULT '' COMMENT '商品名',
    `price` decimal(5, 2) NULL DEFAULT NULL COMMENT '单价',
    `shop_id` int(0) NULL DEFAULT NULL COMMENT '地址',
    `status` varchar(64) NOT NULL DEFAULT '' COMMENT '状态',
    `create_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

DROP TABLE IF EXISTS `shop`;
CREATE TABLE IF NOT EXISTS `mall`.`shop`  (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商家id',
    `name` varchar(64) NOT NULL DEFAULT '' COMMENT '商家名',
    `address` varchar(256) NOT NULL DEFAULT '' COMMENT '地址',
    `settlement_id` bigint(20) NOT NULL COMMENT '结算id',
    `status` varchar(64) NOT NULL DEFAULT '' COMMENT '状态',
    `create_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='商家表';

INSERT INTO `shop` VALUES (10,'KFC','北京市朝阳区',10,'OPEN','2022-01-01 00:00:00','2022-01-01 00:00:00');

DROP TABLE IF EXISTS `reward_record`;
CREATE TABLE IF NOT EXISTS `mall`.`reward_record`  (
    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_id` bigint(20) NOT NULL COMMENT '用户id',
    `amount` decimal(5, 2) NOT NULL COMMENT '积分数',
    `source` varchar(64) NOT NULL COMMENT '来源',
    `source_id` bigint(20) NOT NULL COMMENT '来源id',
    `status` varchar(64) NOT NULL DEFAULT '' COMMENT '状态',
    `create_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

DROP TABLE IF EXISTS `settlement`;
CREATE TABLE `settlement`  (
    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '结算id',
    `order_id` int(0) NULL DEFAULT NULL COMMENT '订单id',
    `transaction_id` int(0) NULL DEFAULT NULL COMMENT '交易id',
    `amount` decimal(9, 2) NULL DEFAULT NULL COMMENT '金额',
    `status` varchar(64) NOT NULL DEFAULT '' COMMENT '状态',
    `create_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='结算表';