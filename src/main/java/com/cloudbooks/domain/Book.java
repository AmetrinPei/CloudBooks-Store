package com.cloudbooks.domain;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @description://TODO create:2026/9/22 10:19
 * author:lenovo
 * version：V1.0
 **/
public class Book implements Serializable, Discountable {
    private static final long serialVersionUID = 1L;
    private String isbn;
    private String title;
    private String author;
    private double price;
    private int stock;
    private String category;
    private LocalDate publishDate;
    public Book() {}
    public Book(String isbn, String title, String author, double price, int stock, String category, LocalDate publishDate) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;

        this.category = category;
        this.publishDate = publishDate;
    }
    @Override
    public double calcDiscount(double discountRate) {
        if (discountRate <= 0||discountRate > 1) {
            throw new IllegalArgumentException("折扣率必须在(0,1]区间");
        }
        return Math.round(price*discountRate*100)/100.0;
    }
    public String getIsbn() {return isbn;  }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDate getPublishDate() { return publishDate; }
    public void setPublishDate(LocalDate publishDate) { this.publishDate = publishDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode(){
        return Objects.hash(isbn);
    }
    @Override
    public String toString(){
        return String.format("《%s》[%s] 作者:%s 价格:%.2f 库存:%d 分类:%s 出版:%s",
                title, isbn, author, price, stock, category, publishDate);
    }

}

