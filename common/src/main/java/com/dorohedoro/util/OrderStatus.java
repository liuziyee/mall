package com.dorohedoro.util;

public enum OrderStatus {
    CREATING, // 创建中
    SHOP_CONFIRMED, // 商家确认中
    DELIVERYMAN_CONFIRMED, // 骑手确认中
    SETTLEMENT_CONFIRMED, // 已结算
    CREATED, // 已创建
    FAILED; // 创建失败
}
