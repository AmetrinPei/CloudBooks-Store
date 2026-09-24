package com.cloudbooks.repository;
import com.cloudbooks.domain.Order;
import java.nio.file.Path;

public class FileOrderRepository extends InMemoryOrderRepository {
    private final Path file;
    public FileOrderRepository(Path file) {
        this.file = file;
        for (Order order : FileStorage.<Order>load(file)) store.put(order.getOrderId(), order);
    }
    @Override
    public void save(Order order) { super.save(order); FileStorage.persist(file, findAll()); }
}
