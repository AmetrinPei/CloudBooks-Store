package com.cloudbooks.repository;
import com.cloudbooks.domain.Book;
import java.util.List;

public interface BookRepository extends Repository<Book> {
    boolean delete(String isbn);
    List<Book> findByKeyword(String keyword);
}
