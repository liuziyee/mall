package com.dorohedoro.util;

public enum OrderStatus {
    ORDER_CREATING(0L, "创建中"),
    SHOP_CONFIRMED(10L, "商家确认中"),
    DELIVERYMAN_CONFIRMED(20L, "骑手确认中"),
    SETTLEMENT_CONFIRMED(30L, "已结算"),
    ORDER_CREATED(40L, "已创建"),
    FAILED(50L, "创建失败");

    private final Long code;
    private final String desc;

    OrderStatus(Long code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Long getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}
