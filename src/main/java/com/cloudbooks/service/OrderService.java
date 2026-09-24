
package com.cloudbooks.service;
import com.cloudbooks.domain.Book;
import com.cloudbooks.domain.Member;
import com.cloudbooks.domain.Order;
import com.cloudbooks.domain.OrderItem;
import com.cloudbooks.domain.OrderStatus;
import com.cloudbooks.domain.ShoppingCart;
import com.cloudbooks.exception.BookstoreException;
import com.cloudbooks.exception.InsufficientStockException;
import com.cloudbooks.exception.InvalidOrderException;
import com.cloudbooks.repository.BookRepository;
import com.cloudbooks.repository.MemberRepository;
import com.cloudbooks.repository.OrderRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @description://TODO create:2026/9/24 09:13
 * author:lenovo
 * version：V1.0
 **/
public class OrderService {
    private static final AtomicLong SEQUENCE = new AtomicLong();   // 订单号序列（第 16 章原子类）
    private static final DateTimeFormatter ORDER_ID_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    public OrderService(OrderRepository orderRepository, BookRepository bookRepository,
                        MemberRepository memberRepository) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }
    public Order checkout(Member member,ShoppingCart cart)throws BookstoreException{
        if (cart == null || cart.isEmpty()) {
            throw new InvalidOrderException("购物车为空，无法下单");
        }
        for (OrderItem item : cart.getItems()) {
            Book latest = bookRepository.findById(item.getBook().getIsbn());
            if (latest == null) {
                throw new InvalidOrderException("图书已下架：" + item.getBook().getTitle());
            }
            if (latest.getStock() < item.getQuantity()) {
                throw new InsufficientStockException(latest.getIsbn(), item.getQuantity(), latest.getStock());
            }
        }
        for (OrderItem item : cart.getItems()) {
            Book latest = bookRepository.findById(item.getBook().getIsbn());
            latest.setStock(latest.getStock() - item.getQuantity());
            bookRepository.save(latest);
        }
        double rate = member.getLevel().getDiscountRate();
        double totalAmount = round2(cart.calcOriginalTotal() * rate);

        Order order = new Order(nextOrderId(), cart.getItems(), totalAmount);
        orderRepository.save(order);
        member.addPoints((int) totalAmount);
        memberRepository.save(member);
        cart.clear();
        return order;
    }

    public void pay(String orderId,String memberId )throws BookstoreException{
        Order order = requireOrder(orderId);
        if(order.getStatus() != OrderStatus.PENDING){
            throw new InvalidOrderException("只有待支付订单可以支付，当前状态："+order.getStatus().getDesc());
        }
        Member member = memberRepository.findById(memberId);
        if (member == null){
            throw new BookstoreException("付款会员不存在："+memberId);
        }
        if (member.getBalance() < order.getTotalAmount()) {
            throw new BookstoreException(String.format("余额不足：应付 %.2f，余额 %.2f，请先充值", order.getTotalAmount(), member.getBalance()));
        }
        member.setBalance(round2(member.getBalance() - order.getTotalAmount()));
        memberRepository.save(member);
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }
    public void ship(String orderId) throws BookstoreException {
        Order order = requireOrder(orderId);
        if (order.getStatus() != OrderStatus.PAID) {
            throw new InvalidOrderException("只有已支付订单可以发货");
        }
        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);
    }
    public void complete(String orderId) throws BookstoreException {
        Order order = requireOrder(orderId);
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new InvalidOrderException("只有已发货订单可以确认完成");
        }
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);
    }
    public void cancel(String orderId) throws BookstoreException {
        Order order = requireOrder(orderId);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderException("只有待支付订单可以取消");
        }
        for (OrderItem item : order.getItems()) {
            Book book = bookRepository.findById(item.getBook().getIsbn());
            if (book != null) {
                book.setStock(book.getStock() + item.getQuantity());   // 回补库存
                bookRepository.save(book);
            }
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
    public List<Order> listOrders() { return orderRepository.findAll(); }
    private Order requireOrder(String orderId) throws InvalidOrderException {
        Order order = orderRepository.findById(orderId);
        if (order == null) throw new InvalidOrderException("订单不存在：" + orderId);
        return order;
    }
    private String nextOrderId() {
        return "O" + LocalDateTime.now().format(ORDER_ID_FORMAT) + String.format("%03d", SEQUENCE.incrementAndGet() % 1000);
    }
    private static double round2(double value) { return Math.round(value * 100) / 100.0; }
}
