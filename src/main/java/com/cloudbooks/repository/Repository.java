package com.cloudbooks.repository;
import java.util.List;

public interface Repository<T> {
    void save(T entity);
    T findById(String id);
    List<T> findAll();
}
