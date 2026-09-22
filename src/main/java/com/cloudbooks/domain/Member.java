package com.cloudbooks.domain;
import java.io.Serializable;



/**
 * @description://TODO create:2026/9/22 14:11
 * author:lenovo
 * version：V1.0
 **/
public class Member implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private MemberLevel level = MemberLevel.NORMAL;
    private double balance;
    private int points;
    public Member(){}
    public Member(String id,String name){
        this.id = id;
        this.name = name;
    }
    public void addPoints(int points){
        this.points += points;
        this.level = MemberLevel.fromPoints(this.points);
    }
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public MemberLevel getLevel() {return level;}
    public void setLevel(MemberLevel level) {this.level = level;}
    public double getBalance() {return balance;}
    public void setBalance(double balance) {this.balance = balance;}
    public int getPoints() {return points;}
    @Override
    public String toString() {
        return String.format(
                "[%s]%s(%s)余额:%.2f积分:%d",
                id,name,level.getDesc(),balance,points
        );
    }

}
