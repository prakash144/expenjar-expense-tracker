# Expenjar: Expense-tracker

Expenjar suggests a digital repository or container for tracking and managing expenses — a modern twist on the traditional "money jar."

---

## GitHub Repo Name

> expenjar or expenjar-expense-tracker (if you want more descriptive)

## Website Domain

> expenjar.com (available as checked)
> expenjar.app (great for apps, check availability)

## LinkedIn Page Name

> Expenjar
> Expenjar - Expense Tracker

## Instagram Handle

> @expenjar
> Or if taken, try @expenjarapp or @expenjar\_official

---

## HLD

[View HLD Document](https://www.notion.so/1fca74f9803c80ff9d2ae0cb107ad5ac?v=1fca74f9803c808dbd4c000c1d7e4788)

---

## 🚀 How to Run in Docker

### Step 1: Build Spring Boot Services

Before building, make sure the JAR file is generated and placed inside each service directory.

* Build Auth-Service:

  ```bash
  docker build -t auth-service ./Auth-Service
  ```

* Build User-Service:

  ```bash
  docker build -t user-service ./User-Service
  ```

### Step 2: Create Required MySQL Databases (using Docker Compose)

* Start the services:

  ```bash
  docker-compose -f services.yml up -d
  ```

* To stop the services:

  ```bash
  docker-compose -f services.yml down
  ```

### Project Structure

```
.
├── Auth-Service
│   ├── app.jar
│   └── Dockerfile
├── services.yml
└── User-Service
    ├── Dockerfile
    └── userservice-0.0.1-SNAPSHOT.jar
```
