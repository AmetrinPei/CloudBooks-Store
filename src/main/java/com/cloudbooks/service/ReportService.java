package com.cloudbooks.service;
import com.cloudbooks.domain.Book;
import com.cloudbooks.domain.Order;
import com.cloudbooks.domain.OrderItem;
import com.cloudbooks.domain.OrderStatus;
import com.cloudbooks.repository.BookRepository;
import com.cloudbooks.repository.MemberRepository;
import com.cloudbooks.repository.OrderRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * @description://TODO create:2026/9/24 09:43
 * author:lenovo
 * version：V1.0
 **/
public class ReportService {
    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    public ReportService(OrderRepository orderRepository, BookRepository bookRepository,
                         MemberRepository memberRepository) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    public double calcTotalRevenue() {
        double sum = 0;
        for (Order order : orderRepository.findAll()) {
            OrderStatus status = order.getStatus();
            if (status == OrderStatus.PAID || status == OrderStatus.SHIPPED
                    || status == OrderStatus.COMPLETED) {
                sum += order.getTotalAmount();
            }
        }
        return Math.round(sum * 100) / 100.0;
    }

    public Map<OrderStatus, Long> countByStatus() {
        return orderRepository.findAll().stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
    }
    public List<Map.Entry<Book, Integer>> topSelling(int n) {
        Map<Book, Integer> sales = new LinkedHashMap<>();
        for (Order order : orderRepository.findAll()) {
            if (order.getStatus() == OrderStatus.CANCELLED) {
                continue;   // 已取消订单不计入销量
            }
            for (OrderItem item : order.getItems()) {
                sales.merge(item.getBook(), item.getQuantity(), Integer::sum);
            }
        }
        return sales.entrySet().stream()
                .sorted(Map.Entry.<Book, Integer>comparingByValue().reversed())
                .limit(n)
                .toList();
    }
    public int bookCount() { return bookRepository.findAll().size(); }
    public int memberCount() { return memberRepository.findAll().size(); }
}
