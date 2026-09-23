package com.cloudbooks.exception;

/**
 * @description://TODO create:2026/9/22 16:18
 * author:lenovo
 * version：V1.0
 **/
public class BookstoreException extends Exception {
    public BookstoreException(String message) {
        super(message);
    }
    public BookstoreException(String message,Throwable cause){
        super(message,cause);
    }
}
