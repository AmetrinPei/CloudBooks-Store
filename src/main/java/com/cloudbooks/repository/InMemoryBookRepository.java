package com.cloudbooks.repository;

import com.cloudbooks.domain.Book;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @description://TODO create:2026/9/23 10:30
 * author:lenovo
 * version：V1.0
 **/
public class InMemoryBookRepository implements BookRepository {
    protected final Map<String, Book> store = new LinkedHashMap<>();
    @Override
    public boolean delete(String isbn) {
        return store.remove(isbn)!=null;
    }

    @Override
    public List<Book> findByKeyword(String keyword) {
        List<Book> result = new ArrayList<>();
        for(Book book : store.values()){
            if(book.getTitle().contains(keyword)||book.getAuthor().contains(keyword)){
                result.add(book);
            }
        }
        return result;
    }

    @Override
    public void save(Book book) {
        store.put(book.getIsbn(),book);

    }

    @Override
    public Book findById(String isbn) {
        return store.get(isbn);
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(store.values());
    }
}
