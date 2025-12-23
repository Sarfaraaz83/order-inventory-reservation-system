# 🛒 Order & Inventory Reservation System

## 📌 Problem Statement

In high-traffic e-commerce systems, multiple users may attempt to purchase the same product concurrently.  
A naive implementation can lead to **overselling**, **inconsistent inventory**, or **invalid payments**.

This project implements a **transaction-safe order and inventory system** that:

- Prevents overselling under concurrency
- Supports inventory reservation with TTL (time-to-live)
- Confirms orders only if payment is completed within the TTL
- Automatically cleans up expired reservations
- Ensures data consistency using database-level locking

---

## 🧠 High-Level Design

### Core Concepts

- Reservation-based ordering
- Row-level database locking
- Transactional guarantees
- Eventual consistency via background cleanup

### Order Lifecycle



1. Order Placement
-----------------
User → POST /orders
|
v
- Inventory row locked (PESSIMISTIC_WRITE)
- available_quantity >= requested_quantity ?
  YES → reserve inventory
  NO  → reject order
- Order created with status = CREATED
- reservationExpiry = now + TTL


2. Payment Submission
--------------------
User → POST /orders/{orderId}/pay
|
v
- Order row locked
- If status != CREATED → reject (idempotency)
- If current_time > reservationExpiry → reject payment
- Else → status = CONFIRMED


3. Background Cleanup (Scheduler)
--------------------------------
Scheduler runs periodically

- Fetch orders where:
  status = CREATED
  AND reservationExpiry < now
- Lock order & inventory rows
- Mark order = EXPIRED
- Release reserved inventory


Final States
------------
- CREATED    → CONFIRMED  (payment within TTL)
- CREATED    → EXPIRED    (TTL expired)
- CONFIRMED  → terminal state
- EXPIRED    → terminal state

System Architecture

Client (Postman / curl)
|
v
+----------------------------+
|  REST Controller Layer     |
|  (OrderController)         |
+----------------------------+
|
v
+----------------------------+
|  Service Layer             |
|  - OrderService            |
|  - InventoryService        |
|  (@Transactional)          |
+----------------------------+
|
v
+----------------------------+
|  Persistence Layer         |
|  - JPA Repositories        |
|  - Row-level locks         |
+----------------------------+
|
v
+----------------------------+
|  PostgreSQL Database       |
|  - orders table            |
|  - inventory table         |
+----------------------------+
|
v
+----------------------------+
|  Scheduled Cleanup Job     |
|  - Expires orders          |
|  - Releases inventory     |
+----------------------------+



---

## ⚙️ Technology Stack

- **Backend:** Java, Spring Boot
- **Persistence:** Spring Data JPA, Hibernate
- **Database:** PostgreSQL
- **Build Tool:** Maven
- **Testing:** Postman, Multithreaded concurrency tests
- **Concurrency Control:** DB-level pessimistic locking

---

## 🔑 Key Design Decisions

### 1️⃣ Inventory Reservation (No Overselling)
- Inventory is reserved at order placement
- Uses **pessimistic write locks** on inventory rows
- Prevents concurrent users from overselling the same product

### 2️⃣ TTL-Based Order Expiry
- Each order has a `reservationExpiry` timestamp
- Payments after TTL are rejected
- Expiry logic is handled by a background job, not during payment

### 3️⃣ Background Cleanup Job
- Periodically scans for expired orders
- Marks them as `EXPIRED`
- Releases reserved inventory
- Ensures eventual consistency

### 4️⃣ Transaction Safety
- All critical operations are wrapped in `@Transactional`
- Rollbacks prevent partial or inconsistent state
- Payment flow is idempotent

---

