package com.example.catalog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/products")
public class CatalogController {

    @GetMapping
    public List<Product> getOrder() {
        return Arrays.asList(
                new Product("product1", "sku1", "pen", 10),
                new Product("product", "sku2", "pencil", 5.5)
        );
    }

    public class Product {
        public Product(String productId, String sku, String productName, double price) {
            this.productId = productId;
            this.sku = sku;
            this.productName = productName;
            this.price = price;
        }

        public String productId;
        public String sku;
        public String productName;
        public double price;
    }

}