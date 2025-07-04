
# 🍽️ Restaurant Billing System

[![Java](https://img.shields.io/badge/Java-8%2B-blue.svg?logo=java)](https://www.java.com/)
[![MySQL](https://img.shields.io/badge/MySQL-Database-orange.svg?logo=mysql)](https://www.mysql.com/)
[![Platform](https://img.shields.io/badge/Platform-Desktop-lightgrey.svg?logo=windows)](https://www.oracle.com/java/technologies/javase/javaswing.html)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A Java Swing application for restaurant billing and order management, featuring a MySQL backend.  
Allows menu display, customer order entry, total calculation, and persistent order storage.

---

## ✨ Features

- 📋 View menu items from a MySQL database
- 🧑‍💼 Enter customer name and select item quantities
- 🧮 Calculate total price for the order
- 💾 Save orders and order items to the database
- 🖥️ Simple, user-friendly desktop interface

---

## 🛠️ Requirements

- ☕ Java 8 or higher
- 🐬 MySQL Server
- 🔗 [MySQL Connector/J (JDBC Driver)](https://dev.mysql.com/downloads/connector/j/)  
  <sub>***(Add the JAR to your project’s classpath. If using IntelliJ IDEA, you can add it via Project Structure > Libraries.)***</sub>
- 💻 [IntelliJ IDEA](https://www.jetbrains.com/idea/) (recommended IDE)
---

## 🗄️ Database Setup

1. **Create the database and tables:**  
   Open your MySQL client and execute:

   ```
   CREATE DATABASE IF NOT EXISTS restaurant_billing;
   USE restaurant_billing;

   CREATE TABLE IF NOT EXISTS menu (
       item_id INT AUTO_INCREMENT PRIMARY KEY,
       item_name VARCHAR(100) NOT NULL,
       price DECIMAL(10,2) NOT NULL
   );

   CREATE TABLE IF NOT EXISTS orders (
       order_id INT AUTO_INCREMENT PRIMARY KEY,
       customer_name VARCHAR(100) NOT NULL,
       total_price DECIMAL(10,2) NOT NULL,
       order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );

   CREATE TABLE IF NOT EXISTS order_items (
       order_item_id INT AUTO_INCREMENT PRIMARY KEY,
       order_id INT NOT NULL,
       item_id INT NOT NULL,
       quantity INT NOT NULL,
       price DECIMAL(10,2) NOT NULL,
       FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
       FOREIGN KEY (item_id) REFERENCES menu(item_id)
   );
   ```

2. **Insert sample menu data (optional):**

   ```
   INSERT INTO menu (item_name, price) VALUES
   ('Margherita Pizza', 8.99),
   ('Veggie Burger', 6.49),
   ('Coke', 1.99),
   ('Pasta Alfredo', 10.99);
   ```

3. **Update database credentials:**  
   Make sure your database URL, username, and password in the Java code match your MySQL setup.

---

## 🚀 Build & Run

1. **Clone the repository:**
   ```
   git clone https://github.com/yourusername/restaurant-billing-system.git
   cd restaurant-billing-system
   ```

2. **Compile the code:**  
   Make sure the MySQL JDBC driver (`mysql-connector-java-x.x.x.jar`) is in your classpath.

   ```
   javac -cp "lib/*" src/Main.java
   ```

3. **Run the application:**
   ```
   java -cp "lib/*:src" Main
   ```
   *(On Windows, use `;` instead of `:` in the classpath.)*

---

## 📁 Project Structure

```
restaurant-billing-system/
├── src/
│   └── Main.java
├── .gitignore
└── README.md

```

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---
## 🤝 Contributing

Contributions are welcome!  
Please open an issue to discuss changes or improvements before submitting a pull request.

If you'd like to add features, improve code quality, or extend functionality, feel free to fork and build upon this project.

---

## 🙋 Author

Developed by **@Avnish1447**


[![GitHub](https://img.shields.io/badge/-GitHub-181717?style=flat&logo=github&logoColor=white)](https://github.com/Avnish1447)&nbsp;&nbsp;[![Email](https://img.shields.io/badge/-Email-D14836?style=flat&logo=gmail&logoColor=white)](mailto:avnishagrawal1447@gmail.com)&nbsp;&nbsp;[![LinkedIn](https://img.shields.io/badge/-LinkedIn-0A66C2?style=flat&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/avnish-agrawal-84b39728a/)


