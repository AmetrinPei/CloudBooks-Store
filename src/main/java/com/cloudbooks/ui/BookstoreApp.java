package com.cloudbooks.ui;
import com.cloudbooks.domain.Book;
import com.cloudbooks.domain.Member;
import com.cloudbooks.domain.Order;
import com.cloudbooks.domain.OrderItem;
import com.cloudbooks.domain.OrderStatus;
import com.cloudbooks.domain.ShoppingCart;
import com.cloudbooks.exception.BookstoreException;
import com.cloudbooks.repository.BookRepository;
import com.cloudbooks.repository.FileBookRepository;
import com.cloudbooks.repository.FileMemberRepository;
import com.cloudbooks.repository.FileOrderRepository;
import com.cloudbooks.repository.MemberRepository;
import com.cloudbooks.repository.OrderRepository;
import com.cloudbooks.service.BookService;
import com.cloudbooks.service.MemberService;
import com.cloudbooks.service.OrderService;
import com.cloudbooks.service.ReportService;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
/**
 * @description://TODO create:2026/9/24 10:13
 * author:lenovo
 * version：V1.0
 **/
public class BookstoreApp {
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final BookService bookService;
    private final MemberService memberService;
    private final OrderService orderService;
    private final ReportService reportService;
    private final Scanner scanner = new Scanner(System.in);
    public BookstoreApp(BookService bookService, MemberService memberService,
                        OrderService orderService, ReportService reportService) {
        this.bookService = bookService;
        this.memberService = memberService;
        this.orderService = orderService;
        this.reportService = reportService;
    }

    public static void main(String[] args) {
        // 装配仓储层
        BookRepository bookRepo = new FileBookRepository(Paths.get("data", "books.dat"));
        MemberRepository memberRepo = new FileMemberRepository(Paths.get("data", "members.dat"));
        OrderRepository orderRepo = new FileOrderRepository(Paths.get("data", "orders.dat"));
        // 装配服务层
        BookstoreApp app = new BookstoreApp(new BookService(bookRepo), new MemberService(memberRepo),
                new OrderService(orderRepo, bookRepo, memberRepo),
                new ReportService(orderRepo, bookRepo, memberRepo));
        app.seedDataIfAbsent();
        app.run();
    }
    public void run() {
        System.out.println("========== 欢迎光临 云间书店 CloudBooks ==========");
        boolean running = true;
        while (running) {
            System.out.println("\n【主菜单】 1 图书管理  2 会员管理  3 购物下单  4 订单管理  5 统计报表  0 退出");
            try {
                switch (readLine("请输入选项：")) {
                    case "1" -> bookMenu();
                    case "2" -> memberMenu();
                    case "3" -> shoppingFlow();
                    case "4" -> orderMenu();
                    case "5" -> showReports();
                    case "0" -> { running = false; System.out.println("数据已保存，感谢使用，再见！"); }
                    default -> System.out.println("无效选项，请重新输入");
                }
            } catch (RuntimeException e) {
                System.out.println("[系统异常] " + e.getMessage());
            }
        }
    }
    private void bookMenu() {
        while (true) {
            System.out.println("\n【图书管理】 1 新增  2 修改  3 删除  4 按 ISBN 查询  5 列表  6 搜索  0 返回");
            switch (readLine("请输入选项：")) {
                case "1" -> addBook();
                case "2" -> updateBook();
                case "3" -> System.out.println(bookService.removeBook(readLine("请输入要删除的图书 ISBN：")) ? "删除成功" : "图书不存在，删除失败");
                case "4" -> {
                    Book book = bookService.getBook(readLine("请输入 ISBN："));
                    System.out.println(book != null ? book : "未找到该图书");
                }
                case "5" -> printBookTable(bookService.listBooks());
                case "6" -> {
                    List<Book> books = bookService.search(readLine("请输入书名/作者关键词："));
                    System.out.println("找到 " + books.size() + " 本：");
                    printBookTable(books);
                }
                case "0" -> { return; }
                default -> System.out.println("无效选项");
            }
        }
    }
    private void addBook() {
        try {
            String isbn = readLine("ISBN：");
            String title = readLine("书名：");
            String author = readLine("作者：");
            double price = readDouble("定价：");
            int stock = readInt("库存：");
            String category = readLine("分类：");
            LocalDate publishDate = readDate("出版日期（yyyy-MM-dd）：");
            bookService.addBook(new Book(isbn, title, author, price, stock, category, publishDate));
            System.out.println("新增成功：《" + title + "》");
        } catch (BookstoreException e) {
            System.out.println("新增失败：" + e.getMessage());
        }
    }
    private void updateBook() {
        Book book = bookService.getBook(readLine("请输入要修改的图书 ISBN："));
        if (book == null) {
            System.out.println("图书不存在");
            return;
        }
        System.out.println("当前信息：" + book);
        try {
            book.setTitle(readLine("新书名："));
            book.setAuthor(readLine("新作者："));
            book.setPrice(readDouble("新定价："));
            book.setStock(readInt("新库存："));
            book.setCategory(readLine("新分类："));
            book.setPublishDate(readDate("新出版日期（yyyy-MM-dd）："));
            bookService.updateBook(book);
            System.out.println("修改成功");
        } catch (BookstoreException e) {
            System.out.println("修改失败：" + e.getMessage());
        }
    }
    private void printBookTable(List<Book> books) {
        if (books.isEmpty()) { System.out.println("（暂无图书）"); return; }
        System.out.println(
                pad("ISBN", 16) + pad("书名", 26) + pad("作者", 18)
                        + padLeft("定价", 8) + padLeft("库存", 5) + "  分类");
        for (Book book : books) {
            System.out.println(
                    pad(book.getIsbn(), 16) + pad(book.getTitle(), 26) + pad(book.getAuthor(), 18)
                            + padLeft(String.format("%.2f", book.getPrice()), 8)
                            + padLeft(String.valueOf(book.getStock()), 5)
                            + "  " + book.getCategory());
        }
    }

