package com.cloudbooks.domain;
import java.util.ArrayList;
import java.util.List;
/**
 * @description://TODO create:2026/9/22 14:45
 * author:lenovo
 * version：V1.0
 **/
public class ShoppingCart {
    private final Member member;
    private final List<OrderItem> items=new ArrayList<>();
    public ShoppingCart(Member member) {
        this.member = member;
    }
    public void addBook(Book book,int quantity) {
        for(OrderItem item : items){
            if(item.getBook().getIsbn().equals(book.getIsbn())){
                item.setQuantity(item.getQuantity()+quantity);
                return;
            }
        }
        items.add(new OrderItem(book,quantity));
    }
    public double calcOriginalTotal() {
        double total=0.0;
        for(OrderItem item : items){
            total+=item.calcSubtotal();
        }
        return total;
    }
    public boolean isEmpty() {return items.isEmpty();}
    public void clear(){items.clear();}
    public Member getMember() {return member;}
    public List<OrderItem> getItems() {return items;}
}
