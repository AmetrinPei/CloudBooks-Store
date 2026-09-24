package com.cloudbooks.domain;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * @description://TODO create:2026/9/22 14:45
 * author:lenovo
 * version：V1.0
 **/
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    private String orderId;
    private List<OrderItem> items =  new ArrayList<>();
    private double totalAmount;
    private OrderStatus status = OrderStatus.PENDING;
    private LocalDateTime createTime;
    public Order() {}
    public Order(String orderId,List<OrderItem> items,double totalAmount) {
        this.orderId = orderId;
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
        this.createTime = LocalDateTime.now();
    }
    public String getOrderId() {return orderId;}
    public List<OrderItem> getItems() {return items;}
    public double getTotalAmount() {return totalAmount;}
    public OrderStatus getStatus() {return status;}
    public void setStatus(OrderStatus status) {this.status = status;}
    public LocalDateTime getCreateTime() {return createTime;}
    @Override
    public String toString(){
        return String.format("[%s] 金额:%.2f 状态:%s", orderId, totalAmount, status.getDesc());
    }
}
