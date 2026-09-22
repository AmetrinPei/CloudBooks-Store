package com.cloudbooks.domain;
import java.io.Serializable;
public class OrderItem implements Serializable{
    private static final long serialVersionUID = 1L;
    private Book book;
    private int quantity;
    private double unitPrice;
    public OrderItem(){}
    public OrderItem(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
        this.unitPrice = book.getPrice();
    }
    public double calcSubtotal(){
        return unitPrice * quantity;
    }
    public Book getBook() {return book;}
    public int  getQuantity() {return quantity;}
    public void setQuantity(int quantity) {this.quantity = quantity;}
    public double getUnitPrice() {return unitPrice;}
    @Override
    public String toString(){
        return String.format("《%s》×%d @%.2f = %.2f",
                book.getTitle(), quantity, unitPrice, calcSubtotal());
    }
}


