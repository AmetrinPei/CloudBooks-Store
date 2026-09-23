package com.cloudbooks.exception;

/**
 * @description://TODO create:2026/9/22 16:19
 * author:lenovo
 * version：V1.0
 **/
public class InsufficientStockException extends BookstoreException{
    private final String isbn;
    private final int requested;
    private final int available;
    public InsufficientStockException(String isbn, int requested, int available) {
        super(String.format("库存不足：图书[%s]需要%d本；仅剩%d本", isbn, requested, available));
        this.isbn = isbn;
        this.requested = requested;
        this.available = available;
    }
    public String getIsbn(){return isbn;}
    public int getRequested(){return requested;}
    public int getAvailable(){return available;}
}