    /** 按终端显示宽度左对齐：中文等全角字符占 2 列 */
    private static String pad(String s, int width) {
        int w = displayWidth(s);
        return w >= width ? s + "  " : s + " ".repeat(width - w);
    }

    /** 右对齐版本（用于数字列） */
    private static String padLeft(String s, int width) {
        int w = displayWidth(s);
        return w >= width ? s : " ".repeat(width - w) + s;
    }

    /** 计算字符串在等宽终端里的显示宽度（全角 2 列，半角 1 列） */
    private static int displayWidth(String s) {
        int w = 0;
        for (char c : s.toCharArray()) {
            if ((c >= 0x2E80 && c <= 0xA4CF)     // CJK 部首、汉字、假名
                    || (c >= 0xAC00 && c <= 0xD7A3)   // 韩文
                    || (c >= 0xF900 && c <= 0xFAFF)   // CJK 兼容表意
                    || (c >= 0xFF00 && c <= 0xFF60)   // 全角形式
                    || (c >= 0xFFE0 && c <= 0xFFE6)) { // 全角符号
                w += 2;
            } else {
                w++;
            }
        }
        return w;
    }
    private void memberMenu() {
        while (true) {
            System.out.println("\n【会员管理】 1 注册  2 充值  3 查询  4 列表  0 返回");
            switch (readLine("请输入选项：")) {
                case "1" -> {
                    try {
                        System.out.println("注册成功：" + memberService.register(readLine("请输入会员姓名：")));
                    } catch (BookstoreException e) { System.out.println("注册失败：" + e.getMessage()); }
                }
                case "2" -> {
                    try {
                        String id = readLine("请输入会员编号：");
                        memberService.recharge(id, readDouble("请输入充值金额："));
                        System.out.println("充值成功：" + memberService.requireMember(id));
                    } catch (BookstoreException e) { System.out.println("充值失败：" + e.getMessage()); }
                }
                case "3" -> {
                    try {
                        System.out.println(memberService.requireMember(readLine("请输入会员编号：")));
                    } catch (BookstoreException e) { System.out.println(e.getMessage()); }
                }
                case "4" -> memberService.listMembers().forEach(System.out::println);
                case "0" -> { return; }
                default -> System.out.println("无效选项");
            }
        }
    }
    private void shoppingFlow() {
        Member member;
        try { member = memberService.requireMember(readLine("请输入会员编号：")); }
        catch (BookstoreException e) { System.out.println(e.getMessage()); return; }
        ShoppingCart cart = new ShoppingCart(member);
        System.out.println("开始加购（输入 ISBN=0 结束）");
        while (true) {
            String isbn = readLine("图书 ISBN（0 结束）：");
            if ("0".equals(isbn)) break;
            Book book = bookService.getBook(isbn);
            if (book == null) { System.out.println("图书不存在，请重新输入"); continue; }
            int quantity = readInt("购买数量：");
            if (quantity <= 0) { System.out.println("数量必须大于 0"); continue; }
            cart.addBook(book, quantity);
            System.out.println("已加入购物车：《" + book.getTitle() + "》×" + quantity);
        }
        if (cart.isEmpty()) { System.out.println("购物车为空，未下单"); return; }
        System.out.printf("原价合计 %.2f 元，%s（%s）折扣率 %.2f%n", cart.calcOriginalTotal(),
                member.getName(), member.getLevel().getDesc(), member.getLevel().getDiscountRate());
        if (!"y".equalsIgnoreCase(readLine("确认下单？（y/n）"))) { System.out.println("已取消"); return; }
        try {
            Order order = orderService.checkout(member, cart);
            System.out.println("下单成功！订单号：" + order.getOrderId());
            System.out.printf("折后应付：%.2f 元，获得积分 %d，当前等级：%s%n",
                    order.getTotalAmount(), (int) order.getTotalAmount(), member.getLevel().getDesc());
        } catch (BookstoreException e) {
            System.out.println("下单失败：" + e.getMessage());
        }
    }
    private void orderMenu() {
        while (true) {
            System.out.println("\n【订单管理】 1 查询全部  2 支付  3 发货  4 完成  5 取消  0 返回");
            switch (readLine("请输入选项：")) {
                case "1" -> listOrders();
                case "2" -> {
                    try {
                        orderService.pay(readLine("请输入订单号："), readLine("请输入付款会员编号："));
                        System.out.println("支付成功");
                    } catch (BookstoreException e) { System.out.println("支付失败：" + e.getMessage()); }
                }
                case "3" -> transit("发货", true);
                case "4" -> transit("完成", false);
                case "5" -> {
                    try {
                        orderService.cancel(readLine("请输入订单号："));
                        System.out.println("取消成功，库存已回补");
                    } catch (BookstoreException e) { System.out.println("取消失败：" + e.getMessage()); }
                }
                case "0" -> { return; }
                default -> System.out.println("无效选项");
            }
        }
    }
    private void listOrders() {
        List<Order> orders = orderService.listOrders();
        if (orders.isEmpty()) { System.out.println("（暂无订单）"); return; }
        for (Order order : orders) {
            System.out.printf("订单 %s ｜ %s ｜ 金额 %.2f ｜ 状态：%s%n", order.getOrderId(),
                    order.getCreateTime().format(TIME_FORMAT), order.getTotalAmount(), order.getStatus().getDesc());
            for (OrderItem item : order.getItems()) {
                System.out.println("    - " + item);
            }
        }
    }
    private void transit(String action, boolean ship) {
        try {
            String orderId = readLine("请输入订单号：");
            if (ship) orderService.ship(orderId);
            else orderService.complete(orderId);
            System.out.println(action + "成功");
        } catch (BookstoreException e) { System.out.println(action + "失败：" + e.getMessage()); }
    }
    private void showReports() {
        System.out.println("\n========== 云间书店经营报表 ==========");
        System.out.printf("在售图书：%d 种 ｜ 注册会员：%d 人%n", reportService.bookCount(), reportService.memberCount());
        System.out.printf("总营收（已支付及以后状态）：%.2f 元%n", reportService.calcTotalRevenue());
        System.out.println("订单状态分布：");
        Map<OrderStatus, Long> statusCount = reportService.countByStatus();
        for (OrderStatus status : OrderStatus.values()) {
            System.out.printf("  %-6s %d 单%n", status.getDesc(), statusCount.getOrDefault(status, 0L));
        }
        System.out.println("畅销榜 TOP3：");
        List<Map.Entry<Book, Integer>> top = reportService.topSelling(3);
        if (top.isEmpty()) System.out.println("  （暂无销量数据）");
        int rank = 1;
        for (Map.Entry<Book, Integer> entry : top) {
            System.out.printf("  第%d名 《%s》 售出 %d 本%n", rank++, entry.getKey().getTitle(), entry.getValue());
        }
        System.out.println("======================================");
    }
    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    private int readInt(String prompt) {
        while (true) {
            try { return Integer.parseInt(readLine(prompt)); }
            catch (NumberFormatException e) { System.out.println("请输入合法整数"); }
        }
    }
    private double readDouble(String prompt) {
        while (true) {
            try { return Double.parseDouble(readLine(prompt)); }
            catch (NumberFormatException e) { System.out.println("请输入合法数字"); }
        }
    }
    private LocalDate readDate(String prompt) {
        while (true) {
            try { return LocalDate.parse(readLine(prompt)); }
            catch (DateTimeParseException e) { System.out.println("日期格式应为 yyyy-MM-dd"); }
        }
    }
    private void seedDataIfAbsent() {
        if (!bookService.listBooks().isEmpty()) return;
        try {
            bookService.addBook(new Book("9787111547426", "Java 核心技术 卷 I",
                    "Cay S. Horstmann", 119.0, 20, "编程", LocalDate.of(2022, 8, 1)));
            bookService.addBook(new Book("9787111632948", "深入理解 Java 虚拟机",
                    "周志明", 129.0, 10, "编程", LocalDate.of(2019, 12, 1)));
            bookService.addBook(new Book("9787115428028", "Head First Java",
                    "Kathy Sierra", 89.0, 15, "编程", LocalDate.of(2022, 4, 1)));
            bookService.addBook(new Book("9787020002207", "红楼梦",
                    "曹雪芹", 59.7, 30, "文学", LocalDate.of(2008, 4, 1)));
            System.out.println("已初始化 4 本示例图书");
        } catch (BookstoreException e) {
            System.out.println("初始化示例数据失败：" + e.getMessage());
        }
    }
}
