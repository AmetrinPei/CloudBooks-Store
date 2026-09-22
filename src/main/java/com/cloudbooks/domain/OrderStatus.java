package com.cloudbooks.domain;

public enum OrderStatus {
    PENDING("待支付"),
    PAID("已支付"),
    SHIPPED("已发货"),
    COMPLETED("已完成"),
    CANCELLED("已取消");
    private final String desc;
    OrderStatus(String desc) {this.desc = desc;}
    public String getDesc() {return desc;}
}
