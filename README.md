# Saga Pattern with Spring Boot and RabbitMQ

## Overview

This is a **simple demonstration** of the **Saga Pattern** using **Spring Boot**, **Spring Cloud Stream**, and **RabbitMQ** for asynchronous messaging. The project shows how multiple services interact in a distributed system to manage transactions consistently, even when certain operations fail. The main goal is to coordinate different microservices using the **Saga Pattern** to handle distributed transactions, ensuring that any failure triggers compensating actions to maintain data integrity.

This example should not be overly criticized for being overly simplified. The primary objective is to illustrate the Saga Pattern using asynchronous messaging, rather than providing a production-ready application.

### Services Involved

1. **Account Service**: Responsible for deducting the balance from the user's account and, if necessary, refunding it.
2. **Order Service**: Handles updates to the order's status, such as whether the payment succeeded or failed.
3. **Inventory Service**: Manages inventory allocation for an order.

### Technologies Used
- **Spring Boot** for building services.
- **Spring Cloud Stream** for messaging abstractions.
- **RabbitMQ** as the message broker.
- **Docker Compose** for orchestrating the services.

## Saga Pattern Explanation
The **Saga Pattern** is a way to manage distributed transactions across multiple microservices without using a centralized coordinator or distributed locks. Each microservice performs a **local transaction** and sends messages to coordinate the next action in the process or trigger **compensating transactions** if something fails. In this example, if an operation fails, a rollback or compensation mechanism is executed to keep the system consistent.

### Key Components
1. **AccountService**: Deducts balance, listens for order and inventory updates to determine if a refund is needed.
2. **OrderService**: Listens for balance deductions to determine if the order can be updated to "Paid" or if it has failed.
3. **InventoryService**: Allocates inventory upon a successful order update.
4. **RabbitMQ**: Facilitates communication between services.

## Implementation Details
### AccountController
The `AccountController` provides an endpoint to deduct balance from a user's account.
- When `/account/deduct-balance` is called, the balance deduction is attempted. On success, a `BalanceDeductedEvent` is sent using `StreamBridge`.

### AccountService
The `AccountService` is responsible for listening to messages from other services and triggering compensating actions if needed.
- **orderUpdateAccount()**: Listens for `OrderPaidEvent` messages, specifically for failures, and triggers a balance refund.
- **inventoryUpdate()**: Listens for `InventoryAllocationEvent` failures and processes a refund if needed.

### InventoryService
The `InventoryService` allocates inventory for orders and sends an event indicating success or failure.
- **orderUpdate()**: Listens for `OrderPaidEvent`. If inventory allocation fails, an `InventoryAllocationFailed` event is sent using `StreamBridge`.

### OrderService
The `OrderService` listens to `BalanceDeductedEvent` messages and determines whether the order is successfully updated or if it should fail.
- On success, it sends an `OrderPaidEvent` indicating the status of the order.

### RabbitMQ
**RabbitMQ** acts as the broker for asynchronous messaging, and the queues used in this example facilitate event-based communication between the different services.

## Configuration
### `application.yml`
The **application.yml** contains configuration for the different Spring Cloud Stream bindings for each service.
- **Bindings** are used to declare the inputs and outputs of each consumer/producer.
- The configuration specifies how messages flow through different queues to achieve the distributed transaction.

```yaml
spring:
  application:
    name: saga-pattern
  cloud:
    function:
      definition: balanceUpdate;orderUpdate;inventoryUpdate;orderUpdateAccount
    stream:
      default:
        content-type: application/json
      bindings:
        balanceUpdate-in-0:
          destination: balance-update-queue
          group: account-group
        orderUpdate-out-0:
          destination: order-update-queue
        orderUpdate-in-0:
          destination: order-update-queue
          group: order-group
        orderUpdateAccount-in-0:
          destination: order-update-queue
          group: order-account-group
        inventoryUpdate-out-0:
          destination: inventory-update-queue
        inventoryUpdate-in-0:
          destination: inventory-update-queue
          group: inventory-group

  rabbitmq:
    host: rabbitmq
    port: 5672
    username: guest
    password: guest
```
### Docker Compose
The **docker-compose** file is used to spin up **RabbitMQ** and the application.

```yaml
services:
  rabbitmq:
    image: rabbitmq:3.11.8-management
    mem_limit: 512m
    ports:
      - 5672:5672
      - 15672:15672
    healthcheck:
      test: [ "CMD", "rabbitmqctl", "status" ]
      interval: 5s
      timeout: 2s
      retries: 60

  app:
    build:
      context: .
    ports:
      - "8080:8080"
    depends_on:
      rabbitmq:
        condition: service_healthy
    environment:
      SPRING_PROFILES_ACTIVE: ""
    healthcheck:
      test: [ "CMD", "curl", "-f", "http://localhost:8080/health" ]
      interval: 30s
      timeout: 15s
      retries: 5
```

## Running the Project
### Prerequisites
- **Docker** and **Docker Compose** installed.
- **Java 17** installed to build and run the Spring Boot application.

### Steps to Run
1. **Build the Application**: Run `./gradlew build` to create the JAR file.
2. **Start Services**: Use `docker-compose up` to start RabbitMQ and the Spring Boot application.
3. **Access RabbitMQ**: RabbitMQ management UI can be accessed at [http://localhost:15672](http://localhost:15672).

### Endpoints
- **Deduct Balance**: `POST /account/deduct-balance`
    - Payload Example:
      ```json
      {
        "userId": "123",
        "orderId": "456",
        "amount": 50.0
      }
      ```

## Summary
This project illustrates the **Saga Pattern** using distributed services and **asynchronous messaging** to maintain data consistency. Each microservice plays a role in ensuring that the complete process either succeeds entirely or compensates where needed, ensuring consistency across the entire workflow. Although this is a basic and simplified example, it serves as a starting point for understanding how to implement saga-based distributed transactions using **Spring Boot** and **RabbitMQ**.

Feel free to experiment with it, and remember that this exercise is just for learning purposes and doesn't represent a production-level implementation of the saga pattern.

