package com.cloudbooks.service;
import com.cloudbooks.domain.Book;
import com.cloudbooks.exception.BookstoreException;
import com.cloudbooks.repository.BookRepository;
import java.util.List;
/**
 * @description://TODO create:2026/9/23 16:45
 * author:lenovo
 * version：V1.0
 **/
public class BookService {
    private final BookRepository repository;
    public BookService(BookRepository repository) {
        this.repository = repository;
    }
    public void addBook(Book book) throws BookstoreException {
        if (book == null || book.getIsbn() == null || book.getIsbn().isBlank()){
            throw new BookstoreException("ISBN 不能为空");
        }
        if(repository.findById(book.getIsbn()) != null){
            throw new BookstoreException("ISBN 已存在："+book.getIsbn());
        }
        repository.save(book);
    }
    public void updateBook(Book book) throws BookstoreException {
        if(repository.findById(book.getIsbn()) == null){
            throw new BookstoreException("图书不存在，无法修改："+book.getIsbn());
        }
        repository.save(book);
    }
    public boolean removeBook(String isbn){return repository.delete(isbn);}
    public Book getBook(String isbn){return repository.findById(isbn);}
    public List<Book> listBooks() { return repository.findAll();}
    public List<Book> search(String keyword) { return repository.findByKeyword(keyword);}
}
