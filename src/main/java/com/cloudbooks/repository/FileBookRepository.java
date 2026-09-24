package com.cloudbooks.repository;
import com.cloudbooks.domain.Book;
import java.nio.file.Path;

/**
 * @description://TODO create:2026/9/23 14:18
 * author:lenovo
 * version：V1.0
 **/
public class FileBookRepository extends InMemoryBookRepository {
    private final Path file;
    public FileBookRepository(Path file) {
        this.file = file;
        for (Book book : FileStorage.<Book>load(file)) store.put(book.getIsbn(), book);
    }
    @Override
    public void save(Book book) { super.save(book); FileStorage.persist(file, findAll()); }
    @Override
    public boolean delete(String isbn) {
        boolean removed = super.delete(isbn);
        if (removed) FileStorage.persist(file, findAll());
        return removed;
    }
}
