# JPMorgan Chase Software Engineering Virtual Experience (Forage)

This repository contains my implementation of the **Midas Core** system as part of the [Forage JPMorgan Chase Software Engineering Virtual Internship](https://www.theforage.com/).

## Project Overview
The **Midas Core** is responsible for receiving, validating, and recording financial transactions.  
It integrates with:
- **Kafka** for incoming transactions
- **SQL Database (H2 in dev/tests)** for storage & validation
- **REST APIs** for incentivization

The system is built with **Spring Boot** for dependency injection and resource integration.

## Tech Stack
- Java 17
- Spring Boot 3.2.5
- Spring Data JPA
- Spring Web
- Spring Kafka
- H2 Database
- Testcontainers (Kafka)
- Maven (via Maven Wrapper)

## Progress

### ✅ Task One
- Set up local development environment
- Added required dependencies
- Fixed Java version issues (Java 21 → Java 17)
- Configured test properties (`general.kafka-topic`)
- Successfully ran **TaskOneTests**

**Output Snippet:**
> ---begin output ---  
> 1142725631254665682354316777216387420489  
> ---end output ---

---
### ✅ Task Two
- Integrated **Kafka** into Midas Core
- Added `KafkaConfig` to configure Producer, Consumer, and Listener with **JSON serialization**
- Implemented `TransactionListener` to consume messages from Kafka topic configured in `application.yml`
- Verified Kafka listener using embedded Testcontainers Kafka setup
- Successfully ran **TaskTwoTests** and captured the first four incoming transactions

**First 4 Transaction Amounts:**
> 122.86, 42.87, 161.79, 22.22

---


### ✅ Task Three
- Integrated **H2 database** with Spring Data JPA
- Created `TransactionRecord` entity with `@ManyToOne` mapping to `UserRecord`
- Added `TransactionRepository` and `TransactionService` for persistence & validation
- Updated `TransactionListener` to:
  - Validate sender/recipient existence
  - Ensure sufficient balance
  - Persist valid transactions
  - Update balances atomically
- Verified with **TaskThreeTests**

**Final Waldorf Balance (floored):**
> 541

---

## How to Run

1. Ensure **Java 17** and **Docker Desktop** are installed.
2. Clone this repo:
   ```bash
   git clone https://github.com/ValupadasuSaiabbhiram/JPMorgan-Chase-Co-Forage-Midas.git

   cd forage-midas
   ```
3. Run tests
    ```bash
    ./mvnw clean test
    ```
4. Run a specific task test
    ```bash
    #for task one
    ./mvnw -Dtest=TaskOneTests test
    ```
    ```bash
    #for task two
    ./mvnw -Dtest=TaskTwoTests test
    ```
    ```bash
    #for task three
    ./mvnw -Dtest=TaskThreeTests test
    ```

## Author
**Sai Abbhiram Valupadasu**  
&nbsp;&nbsp;&nbsp;&nbsp;Independent Software Developer | Passionate about backend systems & data engineering  
- [LinkedIn](https://www.linkedin.com/in/sai-abbhiram-valupadasu)  
- [GitHub](https://github.com/SaiAbbhiramValupadasu)