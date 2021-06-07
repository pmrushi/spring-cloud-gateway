#Spring Cloud Gateway Architecture
Spring Cloud Gateway is API Gateway implementation by Spring Cloud team on top of Spring reactive ecosystem. It consists of the following building blocks-
Route: Route the basic building block of the gateway. It consists of

•	ID
•   Destination URI
•	Collection of predicates and a collection of filters

A route is matched if aggregate predicate is true.
Predicate: This is similar to Java 8 Function Predicate. Using this functionality we can match HTTP request, such as headers , url, cookies or parameters.
Filter: These are instances Spring Framework GatewayFilter. Using this we can modify the request or response as per the requirement.
When the client makes a request to the Spring Cloud Gateway, the Gateway Handler Mapping first checks if the request matches a route. This matching is done using the predicates. If it matches the predicate then the request is sent to the filters.


#Implementing Spring Cloud Gateway
Using Spring Cloud Gateway we can create routes in either of the two ways -
•	Use java based configuration to programmatically create routes
•	Use property based configuration(i.e application.properties or application.yml) to create routes.
Implement First Microservice

The first project will be as follows-

![img_3.png](img_3.png)

The build.gradle will be as follows-


```
plugins {
    id 'org.springframework.boot' version '2.4.5'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    id 'java'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

test {
    useJUnitPlatform()
}
```

#Define the application.yml as follows-
```
server:
  port: 9001

spring:
  application:
    name: first-service
```


#Create a Controller class that exposes the GET REST service as follows-
```
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
```


#Create the bootstrap class with the @SpringBootApplication annotation

```
package com.example.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CatalogApplication {

public static void main(String[] args) {
        SpringApplication.run(CatalogApplication.class, args);
    }

}
```

#Implement Second Microservice
The second project will be as follows-

![img_3.png](img_3.png)

The build.gradle will be as follows-
```
plugins {
    id 'org.springframework.boot' version '2.4.5'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    id 'java'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

test {
    useJUnitPlatform()
}
```
#@Define the application.properties as follows-
```
server:
  port: 9002

spring:
  application:
    name: second-service
```
#Create a Controller class that exposes the GET REST service as follows-
```
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
```
#Create the bootstrap class with the @SpringBootApplication annotation
```
package com.example.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderApplication {

public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

}
```
#Implement Spring Cloud Gateway using property based config

The Spring Cloud Gateway project will be as follows-

![img_4.png](img_4.png)


#The build.gradle will be as follows-
```
plugins {
    id 'org.springframework.boot' version '2.4.5'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    id 'java'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

test {
    useJUnitPlatform()
}
```
Define the application.yml as follows-
```
spring:
  cloud:
    gateway:
      routes:
        - id: orderModule
          uri: http://localhost:9001/
          predicates:
            - Path=/api/v1/orders/**
        - id: catalogModule
          uri: http://localhost:9002/
          predicates:
            - Path=/api/v1/products/**
```
#Implement Spring Cloud Gateway using Java based config

he Spring Cloud Gateway project will be as follows-

![img_4.png](img_4.png)

#Create a Controller class that exposes the GET REST service as follows-
```
ppackage com.example.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringCloudConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/api/v1/orders/**")
                        .uri("http://localhost:9001/"))
                .route(r -> r.path("/api/v1/products/**")
                        .uri("http://localhost:9002/"))
                .build();
    }
}
```

#Rest endpoints

Start the  microservices and the gateway and then hit the rest endpoints-

Get Orders - GET - http://localhost:8080/api/v1/orders/

Response
```
[
   {
      "orderId":"order1",
      "itemName":"pen",
      "quantity":2
   },
   {
      "orderId":"order2",
      "itemName":"book",
      "quantity":2
   }
]
```
Get Products - GET - http://localhost:8080/api/v1/products

Response
```
[
   {
      "productId":"product1",
      "sku":"sku1",
      "productName":"pen",
      "price":10
   },
   {
      "productId":"product",
      "sku":"sku2",
      "productName":"pencil",
      "price":5.5
   }
]
```
#Spring Boot Actuator

Spring Boot includes a number of additional features to help you monitor and manage your application when you push it to production. You can choose to manage and monitor your application by using HTTP endpoints or with JMX. Auditing, health, and metrics gathering can also be automatically applied to your application.

For Gradle, use the following declaration:
```
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
}
```

# Endpoints
Actuator endpoints let you monitor and interact with your application. Spring Boot includes a number of built-in endpoints and lets you add your own.

Each individual endpoint can be enabled or disabled and exposed (made remotely accessible) over HTTP or JMX. An endpoint is considered to be available when it is both enabled and exposed. The built-in endpoints will only be auto-configured when they are available. Most applications choose exposure via HTTP, where the ID of the endpoint along with a prefix of `/actuator` is mapped to a URL. For example, by default, the health endpoint is mapped to `/actuator/health`.

For example, the health endpoint provides basic application health information.
# Actuator health check

OrderServcie : http://localhost:9001/actuator/health

CatalogServcie : http://localhost:9002/actuator/health

ApuGateway : http://localhost:8080/actuator/health

# Services health check (Customized)

GET - http://localhost:8080/actuator/services/status

Response
```
[
   {
      "service":"http://localhost:9001/",
      "status":"UP"
   },
   {
      "service":"http://localhost:9002/",
      "status":"DOWN"
   }
]
```

#References
https://spring.io/projects/spring-cloud-gateway

https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints

https://www.javainuse.com/spring/cloud-gateway
