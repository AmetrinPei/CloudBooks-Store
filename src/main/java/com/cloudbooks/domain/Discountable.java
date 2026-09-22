package com.cloudbooks.domain;

@FunctionalInterface
public interface Discountable {
    double calcDiscount(double discountRate);
}
