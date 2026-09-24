package com.cloudbooks.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Book 折扣计算测试：类名习惯为「被测类名 + Test」 */
class BookDiscountTest {

    private Book book;

    /** 每个测试方法执行前都会重新初始化——保证测试之间互不影响 */
    @BeforeEach
    void setUp() {
        book = new Book("9787111547426", "Java 核心技术 卷 I", "Cay S. Horstmann",
                119.0, 10, "编程", LocalDate.of(2022, 8, 1));
    }

    /** 测试方法：无参、无返回值，用 @Test 标记，名字描述「预期行为」 */
    @Test
    void goldMemberPriceShouldBeNinetyPercent() {
        // 119.0 × 0.9 = 107.1；第三个参数是比较误差容忍度（double 比较必备）
        assertEquals(107.1, book.calcDiscount(MemberLevel.GOLD.getDiscountRate()), 1e-6);
    }

    @Test
    void normalMemberPriceShouldEqualOriginal() {
        assertEquals(119.0, book.calcDiscount(MemberLevel.NORMAL.getDiscountRate()), 1e-6);
    }

    @Test
    void invalidDiscountRateShouldThrow() {
        // 验证非法折扣率会抛异常——异常路径同样需要测试
        assertThrows(IllegalArgumentException.class, () -> book.calcDiscount(0));
    }
}
