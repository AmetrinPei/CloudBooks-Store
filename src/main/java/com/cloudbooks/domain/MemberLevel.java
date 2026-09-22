package com.cloudbooks.domain;

public enum MemberLevel {
    NORMAL("普通会员",1.00),
    SILVER("白银会员",0.95),
    GOLD("黄金会员",0.90);
    private final String desc;
    private final double discountRate;
    MemberLevel(String desc, double discountRate) {
        this.desc = desc;
        this.discountRate = discountRate;
    }
    public String getDesc() {return desc;}
    public double getDiscountRate() {return discountRate;}
    public static MemberLevel fromPoints(int points){
        if(points >= 5000) return GOLD;
        if(points >= 1000) return SILVER;
        return NORMAL;
    }
}
