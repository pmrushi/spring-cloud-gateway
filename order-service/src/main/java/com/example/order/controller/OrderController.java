package com.example.order.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/orders")
public class OrderController {

    @GetMapping
    public List<Order> getOrder() {
        return Arrays.asList(new Order("order1", "pen", 2), new Order("order2", "book", 2));
    }

    public class Order {
        public Order(String orderId, String itemName, int quantity) {
            this.orderId = orderId;
            this.itemName = itemName;
            this.quantity = quantity;
        }

        public String orderId;
        public String itemName;
        public int quantity;
    }

}
