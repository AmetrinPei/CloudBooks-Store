package com.cloudbooks.repository;

import com.cloudbooks.domain.Order;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @description://TODO create:2026/9/23 11:33
 * author:lenovo
 * version：V1.0
 **/
public class InMemoryOrderRepository implements OrderRepository {
    protected final Map<String, Order> store = new LinkedHashMap<>();
    @Override
    public void save(Order entity) {
        store.put(entity.getOrderId(), entity);
    }

    @Override
    public Order findById(String id) {
        return store.get(id);
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(store.values());
    }
}
