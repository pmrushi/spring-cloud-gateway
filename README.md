# spring-cloud-gateway
Spring cloud gateway example

### Run Application 
Start order-service & catalog-service applications

Order Service
```
cd order-service

./gradlew bootRun
gradlew.bat bootRun (windows)
```

Catalog Service
```
cd catalog-service

./gradlew bootRun
gradlew.bat bootRun (windows)
```

API-Gateway
```
./gradlew bootRun
gradlew.bat bootRun (windows)

```

### Test Application 
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

